package com.github.cocosoys.mc.soyshttpovermcerp.service;

import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysPermUserReq;

/**
 * 用户列表业务接口（soys_perm_user 全字段 + 绑定 KEY / 所属组；权限、权限组、分配 KEY、延期管理）。
 */
public interface SoysPermUserService {

    /** 分页查询用户列表（支持玩家名 / UUID 模糊搜索）。 */
    TableDataInfo list(Integer pageNum, Integer pageSize, String keyword);

    /** 查询用户所属权限组 id 列表。 */
    AjaxResult groups(String uuid);

    /** 全量保存用户权限组。 */
    AjaxResult saveGroups(SoysPermUserReq req);

    /** 查询用户直接权限。 */
    AjaxResult perms(String uuid);

    /** 添加用户权限。 */
    AjaxResult addPerm(SoysPermUserReq req);

    /** 移除用户权限。 */
    AjaxResult removePerm(SoysPermUserReq req);

    /** 设置用户权限过期时间（null / "0" / "clear" = 永久）。 */
    AjaxResult expiry(SoysPermUserReq req);

    /** 为用户生成 X-API-KEY 并绑定（明文仅返回一次）。 */
    AjaxResult assignKey(SoysPermUserReq req);
}
