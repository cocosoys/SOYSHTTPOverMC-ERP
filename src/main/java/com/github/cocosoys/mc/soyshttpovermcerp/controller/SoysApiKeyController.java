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
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysApiKeyReq;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysApiKeyService;

/**
 * X-API-KEY 端点（若依契约）：路由 /api/plugins/SOYSHTTPOverMC-ERP/erp/apikey/*。
 * 仅声明接口并转发 {@link SoysApiKeyService}，不含任何业务实现。
 */
@RequestMapping("/erp/apikey")
public class SoysApiKeyController {

    private final SoysApiKeyService apiKeyService;

    public SoysApiKeyController(SoysApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @ApiName("APIKEY 列表")
    @ApiPermission("soyshttpovermc:erp:apikey:list")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(name = "pageNum", required = false) Integer pageNum,
                              @RequestParam(name = "pageSize", required = false) Integer pageSize,
                              @RequestParam(name = "keyword", required = false) String keyword) {
        return apiKeyService.list(pageNum, pageSize, keyword);
    }

    @ApiName("APIKEY 生成")
    @ApiPermission("soyshttpovermc:erp:apikey:add")
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.generate(req);
    }

    @ApiName("APIKEY 启停")
    @ApiPermission("soyshttpovermc:erp:apikey:edit")
    @PostMapping("/toggle")    public AjaxResult toggle(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.toggle(req);
    }

    @ApiName("APIKEY 过期时间")
    @ApiPermission("soyshttpovermc:erp:apikey:expiry")
    @PostMapping("/expiry")
    public AjaxResult expiry(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.expiry(req);
    }

    @ApiName("APIKEY 绑定玩家")
    @ApiPermission("soyshttpovermc:erp:apikey:bind")
    @PostMapping("/bind")
    public AjaxResult bind(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.bind(req);
    }

    @ApiName("APIKEY 解绑")
    @ApiPermission("soyshttpovermc:erp:apikey:unbind")
    @PostMapping("/unbind")
    public AjaxResult unbind(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.unbind(req);
    }

    @ApiName("APIKEY 删除")
    @ApiPermission("soyshttpovermc:erp:apikey:remove")
    @PostMapping("/remove")
    public AjaxResult remove(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.remove(req);
    }

    @ApiName("APIKEY 权限列表")
    @ApiPermission("soyshttpovermc:erp:apikey:perm:list")
    @GetMapping("/perms")
    public AjaxResult perms(@RequestParam(name = "keyId", required = true) String keyId) {
        return apiKeyService.perms(keyId);
    }

    @ApiName("APIKEY 权限添加")
    @ApiPermission("soyshttpovermc:erp:apikey:perm:add")
    @PostMapping("/perm/add")
    public AjaxResult addPerm(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.addPerm(req);
    }

    @ApiName("APIKEY 权限移除")
    @ApiPermission("soyshttpovermc:erp:apikey:perm:remove")
    @PostMapping("/perm/remove")
    public AjaxResult removePerm(@RequestBody SoysApiKeyReq req) {
        return apiKeyService.removePerm(req);
    }
}
