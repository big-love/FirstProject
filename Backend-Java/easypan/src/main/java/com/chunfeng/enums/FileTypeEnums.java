package com.chunfeng.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @ClassName FileTypeEnums
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:24
 * @Version 1.0
 */

@Slf4j
@AllArgsConstructor
@Getter
public enum FileTypeEnums {
    //1:视频 2:音频 3:图片 4:pdf 5:word 6:excel 7:txt 8:code 9:zip 10:其他文件
    VIDEO(FileCategoryEnums.VIDEO, 1, new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"}, "视频"),
    MUSIC(FileCategoryEnums.MUSIC, 2, new String[]{".mp3", ".wav", ".wma", ".mp2", ".flac", ".midi", ".ra", ".ape", ".aac", ".cda"}, "音频"),
    IMAGE(FileCategoryEnums.IMAGE, 3, new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".dds", ".psd", ".pdt", ".webp", ".xmp", ".svg", ".tiff"}, "图片"),
    PDF(FileCategoryEnums.DOC, 4, new String[]{".pdf"}, "pdf"),
    WORD(FileCategoryEnums.DOC, 5, new String[]{".docx"}, "word"),
    EXCEL(FileCategoryEnums.DOC, 6, new String[]{".xlsx"}, "excel"),
    TXT(FileCategoryEnums.DOC, 7, new String[]{".txt"}, "txt文本"),
    PROGRAM(FileCategoryEnums.OTHERS, 8, new String[]{".h", ".c", ".hpp", ".hxx", ".cpp", ".cc", "c++", ".cxx", ".m", ".o", ".s", ".dll", ".cs",
        ".java", ".class", ".js", ".ts", ".css", ".scss", ".vue", ".jsx", ".sql", ".md", ".json", ".html", ".xml"}, "CODE"),
    ZIP(FileCategoryEnums.OTHERS, 9, new String[]{".rar", ".zip", ".7z", ".cab", ".arj", ".lzh", ".tar", ".gz", ".ace", ".uue", ".bz", ".jar", ".iso", ".mpd"}, "压缩包"),
    OTHERS(FileCategoryEnums.OTHERS, 10, new String[]{}, "其他");

    private FileCategoryEnums category;
    private int type;
    private String[] suffixs;
    private String desc;

    public static FileTypeEnums getFileTypeBySuffix(String suffix) {
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (ArrayUtils.contains(item.suffixs, suffix)) {
                return item;
            }
        }
        log.info("文件类型是：{}", suffix);
        return FileTypeEnums.OTHERS;
    }

    public static FileTypeEnums getByType(int type) {
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        return null;
    }
}

