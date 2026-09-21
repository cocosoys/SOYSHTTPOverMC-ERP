package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * X-API-KEY 端点请求体（字段按端点取用，多余字段忽略）。
 */
@Data
public class SoysApiKeyReq {

    /** 密钥主键 id。 */
    private String keyId;

    /** 备注（生成时写入）。 */
    private String remark;

    /** 启停开关。 */
    private Boolean enabled;

    /** 过期时间：yyyy-MM-dd HH:mm:ss / epoch 毫秒数字 / null / "0" / "clear" = 永久。 */
    private String expiry;

    /** 绑定玩家名 / UUID。 */
    private String player;

    /** 权限节点（'-' 前缀 = 否定）。 */
    private String permission;
}
