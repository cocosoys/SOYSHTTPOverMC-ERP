# SOYSHTTPOverMC-ERP

> SOYSHTTPOverMC 的 ERP 管理台扩展插件 —— 以 MCERP 管理台为宿主，提供游戏用户 / 权限组 / X-API-KEY / 插件配置 / 语言包的可视化管理界面。

[![version](https://img.shields.io/badge/version-1.0.0-brightgreen.svg)](https://github.com/cocosoys/SOYSHTTPOverMC-ERP/releases)
[![java](https://img.shields.io/badge/java-8-blue.svg)](#构建)
[![platform](https://img.shields.io/badge/spigot-1.12.2-lightgrey.svg)](#兼容性)

---

## 项目定位

本插件是一个 **MCERP 扩展（Expansion）**，本身不独立提供 HTTP 服务或 Web 管理台，而是挂载在 [MCERP](https://github.com/cocosoys/MCERP) 主管理台之下，通过继承 `McerpExpansion` 声明式注册菜单与接口，由主插件 [SOYSHTTPOverMC](https://github.com/cocosoys/SOYSHTTPOverMC) 提供运行时能力：

```
浏览器
  │  HTTPS
  ▼
SOYSHTTPOverMC（主插件：HTTP 网关 + 注解式 API + Web 资源托管 + 本地权限存储）
  │
  ├─ /api/plugins/SOYSHTTPOverMC-ERP/erp/*   ← 本插件注册的业务端点
  ├─ /web/plugins/SOYSHTTPOverMC-ERP/*      ← 本插件 dist 前端产物
  ▼
MCERP（管理台宿主：登录鉴权 / 菜单路由 / 侧边栏 / TagsView）
  │
  ▼
SOYSHTTPOverMC-ERP（本插件：声明菜单树 + 控制器转发，无业务实现层重复）
```

- **后端**：Java 8，`com.github.cocosoys.mc:SOYSHTTPOverMC-ERP:1.0-SNAPSHOT`，shade 打包为单个 jar。
- **前端**：Vue 2.6 + Element UI 2.15（RuoYi-Vue2 剪裁版），`npm run build:prod` 产物打入 `src/main/resources/dist`，随 jar 分发，无需独立部署 Web 服务器。

---

## 功能清单

### 1. 游戏用户列表（菜单 `user`）

复用主插件本地权限表 `soys_perm_user`，列表展示用户全部字段，并额外展示：

- 是否绑定 X-API-KEY（`hasKey` + `keyFingerprint` 指纹）
- 所属权限组（`groups` + `groupsDisplay` 组名展示）

行内操作按钮：

| 按钮 | 端点 | 权限节点 | 作用 |
|---|---|---|---|
| 权限 | `GET /erp/user/perms?uuid=` | `soyshttpovermc:erp:user:perm:list` | 查看用户个人权限（非继承自组） |
| 权限保存 | `POST /erp/user/perm/add`、`POST /erp/user/perm/remove` | `soyshttpovermc:erp:user:perm:add` / `:remove` | 为用户单独增删权限节点 |
| 权限组 | `GET /erp/user/groups?uuid=` | `soyshttpovermc:erp:user:group:list` | 查看用户所属权限组 |
| 权限组保存 | `POST /erp/user/groups` | `soyshttpovermc:erp:user:group:edit` | 修改用户所属权限组 |
| 分配 X-API-KEY | `POST /erp/user/assign-key` | `soyshttpovermc:erp:user:apikey:assign` | 直接为该用户生成新 X-API-KEY 并绑定 |
| 用户级权限延期 | `POST /erp/user/expiry` | `soyshttpovermc:erp:user:perm:renew` | 延长用户个人权限到期时间，或设为永久 |

列表查询：`GET /erp/user/list?pageNum=&pageSize=&keyword=`（若依 `TableDataInfo` 分页契约）。

### 2. 权限组列表（菜单 `group`）

操作对象为主插件权限组表，扁平化展示（无父子层级），每行展示成员数 `memberCount` 与权限数 `permCount`。

| 功能 | 端点 | 权限节点 |
|---|---|---|
| 组列表分页 | `GET /erp/group/list` | `soyshttpovermc:erp:group:list` |
| 新增组 | `POST /erp/group/create` | `soyshttpovermc:erp:group:add` |
| 编辑组 | `POST /erp/group/update` | `soyshttpovermc:erp:group:edit` |
| 删除组 | `POST /erp/group/remove` | `soyshttpovermc:erp:group:remove` |
| 组权限列表 | `GET /erp/group/perms?id=` | `soyshttpovermc:erp:group:perm:list` |
| 组权限增 / 删 | `POST /erp/group/perm/add`、`POST /erp/group/perm/remove` | `soyshttpovermc:erp:group:perm:add` / `:remove` |
| **整组权限复制** | `POST /erp/group/perm/copy` | `soyshttpovermc:erp:group:perm:copy` |
| **按来源清空权限** | `POST /erp/group/perm/remove-by` | `soyshttpovermc:erp:group:perm:clear` |
| 组成员列表 | `GET /erp/group/members?id=` | `soyshttpovermc:erp:group:member:list` |
| 组成员增 / 删 | `POST /erp/group/member/add`、`POST /erp/group/member/remove` | `soyshttpovermc:erp:group:member:add` / `:remove` |

其中"整组权限复制"支持在编辑组权限时直接勾选另一个现有权限组，将其全部权限一键并入当前组；"按来源清空"则按来源组批量移除，实现组间权限的整体迁移。

### 3. APIKEY 管理（菜单 `apikey`）

操作主插件独立出来的 `soys_apikey` 表，列表展示 KEY 全字段；已绑定玩家时展示玩家名称。

| 功能 | 端点 | 权限节点 |
|---|---|---|
| KEY 列表分页 | `GET /erp/apikey/list` | `soyshttpovermc:erp:apikey:list` |
| 生成 KEY | `POST /erp/apikey/generate` | `soyshttpovermc:erp:apikey:add` |
| 启用 / 停用 | `POST /erp/apikey/toggle` | `soyshttpovermc:erp:apikey:edit` |
| 过期时间设置 | `POST /erp/apikey/expiry` | `soyshttpovermc:erp:apikey:expiry` |
| 绑定 / 解绑玩家 | `POST /erp/apikey/bind`、`POST /erp/apikey/unbind` | `soyshttpovermc:erp:apikey:bind` / `:unbind` |
| 删除 KEY | `POST /erp/apikey/remove` | `soyshttpovermc:erp:apikey:remove` |
| KEY 权限列表 / 增 / 删 | `GET /erp/apikey/perms?keyId=`、`POST /erp/apikey/perm/add`、`POST /erp/apikey/perm/remove` | `soyshttpovermc:erp:apikey:perm:list` / `:add` / `:remove` |

### 4. 语言管理（菜单 `lang`）

可视化编辑主插件 `plugins/SOYSHTTPOverMC/language/` 下的国际化语言文件：

- `GET /erp/lang/list`：列出全部可用语言文件（`soyshttpovermc:erp:lang:list`）
- `GET /erp/lang/entries?file=`：读取某语言文件全部 key-value 条目（`soyshttpovermc:erp:lang:query`）
- `POST /erp/lang/save?file=`：批量保存条目（`soyshttpovermc:erp:lang:edit`），支持按值模糊查找

### 5. 插件配置（目录 `config`）

不内嵌 YAML 文本编辑器，而是针对每个配置文件生成表单化页面（`ConfigLayout`：左侧电梯导航 + 右侧帮助说明气泡，顶部固定 `ConfigTopBar`）。读写**全部复用主插件平台层**：`PlatformYaml.load/save`（统一处理各版本编码差异）+ `ConfigUtil.toMap/toYaml`（YAML ↔ Map 递归转换），本插件不做任何文件层解析，保存后提示执行 `/soyshttp reload` 热重载生效。

| 菜单 | 对应文件（`plugins/SOYSHTTPOverMC/` 下） | 说明 |
|---|---|---|
| 核心配置 | `config.yml` | 服务器地址/端口、嗅探器、HTTP 后端模式、存储后端等 |
| 国际化 | `language.yml` | 当前语言 / 加载策略 / 额外语言源 |
| 页面与资源 | `pages.yml` | 前端资源目录 / 首页 / 缓存 / 手动页面登记 |
| 使用协议 | `EULA.yml` | SOYSHTTPOverMC 使用与开发协议同意开关（zh_cn/zh_tw/en_us/ja_jp/ko_kr 五语言静态展示） |

**网关子目录**（`config/gateway/`，8 个文件级页面）：

| 菜单 | 对应文件 | 说明 |
|---|---|---|
| 网关总开关 | `gateway/config.yml` | 安全网关总开关 / API 全局前缀 / 事件调试（含 `swagger.enabled`） |
| HTTPS 设置 | `gateway/https.yml` | TLS 证书来源 / 协议版本 / 主机名 |
| 认证鉴权 | `gateway/policies/auth.yml` | 认证来源 / 保护路径 / 豁免路径 / 自动登录 / KEY 降级 |
| 令牌桶限流 | `gateway/policies/rate-limit.yml` | 按 IP / KEY 维度的 rpm / burst 限流 |
| 访问限制器 | `gateway/policies/access-limiter.yml` | 按 scope 的固定窗口访问次数限制 |
| IP 白名单 | `gateway/policies/ip-allowlist.yml` | IP / CIDR 白黑名单 |
| TLS 强制 | `gateway/policies/tls.yml` | 明文 HTTP 强制升级 HTTPS |
| 会话令牌 | `gateway/issuers/session-token.yml` | 会话令牌签发 / Cookie 名 / TTL / 时钟容差 |

配置端点：`GET /erp/config/files`（清单）、`GET /erp/config/load?file=`（读取）、`POST /erp/config/save?file=`（保存）。

配置页面内置通用组件：

- **ByteConverter**：字节单位转换器。左侧固定字节输入，右侧选单位（KB/MB/GB 等）后点转换图标双向换算，解决"文件大小 / 缓冲区字节"类配置项的手算负担。
- **电梯导航**：基于 `IntersectionObserver` 自动识别当前滚动到的配置分组并高亮，支持子分组嵌套；右侧帮助说明面板独立滚动。

---

## 权限节点

所有 API 与菜单节点统一使用 `soyshttpovermc:erp:*` 命名空间（与主插件权限前缀一致）：

| 节点 | 默认 | 作用 |
|---|---|---|
| `soyshttpovermc:erp:menu` | op | ERP 模块菜单可见 |
| `soyshttpovermc:erp:user:list` | op | 用户列表查看 |
| `soyshttpovermc:erp:user:group:list` / `:group:edit` | op | 用户权限组查看 / 修改 |
| `soyshttpovermc:erp:user:perm:list` / `:add` / `:remove` / `:renew` | op | 用户个人权限查看 / 增 / 删 / 延期 |
| `soyshttpovermc:erp:user:apikey:assign` | op | 为用户分配 X-API-KEY |
| `soyshttpovermc:erp:group:list` / `:add` / `:edit` / `:remove` | op | 权限组 CRUD |
| `soyshttpovermc:erp:group:perm:list` / `:add` / `:remove` / `:copy` / `:clear` | op | 组权限查看 / 增 / 删 / 复制 / 按源清空 |
| `soyshttpovermc:erp:group:member:list` / `:add` / `:remove` | op | 组成员查看 / 增 / 删 |
| `soyshttpovermc:erp:apikey:list` / `:add` / `:edit` / `:expiry` / `:bind` / `:unbind` / `:remove` | op | APIKEY 全量管理 |
| `soyshttpovermc:erp:apikey:perm:list` / `:add` / `:remove` | op | KEY 权限管理 |
| `soyshttpovermc:erp:config:list` / `:query` / `:edit` | op | 配置文件查看 / 读取 / 保存 |
| `soyshttpovermc:erp:lang:list` / `:query` / `:edit` | op | 语言文件查看 / 读取 / 保存 |

---

## 安装与依赖

按顺序将三个 jar 放入服务端 `plugins/` 目录：

1. `SOYSHTTPOverMC-<游戏版本>-<版本>.jar`（主插件，按服务端版本选档）
2. `MCERP-<版本>.jar`（管理台宿主）
3. `SOYSHTTPOverMC-ERP-<版本>.jar`（本插件）

首次启动主插件后，请编辑 `plugins/SOYSHTTPOverMC/EULA.yml` 将 `eula: false` 改为 `eula: true` 并重启，否则 SOYS 网关不会启用，本插件的菜单与接口也会因 API 类不可见而报错。

访问入口：MCERP 管理台登录后，在侧边栏 **"SOYS HTTP Over MC"** 模块下看到全部菜单。

---

## 构建

本工程为独立仓库，但编译期通过 Maven `system` scope 依赖同级目录中另外两个工程的产物，目录布局需与本地开发一致：

```
1.12.2/
├── SOYSHTTPOverMC/          # 主插件（提供 core shaded jar）
├── MCERP/                   # 管理台（提供 MCERP shaded jar）
└── SOYSHTTPOverMC-ERP/     # 本仓库
```

构建步骤（按顺序）：

```bash
# 1. 构建主插件（产出 SOYSHTTPOverMC/core/target/SOYSHTTPOverMC-1_12-<版本>.jar）
cd ../SOYSHTTPOverMC
mvn clean install -DskipTests

# 2. 构建 MCERP（产出 MCERP/target/MCERP-1.0-SNAPSHOT.jar）
cd ../MCERP
mvn clean package -DskipTests

# 3. 构建本插件前端
cd RuoYi-Vue2-master
npm ci
npm run build:prod
# 将 dist/* 复制覆盖到 ../src/main/resources/dist/

# 4. 打包本插件
cd ..
mvn clean package -DskipTests
```

产物：`target/SOYSHTTPOverMC-ERP-1.0-SNAPSHOT.jar`。

---

## 技术要点

- **分层**：Controller 仅声明路由与 `@ApiPermission`，业务在 `service` 接口，实现在 `impl`；VO 继承主插件实体类（如 `SoysApiKeyVo extends SoysApiKey`），不重复写字段。
- **不重复造轮子**：YAML 读写复用主插件 `PlatformYaml` / `ConfigUtil`，跟随主插件版本升级即可适配各 Spigot 版本编码差异；不内置任何独立 YAML 解析器。
- **菜单自动登记**：`SoysConfigFile.values()` 枚举驱动"插件配置"目录下 4 + 8 个文件级菜单，新增配置文件只需在枚举里加一行。
- **前端**：微前端模式（wujie）嵌入 MCERP 主应用，自身只保留业务页面，不实现登录页 / Layout / 系统管理。

## 许可证

见主插件 [SOYSHTTPOverMC EULA](https://github.com/cocosoys/SOYSHTTPOverMC)。使用即视为同意其《使用与开发协议》：禁止用于违法犯罪活动，违规责任由使用者自行承担。