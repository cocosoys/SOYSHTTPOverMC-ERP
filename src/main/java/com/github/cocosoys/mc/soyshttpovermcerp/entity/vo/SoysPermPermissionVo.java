package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermPermission;

/**
 * 权限节点行（用户/组/KEY 权限列表共用）：直接继承 {@link SoysPermPermission}，
 * 前端仅取用 permission / negative，其余字段由实体带出。
 */
public class SoysPermPermissionVo extends SoysPermPermission {

    /** 实体 → VO 拷贝（用户/组/KEY 权限列表统一展示行）。 */
    public static SoysPermPermissionVo from(SoysPermPermission p) {
        SoysPermPermissionVo vo = new SoysPermPermissionVo();
        vo.setId(p.getId());
        vo.setOwnerType(p.getOwnerType());
        vo.setOwnerId(p.getOwnerId());
        vo.setPermission(p.getPermission());
        vo.setNegative(p.isNegative());
        vo.setCreateTime(p.getCreateTime());
        vo.setUpdateTime(p.getUpdateTime());
        return vo;
    }
}
