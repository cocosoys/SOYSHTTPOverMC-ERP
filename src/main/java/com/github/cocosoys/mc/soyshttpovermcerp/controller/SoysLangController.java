package com.github.cocosoys.mc.soyshttpovermcerp.controller;

import com.github.cocosoys.mc.soyshttpovermc.annotations.ApiName;
import com.github.cocosoys.mc.soyshttpovermc.annotations.ApiPermission;
import com.github.cocosoys.mc.soyshttpovermc.annotations.GetMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.PostMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestBody;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestMapping;
import com.github.cocosoys.mc.soyshttpovermc.annotations.RequestParam;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysLangService;

import java.util.Map;

/**
 * 语言包管理端点：/api/plugins/SOYSHTTPOverMC-ERP/erp/lang/*。
 */
@RequestMapping("/erp/lang")
public class SoysLangController {

    private final SoysLangService langService;

    public SoysLangController(SoysLangService langService) {
        this.langService = langService;
    }

    @ApiName("语言文件列表")
    @ApiPermission("soyshttpovermc:erp:lang:list")
    @GetMapping("/list")
    public AjaxResult list() {
        return langService.list();
    }

    @ApiName("语言条目读取")
    @ApiPermission("soyshttpovermc:erp:lang:query")
    @GetMapping("/entries")
    public AjaxResult entries(@RequestParam(name = "file", required = true) String file) {
        return langService.entries(file);
    }

    @ApiName("语言条目保存")
    @ApiPermission("soyshttpovermc:erp:lang:edit")
    @PostMapping("/save")
    public AjaxResult save(@RequestParam(name = "file", required = true) String file,
                           @RequestBody Map<String, String> entries) {
        return langService.save(file, entries);
    }
}
