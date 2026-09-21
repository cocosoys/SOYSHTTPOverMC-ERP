package com.github.cocosoys.mc.soyshttpovermcerp.entity;

import lombok.Data;
import lombok.Getter;

/**
 * ERP 可编辑的 SOYSHTTPOverMC 配置文件清单（数据目录 = plugins/SOYSHTTPOverMC/）。
 *
 * <p>分组：plugin=插件配置目录、gateway=网关目录。修改后执行 /soyshttp reload 热重载
 * （gateway 中部分项需重启，见各文件注释）。</p>
 */
public enum SoysConfigFile {

    CONFIG("config", "核心配置", "plugin", "config.yml",
            "服务器地址/端口、嗅探器、HTTP 后端模式、存储后端等"),
    LANGUAGE("language", "国际化", "plugin", "language.yml",
            "当前语言 / 加载策略 / 额外语言源"),
    PAGES("pages", "页面与资源", "plugin", "pages.yml",
            "前端资源目录 / 首页 / 缓存 / 手动页面登记"),
    EULA("eula", "使用协议", "plugin", "EULA.yml",
            "SOYSHTTPOverMC 使用与开发协议同意开关"),

    GW_CONFIG("gw-config", "网关总开关", "gateway", "gateway/config.yml",
            "安全网关总开关 / API 全局前缀 / 事件调试"),
    GW_HTTPS("gw-https", "HTTPS 设置", "gateway", "gateway/https.yml",
            "TLS 证书来源 / 协议版本 / 主机名"),
    GW_AUTH("gw-auth", "认证鉴权", "gateway", "gateway/policies/auth.yml",
            "认证来源 / 保护路径 / 豁免路径 / 自动登录 / KEY 降级"),
    GW_RATE("gw-rate", "令牌桶限流", "gateway", "gateway/policies/rate-limit.yml",
            "按 IP / KEY 维度的 rpm / burst 限流"),
    GW_ACCESS("gw-access", "访问限制器", "gateway", "gateway/policies/access-limiter.yml",
            "按 scope 的固定窗口访问次数限制"),
    GW_ALLOW("gw-allow", "IP 白名单", "gateway", "gateway/policies/ip-allowlist.yml",
            "IP / CIDR 白黑名单"),
    GW_TLS("gw-tls", "TLS 强制", "gateway", "gateway/policies/tls.yml",
            "明文 HTTP 强制升级 HTTPS"),
    GW_SESSION("gw-session", "会话令牌", "gateway", "gateway/issuers/session-token.yml",
            "会话令牌签发 / Cookie 名 / TTL / 时钟容差");

    private @Getter final String id;
    private @Getter final String name;
    private @Getter final String group;
    private @Getter final String file;
    private @Getter final String desc;

    SoysConfigFile(String id, String name, String group, String file, String desc) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.file = file;
        this.desc = desc;
    }

    public static SoysConfigFile byId(String id) {
        if (id == null) {
            return null;
        }
        for (SoysConfigFile f : values()) {
            if (f.id.equals(id)) {
                return f;
            }
        }
        return null;
    }
}
