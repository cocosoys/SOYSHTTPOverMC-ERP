<template>
  <config-layout title="核心配置" file="config.yml" :dirty="dirty" :loading="loading" :saving="saving"
    @reload="reload" @save="save">

      <el-card shadow="never" class="mb" id="sec-upload">
        <div slot="header">数据贡献 (upload)</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="启用数据贡献">
            <el-switch v-model="model.upload.enabled" />
            <div class="hint">将服务器公网地址（IP:端口）匿名贡献给数据服务器，仅用于用量/地域统计。有顾虑请关闭。</div>
          </el-form-item>
          <el-form-item label="统计上报服务器">
            <el-input v-model="model.upload.server" />
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-channel">
        <div slot="header">插件消息通道</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="channel">
            <el-input v-model="model.channel" />
            <div class="hint">Bukkit 插件消息通道名，默认 httpproxy:main。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-mc">
        <div slot="header">MC 服务器地址与端口</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="host">
            <el-input v-model="model.mc.host" placeholder="留空=自动取 server.properties server-ip" />
          </el-form-item>
          <el-form-item label="port">
            <el-input-number v-model="model.mc.port" :min="0" />
            <div class="hint">0=自动取 server.properties server-port；必须等于游戏对外端口。</div>
          </el-form-item>
          <el-form-item label="public-host">
            <el-input v-model="model.mc['public-host']" placeholder="群组服对外公布的公网地址，留空=沿用 host" />
          </el-form-item>
          <el-form-item label="public-port">
            <el-input-number v-model="model.mc['public-port']" :min="0" />
          </el-form-item>
          <el-form-item label="信任前置代理">
            <el-switch v-model="model.mc['trust-proxy']" />
            <div class="hint">读取 X-Forwarded-For 恢复真实访客 IP（用于限流/白名单）。后端可被直连时建议关。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-proxy">
        <div slot="header">群组服（BungeeCord / Velocity）</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="server-name">
            <el-input v-model="model.proxy['server-name']" placeholder="本服在代理中的唯一名称，独立服留空" />
          </el-form-item>
          <el-form-item label="proxy-address">
            <el-input v-model="model.proxy['proxy-address']" placeholder="127.0.0.1:代理端口；留空=直连后端，跨服功能失效" />
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-sniffer">
        <div slot="header">同端口 HTTP 嗅探器</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="启用嗅探">
            <el-switch v-model="model.sniffer.enabled" />
            <div class="hint">访问端口=MC 端口，在 Spigot 监听上嗅探 HTTP，MC/HTTP/HTTPS 三协议共用端口。</div>
          </el-form-item>
          <el-form-item label="请求体上限（字节）">
            <el-input-number v-model="model.sniffer['max-body-bytes']" :step="1048576" />
            <div class="hint">超过返回 413，默认 8MB。</div>
          </el-form-item>
          <el-form-item label="HTTP 并发上限">
            <el-input-number v-model="model.sniffer['http-concurrency']" :min="1" />
            <div class="hint">同时等待 HTTP 后端的请求数，满了新请求直接 503。日常 4~8。</div>
          </el-form-item>
          <el-form-item label="等待队列容量">
            <el-input-number v-model="model.sniffer['http-queue-size']" :min="0" />
          </el-form-item>
          <el-form-item label="keep-alive 空闲秒">
            <el-input-number v-model="model.sniffer['keep-alive-idle-seconds']" :min="0" />
            <div class="hint">长连接空闲 N 秒后关闭；调大可减少连接重建与证书重校验。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-backend">
        <div slot="header">HTTP 后端传输模式</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="传输模式">
            <el-select v-model="model['http-backend'].mode">
              <el-option label="direct 直接调用（延迟最低）" value="direct" />
              <el-option label="netty-eventloop（推荐，默认）" value="netty-eventloop" />
              <el-option label="memory-queue 内存队列（背压）" value="memory-queue" />
              <el-option label="standalone-server 独立端口服务器" value="standalone-server" />
            </el-select>
          </el-form-item>
          <el-form-item label="netty-eventloop 线程数">
            <el-input-number v-model="model['http-backend']['netty-eventloop'].threads" :min="1"
              :disabled="model['http-backend'].mode !== 'netty-eventloop'" />
          </el-form-item>
          <el-form-item label="memory-queue 队列容量">
            <el-input-number v-model="model['http-backend']['memory-queue'].capacity" :min="1"
              :disabled="model['http-backend'].mode !== 'memory-queue'" />
          </el-form-item>
          <el-form-item label="memory-queue worker 数">
            <el-input-number v-model="model['http-backend']['memory-queue'].workers" :min="1"
              :disabled="model['http-backend'].mode !== 'memory-queue'" />
          </el-form-item>
          <el-form-item label="standalone 监听地址">
            <el-input v-model="model['http-backend']['standalone-server'].host"
              :disabled="model['http-backend'].mode !== 'standalone-server'" />
          </el-form-item>
          <el-form-item label="standalone 监听端口">
            <el-input-number v-model="model['http-backend']['standalone-server'].port" :min="1"
              :disabled="model['http-backend'].mode !== 'standalone-server'" />
            <div class="hint">需与 MC 端口不同。仅 standalone-server 模式生效。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-log">
        <div slot="header">日志管控</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="日志级别">
            <el-select v-model="model.log.level" style="width:200px">
              <el-option v-for="l in ['OFF','ERROR','WARN','INFO','DEBUG','TRACE']" :key="l" :label="l" :value="l" />
            </el-select>
            <div class="hint">热重载生效，无需重启。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-perm">
        <div slot="header">权限判断组合</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="权限插件 providers">
            <el-tag v-for="(p,i) in model.permission.providers" :key="i" closable size="mini" style="margin-right:6px"
                     @close="model.permission.providers.splice(i,1)">{{ p }}</el-tag>
            <el-button size="mini" type="text" icon="el-icon-plus" @click="addProvider">添加 provider</el-button>
            <div class="hint">留空=自动加入所有已安装插件；可选 luckperms / permsex / essentials / essentialx / local。</div>
          </el-form-item>
          <el-form-item label="离线玩家降级">
            <el-select v-model="model.permission['offline-fallback']" style="width:200px">
              <el-option label="op-only 仅 OP（默认）" value="op-only" />
              <el-option label="local 查本地权限表" value="local" />
              <el-option label="false 全部拒绝（最严格）" value="false" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-storage">
        <div slot="header">数据存储</div>
        <el-form label-width="180px" size="small">
          <el-divider content-position="left">YAML 后端（零依赖）</el-divider>
          <el-form-item label="启用 YAML">
            <el-switch v-model="model.storage.backends.yaml.enabled" />
          </el-form-item>
          <el-form-item label="YAML 文件目录">
            <el-input v-model="model.storage.backends.yaml.file" />
          </el-form-item>
          <el-form-item label="保存时备份">
            <el-switch v-model="model.storage.backends.yaml['backup-on-save']" />
          </el-form-item>

          <el-divider content-position="left">SQLite 后端</el-divider>
          <el-form-item label="启用 SQLite">
            <el-switch v-model="model.storage.backends.sqlite.enabled" />
          </el-form-item>
          <el-form-item label="数据库文件">
            <el-input v-model="model.storage.backends.sqlite.file" />
          </el-form-item>
          <el-form-item label="表前缀">
            <el-input v-model="model.storage.backends.sqlite['table-prefix']" />
          </el-form-item>

          <el-divider content-position="left">MySQL 后端</el-divider>
          <el-form-item label="启用 MySQL">
            <el-switch v-model="model.storage.backends.mysql.enabled" />
          </el-form-item>
          <el-form-item label="JDBC URL">
            <el-input v-model="model.storage.backends.mysql.url" />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="model.storage.backends.mysql.username" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="model.storage.backends.mysql.password" show-password />
          </el-form-item>
          <el-form-item label="表前缀">
            <el-input v-model="model.storage.backends.mysql['table-prefix']" />
          </el-form-item>

          <el-divider content-position="left">跨服同步</el-divider>
          <el-form-item label="开启跨服同步">
            <el-switch v-model="model.storage['cross-server']" />
            <div class="hint">需所有实例 MySQL 指向同一库；YAML 后端无法跨实例共享。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb" id="sec-auto">
        <div slot="header">自动运维</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="总开关">
            <el-switch v-model="model['auto'].ops.enabled" />
          </el-form-item>
          <el-form-item label="自动初始化">
            <el-switch v-model="model['auto'].ops.init" />
            <div class="hint">默认文件复制 + init.sql + 种子数据（表空才插入）。</div>
          </el-form-item>
          <el-form-item label="自动更新">
            <el-switch v-model="model['auto'].ops.update" />
          </el-form-item>
          <el-form-item label="失败策略">
            <el-select v-model="model['auto'].ops.fail" style="width:200px">
              <el-option label="disable 仅禁用失败插件（默认）" value="disable" />
              <el-option label="warn 跳过并告警" value="warn" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-card>

  </config-layout>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigLayout from '../components/ConfigLayout.vue'

export default {
  name: 'SettingsMain',
  mixins: [configPage],
  components: { ConfigLayout },
  data() {
    return { fileId: 'config' }
  },
  methods: {
    addProvider() {
      this.$prompt('provider 名', '添加权限插件', { inputValue: 'luckperms' }).then(({ value }) => {
        this.model.permission.providers.push(value)
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb { margin-bottom: 14px; }
.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }
</style>
