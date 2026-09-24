package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 权限列表统一包装：rows 字段承载 {@link SoysPermPermissionVo} 列表。
 * 用于 APIKEY / 用户 / 权限组三个 perms 接口响应。
 */
@Data
public class SoysPermRowsVo {
    /** 权限行列表。 */
    private List<SoysPermPermissionVo> rows;

    /** 静态工厂：从权限实体列表构建。 */
    public static SoysPermRowsVo of(List<SoysPermPermissionVo> rows) {
        SoysPermRowsVo vo = new SoysPermRowsVo();
        vo.setRows(rows);
        return vo;
    }
}
