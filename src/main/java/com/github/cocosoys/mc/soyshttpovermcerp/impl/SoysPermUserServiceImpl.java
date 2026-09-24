package com.github.cocosoys.mc.soyshttpovermcerp.impl;

import com.github.cocosoys.mc.soyshttpovermc.orm.DATA;
import com.github.cocosoys.mc.soyshttpovermc.permission.local.LocalPermissionStore;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysApiKey;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermGroup;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermPermission;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermUser;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.PageUtils;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysApiKeyGenVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermPermissionVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermRowsVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermUserReq;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermUserVo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysPermUserService;
import com.github.cocosoys.mc.soyshttpovermcerp.util.ErpSoysStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户列表业务实现：直接复用主插件 LocalPermissionStore / ApiKeyStore。
 */
public class SoysPermUserServiceImpl implements SoysPermUserService {

    @Override
    public TableDataInfo list(Integer pageNum, Integer pageSize, String keyword) {
        List<SoysPermUser> all = DATA.select(SoysPermUser.class);
        List<SoysPermUserVo> rows = new ArrayList<>();
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        for (SoysPermUser u : all) {
            if (kw != null && !kw.isEmpty()) {
                String name = u.getPlayer() == null ? "" : u.getPlayer().toLowerCase();
                String uuid = u.getUuid() == null ? "" : u.getUuid().toLowerCase();
                if (!name.contains(kw) && !uuid.contains(kw)) {
                    continue;
                }
            }
            rows.add(toRow(u));
        }
        return PageUtils.page(rows, pageNum, pageSize);
    }

    /** 组装用户行：继承实体全字段 + groups/groupsDisplay/hasKey/keyFingerprint。 */
    private SoysPermUserVo toRow(SoysPermUser u) {
        SoysPermUserVo row = new SoysPermUserVo();
        row.setUuid(u.getUuid());
        row.setPlayer(u.getPlayer());
        row.setExpiry(u.getExpiry());
        row.setCreateTime(u.getCreateTime());
        row.setUpdateTime(u.getUpdateTime());
        List<String> gids = ErpSoysStore.localStore().listUserGroups(u.getUuid());
        row.setGroups(gids);
        List<String> displays = new ArrayList<>();
        for (String gid : gids) {
            SoysPermGroup g = ErpSoysStore.localStore().getGroup(gid);
            displays.add(g == null || g.getDisplay() == null || g.getDisplay().isEmpty() ? gid : g.getDisplay());
        }
        row.setGroupsDisplay(displays);
        SoysApiKey key = firstKeyOf(u.getUuid());
        row.setHasKey(key != null);
        row.setKeyFingerprint(key == null ? null : key.getFingerprint());
        return row;
    }

    private SoysApiKey firstKeyOf(String uuid) {
        for (SoysApiKey k : ErpSoysStore.apiKeyStore().list()) {
            if (uuid != null && uuid.equalsIgnoreCase(k.getUuid())) {
                return k;
            }
        }
        return null;
    }

    @Override
    public AjaxResult groups(String uuid) {
        try {
            return AjaxResult.success(ErpSoysStore.localStore().listUserGroups(userKey(uuid)));
        } catch (Throwable t) {
            return AjaxResult.error("权限组列表查询失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult saveGroups(SoysPermUserReq req) {
        try {
            if (req == null || req.getUuid() == null || req.getUuid().trim().isEmpty()) {
                return AjaxResult.error("uuid 不能为空");
            }
            String uuid = userKey(req.getUuid());
            List<String> want = req.getGroups() == null ? Collections.emptyList() : req.getGroups();
            List<String> old = ErpSoysStore.localStore().listUserGroups(uuid);
            for (String g : old) {
                if (!want.contains(g)) {
                    ErpSoysStore.localStore().removeUserGroup(uuid, g);
                }
            }
            for (String g : want) {
                if (g != null && !g.trim().isEmpty() && !old.contains(g)) {
                    ErpSoysStore.localStore().addUserGroup(uuid, g);
                }
            }
            return AjaxResult.success();
        } catch (Throwable t) {
            return AjaxResult.error("权限组保存失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult perms(String uuid) {
        try {
            String key = userKey(uuid);
            SoysPermRowsVo data = SoysPermRowsVo.of(permRows(ErpSoysStore.localStore().listUserPermissions(key)));
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("用户权限查询失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult addPerm(SoysPermUserReq req) {
        try {
            if (req == null || req.getUuid() == null || req.getPermission() == null || req.getPermission().trim().isEmpty()) {
                return AjaxResult.error("uuid 与 permission 不能为空");
            }
            boolean ok = ErpSoysStore.localStore().addUserPermission(userKey(req.getUuid()), req.getPermission());
            return ok ? AjaxResult.success() : AjaxResult.error("权限添加失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限添加失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult removePerm(SoysPermUserReq req) {
        try {
            if (req == null || req.getUuid() == null || req.getPermission() == null || req.getPermission().trim().isEmpty()) {
                return AjaxResult.error("uuid 与 permission 不能为空");
            }
            boolean ok = ErpSoysStore.localStore().removeUserPermission(userKey(req.getUuid()), req.getPermission());
            return ok ? AjaxResult.success() : AjaxResult.error("权限移除失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限移除失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult expiry(SoysPermUserReq req) {
        try {
            if (req == null || req.getUuid() == null || req.getUuid().trim().isEmpty()) {
                return AjaxResult.error("uuid 不能为空");
            }
            boolean ok = ErpSoysStore.localStore().setUserExpiry(userKey(req.getUuid()), req.getExpiry());
            return ok ? AjaxResult.success() : AjaxResult.error("过期时间设置失败（格式：yyyy-MM-dd HH:mm:ss，或留空/0 为永久）");
        } catch (Throwable t) {
            return AjaxResult.error("过期时间设置失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult assignKey(SoysPermUserReq req) {
        try {
            if (req == null || req.getUuid() == null || req.getUuid().trim().isEmpty()) {
                return AjaxResult.error("uuid 不能为空");
            }
            String uuid = userKey(req.getUuid());
            SoysPermUser user = ErpSoysStore.localStore().getUser(uuid);
            if (user == null) {
                return AjaxResult.error("用户不存在：" + req.getUuid());
            }
            String player = user.getPlayer() == null ? uuid : user.getPlayer();
            String remark = "自动分配 · 用户 " + player;
            String plain = ErpSoysStore.apiKeyStore().generate(remark);
            if (plain == null || plain.isEmpty()) {
                return AjaxResult.error("密钥生成失败");
            }
            SoysApiKey key = ErpSoysStore.apiKeyStore().findByPresented(plain);
            if (key == null) {
                return AjaxResult.error("密钥生成失败（未落库）");
            }
            ErpSoysStore.apiKeyStore().bind(key.getId(), uuid);
            SoysApiKeyGenVo data = new SoysApiKeyGenVo();
            data.setFingerprint(key.getFingerprint());
            data.setPlain(plain);
            data.setRemark(remark);
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("分配 KEY 失败：" + t.getMessage());
        }
    }

    /** 玩家名 / UUID → 用户主键（UUID 归一，玩家名推导离线 UUID）。 */
    private static String userKey(String identity) {
        return LocalPermissionStore.userKey(identity);
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
