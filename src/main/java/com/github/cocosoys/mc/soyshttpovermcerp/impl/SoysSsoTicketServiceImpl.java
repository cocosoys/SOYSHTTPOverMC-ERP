package com.github.cocosoys.mc.soyshttpovermcerp.impl;

import com.github.cocosoys.mc.soyshttpovermc.orm.DATA;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysSsoTicket;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.PageUtils;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysSsoTicketVo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysSsoTicketService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * SSO 票据业务实现：复用主插件 {@link DATA} 门面读写 soys_sso_ticket 表。
 *
 * <p>只读 + 清理 + 单删，不提供新增/编辑。</p>
 */
public class SoysSsoTicketServiceImpl implements SoysSsoTicketService {

    private static final String TS_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public TableDataInfo list(Integer pageNum, Integer pageSize, String keyword,
                             String subject, String status, Long from, Long to) {
        List<SoysSsoTicket> all = DATA.select(SoysSsoTicket.class);
        long now = System.currentTimeMillis();
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        List<SoysSsoTicketVo> rows = new ArrayList<>();

        for (SoysSsoTicket t : all) {
            if (subject != null && !subject.trim().isEmpty()
                    && !subject.equalsIgnoreCase(nvl(t.getSubject()))) {
                continue;
            }
            if (kw != null && !kw.isEmpty()) {
                String blob = (nvl(t.getTicket()) + " " + nvl(t.getSubject()) + " "
                        + nvl(t.getIssuedServer()) + " " + nvl(t.getClientIp())).toLowerCase();
                if (!blob.contains(kw)) {
                    continue;
                }
            }
            if (from != null && t.getCreateTime() != null) {
                long ct = t.getCreateTime().getTime();
                if (ct < from) {
                    continue;
                }
            }
            if (to != null && t.getCreateTime() != null) {
                long ct = t.getCreateTime().getTime();
                if (ct > to) {
                    continue;
                }
            }
            String st = statusOf(t, now);
            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase(st)) {
                continue;
            }
            rows.add(toRow(t, now));
        }
        return PageUtils.page(rows, pageNum, pageSize);
    }

    @Override
    public AjaxResult clean(String mode) {
        if (mode == null || mode.isEmpty()) {
            return AjaxResult.error("清理模式不能为空（expired / consumed / all）");
        }
        List<SoysSsoTicket> all;
        try {
            all = DATA.select(SoysSsoTicket.class);
        } catch (Throwable t) {
            return AjaxResult.error("读取票据表失败: " + t.getMessage());
        }
        long now = System.currentTimeMillis();
        int removed = 0;
        for (SoysSsoTicket t : all) {
            boolean expired = t.getExpiresAt() != null && t.getExpiresAt() < now;
            boolean consumed = t.getConsumedAt() != null;
            boolean hit;
            switch (mode.toLowerCase()) {
                case "expired":
                    hit = expired;
                    break;
                case "consumed":
                    hit = consumed;
                    break;
                case "all":
                    hit = expired || consumed;
                    break;
                default:
                    return AjaxResult.error("未知清理模式: " + mode);
            }
            if (hit && t.getId() != null && DATA.deleteById(SoysSsoTicket.class, t.getId())) {
                removed++;
            }
        }
        return AjaxResult.success("已清理 " + removed + " 条票据", removed);
    }

    @Override
    public AjaxResult remove(Long id) {
        if (id == null) {
            return AjaxResult.error("id 不能为空");
        }
        if (DATA.deleteById(SoysSsoTicket.class, id)) {
            return AjaxResult.success("已删除票据 #" + id);
        }
        return AjaxResult.error("删除失败（记录可能已不存在）");
    }

    // ---------- 私有辅助 ----------

    private SoysSsoTicketVo toRow(SoysSsoTicket t, long now) {
        SoysSsoTicketVo row = new SoysSsoTicketVo();
        row.setId(t.getId());
        row.setTicket(t.getTicket());
        row.setSubject(t.getSubject());
        row.setRedirectUrl(t.getRedirectUrl());
        row.setIssuedServer(t.getIssuedServer());
        row.setClientIp(t.getClientIp());
        row.setConsumedAt(t.getConsumedAt());
        row.setExpiresAt(t.getExpiresAt());
        row.setCreateTime(t.getCreateTime());
        row.setUpdateTime(t.getUpdateTime());
        row.setStatusLabel(statusLabel(statusOf(t, now)));
        row.setConsumedAtText(t.getConsumedAt() == null ? null : fmt(t.getConsumedAt()));
        row.setExpiresAtText(t.getExpiresAt() == null ? null : fmt(t.getExpiresAt()));
        return row;
    }

    /** 状态判定：consumed=已消费 / expired=已过期 / active=未消费且未过期 */
    private static String statusOf(SoysSsoTicket t, long now) {
        if (t.getConsumedAt() != null) {
            return "consumed";
        }
        if (t.getExpiresAt() != null && t.getExpiresAt() < now) {
            return "expired";
        }
        return "active";
    }

    private static String statusLabel(String st) {
        if ("consumed".equals(st)) return "已消费";
        if ("expired".equals(st)) return "已过期";
        return "未消费";
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    private static String fmt(long ts) {
        try {
            return new SimpleDateFormat(TS_PATTERN).format(new java.util.Date(ts));
        } catch (Throwable t) {
            return String.valueOf(ts);
        }
    }

    private static Long parseTime(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat(TS_PATTERN).parse(s).getTime();
        } catch (Throwable t) {
            return null;
        }
    }
}
