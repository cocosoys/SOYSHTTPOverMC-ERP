package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 权限组成员列表包装：rows 字段承载 {@link SoysGroupMemberVo} 列表。
 * 用于 members 接口响应。
 */
@Data
public class SoysGroupMembersVo {
    /** 成员行列表。 */
    private List<SoysGroupMemberVo> rows;

    /** 静态工厂。 */
    public static SoysGroupMembersVo of(List<SoysGroupMemberVo> rows) {
        SoysGroupMembersVo vo = new SoysGroupMembersVo();
        vo.setRows(rows);
        return vo;
    }
}
