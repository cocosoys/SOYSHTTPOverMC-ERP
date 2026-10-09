<template>
  <config-layout title="网关总开关" :help-map="helpMap" file="gateway/config.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" >
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
          <el-form-item label="Swagger 自文档">
            <el-switch v-model="model.swagger.enabled" />
            <div class="hint">开启后访问 /swagger（跳转 /swagger/ui/index.html）或 /swagger/api-docs（JSON）；仅已登录且为服务器 OP 的玩家可访问。</div>
          </el-form-item>
        </el-form>
      </el-card>
  </config-layout>
</template>

<script>
import configPage from '../../mixins/configPage'
import ConfigLayout from '../../components/ConfigLayout.vue'

export default {
  name: 'SettingsGwConfig',
  mixins: [configPage],
  components: { ConfigLayout },
  data() {
    return {
      fileId: 'gw-config',
      helpMap: {
        '启用安全网关': '网关总开关。关闭后所有策略（鉴权/限流/白名单/TLS）均不生效。',
        'API 全局前缀': '注解式 API 全局前缀，始终生效。@GetMapping("/ping") 一律映射为 /api/ping。修改需重启服务器。',
        '事件调试': 'true 时在控制台打印网关事件（请求进入/拒绝/完成/凭证下发/API注册），便于排查。',
        'Swagger 自文档': 'Swagger 自文档开关（gateway/config.yml → swagger.enabled，默认 true=开启）：开启后提供 OpenAPI 3.0 自文档与 swagger-ui（独立命名空间 /swagger），访问 /swagger 跳转 /swagger/ui/index.html，/swagger/api-docs 返回 JSON 文档。访问控制：仅「已登录且为服务器 OP 的玩家」可访问（未登录 401、非 OP 403）；机器凭证（X-API-Key）因无法解析玩家名同样被拒。设为 false 则彻底关闭，访问一律 404。修改后执行 /soyshttp reload 热重载生效。'
      }
    }
  }
}
</script>

<style scoped>.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }</style>
