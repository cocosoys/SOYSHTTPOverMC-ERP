package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * 语言文件行：文件名 + 语言代码。
 * 用于 lang list 接口响应。
 */
@Data
public class SoysLangFileVo {
    /** 完整文件名（含 .yml 后缀）。 */
    private String file;
    /** 语言代码（文件名去掉 .yml 后缀）。 */
    private String code;
}
