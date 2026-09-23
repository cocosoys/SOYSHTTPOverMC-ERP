<template>
  <config-layout title="IP 白/黑名单" file="gateway/policies/ip-allowlist.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" >
      <el-card shadow="never">
        <div slot="header">IP 访问控制</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="启用">
            <el-switch v-model="model.enabled" />
          </el-form-item>
          <el-form-item label="默认策略">
            <el-radio-group v-model="model.default">
              <el-radio label="allow">allow（列表=黑名单，其余放行）</el-radio>
              <el-radio label="deny">deny（列表=白名单，仅命中放行）</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="信任前置代理">
            <el-switch v-model="model['trust-proxy']" />
            <div class="hint">优先取 X-Forwarded-For 首个 IP。</div>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card shadow="never" style="margin-top:14px">
        <div slot="header">IP / CIDR 列表</div>
        <div v-for="(ip,i) in (model.list||[])" :key="i" class="srow">
          <el-input :value="ip" size="small" @change="v => $set(model.list, i, v)" placeholder="如 127.0.0.1 或 192.168.1.0/24" />
          <el-button type="text" size="mini" icon="el-icon-delete" @click="model.list.splice(i,1)" />
        </div>
        <el-button size="mini" type="text" icon="el-icon-plus" @click="model.list.push('')">添加一行</el-button>
      </el-card>
  </config-layout>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigLayout from '../components/ConfigLayout.vue'

export default {
  name: 'SettingsGwAllow',
  mixins: [configPage],
  components: { ConfigLayout },
  data() { return { fileId: 'gw-allow' } }
}
</script>

<style scoped>
.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }
.srow { display: flex; align-items: center; margin-bottom: 6px; }
.srow .el-input { flex: 1; }
</style>
