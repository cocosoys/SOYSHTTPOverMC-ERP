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
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysDeviceBindingService;

import java.util.Map;

/**
 * 设备绑定管理端点：/api/plugins/SOYSHTTPOverMC-ERP/erp/device-binding/*。
 *
 * <p>列表 + 吊销/恢复（toggle revoked）+ 删除；不提供手动新增/编辑表单。</p>
 */
@RequestMapping("/erp/device-binding")
public class SoysDeviceBindingController {

    private final SoysDeviceBindingService bindingService;

    public SoysDeviceBindingController(SoysDeviceBindingService bindingService) {
        this.bindingService = bindingService;
    }

    @ApiName("设备绑定列表")
    @ApiPermission("soyshttpovermc:erp:device-binding:list")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(name = "pageNum", required = false) Integer pageNum,
                              @RequestParam(name = "pageSize", required = false) Integer pageSize,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "player", required = false) String player,
                              @RequestParam(name = "revoked", required = false) Integer revoked) {
        return bindingService.list(pageNum, pageSize, keyword, player, revoked);
    }

    @ApiName("切换设备吊销状态")
    @ApiPermission("soyshttpovermc:erp:device-binding:toggle")
    @PostMapping("/toggle")
    public AjaxResult toggle(@RequestBody Map<String, Object> body) {
        Object id = body == null ? null : body.get("id");
        if (id == null) {
            return AjaxResult.error("id 不能为空");
        }
        try {
            return bindingService.toggle(Long.parseLong(String.valueOf(id)));
        } catch (NumberFormatException e) {
            return AjaxResult.error("id 格式错误");
        }
    }

    @ApiName("删除设备绑定")
    @ApiPermission("soyshttpovermc:erp:device-binding:remove")
    @PostMapping("/remove")
    public AjaxResult remove(@RequestBody Map<String, Object> body) {
        Object id = body == null ? null : body.get("id");
        if (id == null) {
            return AjaxResult.error("id 不能为空");
        }
        try {
            return bindingService.remove(Long.parseLong(String.valueOf(id)));
        } catch (NumberFormatException e) {
            return AjaxResult.error("id 格式错误");
        }
    }
}
