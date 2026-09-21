package com.github.cocosoys.mc.soyshttpovermcerp.impl;

import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysApiKey;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermPermission;
import com.github.cocosoys.mc.soyshttpovermc.spring.entity.SoysPermUser;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.PageUtils;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysApiKeyReq;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysApiKeyVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermPermissionVo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysApiKeyService;
import com.github.cocosoys.mc.soyshttpovermcerp.util.ErpSoysStore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * X-API-KEY 业务实现：复用主插件 ApiKeyStore；列表绝不返回 apiKey 哈希字段。
 */
public class SoysApiKeyServiceImpl implements SoysApiKeyService {

    @Override
    public TableDataInfo list(Integer pageNum, Integer pageSize, String keyword) {
        List<SoysApiKey> all = ErpSoysStore.apiKeyStore().list();
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        List<SoysApiKeyVo> rows = new ArrayList<>();
        for (SoysApiKey k : all) {
            if (kw != null && !kw.isEmpty()) {
                String fp = k.getFingerprint() == null ? "" : k.getFingerprint().toLowerCase();
                String player = k.getPlayer() == null ? "" : k.getPlayer().toLowerCase();
                String remark = k.getRemark() == null ? "" : k.getRemark().toLowerCase();
                if (!fp.contains(kw) && !player.contains(kw) && !remark.contains(kw)) {
                    continue;
                }
            }
            rows.add(toRow(k));
        }
        return PageUtils.page(rows, pageNum, pageSize);
    }

    /** 组装 KEY 行：继承实体全字段（apiKey 哈希经 VO JsonIgnore 排除）。 */
    private SoysApiKeyVo toRow(SoysApiKey k) {
        SoysApiKeyVo row = new SoysApiKeyVo();
        row.setId(k.getId());
        row.setFingerprint(k.getFingerprint());
        row.setUuid(k.getUuid());
        row.setPlayer(resolvePlayer(k));
        row.setEnabled(k.isEnabled());
        row.setExpiry(k.getExpiry());
        row.setLastUsedAt(k.getLastUsedAt());
        row.setUsedCount(k.getUsedCount());
        row.setRemark(k.getRemark());
        row.setCreateTime(k.getCreateTime());
        row.setUpdateTime(k.getUpdateTime());
        return row;
    }

    /** 绑定玩家名优先；缺失时按 uuid 反查用户表，再兜底 null。 */
    private String resolvePlayer(SoysApiKey k) {
        if (k.getPlayer() != null && !k.getPlayer().trim().isEmpty()) {
            return k.getPlayer();
        }
        if (k.getUuid() != null && !k.getUuid().trim().isEmpty()) {
            SoysPermUser u = ErpSoysStore.localStore().getUser(k.getUuid());
            if (u != null && u.getPlayer() != null && !u.getPlayer().trim().isEmpty()) {
                return u.getPlayer();
            }
        }
        return null;
    }

    @Override
    public AjaxResult generate(SoysApiKeyReq req) {
        try {
            String remark = req == null ? null : req.getRemark();
            String plain = ErpSoysStore.apiKeyStore().generate(remark == null ? "" : remark.trim());
            if (plain == null || plain.isEmpty()) {
                return AjaxResult.error("密钥生成失败");
            }
            SoysApiKey key = ErpSoysStore.apiKeyStore().findByPresented(plain);
            if (key == null) {
                return AjaxResult.error("密钥生成失败（未落库）");
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("fingerprint", key.getFingerprint());
            data.put("plain", plain);
            data.put("remark", key.getRemark());
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("密钥生成失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult toggle(SoysApiKeyReq req) {
        try {
            if (req == null || req.getKeyId() == null || req.getKeyId().trim().isEmpty() || req.getEnabled() == null) {
                return AjaxResult.error("keyId 与 enabled 不能为空");
            }
            boolean ok = ErpSoysStore.apiKeyStore().setEnabled(req.getKeyId().trim(), req.getEnabled());
            return ok ? AjaxResult.success() : AjaxResult.error("状态修改失败");
        } catch (Throwable t) {
            return AjaxResult.error("状态修改失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult expiry(SoysApiKeyReq req) {
        try {
            if (req == null || req.getKeyId() == null || req.getKeyId().trim().isEmpty()) {
                return AjaxResult.error("keyId 不能为空");
            }
            boolean ok = ErpSoysStore.apiKeyStore().setExpiry(req.getKeyId().trim(), req.getExpiry());
            return ok ? AjaxResult.success() : AjaxResult.error("过期时间设置失败（格式：yyyy-MM-dd HH:mm:ss，或留空/0 为永久）");
        } catch (Throwable t) {
            return AjaxResult.error("过期时间设置失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult bind(SoysApiKeyReq req) {
        try {
            if (req == null || req.getKeyId() == null || req.getKeyId().trim().isEmpty()) {
                return AjaxResult.error("keyId 不能为空");
            }
            if (req.getPlayer() == null || req.getPlayer().trim().isEmpty()) {
                return AjaxResult.error("player 不能为空");
            }
            boolean ok = ErpSoysStore.apiKeyStore().bind(req.getKeyId().trim(), req.getPlayer().trim());
            return ok ? AjaxResult.success() : AjaxResult.error("绑定失败");
        } catch (Throwable t) {
            return AjaxResult.error("绑定失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult unbind(SoysApiKeyReq req) {
        try {
            if (req == null || req.getKeyId() == null || req.getKeyId().trim().isEmpty()) {
                return AjaxResult.error("keyId 不能为空");
            }
            boolean ok = ErpSoysStore.apiKeyStore().unbind(req.getKeyId().trim());
            return ok ? AjaxResult.success() : AjaxResult.error("解绑失败");
        } catch (Throwable t) {
            return AjaxResult.error("解绑失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult remove(SoysApiKeyReq req) {
        try {
            if (req == null || req.getKeyId() == null || req.getKeyId().trim().isEmpty()) {
                return AjaxResult.error("keyId 不能为空");
            }
            boolean ok = ErpSoysStore.apiKeyStore().remove(req.getKeyId().trim());
            return ok ? AjaxResult.success() : AjaxResult.error("删除失败（可能不存在）");
        } catch (Throwable t) {
            return AjaxResult.error("删除失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult perms(String keyId) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("rows", permRows(ErpSoysStore.apiKeyStore().listPermissions(keyId.trim())));
            return AjaxResult.success(data);
        } catch (Throwable t) {
            return AjaxResult.error("APIKEY 权限查询失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult addPerm(SoysApiKeyReq req) {
        try {
            if (req == null || req.getKeyId() == null || req.getPermission() == null || req.getPermission().trim().isEmpty()) {
                return AjaxResult.error("keyId 与 permission 不能为空");
            }
            boolean ok = ErpSoysStore.apiKeyStore().addPermission(req.getKeyId().trim(), req.getPermission());
            return ok ? AjaxResult.success() : AjaxResult.error("权限添加失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限添加失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult removePerm(SoysApiKeyReq req) {
        try {
            if (req == null || req.getKeyId() == null || req.getPermission() == null || req.getPermission().trim().isEmpty()) {
                return AjaxResult.error("keyId 与 permission 不能为空");
            }
            boolean ok = ErpSoysStore.apiKeyStore().removePermission(req.getKeyId().trim(), req.getPermission());
            return ok ? AjaxResult.success() : AjaxResult.error("权限移除失败");
        } catch (Throwable t) {
            return AjaxResult.error("权限移除失败：" + t.getMessage());
        }
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
