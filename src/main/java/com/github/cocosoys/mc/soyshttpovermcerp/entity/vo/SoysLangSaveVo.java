package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * 语言文件保存结果：文件名 + 条目数 + 热重载提示。
 * 用于 lang save 接口响应。
 */
@Data
public class SoysLangSaveVo {
    /** 已保存的文件名。 */
    private String file;
    /** 保存的条目数。 */
    private int count;
    /** 热重载提示。 */
    private String tip;

    /** 静态工厂。 */
    public static SoysLangSaveVo of(String file, int count, String tip) {
        SoysLangSaveVo vo = new SoysLangSaveVo();
        vo.setFile(file);
        vo.setCount(count);
        vo.setTip(tip);
        return vo;
    }
}
