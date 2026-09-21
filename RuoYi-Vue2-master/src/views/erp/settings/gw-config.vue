<template>
  <div class="app-container" style="padding:0" v-loading="loading">
    <config-top-bar title="网关总开关" file="gateway/config.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" />
    <div style="padding:16px 20px; max-width:720px">
      <el-card shadow="never">
        <el-form label-width="180px" size="small">
          <el-form-item label="启用安全网关">
            <el-switch v-model="model.enabled" />
            <div class="hint">总开关；关闭后所有策略（鉴权/限流/白名单/TLS）均不生效。</div>
          </el-form-item>
          <el-form-item label="API 全局前缀">
            <el-input v-model="model['api-prefix']" />
            <div class="hint">@GetMapping("/ping") 一律映射为 /api/ping；修改需重启服务器。</div>
          </el-form-item>
          <el-form-item label="事件调试">
            <el-switch v-model="model['debug-events']" />
            <div class="hint">在控制台打印网关事件（请求进入/拒绝/完成/凭证下发/API 注册）。</div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigTopBar from '../components/ConfigTopBar.vue'

export default {
  name: 'SettingsGwConfig',
  mixins: [configPage],
  components: { ConfigTopBar },
  data() { return { fileId: 'gw-config' } }
}
</script>

<style scoped>.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }</style>
