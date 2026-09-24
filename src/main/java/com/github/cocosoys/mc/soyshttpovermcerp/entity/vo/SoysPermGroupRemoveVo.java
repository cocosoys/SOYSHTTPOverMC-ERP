package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * 权限组批量移除权限结果：移除条数。
 * 用于 removeByPerms 接口响应。
 */
@Data
public class SoysPermGroupRemoveVo {
    /** 本次移除的权限条数。 */
    private int removed;

    /** 静态工厂。 */
    public static SoysPermGroupRemoveVo of(int removed) {
        SoysPermGroupRemoveVo vo = new SoysPermGroupRemoveVo();
        vo.setRemoved(removed);
        return vo;
    }
}
