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
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermGroupReq;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysPermGroupService;

/**
 * 权限组端点（若依契约）：路由 /api/plugins/SOYSHTTPOverMC-ERP/erp/group/*。
 * 仅声明接口并转发 {@link SoysPermGroupService}，不含任何业务实现。
 */
@RequestMapping("/erp/group")
public class SoysPermGroupController {

    private final SoysPermGroupService groupService;

    public SoysPermGroupController(SoysPermGroupService groupService) {
        this.groupService = groupService;
    }

    @ApiName("权限组列表")
    @ApiPermission("soyshttpovermc:erp:group:list")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(name = "pageNum", required = false) Integer pageNum,
                              @RequestParam(name = "pageSize", required = false) Integer pageSize,
                              @RequestParam(name = "keyword", required = false) String keyword) {
        return groupService.list(pageNum, pageSize, keyword);
    }

    @ApiName("权限组新增")
    @ApiPermission("soyshttpovermc:erp:group:add")
    @PostMapping("/create")
    public AjaxResult create(@RequestBody SoysPermGroupReq req) {
        return groupService.create(req);
    }

    @ApiName("权限组编辑")
    @ApiPermission("soyshttpovermc:erp:group:edit")
    @PostMapping("/update")    public AjaxResult update(@RequestBody SoysPermGroupReq req) {
        return groupService.update(req);
    }

    @ApiName("权限组删除")
    @ApiPermission("soyshttpovermc:erp:group:remove")
    @PostMapping("/remove")
    public AjaxResult remove(@RequestBody SoysPermGroupReq req) {
        return groupService.remove(req);
    }

    @ApiName("权限组权限列表")
    @ApiPermission("soyshttpovermc:erp:group:perm:list")
    @GetMapping("/perms")
    public AjaxResult perms(@RequestParam(name = "id", required = true) String id) {
        return groupService.perms(id);
    }

    @ApiName("权限组权限添加")
    @ApiPermission("soyshttpovermc:erp:group:perm:add")
    @PostMapping("/perm/add")
    public AjaxResult addPerm(@RequestBody SoysPermGroupReq req) {
        return groupService.addPerm(req);
    }

    @ApiName("权限组权限移除")
    @ApiPermission("soyshttpovermc:erp:group:perm:remove")
    @PostMapping("/perm/remove")
    public AjaxResult removePerm(@RequestBody SoysPermGroupReq req) {
        return groupService.removePerm(req);
    }

    @ApiName("权限组批量复制")
    @ApiPermission("soyshttpovermc:erp:group:perm:copy")
    @PostMapping("/perm/copy")
    public AjaxResult copyPerms(@RequestBody SoysPermGroupReq req) {
        return groupService.copyPerms(req);
    }

    @ApiName("权限组按源移除")
    @ApiPermission("soyshttpovermc:erp:group:perm:clear")
    @PostMapping("/perm/remove-by")
    public AjaxResult removeByPerms(@RequestBody SoysPermGroupReq req) {
        return groupService.removeByPerms(req);
    }

    @ApiName("权限组成员列表")
    @ApiPermission("soyshttpovermc:erp:group:member:list")
    @GetMapping("/members")
    public AjaxResult members(@RequestParam(name = "id", required = true) String id) {
        return groupService.members(id);
    }

    @ApiName("权限组添加成员")
    @ApiPermission("soyshttpovermc:erp:group:member:add")
    @PostMapping("/member/add")
    public AjaxResult addMember(@RequestBody SoysPermGroupReq req) {
        return groupService.addMember(req);
    }

    @ApiName("权限组移除成员")
    @ApiPermission("soyshttpovermc:erp:group:member:remove")
    @PostMapping("/member/remove")
    public AjaxResult removeMember(@RequestBody SoysPermGroupReq req) {
        return groupService.removeMember(req);
    }
}
