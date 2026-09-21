package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysApiKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * X-API-KEY 列表行：直接继承 {@link SoysApiKey}（id/fingerprint/uuid/player/enabled/expiry/lastUsedAt/
 * usedCount/remark/createTime/updateTime 全字段自动带出）。
 *
 * <p>唯一处理：{@code apiKey}（SHA-256 哈希）经 {@link JsonIgnore} 排除，
 * 列表/详情绝不暴露密钥哈希字段。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SoysApiKeyVo extends SoysApiKey {

    /** 密钥哈希字段列表不外露（覆写 getter + JsonIgnore）。 */
    @Override
    @JsonIgnore
    public String getApiKey() {
        return super.getApiKey();
    }
}
