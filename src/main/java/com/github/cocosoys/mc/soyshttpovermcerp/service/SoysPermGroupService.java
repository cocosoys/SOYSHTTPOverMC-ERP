package com.github.cocosoys.mc.soyshttpovermcerp.service;

import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermGroupReq;

/**
 * 权限组业务接口（扁平化组 CRUD + 权限管理，含组间批量"复制加入 / 按此移除" + 成员管理）。
 */
public interface SoysPermGroupService {

    /** 分页查询权限组列表（按权重降序，支持 id / 显示名搜索）。 */
    TableDataInfo list(Integer pageNum, Integer pageSize, String keyword);

    /** 新增权限组。 */
    AjaxResult create(SoysPermGroupReq req);

    /** 编辑权限组（upsert 元数据）。 */
    AjaxResult update(SoysPermGroupReq req);

    /** 删除权限组。 */
    AjaxResult remove(SoysPermGroupReq req);

    /** 查询权限组权限。 */
    AjaxResult perms(String id);

    /** 添加权限组权限。 */
    AjaxResult addPerm(SoysPermGroupReq req);

    /** 移除权限组权限。 */
    AjaxResult removePerm(SoysPermGroupReq req);

    /** 组间批量复制：把源组全部权限加入目标组（跳过已存在，保留否定标记）。 */
    AjaxResult copyPerms(SoysPermGroupReq req);

    /** 组间批量移除：按源组权限精确移除目标组权限（含否定匹配）。 */
    AjaxResult removeByPerms(SoysPermGroupReq req);

    /** 查询权限组成员。 */
    AjaxResult members(String id);

    /** 添加成员（玩家名 / UUID）。 */
    AjaxResult addMember(SoysPermGroupReq req);

    /** 移除成员。 */
    AjaxResult removeMember(SoysPermGroupReq req);
}
