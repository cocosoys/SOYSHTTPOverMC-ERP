package com.github.cocosoys.mc.soyshttpovermcerp;

import com.github.cocosoys.mc.mcerp.ErpMenus;
import com.github.cocosoys.mc.mcerp.McerpExpansion;
import com.github.cocosoys.mc.soyshttpovermc.api.SoysHttpOverMcApi;
import com.github.cocosoys.mc.soyshttpovermcerp.controller.SoysApiKeyController;
import com.github.cocosoys.mc.soyshttpovermcerp.controller.SoysConfigController;
import com.github.cocosoys.mc.soyshttpovermcerp.controller.SoysLangController;
import com.github.cocosoys.mc.soyshttpovermcerp.controller.SoysPermGroupController;
import com.github.cocosoys.mc.soyshttpovermcerp.controller.SoysPermUserController;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.SoysConfigFile;
import com.github.cocosoys.mc.soyshttpovermcerp.impl.SoysApiKeyServiceImpl;
import com.github.cocosoys.mc.soyshttpovermcerp.impl.SoysConfigServiceImpl;
import com.github.cocosoys.mc.soyshttpovermcerp.impl.SoysLangServiceImpl;
import com.github.cocosoys.mc.soyshttpovermcerp.impl.SoysPermGroupServiceImpl;
import com.github.cocosoys.mc.soyshttpovermcerp.impl.SoysPermUserServiceImpl;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysApiKeyService;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysConfigService;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysLangService;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysPermGroupService;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysPermUserService;

import java.util.ArrayList;
import java.util.List;

/**
 * SOYSHTTPOverMC ERP 模块扩展（挂载于 MCERP 管理台）。
 *
 * <p>继承新版 {@link McerpExpansion}（已泛化为 SoysExpansion 子类），一行 register()
 * 自动完成：端点登记（/api/plugins/SOYSHTTPOverMC-ERP/erp/*）、页面托管
 * （resourceRoot=dist → /web/plugins/SOYSHTTPOverMC-ERP/*）、MCERP 菜单登记（ErpMenus 声明式）。</p>
 *
 * <p>菜单树：用户列表 / 权限组列表 / APIKEY 管理 + 插件配置（dir，含 配置文件 与 网关 子目录）。
 * 页面 URL 前缀经 {@link com.github.cocosoys.mc.soyshttpovermc.api.ApiToolkitApi#pageFullPrefix}
 * 由 SOYS 提供，不手写常量。</p>
 */
public class SoysErpExpansion extends McerpExpansion {

    @Override
    public String getIdentifier() {
        return SOYSHTTPOverMC_ERP.getInstance().getName();
    }

    @Override
    protected String displayName() {
        return "SOYS HTTP Over MC";
    }

    @Override
    protected String icon() {
        return "peoples";
    }

    @Override
    protected int sortOrder() {
        return 10;
    }

    @Override
    protected String permission() {
        return "soyshttpovermc:erp:menu";
    }

    @Override
    protected ErpMenus menus() {
        return ErpMenus.create()
                .menu("user", "游戏用户列表", "user", SoysPermUserController.class)
                .perms("soyshttpovermc:erp:user:list")
                .orderNum(10)
                .menu("group", "权限组列表", "lock", SoysPermGroupController.class)
                .perms("soyshttpovermc:erp:group:list")
                .orderNum(20)
                .menu("apikey", "APIKEY 管理", "lock", SoysApiKeyController.class)
                .perms("soyshttpovermc:erp:apikey:list")
                .orderNum(30)
                .menu("lang", "语言管理", "language", SoysLangController.class)
                .perms("soyshttpovermc:erp:lang:list")
                .orderNum(35)
                // 插件配置 dir：按 SoysConfigFile.values() 逐个登记文件级菜单，点击直达对应配置文件
                .dir("config", "插件配置", "edit", d -> {
                    for (int i = 0; i < SoysConfigFile.values().length; i++) {
                        SoysConfigFile f = SoysConfigFile.values()[i];
                        if (!"plugin".equals(f.getGroup())) {
                            continue;
                        }
                        d.menu(f.getId(), f.getName(), "input")
                                .perms("soyshttpovermc:erp:config:list")
                                .orderNum(i);
                    }
                    // 网关 dir：gateway 组 8 个文件级菜单
                    d.dir("gateway", "网关", "form", g -> {
                        for (int i = 0; i < SoysConfigFile.values().length; i++) {
                            SoysConfigFile f = SoysConfigFile.values()[i];
                            if (!"gateway".equals(f.getGroup())) {
                                continue;
                            }
                            g.menu(f.getId(), f.getName(), "input")
                                    .perms("soyshttpovermc:erp:config:list")
                                    .orderNum(i);
                        }
                    }).perms("soyshttpovermc:erp:config:list");
                })
                // 配置管理端点按钮（files/load/save）自动挂到"插件配置"dir 下
                .permsFrom(SoysConfigController.class)
                .perms("soyshttpovermc:erp:config:list")
                .orderNum(40);
    }

    @Override
    protected List<Object> buildControllers() {
        // Controller 仅依赖 Service 抽象，实现类在此组装并构造器注入
        SoysPermUserService userService = new SoysPermUserServiceImpl();
        SoysPermGroupService groupService = new SoysPermGroupServiceImpl();
        SoysApiKeyService apiKeyService = new SoysApiKeyServiceImpl();
        SoysConfigService configService = new SoysConfigServiceImpl();
        SoysLangService langService = new SoysLangServiceImpl();
        List<Object> list = new ArrayList<>();
        list.add(new SoysPermUserController(userService));
        list.add(new SoysPermGroupController(groupService));
        list.add(new SoysApiKeyController(apiKeyService));
        list.add(new SoysConfigController(configService));
        list.add(new SoysLangController(langService));
        return list;
    }

    @Override
    protected String resourceRoot() {
        return "dist";
    }
}
