package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 权限组端点请求体（字段按端点取用，多余字段忽略）。
 */
@Data
public class SoysPermGroupReq {

    /** 组 id（归一化：LocalPermissionStore 规则 + 小写）。 */
    private String id;

    /** 显示名。 */
    private String display;

    /** 描述。 */
    private String description;

    /** 权重（越大越优先）。 */
    private Integer weight;

    /** 前缀（仅用于组展示/索引）。 */
    private String prefix;

    /** 权限节点（'-' 前缀 = 否定）。 */
    private String permission;

    /** 批量操作目标组 id。 */
    private String targetGroup;

    /** 批量操作源组 id 列表（复制加入 / 按此移除）。 */
    private List<String> sourceGroups;

    /** 成员操作：玩家名 / UUID。 */
    private String player;

    /** 成员操作：用户 UUID（移除成员用）。 */
    private String uuid;
}
