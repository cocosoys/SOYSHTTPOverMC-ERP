package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * 配置文件保存结果：文件名 + 热重载提示。
 * 用于 config save 接口响应。
 */
@Data
public class SoysConfigSaveVo {
    /** 已保存的文件名。 */
    private String file;
    /** 热重载提示。 */
    private String tip;

    /** 静态工厂。 */
    public static SoysConfigSaveVo of(String file, String tip) {
        SoysConfigSaveVo vo = new SoysConfigSaveVo();
        vo.setFile(file);
        vo.setTip(tip);
        return vo;
    }
}
