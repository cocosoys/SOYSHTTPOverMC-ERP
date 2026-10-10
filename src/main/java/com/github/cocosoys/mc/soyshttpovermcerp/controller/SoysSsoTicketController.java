package com.github.cocosoys.mc.soyshttpovermcerp.controller;

import com.github.cocosoys.mc.soyshttpovermc.annotations.ApiName;
import com.github.cocosoys.mc.soyshttpovermc.annotations.ApiPermission;
import com.github.cocosoys.mc.soyshttpovermc.annotations.GetMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.PostMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestBody;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestParam;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysSsoTicketService;

import java.util.Map;

/**
 * SSO 票据审计端点：/api/plugins/SOYSHTTPOverMC-ERP/erp/sso-ticket/*。
 *
 * <p>只读列表 + 批量清理 + 单条删除；不提供新增/编辑。</p>
 */
@RequestMapping("/erp/sso-ticket")
public class SoysSsoTicketController {

    private final SoysSsoTicketService ticketService;

    public SoysSsoTicketController(SoysSsoTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @ApiName("SSO 票据列表")
    @ApiPermission("soyshttpovermc:erp:sso-ticket:list")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(name = "pageNum", required = false) Integer pageNum,
                              @RequestParam(name = "pageSize", required = false) Integer pageSize,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "subject", required = false) String subject,
                              @RequestParam(name = "status", required = false) String status,
                              @RequestParam(name = "from", required = false) Long from,
                              @RequestParam(name = "to", required = false) Long to) {
        return ticketService.list(pageNum, pageSize, keyword, subject, status, from, to);
    }

    @ApiName("批量清理票据")
    @ApiPermission("soyshttpovermc:erp:sso-ticket:clean")
    @PostMapping("/clean")
    public AjaxResult clean(@RequestBody Map<String, Object> body) {
        Object mode = body == null ? null : body.get("mode");
        return ticketService.clean(mode == null ? null : String.valueOf(mode));
    }

    @ApiName("删除票据")
    @ApiPermission("soyshttpovermc:erp:sso-ticket:remove")
    @PostMapping("/remove")
    public AjaxResult remove(@RequestBody Map<String, Object> body) {
        Object id = body == null ? null : body.get("id");
        if (id == null) {
            return AjaxResult.error("id 不能为空");
        }
        try {
            return ticketService.remove(Long.parseLong(String.valueOf(id)));
        } catch (NumberFormatException e) {
            return AjaxResult.error("id 格式错误");
        }
    }
}
