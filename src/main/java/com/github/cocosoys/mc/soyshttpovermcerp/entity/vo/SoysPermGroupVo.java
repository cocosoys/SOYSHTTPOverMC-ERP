package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限组列表行：直接继承 {@link SoysPermGroup}（id/display/prefix/weight/description/createTime/updateTime
 * 全字段自动带出），仅补充成员数 / 权限数两个聚合字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SoysPermGroupVo extends SoysPermGroup {

    /** 成员数。 */
    private Integer memberCount;

    /** 权限数。 */
    private Integer permCount;
}
