package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户列表行：直接继承 {@link SoysPermUser}（uuid/player/expiry/createTime/updateTime 全字段自动带出），
 * 仅补充 ERP 派生的展示信息（所属组 / 绑定 KEY）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SoysPermUserVo extends SoysPermUser {

    /** 所属权限组 id 列表。 */
    private List<String> groups;

    /** 所属权限组显示名列表（无显示名时回退组 id）。 */
    private List<String> groupsDisplay;

    /** 是否已绑定 X-API-KEY。 */
    private Boolean hasKey;

    /** 绑定的 X-API-KEY 指纹（未绑定时为 null）。 */
    private String keyFingerprint;
}
