package com.github.cocosoys.mc.soyshttpovermcerp.service;

import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;

/**
 * SSO 一次性登录票据业务接口（审计日志型，只读 + 批量清理 + 单条删除）。
 *
 * <p>票据由主插件 AuthLoginBridge 自动签发/消费，本服务不提供新增/编辑入口。</p>
 */
public interface SoysSsoTicketService {

    /**
     * 分页查询票据。
     *
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 页大小
     * @param keyword  模糊匹配 ticket / subject / issuedServer / clientIp
     * @param subject  按玩家名精确筛选（用户列表"查看该玩家票据"跳转入口）
     * @param status   状态筛选：unconsumed=未消费 / consumed=已消费 / expired=已过期 / null=全部
     * @param from     起始时间戳（毫秒，按 createTime 过滤），可空
     * @param to       结束时间戳（毫秒，按 createTime 过滤），可空
     */
    TableDataInfo list(Integer pageNum, Integer pageSize, String keyword,
                       String subject, String status, Long from, Long to);

    /**
     * 批量清理。
     *
     * @param mode expired=仅清理已过期 / consumed=仅清理已消费 / all=清理已过期+已消费
     */
    AjaxResult clean(String mode);

    /** 单条删除。 */
    AjaxResult remove(Long id);
}
