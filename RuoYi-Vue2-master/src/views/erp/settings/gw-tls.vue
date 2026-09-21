<template>
  <div class="app-container" style="padding:0" v-loading="loading">
    <config-top-bar title="TLS 强制" file="gateway/policies/tls.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" />
    <div style="padding:16px 20px; max-width:720px">
      <el-card shadow="never">
        <div slot="header">强制 HTTPS</div>
        <el-form label-width="160px" size="small">
          <el-form-item label="启用">
            <el-switch v-model="model.enabled" />
            <div class="hint">明文 HTTP → 426 Upgrade Required，Location 指向同端口 https。</div>
          </el-form-item>
          <el-form-item label="跳转主机名">
            <el-input v-model="model.host" placeholder="留空=用 gateway/https.yml 的 host" />
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
  name: 'SettingsGwTls',
  mixins: [configPage],
  components: { ConfigTopBar },
  data() { return { fileId: 'gw-tls' } }
}
</script>

<style scoped>.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }</style>
