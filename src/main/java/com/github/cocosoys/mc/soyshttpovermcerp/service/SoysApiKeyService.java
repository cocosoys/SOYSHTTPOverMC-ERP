package com.github.cocosoys.mc.soyshttpovermcerp.service;

import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.TableDataInfo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysApiKeyReq;

/**
 * X-API-KEY 业务接口（生成 / 启停 / 过期 / 绑定玩家 / 权限 / 删除；明文仅生成时返回一次）。
 */
public interface SoysApiKeyService {

    /** 分页查询 KEY 列表（支持指纹 / 玩家 / 备注搜索）。 */
    TableDataInfo list(Integer pageNum, Integer pageSize, String keyword);

    /** 生成新 KEY（明文仅此一次返回）。 */
    AjaxResult generate(SoysApiKeyReq req);

    /** 启停 KEY。 */
    AjaxResult toggle(SoysApiKeyReq req);

    /** 设置 KEY 过期时间（null / "0" / "clear" = 永久）。 */
    AjaxResult expiry(SoysApiKeyReq req);

    /** 绑定玩家（玩家名 / UUID）。 */
    AjaxResult bind(SoysApiKeyReq req);

    /** 解除绑定。 */
    AjaxResult unbind(SoysApiKeyReq req);

    /** 删除 KEY。 */
    AjaxResult remove(SoysApiKeyReq req);

    /** 查询 KEY 权限。 */
    AjaxResult perms(String keyId);

    /** 添加 KEY 权限。 */
    AjaxResult addPerm(SoysApiKeyReq req);

    /** 移除 KEY 权限。 */
    AjaxResult removePerm(SoysApiKeyReq req);
}
