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
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermUserReq;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysPermUserService;

/**
 * 用户列表端点（若依契约）：路由 /api/plugins/SOYSHTTPOverMC-ERP/erp/user/*。
 * 仅声明接口并转发 {@link SoysPermUserService}，不含任何业务实现。
 */
@RequestMapping("/erp/user")
public class SoysPermUserController {

    private final SoysPermUserService userService;

    public SoysPermUserController(SoysPermUserService userService) {
        this.userService = userService;
    }

    @ApiName("用户列表")
    @ApiPermission("soyshttpovermc:erp:user:list")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(name = "pageNum", required = false) Integer pageNum,
                              @RequestParam(name = "pageSize", required = false) Integer pageSize,
                              @RequestParam(name = "keyword", required = false) String keyword) {
        return userService.list(pageNum, pageSize, keyword);
    }

    @ApiName("用户权限组列表")
    @ApiPermission("soyshttpovermc:erp:user:group:list")
    @GetMapping("/groups")
    public AjaxResult groups(@RequestParam(name = "uuid", required = true) String uuid) {
        return userService.groups(uuid);
    }

    @ApiName("用户权限组保存")
    @ApiPermission("soyshttpovermc:erp:user:group:edit")
    @PostMapping("/groups")
    public AjaxResult saveGroups(@RequestBody SoysPermUserReq req) {
        return userService.saveGroups(req);
    }

    @ApiName("用户权限列表")
    @ApiPermission("soyshttpovermc:erp:user:perm:list")
    @GetMapping("/perms")
    public AjaxResult perms(@RequestParam(name = "uuid", required = true) String uuid) {
        return userService.perms(uuid);
    }

    @ApiName("用户权限添加")
    @ApiPermission("soyshttpovermc:erp:user:perm:add")
    @PostMapping("/perm/add")
    public AjaxResult addPerm(@RequestBody SoysPermUserReq req) {
        return userService.addPerm(req);
    }

    @ApiName("用户权限移除")
    @ApiPermission("soyshttpovermc:erp:user:perm:remove")
    @PostMapping("/perm/remove")
    public AjaxResult removePerm(@RequestBody SoysPermUserReq req) {
        return userService.removePerm(req);
    }

    @ApiName("用户权限时间延期")
    @ApiPermission("soyshttpovermc:erp:user:perm:renew")
    @PostMapping("/expiry")
    public AjaxResult expiry(@RequestBody SoysPermUserReq req) {
        return userService.expiry(req);
    }

    @ApiName("分配 X-API-KEY")
    @ApiPermission("soyshttpovermc:erp:user:apikey:assign")
    @PostMapping("/assign-key")
    public AjaxResult assignKey(@RequestBody SoysPermUserReq req) {
        return userService.assignKey(req);
    }
}
