<template>
  <div class="app-container" style="padding:0" v-loading="loading">
    <config-top-bar title="会话令牌颁发器" file="gateway/issuers/session-token.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" />
    <div style="padding:16px 20px; max-width:720px">
      <el-alert type="info" :closable="false" style="margin-bottom:14px">
        启用后 /soyshttp key &lt;subject&gt; 下发会话令牌；令牌为内存态，服务重启后全部失效。
      </el-alert>
      <el-card shadow="never">
        <el-form label-width="180px" size="small">
          <el-form-item label="启用颁发器">
            <el-switch v-model="model.enabled" />
          </el-form-item>
          <el-form-item label="Cookie 名">
            <el-input v-model="model['cookie-name']" />
          </el-form-item>
          <el-form-item label="有效期（秒）">
            <el-input-number v-model="model['ttl-seconds']" :min="60" />
            <div class="hint">默认 86400 = 24 小时。</div>
          </el-form-item>
          <el-form-item label="时钟容差（秒）">
            <el-input-number v-model="model['clock-skew-seconds']" :min="0" />
            <div class="hint">跨服时钟偏移容忍，防 NTP 未对齐误拒。</div>
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
  name: 'SettingsGwSession',
  mixins: [configPage],
  components: { ConfigTopBar },
  data() { return { fileId: 'gw-session' } }
}
</script>

<style scoped>.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }</style>
