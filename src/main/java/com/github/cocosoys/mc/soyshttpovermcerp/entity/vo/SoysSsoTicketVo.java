package com.github.cocosoys.mc.soyshttpovermcerp.entity.vo;

import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysSsoTicket;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SSO 票据行 VO：继承主插件实体全字段，附加展示用派生字段。
 *
 * <p>列表不提供手动新增/编辑入口，仅用于审计查看与批量清理。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SoysSsoTicketVo extends SoysSsoTicket {

    /** 状态文案：未消费 / 已消费 / 已过期 */
    private String statusLabel;

    /** 消费时间格式化（yyyy-MM-dd HH:mm:ss），未消费为 null */
    private String consumedAtText;

    /** 过期时间格式化（yyyy-MM-dd HH:mm:ss） */
    private String expiresAtText;
}
