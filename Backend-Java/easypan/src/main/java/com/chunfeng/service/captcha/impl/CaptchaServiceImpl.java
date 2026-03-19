package com.chunfeng.service.captcha.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.util.StrUtil;
import com.chunfeng.componet.RedisComponent;
import com.chunfeng.config.AppConfig;
import com.chunfeng.dto.request.EmailCaptchaRequest;
import com.chunfeng.dto.request.RegisterRequest;
import com.chunfeng.dto.response.LoginResponse;
import com.chunfeng.entity.captcha.CaptchaVO;
import com.chunfeng.entity.constants.Constants;
import com.chunfeng.entity.po.UserInfo;
import com.chunfeng.enums.EmailCaptchaType;
import com.chunfeng.exception.BusinessException;
import com.chunfeng.mapper.UserMapper;
import com.chunfeng.security.JwtUtil;
import com.chunfeng.service.captcha.CaptchaService;
import com.chunfeng.service.redis.TokenRedisService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;
import java.time.Year;


/**
 * @ClassName CaptchaServiceImpl
 * @Author chunfeng
 * @Description
 * @date 2026/3/6 10:13
 * @Version 1.0
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private TokenRedisService tokenRedisService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String IMAGE_CAPTCHA_KEY_PREFIX = "captcha:image:";
    private static final String EMAIL_CAPTCHA_KEY_PREFIX = "captcha:email:";

    // 图片验证码有效期 1分钟
    private static final long IMAGE_EXPIRE = 60;
    // 邮箱验证码有效期 10分钟
    private static final long EMAIL_EXPIRE = 50 * 60;

    @Override
    public void generateEmailCaptcha(EmailCaptchaRequest request) {
        String email = request.getEmail();
        String typeCode = request.getType();

        // 1.验证码邮箱验证码生成类型
        EmailCaptchaType type = EmailCaptchaType.fromCode(typeCode);
        if (type == null) {
            throw new BusinessException("邮箱验证码类型错误");
        }

        // 2.频率限制
        String limitKey = "captcha:email:limit:" + typeCode + ":" + email;
        String lastSendStr = redisComponent.getString(limitKey);
        if (lastSendStr != null) {
            long lastSend = Long.parseLong(lastSendStr);
            long now = System.currentTimeMillis()/1000;
            if (now - lastSend < Constants.EMAIL_SEND_MIN_INTERVAL_SECONDS) {
                throw new BusinessException("请勿频繁发送验证码");
            }
        }

        // 3.生成验证码
        String captchaCode = generateRandomCode();

        // 4.存入redis
        redisComponent.set(EMAIL_CAPTCHA_KEY_PREFIX + typeCode + ":" + email, captchaCode, EMAIL_EXPIRE);

        // 5.准备模板
        Context context = new Context();
        context.setVariable("subject", type.getDesc());
        context.setVariable("appName", Constants.appName);
        context.setVariable("tips", type.getMailTips());
        context.setVariable("code", captchaCode);
        context.setVariable("expireMinutes", Constants.EMAIL_EXPIRE_MINUTES);
        context.setVariable("year", Year.now().getValue());

        // 6.渲染模板
        String htmlContent = templateEngine.process("email/captcha", context);

        // 7.发送邮件
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(appConfig.getSendUserName());
            helper.setTo(email);
            helper.setSubject(Constants.appName + "-" + type.getDesc() + "验证码");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            // 8.记录发送时间
            redisComponent.set(limitKey, String.valueOf(System.currentTimeMillis() / 1000) , Constants.EMAIL_SEND_MIN_INTERVAL_SECONDS);

            log.info("邮箱验证码发送成功 | email:{} | type:{} | code:{}", email, typeCode, captchaCode);

        } catch (Exception e) {
            log.error("发送邮箱验证码失败 | email:{} | type:{}", email, typeCode, e);
            throw new RuntimeException("验证码发送失败，请稍后再试");
        }

    }

    private String generateRandomCode() {
        return String.format("%0" + Constants.EMAIL_CODE_LENGTH + "d",
            new SecureRandom().nextInt((int) Math.pow(10, Constants.EMAIL_CODE_LENGTH)));
    }

    @Override
    public CaptchaVO generateImageCaptcha() {
        // 1.生成唯一 key
        String key = UUID.randomUUID().toString().replace("_","");
        // 2.使用 hutool 生成带圆圈干扰的验证码
        // 参数依次为: 宽130， 高40， 验证码长度4， 干扰圆圈个数10
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(100, 40, 4, 5);
        // 3.从captcha 对象中获取验证码字符，并转为大写
        String code = captcha.getCode().toUpperCase();
        // 4.获取验证码的base64图片字符串
        String imageBase64 = captcha.getImageBase64();
        // 5.存入redis中
        String redisKey = IMAGE_CAPTCHA_KEY_PREFIX + key;
        redisComponent.set(redisKey, code, IMAGE_EXPIRE);
        // 6.组装返回给前端
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setKey(key);
        captchaVO.setImageBase64("data:image/png;base64," + imageBase64);
        return captchaVO;
    }

    @Override
    public void verifyImageCaptcha(String key, String code) {
        if (StrUtil.isBlank( key) || StrUtil.isBlank(code))
            throw new BusinessException("图像验证码不能为空");
        String redisKey = IMAGE_CAPTCHA_KEY_PREFIX + key;
        String redisCode = redisComponent.getString(redisKey);
        if (redisCode == null)
            throw new BusinessException("图像验证码已过期或不存在");
        if (!redisCode.equalsIgnoreCase(code))
            throw new BusinessException("图像验证码错误");
        redisComponent.delete(redisKey);
    }

    @Override
    public void verifyEmailCaptcha(String email, String type, String code) {

        if (StrUtil.isBlank(email) || StrUtil.isBlank(type) || StrUtil.isBlank(code))
            throw new BusinessException("邮箱验证码不能为空");

        EmailCaptchaType typeCode = EmailCaptchaType.fromCode(type);
        if (typeCode == null)
            throw new BusinessException("邮箱验证码类型错误");

        String redisKey = EMAIL_CAPTCHA_KEY_PREFIX + type + ":" + email;
        String redisCode = redisComponent.getString(redisKey);
        if (redisCode == null)
            throw new BusinessException("邮箱验证码已过期或不存在");

        if (!redisCode.equals(code))
            throw new BusinessException("邮箱验证码错误");

        redisComponent.delete(redisKey);
    }

    @Override
    public LoginResponse login(String email, String password) {
        // 1. 查询用户信息
        UserInfo userInfo = userMapper.selectByEmail(email);

        // 2. 判断用户是否存在
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 判断密码是否正确
        if (!bCryptPasswordEncoder.matches(password, userInfo.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 4. 账号状态检查
        if (userInfo.getStatus() == 0) {
            throw new BusinessException("账号被禁用");
        }

        // 5. 组装返回LoginResponse（包含双token生成和Redis存储）
        return assembleLoginResponse(userInfo);
    }

    /**
     * 组装登录响应
     * 生成双token并保存到Redis
     *
     * @param userInfo 用户信息PO
     * @return LoginResponse 登录响应DTO
     */
    private LoginResponse assembleLoginResponse(UserInfo userInfo) {
        // 1. 生成双token
        String accessToken = jwtUtil.generateAccessToken(userInfo.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUserId());

        // 2. 获取过期时间（毫秒）
        long now = System.currentTimeMillis();
        long accessExpireTime = now + jwtUtil.getAccessTokenExpireDuration();
        long refreshExpireTime = now + jwtUtil.getRefreshTokenExpireDuration();

        // 3. 计算Redis存储的过期时长
        // AccessToken: 转换为分钟（通常30分钟）
        long accessExpireMinutes = jwtUtil.getAccessTokenExpireDuration() / (1000 * 60);
        // RefreshToken: 转换为天（通常7天）
        long refreshExpireDays = jwtUtil.getRefreshTokenExpireDuration() / (1000 * 60 * 60 * 24);

        // 4. 保存token到Redis（核心步骤）
        tokenRedisService.saveAccessToken(accessToken, userInfo.getUserId(), accessExpireMinutes);
        tokenRedisService.saveRefreshToken(refreshToken, userInfo.getUserId(), refreshExpireDays);

        // 5. 构建并返回登录响应
        return new LoginResponse()
            .setUserId(userInfo.getUserId())
            .setNickName(userInfo.getNickName())
            .setAvatar(userInfo.getQqAvatar())
            .setAccessToken(accessToken)
            .setRefreshToken(refreshToken)
            .setAccessTokenExpireTime(accessExpireTime)
            .setRefreshTokenExpireTime(refreshExpireTime)
            .setEmail(userInfo.getEmail());
    }

    /**
     * 用户登出
     * 删除Redis中的token
     *
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     */
    @Override
    public void logout(String accessToken, String refreshToken) {
        // 删除双token
        tokenRedisService.deleteAccessToken(accessToken);
        tokenRedisService.deleteRefreshToken(refreshToken);
    }

    /**
     * 刷新AccessToken
     * 使用RefreshToken获取新的AccessToken
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录响应（包含新的AccessToken）
     */
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // 1. 验证RefreshToken并获取userId
        Long userId = tokenRedisService.validateRefreshToken(refreshToken);
        if (userId == null) {
            throw new BusinessException("RefreshToken无效或已过期，请重新登录");
        }

        // 2. 查询用户信息（确保用户状态正常）
        UserInfo userInfo = userMapper.selectById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        if (userInfo.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 3. 生成新的AccessToken
        String newAccessToken = jwtUtil.generateAccessToken(userId);
        long accessExpireMinutes = jwtUtil.getAccessTokenExpireDuration() / (1000 * 60);

        // 4. 保存新的AccessToken到Redis
        tokenRedisService.saveAccessToken(newAccessToken, userId, accessExpireMinutes);

        // 5. 返回新的token信息
        long now = System.currentTimeMillis();
        return new LoginResponse()
            .setUserId(userInfo.getUserId())
            .setNickName(userInfo.getNickName())
            .setAvatar(userInfo.getQqAvatar())
            .setAccessToken(newAccessToken)
            .setRefreshToken(refreshToken)  // RefreshToken保持不变
            .setAccessTokenExpireTime(now + jwtUtil.getAccessTokenExpireDuration())
            .setRefreshTokenExpireTime(now + jwtUtil.getRefreshTokenExpireDuration())
            .setEmail(userInfo.getEmail());
    }

    /**
     * 用户注册
     * 1. 检验两次密码是否一致
     * 2. 检验邮箱验证码
     * 3. 创建用户
     * 4. 登录
     *
     * @param request 注册请求
     * @return 登录响应
     */
    @Transactional
    @Override
    public LoginResponse register(RegisterRequest request) {
        // 1.检验两次密码是否准确
        if (!request.getRegisterPassword().equals(request.getReRegisterPassword()))
            throw new BusinessException("两次密码不一致");
        // 2.检验邮箱验证码
        verifyEmailCaptcha(request.getEmail(), "register", request.getEmailCode());
        // 3.检测邮箱是否存在
        if (userMapper.selectByEmail(request.getEmail()) != null)
            throw new BusinessException("邮箱已存在");

        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(bCryptPasswordEncoder.encode(request.getRegisterPassword()));
        userInfo.setEmail(request.getEmail());
        userInfo.setNickName(request.getNickName());
        userInfo.setStatus(1);

        userMapper.register(userInfo);

        return login(request.getEmail(), request.getRegisterPassword());

    }
}
