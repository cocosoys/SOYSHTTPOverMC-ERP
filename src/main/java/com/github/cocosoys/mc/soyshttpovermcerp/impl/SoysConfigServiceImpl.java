package com.github.cocosoys.mc.soyshttpovermcerp.impl;

import com.github.cocosoys.mc.soyshttpovermc.platform.PlatformYaml;
import com.github.cocosoys.mc.soyshttpovermc.util.AjaxResult;
import com.github.cocosoys.mc.soyshttpovermc.util.ConfigUtil;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.SoysConfigFile;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysConfigFileVo;
import com.github.cocosoys.mc.soyshttpovermcerp.entity.vo.SoysConfigSaveVo;
import com.github.cocosoys.mc.soyshttpovermcerp.service.SoysConfigService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 配置文件编辑实现：目标目录 = 主插件（SOYSHTTPOverMC）数据目录。
 *
 * <p>读写<b>全部复用主插件平台层</b>：{@link PlatformYaml#load}/{@link PlatformYaml#save}
 * （版本模块统一接管编码差异，低版本 UTF-8 不乱码）+ {@link ConfigUtil#toMap}/{@link ConfigUtil#toYaml}
 * （YAML ↔ Map 递归转换）。本实现不做任何文件层解析，不绑定具体版本。
 * 保存为 YamlConfiguration 全量写回（与主插件自身落盘语义一致，注释不保留），
 * 保存后提示 /soyshttp reload 热重载生效。</p>
 */
public class SoysConfigServiceImpl implements SoysConfigService {

    /** 主插件名（plugin.yml name）。 */
    private static final String SOYS_PLUGIN = "SOYSHTTPOverMC";

    @Override
    public AjaxResult list() {
        try {
            List<SoysConfigFileVo> rows = new ArrayList<>();
            for (SoysConfigFile f : SoysConfigFile.values()) {
                SoysConfigFileVo vo = new SoysConfigFileVo();
                vo.setId(f.getId());
                vo.setName(f.getName());
                vo.setGroup(f.getGroup());
                vo.setFile(f.getFile());
                vo.setDesc(f.getDesc());
                rows.add(vo);
            }
            return AjaxResult.success(rows);
        } catch (Throwable t) {
            return AjaxResult.error("配置清单查询失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult load(String fileId) {
        try {
            SoysConfigFile f = requireFile(fileId);
            File file = resolve(f);
            if (!file.isFile()) {
                return AjaxResult.error("配置文件不存在，请先启动一次 SOYSHTTPOverMC 生成默认配置: "
                        + file.getPath());
            }
            return AjaxResult.success(ConfigUtil.toMap(PlatformYaml.load(file)));
        } catch (Throwable t) {
            return AjaxResult.error("配置读取失败：" + t.getMessage());
        }
    }

    @Override
    public AjaxResult save(String fileId, Map<String, Object> data) {
        try {
            SoysConfigFile f = requireFile(fileId);
            File file = resolve(f);
            if (!file.isFile()) {
                return AjaxResult.error("配置文件不存在，请先启动一次 SOYSHTTPOverMC 生成默认配置: "
                        + file.getPath());
            }
            if (data == null) {
                return AjaxResult.error("配置内容为空");
            }
            PlatformYaml.save(ConfigUtil.toYaml(data), file);
            SoysConfigSaveVo resp = SoysConfigSaveVo.of(f.getFile(),
                    "已保存。执行 /soyshttp reload 热重载生效（部分网关项需重启服务器）");
            return AjaxResult.success(resp);
        } catch (Throwable t) {
            return AjaxResult.error("配置保存失败：" + t.getMessage());
        }
    }

    // ===== 内部 =====

    private static SoysConfigFile requireFile(String fileId) {
        SoysConfigFile f = SoysConfigFile.byId(fileId);
        if (f == null) {
            throw new IllegalArgumentException("未知配置文件 id: " + fileId);
        }
        return f;
    }

    /** 主插件数据目录下解析目标文件。 */
    private static File resolve(SoysConfigFile f) {
        Plugin soys = Bukkit.getPluginManager().getPlugin(SOYS_PLUGIN);
        if (soys == null) {
            throw new IllegalStateException("SOYSHTTPOverMC 主插件未加载");
        }
        return new File(soys.getDataFolder(), f.getFile());
    }
}
