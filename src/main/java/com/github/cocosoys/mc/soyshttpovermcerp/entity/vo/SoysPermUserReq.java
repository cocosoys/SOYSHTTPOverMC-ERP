package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户管理端点请求体（字段按端点取用，多余字段忽略）。
 */
@Data
public class SoysPermUserReq {

    /** 玩家 UUID（或玩家名，后端自动归一）。 */
    private String uuid;

    /** 权限节点（'-' 前缀 = 否定）。 */
    private String permission;

    /** 权限组 id 列表（全量替换）。 */
    private List<String> groups;

    /** 过期时间：yyyy-MM-dd HH:mm:ss / epoch 毫秒数字 / null / "0" / "clear" = 永久。 */
    private String expiry;
}
