package com.github.cocosoys.mc.soyshttpovermcerp.impl;

import com.github.cocosoys.mc.soyshttpovermc.orm.DATA;
import com.github.cocosoys.mc.soyshttpovermc.permission.local.LocalPermissionStore;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermGroup;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermPermission;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermUser;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermUserGroup;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.PageUtils;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysGroupMemberVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysGroupMembersVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermGroupCopyVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermGroupRemoveVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermGroupReq;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermGroupVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermPermissionVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermRowsVo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysPermGroupService;
import com.github.cocosoys.mc.soyshttpovermcerp.util.ErpSoysStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限组业务实现（扁平化）：组 CRUD + 权限管理（组间批量复制/移除）+ 成员管理。
 */
public class SoysPermGroupServiceImpl implements SoysPermGroupService {

    @Override
    public TableDataInfo list(Integer pageNum, Integer pageSize, String keyword) {
        List<SoysPermGroup> all = new ArrayList<>(ErpSoysStore.localStore().listGroups());
        all.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        List<SoysPermGroupVo> rows = new ArrayList<>();
        for (SoysPermGroup g : all) {
            if (kw != null && !kw.isEmpty()) {
                String id = g.getId() == null ? "" : g.getId().toLowerCase();
                String display = g.getDisplay() == null ? "" : g.getDisplay().toLowerCase();
                if (!id.contains(kw) && !display.contains(kw)) {
                    continue;
                }
            }
            SoysPermGroupVo row = new SoysPermGroupVo();
            row.setId(g.getId());
            row.setDisplay(g.getDisplay());
            row.setPrefix(g.getPrefix());
            row.setWeight(g.getWeight());
            row.setDescription(g.getDescription());
            row.setCreateTime(g.getCreateTime());
            row.setUpdateTime(g.getUpdateTime());
            row.setMemberCount(ErpSoysStore.localStore().listGroupMembers(g.getId()).size());
            row.setPermCount(ErpSoysStore.localStore().listGroupPermissions(g.getId()).size());
            rows.add(row);
        }
        return PageUtils.page(rows, pageNum, pageSize);
    }

    @Override
    public AjaxResult create(SoysPermGroupReq req) {
        try {
            if (req == null || req.getId() == null || req.getId().trim().isEmpty()) {
                return AjaxResult.error("组 id 不能为空");
            }
            String id = gid(req.getId());
            if (id.isEmpty()) {
                return AjaxResult.error("组 id 非法");
            }
            int weight = req.getWeight() == null ? 0 : req.getWeight();
            boolean ok = ErpSoysStore.localStore().createGroup(id, weight, req.getDisplay(), req.getDescription());
            if (ok) {
                applyPrefix(id, req.getPrefix());
                return AjaxResult.success();
            }
            return AjaxResult.error("权限组创建失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限组创建失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult update(SoysPermGroupReq req) {
        try {
            if (req == null || req.getId() == null || req.getId().trim().isEmpty()) {
                return AjaxResult.error("组 id 不能为空");
            }
            String id = gid(req.getId());
            if (ErpSoysStore.localStore().getGroup(id) == null) {
                return AjaxResult.error("权限组不存在：" + id);
            }
            int weight = req.getWeight() == null ? 0 : req.getWeight();
            // createGroup 为 upsert：更新元数据（prefix 单独应用）
            boolean ok = ErpSoysStore.localStore().createGroup(id, weight, req.getDisplay(), req.getDescription());
            if (ok) {
                applyPrefix(id, req.getPrefix());
                return AjaxResult.success();
            }
            return AjaxResult.error("权限组更新失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限组更新失败：" + t.getMessage());
        }
    }

    /** prefix 不在 Store upsert 参数内，经 ORM 单独补写。 */
    private void applyPrefix(String id, String prefix) {
        if (prefix == null) {
            return;
        }
        SoysPermGroup g = ErpSoysStore.localStore().getGroup(id);
        if (g == null) {
            return;
        }
        String p = prefix.trim();
        String cur = g.getPrefix() == null ? "" : g.getPrefix();
        if (!p.equals(cur)) {
            g.setPrefix(p);
            g.setUpdateTime(new java.util.Date());
            DATA.updateById(g);
        }
    }

    @Override
    public AjaxResult remove(SoysPermGroupReq req) {
        try {
            if (req == null || req.getId() == null || req.getId().trim().isEmpty()) {
                return AjaxResult.error("组 id 不能为空");
            }
            String id = gid(req.getId());
            boolean ok = ErpSoysStore.localStore().deleteGroup(id);
            return ok ? AjaxResult.success() : AjaxResult.error("权限组删除失败（可能不存在）");
        } catch (Throwable t) {
            return AjaxResult.error("权限组删除失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult perms(String id) {
        try {
            SoysPermRowsVo data = SoysPermRowsVo.of(permRows(ErpSoysStore.localStore().listGroupPermissions(gid(id))));
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("权限组权限查询失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult addPerm(SoysPermGroupReq req) {
        try {
            if (req == null || req.getId() == null || req.getPermission() == null || req.getPermission().trim().isEmpty()) {
                return AjaxResult.error("组 id 与 permission 不能为空");
            }
            boolean ok = ErpSoysStore.localStore().addGroupPermission(gid(req.getId()), req.getPermission());
            return ok ? AjaxResult.success() : AjaxResult.error("权限添加失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限添加失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult removePerm(SoysPermGroupReq req) {
        try {
            if (req == null || req.getId() == null || req.getPermission() == null || req.getPermission().trim().isEmpty()) {
                return AjaxResult.error("组 id 与 permission 不能为空");
            }
            boolean ok = ErpSoysStore.localStore().removeGroupPermission(gid(req.getId()), req.getPermission());
            return ok ? AjaxResult.success() : AjaxResult.error("权限移除失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限移除失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult copyPerms(SoysPermGroupReq req) {
        try {
            String target = req == null ? null : gid(req.getTargetGroup());
            List<String> sources = req == null ? null : req.getSourceGroups();
            if (target == null || target.isEmpty()) {
                return AjaxResult.error("targetGroup 不能为空");
            }
            if (sources == null || sources.isEmpty()) {
                return AjaxResult.error("sourceGroups 不能为空");
            }
            if (sources.contains(target)) {
                return AjaxResult.error("源组不能包含目标组自身");
            }
            Map<String, Boolean> existing = new HashMap<>();
            for (SoysPermPermission p : ErpSoysStore.localStore().listGroupPermissions(target)) {
                existing.put(negKey(p), Boolean.TRUE);
            }
            int added = 0;
            int skipped = 0;
            for (String s : sources) {
                String sid = gid(s);
                if (sid.isEmpty() || sid.equals(target)) {
                    continue;
                }
                for (SoysPermPermission p : ErpSoysStore.localStore().listGroupPermissions(sid)) {
                    String k = negKey(p);
                    if (existing.containsKey(k)) {
                        skipped++;
                        continue;
                    }
                    if (ErpSoysStore.localStore().addGroupPermission(target, (p.isNegative() ? "-" : "") + p.getPermission())) {
                        existing.put(k, Boolean.TRUE);
                        added++;
                    }
                }
            }
            SoysPermGroupCopyVo data = SoysPermGroupCopyVo.of(added, skipped);
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("批量复制失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult removeByPerms(SoysPermGroupReq req) {
        try {
            String target = req == null ? null : gid(req.getTargetGroup());
            List<String> sources = req == null ? null : req.getSourceGroups();
            if (target == null || target.isEmpty()) {
                return AjaxResult.error("targetGroup 不能为空");
            }
            if (sources == null || sources.isEmpty()) {
                return AjaxResult.error("sourceGroups 不能为空");
            }
            Map<String, Boolean> srcSet = new HashMap<>();
            for (String s : sources) {
                String sid = gid(s);
                if (sid.isEmpty()) {
                    continue;
                }
                for (SoysPermPermission p : ErpSoysStore.localStore().listGroupPermissions(sid)) {
                    srcSet.put(negKey(p), Boolean.TRUE);
                }
            }
            int removed = 0;
            List<SoysPermPermission> targetPerms = ErpSoysStore.localStore().listGroupPermissions(target);
            for (SoysPermPermission p : targetPerms) {
                if (srcSet.containsKey(negKey(p))) {
                    if (ErpSoysStore.localStore().removeGroupPermission(target, (p.isNegative() ? "-" : "") + p.getPermission())) {
                        removed++;
                    }
                }
            }
            SoysPermGroupRemoveVo data = SoysPermGroupRemoveVo.of(removed);
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("批量移除失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult members(String id) {
        try {
            List<SoysPermUserGroup> ugs = ErpSoysStore.localStore().listGroupMembers(gid(id));
            List<SoysGroupMemberVo> rows = new ArrayList<>();
            for (SoysPermUserGroup ug : ugs) {
                SoysPermUser u = ErpSoysStore.localStore().getUser(ug.getUuid());
                rows.add(SoysGroupMemberVo.of(ug.getUuid(), u == null ? null : u.getPlayer()));
            }
            SoysGroupMembersVo data = SoysGroupMembersVo.of(rows);
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("组成员查询失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult addMember(SoysPermGroupReq req) {
        try {
            if (req == null || req.getId() == null || req.getPlayer() == null || req.getPlayer().trim().isEmpty()) {
                return AjaxResult.error("组 id 与 player 不能为空");
            }
            String id = gid(req.getId());
            String uuid = LocalPermissionStore.userKey(req.getPlayer());
            boolean ok = ErpSoysStore.localStore().addUserGroup(uuid, id);
            return ok ? AjaxResult.success() : AjaxResult.error("添加成员失败");
        } catch (Throwable t) {
            return AjaxResult.error("添加成员失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult removeMember(SoysPermGroupReq req) {
        try {
            if (req == null || req.getId() == null || req.getUuid() == null || req.getUuid().trim().isEmpty()) {
                return AjaxResult.error("组 id 与 uuid 不能为空");
            }
            String id = gid(req.getId());
            boolean ok = ErpSoysStore.localStore().removeUserGroup(LocalPermissionStore.userKey(req.getUuid()), id);
            return ok ? AjaxResult.success() : AjaxResult.error("移除成员失败");
        } catch (Throwable t) {
            return AjaxResult.error("移除成员失败：" + t.getMessage());
        }
    }

    /** 组 id 归一化：LocalPermissionStore 规则 + 小写。 */
    private static String gid(String id) {
        return LocalPermissionStore.normalize(id == null ? "" : id).toLowerCase();
    }

    /** 正/负标记键：'-' 前缀 = 否定，组间批量比对时区分相同节点名的允许/否定。 */
    private static String negKey(SoysPermPermission p) {
        return (p.isNegative() ? "-" : "+") + p.getPermission();
    }

    /** 权限实体 → 展示行（VO 继承实体，仅拷贝类型）。 */
    private static List<SoysPermPermissionVo> permRows(List<SoysPermPermission> perms) {
        List<SoysPermPermissionVo> rows = new ArrayList<>();
        if (perms != null) {
            for (SoysPermPermission p : perms) {
                rows.add(SoysPermPermissionVo.from(p));
            }
        }
        return rows;
    }
}
