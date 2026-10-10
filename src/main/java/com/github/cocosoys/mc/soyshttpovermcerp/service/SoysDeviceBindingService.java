package com.github.cocosoys.mc.soyshttpovermcerp.service;

import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;

/**
 * 设备绑定业务接口（用户设备管理型：列表 + 吊销/恢复 + 删除）。
 *
 * <p>设备记录由主插件 AuthLoginBridge 在玩家勾选"记住我"时自动写入，
 * 本服务不提供手动新增/编辑表单，仅运维入口。</p>
 */
public interface SoysDeviceBindingService {

    /**
     * 分页查询设备绑定。
     *
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 页大小
     * @param keyword  模糊匹配 player / uuid / deviceLabel / lastIp
     * @param player   按玩家名精确筛选（用户列表"查看该玩家设备"跳转入口）
     * @param revoked  状态筛选：0/null=有效 / 1=已吊销 / null=全部
     */
    TableDataInfo list(Integer pageNum, Integer pageSize, String keyword, String player, Integer revoked);

    /** 切换吊销状态：revoked=0 ↔ 1。 */
    AjaxResult toggle(Long id);

    /** 删除整条绑定记录。 */
    AjaxResult remove(Long id);
}
