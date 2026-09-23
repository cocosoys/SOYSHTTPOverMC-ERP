<template>
  <config-layout title="令牌桶限流" file="gateway/policies/rate-limit.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" >
      <el-card shadow="never">
        <div slot="header">令牌桶策略</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="启用限流">
            <el-switch v-model="model.enabled" />
          </el-form-item>
          <el-form-item label="限流维度">
            <el-radio-group v-model="model.scope">
              <el-radio label="ip">按 IP</el-radio>
              <el-radio label="key">按 API Key</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="每分钟补充（rpm）">
            <el-input-number v-model="model.rpm" :min="1" />
          </el-form-item>
          <el-form-item label="突发上限（burst）">
            <el-input-number v-model="model.burst" :min="1" />
          </el-form-item>
          <div class="hint">超限返回 429 + Retry-After。</div>
        </el-form>
      </el-card>
  </config-layout>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigLayout from '../components/ConfigLayout.vue'

export default {
  name: 'SettingsGwRate',
  mixins: [configPage],
  components: { ConfigLayout },
  data() { return { fileId: 'gw-rate' } }
}
</script>

<style scoped>.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }</style>
