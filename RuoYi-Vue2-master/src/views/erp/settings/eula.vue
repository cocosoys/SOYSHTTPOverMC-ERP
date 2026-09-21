<template>
  <div class="app-container" style="padding:0" v-loading="loading">
    <config-top-bar title="用户协议 (EULA)" file="EULA.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" />
    <div style="padding:16px 20px; max-width:720px">
      <el-card shadow="never">
        <el-alert type="info" :closable="false" style="margin-bottom:16px">
          根据 Mojang EULA，运行本插件代表您已接受 Minecraft EULA。此开关仅作记录，关闭时插件不会启用 Web 服务。
        </el-alert>
        <el-form label-width="160px" size="small">
          <el-form-item label="同意 EULA">
            <el-switch v-model="model.eula" active-text="已同意" inactive-text="未同意" />
            <div class="hint">对应 EULA.yml 中 eula: true / false。</div>
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
  name: 'SettingsEula',
  mixins: [configPage],
  components: { ConfigTopBar },
  data() {
    return { fileId: 'eula' }
  }
}
</script>

<style scoped>
.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }
</style>
