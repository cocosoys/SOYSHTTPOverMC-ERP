package com.github.cocosoys.mc.soyshttpovermcerp.util;

import com.github.cocosoys.mc.soyshttpovermc.HttpOverMcPlugin;
import com.github.cocosoys.mc.soyshttpovermc.permission.local.ApiKeyStore;
import com.github.cocosoys.mc.soyshttpovermc.permission.local.LocalPermissionStore;

/**
 * SOYSHTTPOverMC 主插件门面：统一暴露本地权限存储与 X-API-KEY 存储，
 * 供 Service 实现层复用（复用主插件单例，避免重复建连/状态不一致）。
 */
public final class ErpSoysStore {

    private ErpSoysStore() {
    }

    /** 本地权限表门面（用户/组/权限 CRUD，复用主插件单例）。 */
    public static LocalPermissionStore localStore() {
        HttpOverMcPlugin soys = HttpOverMcPlugin.getInstance();
        if (soys == null || soys.getCombinedPermissionService() == null) {
            throw new IllegalStateException("SOYSHTTPOverMC 主插件未就绪");
        }
        return soys.getCombinedPermissionService().getLocalStore();
    }

    /** X-API-Key 存储门面（复用主插件 AuthPolicy 单例）。 */
    public static ApiKeyStore apiKeyStore() {
        HttpOverMcPlugin soys = HttpOverMcPlugin.getInstance();
        if (soys == null || soys.getGateway() == null || soys.getGateway().getAuthPolicy() == null) {
            throw new IllegalStateException("SOYSHTTPOverMC 网关未就绪");
        }
        return soys.getGateway().getAuthPolicy().getApiKeyStore();
    }
}
