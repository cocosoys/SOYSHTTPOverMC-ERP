<template>
  <div class="app-container" style="padding:0" v-loading="loading">
    <config-top-bar title="认证鉴权" file="gateway/policies/auth.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" />
    <div style="padding:16px 20px; max-width:860px">
      <el-card shadow="never" class="mb">
        <div slot="header">基础</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="启用鉴权">
            <el-switch v-model="model.enabled" />
          </el-form-item>
          <el-form-item label="API Key 请求头">
            <el-input v-model="model.header" />
          </el-form-item>
          <el-form-item label="登录提供者">
            <el-input v-model="model['login-provider']" placeholder="留空=自动选第一个可用登录插件；无登录插件=免密模式" />
          </el-form-item>
          <el-form-item label="保护路径">
            <el-input :value="(model.paths||[]).join(', ')" size="small"
                      @change="v => $set(model, 'paths', v.split(/[,，]/).map(x=>x.trim()).filter(Boolean))"
                      placeholder="逗号分隔；空=全部；/api/* 前缀" />
          </el-form-item>
          <el-form-item label="豁免路径">
            <el-input :value="(model.exempt||[]).join(', ')" size="small"
                      @change="v => $set(model, 'exempt', v.split(/[,，]/).map(x=>x.trim()).filter(Boolean))" />
            <div class="hint">公开端点，命中跳过鉴权。如 /ping、/auth/login、/homepage/*。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">接受的凭证来源</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="X-API-Key 头"><el-switch v-model="model.accept.header" /></el-form-item>
          <el-form-item label="Authorization: Bearer"><el-switch v-model="model.accept.bearer" /></el-form-item>
          <el-form-item label="Authorization: Basic"><el-switch v-model="model.accept.basic" /></el-form-item>
          <el-form-item label="Cookie（颁发器校验）"><el-switch v-model="model.accept.cookie" /></el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">自动登录</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="记住我总开关">
            <el-switch v-model="model.auto.login.ttl.enable" />
            <div class="hint">登录时勾选"记住我"则签发长期设备 Cookie，自动登录。</div>
          </el-form-item>
          <el-form-item label="记住我有效期（天）">
            <el-input-number v-model="model.auto.login.ttl.activetime" :min="1" />
          </el-form-item>
          <el-form-item label="IP 匹配自动登录">
            <el-switch v-model="model.auto.login.ip.enabled" />
            <div class="hint">旧行为：游戏在线且网页 IP==游戏 IP 即放行。同 NAT 会误伤，仅局域网建议开。</div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">X-API-Key 降级</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="local 不可用时放行全部">
            <el-switch v-model="model['api-key']['local-fallback-all']" />
            <div class="hint">false=安全优先（403）；true=fail-open 退化为全权限，仅信任内网使用。</div>
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
  name: 'SettingsGwAuth',
  mixins: [configPage],
  components: { ConfigTopBar },
  data() { return { fileId: 'gw-auth' } }
}
</script>

<style scoped>.mb{margin-bottom:14px}.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }</style>
