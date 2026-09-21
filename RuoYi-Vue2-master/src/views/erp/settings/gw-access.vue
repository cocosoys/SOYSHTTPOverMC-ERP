<template>
  <div class="app-container" style="padding:0" v-loading="loading">
    <config-top-bar title="访问限制器" file="gateway/policies/access-limiter.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" />
    <div style="padding:16px 20px; max-width:960px">
      <el-card shadow="never">
        <div slot="header">固定窗口访问限制（{{ (model['path-patterns']||[]).length }} 条）</div>
        <el-form label-width="100px" size="small" style="margin-bottom:10px">
          <el-form-item label="启用">
            <el-switch v-model="model.enabled" />
            <div class="hint">超出窗口内 limit 次 → 429 + Retry-After；被动刷新（无后台定时任务）。</div>
          </el-form-item>
        </el-form>
        <el-table :data="model['path-patterns']" size="mini" border>
          <el-table-column label="名称" width="150">
            <template slot-scope="s"><el-input v-model="s.row.name" size="mini" /></template>
          </el-table-column>
          <el-table-column label="描述">
            <template slot-scope="s"><el-input v-model="s.row.description" size="mini" /></template>
          </el-table-column>
          <el-table-column label="维度" width="120">
            <template slot-scope="s">
              <el-select v-model="s.row.scope" size="mini">
                <el-option label="ip" value="ip" />
                <el-option label="key" value="key" />
                <el-option label="path" value="path" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="上限" width="90">
            <template slot-scope="s"><el-input-number v-model="s.row.limit" size="mini" :min="1" controls-position="right" style="width:90px" /></template>
          </el-table-column>
          <el-table-column label="窗口(秒)" width="110">
            <template slot-scope="s"><el-input-number v-model="s.row['window-seconds']" size="mini" :min="1" controls-position="right" style="width:100px" /></template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template slot-scope="s">
              <el-button type="text" size="mini" style="color:#f56c6c" @click="model['path-patterns'].splice(s.$index,1)">删</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button size="mini" type="text" icon="el-icon-plus" style="margin-top:8px" @click="addRow">添加规则</el-button>
      </el-card>
    </div>
  </div>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigTopBar from '../components/ConfigTopBar.vue'

export default {
  name: 'SettingsGwAccess',
  mixins: [configPage],
  components: { ConfigTopBar },
  data() { return { fileId: 'gw-access' } },
  methods: {
    addRow() {
      if (!Array.isArray(this.model['path-patterns'])) this.$set(this.model, 'path-patterns', [])
      this.model['path-patterns'].push({ name: '', description: '', scope: 'ip', limit: 50, 'window-seconds': 3600 })
    }
  }
}
</script>
