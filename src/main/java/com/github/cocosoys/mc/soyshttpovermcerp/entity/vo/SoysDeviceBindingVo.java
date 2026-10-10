package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import com.github.cocosoys.mc.soyshttpovermc.web.gateway.policy.auth.bridge.SoysDeviceBinding;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备绑定行 VO：继承主插件实体全字段，附加状态文案。
 *
 * <p>仅支持列表查看、吊销/恢复（toggle revoked）与删除，不提供手动新增/编辑表单。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SoysDeviceBindingVo extends SoysDeviceBinding {

    /** 状态文案：有效 / 已吊销 */
    private String statusLabel;

    /** 指纹哈希截断展示（前端表格不展示 64 位完整哈希） */
    private String fingerprintShort;
}
