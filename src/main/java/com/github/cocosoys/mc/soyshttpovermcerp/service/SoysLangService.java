package com.github.cocosoys.mc.soyshttpovermcerp.service;

import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;

import java.util.Map;

/**
 * 语言包管理业务接口：列出 language/ 下语言文件、读取平铺键值、保存。
 */
public interface SoysLangService {

    /** 列出 language/ 文件夹下所有 .yml 语言文件（文件名即语言代码）。 */
    AjaxResult list();

    /** 读取指定语言文件 → 平铺键值 Map（{@code data} 字段）。 */
    AjaxResult entries(String file);

    /** 保存整个语言文件（平铺键值 → yml 写回）。 */
    AjaxResult save(String file, Map<String, String> entries);
}
