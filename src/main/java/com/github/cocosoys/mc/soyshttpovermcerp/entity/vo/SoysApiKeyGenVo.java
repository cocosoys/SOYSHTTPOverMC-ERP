package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import lombok.Data;

/**
 * X-API-KEY 生成结果：fingerprint + 明文密钥（仅此一次返回）+ 备注。
 * 用于 generate / assignKey 接口响应。
 */
@Data
public class SoysApiKeyGenVo {
    /** KEY 指纹（唯一标识）。 */
    private String fingerprint;
    /** 明文密钥（仅生成/分配时返回一次，后续不可查）。 */
    private String plain;
    /** 备注。 */
    private String remark;
}
