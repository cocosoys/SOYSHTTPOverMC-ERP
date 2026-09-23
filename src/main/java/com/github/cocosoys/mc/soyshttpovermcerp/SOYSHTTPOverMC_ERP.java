package com.github.cocosoys.mc.soyshttpovermcerp;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SOYSHTTPOverMC-ERP 主类：生命周期入口，onEnable 时登记 ERP 模块扩展
 * （{@link SoysErpExpansion}，继承 MCERP 的 {@code McerpExpansion}）。
 */
public final class SOYSHTTPOverMC_ERP extends JavaPlugin {

    private @Getter @Setter static SOYSHTTPOverMC_ERP instance;

    @Override
    public void onEnable() {
        setInstance(this);

        boolean ok = new SoysErpExpansion().register();
        if (!ok) {
            getLogger().severe("SOYSHTTPOverMC-ERP 扩展登记失败：请确认 MCERP / SOYSHTTPOverMC 已加载且插件名不冲突");
        } else {
            getLogger().info("SOYSHTTPOverMC-ERP 已登记：用户列表 / 权限组列表 / APIKEY 管理");
        }
    }

    @Override
    public void onDisable() {
        // 扩展注销由 SoysExpansion 生命周期自动处理
    }
}
