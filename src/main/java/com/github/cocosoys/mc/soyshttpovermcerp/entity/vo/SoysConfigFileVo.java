package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * 配置文件清单行：id / 名称 / 分组 / 文件名 / 描述。
 * 用于 config list 接口响应。
 */
@Data
public class SoysConfigFileVo {
    /** 配置文件内部 ID。 */
    private String id;
    /** 显示名称。 */
    private String name;
    /** 分组名。 */
    private String group;
    /** 实际文件名（相对主插件数据目录）。 */
    private String file;
    /** 描述。 */
    private String desc;
}
