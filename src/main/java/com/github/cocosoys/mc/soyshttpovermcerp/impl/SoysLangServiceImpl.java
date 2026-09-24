package com.github.cocosoys.mc.soyshttpovermcerp.impl;

import com.github.cocosoys.mc.soyshttpovermc.platform.PlatformYaml;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysLangFileVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysLangSaveVo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysLangService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 语言包管理实现：目录 = 主插件数据目录/language/。
 * 读取用 {@link PlatformYaml#load} + getKeys(true) 平铺；保存用 YamlConfiguration 逐键 set 后 {@link PlatformYaml#save}。
 */
public class SoysLangServiceImpl implements SoysLangService {

    private static final String SOYS_PLUGIN = "SOYSHTTPOverMC";
    private static final String DIR = "language";

    @Override
    public AjaxResult list() {
        try {
            File dir = dir();
            List<SoysLangFileVo> rows = new ArrayList<>();
            File[] files = dir.listFiles((d, n) -> n.endsWith(".yml"));
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    String code = name.substring(0, name.length() - 4);
                    SoysLangFileVo vo = new SoysLangFileVo();
                    vo.setFile(name);
                    vo.setCode(code);
                    rows.add(vo);
                }
            }
            return AjaxResult.success(rows);
        } catch (Throwable t) {
            return AjaxResult.error("语言文件列表查询失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult entries(String file) {
        try {
            File f = resolve(file);
            if (!f.isFile()) return AjaxResult.error("语言文件不存在: " + f.getPath());
            YamlConfiguration cfg = PlatformYaml.load(f);
            Map<String, String> flat = new TreeMap<>();
            for (String key : cfg.getKeys(true)) {
                Object v = cfg.get(key);
                if (v != null && !(v instanceof ConfigurationSection)) {
                    flat.put(key, String.valueOf(v));
                }
            }
            return AjaxResult.success(flat);
        } catch (Throwable t) {
            return AjaxResult.error("语言文件读取失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult save(String file, Map<String, String> entries) {
        try {
            File f = resolve(file);
            if (entries == null || entries.isEmpty()) return AjaxResult.error("内容为空");
            YamlConfiguration cfg = new YamlConfiguration();
            for (Map.Entry<String, String> e : entries.entrySet()) {
                cfg.set(e.getKey(), e.getValue());
            }
            PlatformYaml.save(cfg, f);
            SoysLangSaveVo resp = SoysLangSaveVo.of(file, entries.size(),
                    "已保存。执行 /soyshttp reload 或 /soyshttp lang reload 生效");
            return AjaxResult.success(resp);
        } catch (Throwable t) {
            return AjaxResult.error("语言文件保存失败：" + t.getMessage());
        }
    }

    // ===== 内部 =====

    private static File dir() {
        Plugin soys = Bukkit.getPluginManager().getPlugin(SOYS_PLUGIN);
        if (soys == null) throw new IllegalStateException("SOYSHTTPOverMC 主插件未加载");
        File d = new File(soys.getDataFolder(), DIR);
        if (!d.isDirectory()) d.mkdirs();
        return d;
    }

    private static File resolve(String file) {
        if (file == null || file.contains("..") || file.contains("/") || file.contains("\\")) {
            throw new IllegalArgumentException("非法文件名: " + file);
        }
        return new File(dir(), file);
    }
}
