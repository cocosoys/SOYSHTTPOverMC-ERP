package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * 权限组批量复制权限结果：新增数 + 跳过数（已存在）。
 * 用于 copyPerms 接口响应。
 */
@Data
public class SoysPermGroupCopyVo {
    /** 本次新增的权限条数。 */
    private int added;
    /** 因目标组已存在而跳过的条数。 */
    private int skipped;

    /** 静态工厂。 */
    public static SoysPermGroupCopyVo of(int added, int skipped) {
        SoysPermGroupCopyVo vo = new SoysPermGroupCopyVo();
        vo.setAdded(added);
        vo.setSkipped(skipped);
        return vo;
    }
}
