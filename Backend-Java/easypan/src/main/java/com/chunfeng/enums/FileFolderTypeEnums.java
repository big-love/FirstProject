package com.chunfeng.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName FileFolderTypeEnums
 * @Author chunfeng
 * @Description
 * @date 2026/3/21 11:40
 * @Version 1.0
 */
@AllArgsConstructor
@Getter
public enum FileFolderTypeEnums {
    FILE(0, "文件"),
    FOLDER(1, "目录");

    private Integer type;
    private String description;
}
