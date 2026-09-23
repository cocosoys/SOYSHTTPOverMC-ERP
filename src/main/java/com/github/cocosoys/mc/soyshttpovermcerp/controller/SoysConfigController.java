package com.github.cocosoys.mc.soyshttpovermcerp.controller;

import com.github.cocosoys.mc.soyshttpovermc.annotations.ApiName;
import com.github.cocosoys.mc.soyshttpovermc.annotations.ApiPermission;
import com.github.cocosoys.mc.soyshttpovermc.annotations.GetMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.PostMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestBody;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestParam;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysConfigService;

import java.util.Map;

/**
 * 配置文件可视化编辑端点：/api/plugins/SOYSHTTPOverMC-ERP/erp/config/*。
 * 仅声明接口并转发 {@link SoysConfigService}。
 */
@RequestMapping("/erp/config")
public class SoysConfigController {

    private final SoysConfigService configService;

    public SoysConfigController(SoysConfigService configService) {
        this.configService = configService;
    }

    @ApiName("配置文件清单")
    @ApiPermission("soyshttpovermc:erp:config:list")
    @GetMapping("/files")
    public AjaxResult files() {
        return configService.list();
    }

    @ApiName("配置文件读取")
    @ApiPermission("soyshttpovermc:erp:config:query")
    @GetMapping("/load")
    public AjaxResult load(@RequestParam(name = "file", required = true) String file) {
        return configService.load(file);
    }

    @ApiName("配置文件保存")
    @ApiPermission("soyshttpovermc:erp:config:edit")
    @PostMapping("/save")
    public AjaxResult save(@RequestParam(name = "file", required = true) String file,
                           @RequestBody Map<String, Object> data) {
        return configService.save(file, data);
    }

    @ApiName("配置文件注释")
    @ApiPermission("soyshttpovermc:erp:config:query")
    @GetMapping("/help")
    public AjaxResult help(@RequestParam(name = "file", required = true) String file) {
        return configService.help(file);
    }
}
