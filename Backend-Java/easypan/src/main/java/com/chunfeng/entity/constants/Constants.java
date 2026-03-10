package com.chunfeng.entity.constants;

public class Constants {

    public static final String appName = "私有云盘";

    public static final String ZERO_STR = "0";
    public static final Integer ZERO = 0;
    public static final Integer LENGTH_5 = 5;
    public static final Integer LENGTH_10 = 10;
    public static final Integer LENGTH_15 = 15;
    public static final Integer LENGTH_20 = 20;
    public static final Integer LENGTH_30 = 30;
    public static final Integer LENGTH_50 = 50;


    public static final Integer LENGTH_150 = 150;

    public static final String SESSION_KEY = "session_key";
    public static final String SESSION_SHARE_KEY = "session_share_key";

    public static final Long MB = 1024 * 1024L;
    public static final String CHECK_CODE_KEY = "check_code_key";
    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";
    public static final String REDIS_KEY_SYS_SETTING = "easypan:sysetting:";

    public static final Integer REDIS_KEY_EXPIRES_ONE_MINUTE = 60;
    public static final Integer REDIS_KEY_EXPIRES_FIVE_MINUTE = REDIS_KEY_EXPIRES_ONE_MINUTE * 5;
    public static final Integer REDIS_KEY_EXPIRES_ONE_HOUR = REDIS_KEY_EXPIRES_ONE_MINUTE * 60;

    // 数据盘
    public static final String REDIS_KEY_USER_SPACE_USE = "easypan:user:space_use:";
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MINUTE * 60 * 24;

    //头像相关
    public static final String FILE_FOLDER_FILE = "/file/";
    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";
    public static final String AVATAR_SUFFIX = ".jpg";
    public static final String AVATAR_DEFAULT = "default_avatar.jpg";

    // 数据存储
    public static final String FILE_FOLDER_TEMP = "/temp/";
    public static final String REDIS_KEY_USER_FILE_TEMP_SIZE = "easypan:user:file:temp:";
    // 视频
    public static final String TS_NAME = "index.ts";
    public static final String M3U8_NAME = "index.m3u8";
    public static final String IMAGE_PNG_SUFFIX = ".png";

    // qq登录相关
    public static final String VIEW_OBJ_RESULT_KEY = "result";

    // 下载
    public static final String REDIS_KEY_DOWNLOAD = "easypan:download:";
    public static final Integer REDIS_DOWNLOAD_TEN_MIN = REDIS_KEY_EXPIRES_ONE_MINUTE * 10;

    //redisKey
    public static final String REDIS_CAPTCHA = "captcha:";
    public static final String REDIS_KEY_USER_SESSION = "user:session:";
    public static final String REDIS_KEY_REFRESH_TOKEN = "refresh:token:";
    public static final String REDIS_KEY_USER_FIRST_LOGIN = "user:first:login:";
    public static final String REDIS_KEY_TOKEN_BLACK_LIST = "token:blacklist:";

    // 邮件 验证码
    public static final int EMAIL_CODE_LENGTH = 6;
    public static final int EMAIL_EXPIRE_MINUTES = 5;
    public static final long EMAIL_SEND_MIN_INTERVAL_SECONDS = 60;
    public static final String EMAIL_CAPTCHA_LIMIT = "captcha:email:limit:";
}
