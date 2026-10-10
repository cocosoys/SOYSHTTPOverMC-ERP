package com.github.cocosoys.mc.soyshttpovermcerp.impl;

import com.github.cocosoys.mc.soyshttpovermc.orm.DATA;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.PageUtils;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermc.web.gateway.policy.auth.bridge.SoysDeviceBinding;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysDeviceBindingVo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysDeviceBindingService;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备绑定业务实现：复用主插件 {@link DATA} 门面读写 soys_device_binding 表。
 *
 * <p>仅列表 + toggle revoked + 删除，不提供手动新增/编辑表单。</p>
 */
public class SoysDeviceBindingServiceImpl implements SoysDeviceBindingService {

    @Override
    public TableDataInfo list(Integer pageNum, Integer pageSize, String keyword, String player, Integer revoked) {
        List<SoysDeviceBinding> all;
        try {
            all = DATA.select(SoysDeviceBinding.class);
        } catch (Throwable t) {
            return PageUtils.page(new ArrayList<>(), pageNum, pageSize);
        }
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        List<SoysDeviceBindingVo> rows = new ArrayList<>();
        for (SoysDeviceBinding b : all) {
            if (player != null && !player.trim().isEmpty()
                    && !player.equalsIgnoreCase(nvl(b.getPlayer()))) {
                continue;
            }
            if (revoked != null) {
                int rv = b.getRevoked() == null ? 0 : b.getRevoked();
                if (rv != revoked) {
                    continue;
                }
            }
            if (kw != null && !kw.isEmpty()) {
                String blob = (nvl(b.getPlayer()) + " " + nvl(b.getUuid()) + " "
                        + nvl(b.getDeviceLabel()) + " " + nvl(b.getLastIp())).toLowerCase();
                if (!blob.contains(kw)) {
                    continue;
                }
            }
            rows.add(toRow(b));
        }
        return PageUtils.page(rows, pageNum, pageSize);
    }

    @Override
    public AjaxResult toggle(Long id) {
        if (id == null) {
            return AjaxResult.error("id 不能为空");
        }
        SoysDeviceBinding b = DATA.get(SoysDeviceBinding.class, id);
        if (b == null) {
            return AjaxResult.error("记录 #" + id + " 不存在");
        }
        int cur = b.getRevoked() == null ? 0 : b.getRevoked();
        b.setRevoked(cur == 1 ? 0 : 1);
        if (DATA.updateById(b)) {
            return AjaxResult.success(b.getRevoked() == 1 ? "已吊销该设备" : "已恢复该设备");
        }
        return AjaxResult.error("切换状态失败");
    }

    @Override
    public AjaxResult remove(Long id) {
        if (id == null) {
            return AjaxResult.error("id 不能为空");
        }
        if (DATA.deleteById(SoysDeviceBinding.class, id)) {
            return AjaxResult.success("已删除设备绑定 #" + id);
        }
        return AjaxResult.error("删除失败（记录可能已不存在）");
    }

    // ---------- 私有辅助 ----------

    private SoysDeviceBindingVo toRow(SoysDeviceBinding b) {
        SoysDeviceBindingVo row = new SoysDeviceBindingVo();
        row.setId(b.getId());
        row.setPlayer(b.getPlayer());
        row.setUuid(b.getUuid());
        row.setFingerprintHash(b.getFingerprintHash());
        row.setDeviceLabel(b.getDeviceLabel());
        row.setLastIp(b.getLastIp());
        row.setLastBindAt(b.getLastBindAt());
        row.setRevoked(b.getRevoked());
        row.setCreateTime(b.getCreateTime());
        row.setUpdateTime(b.getUpdateTime());
        int rv = b.getRevoked() == null ? 0 : b.getRevoked();
        row.setStatusLabel(rv == 1 ? "已吊销" : "有效");
        String fp = b.getFingerprintHash();
        row.setFingerprintShort(fp == null || fp.length() <= 16 ? fp : fp.substring(0, 10) + "…" + fp.substring(fp.length() - 6));
        return row;
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
