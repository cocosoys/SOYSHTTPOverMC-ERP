package com.github.cocosoys.mc.soyshttpovermcerp.service;

import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;

import java.util.Map;

/**
 * SOYS 配置文件可视化编辑业务接口（插件配置 / 网关配置）。
 * 读写均落在主插件数据目录（plugins/SOYSHTTPOverMC/），保存保留注释。
 */
public interface SoysConfigService {

    /** 可编辑配置文件清单（分组 + 文件元信息）。 */
    AjaxResult list();

    /** 读取单个配置文件 → 树形 Map（{@code data} 字段返回）。 */
    AjaxResult load(String fileId);

    /** 保存单个配置文件（全量树 → 保留注释写回）。 */
    AjaxResult save(String fileId, Map<String, Object> data);

    /** 读取指定配置文件的 yml 注释 → { 配置路径: 注释文本 }。 */
    AjaxResult help(String fileId);
}
