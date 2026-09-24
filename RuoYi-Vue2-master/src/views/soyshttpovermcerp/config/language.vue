<template>
  <config-layout title="国际化" :help-map="helpMap" file="language.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" >

      <el-card shadow="never" class="mb">
        <div slot="header">语言设置</div>
        <el-form label-width="180px" size="small">
          <el-form-item label="当前语言">
            <el-select v-model="model.language.current" style="width:200px">
              <el-option label="zh_cn 简体中文" value="zh_cn" />
              <el-option label="en_us English" value="en_us" />
            </el-select>
            <div class="hint">可用 /soyshttp lang &lt;代码&gt; 切换并自动持久化。</div>
          </el-form-item>
          <el-form-item label="加载策略">
            <el-select v-model="model.language.rule" style="width:260px">
              <el-option label="internationalization（默认，en_us 保底）" value="internationalization" />
              <el-option label="clear（清空后重载）" value="clear" />
              <el-option label="overlay（覆盖同键，其余保留）" value="overlay" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">额外语言源（{{ (model.language.sources||[]).length }} 条）</div>
        <el-button size="mini" type="text" icon="el-icon-plus" @click="addSource">添加语言源</el-button>
        <el-table :data="model.language.sources" size="mini" border style="margin-top:8px">
          <el-table-column label="名称" width="140">
            <template slot-scope="s"><el-input v-model="s.row.name" size="mini" /></template>
          </el-table-column>
          <el-table-column label="描述">
            <template slot-scope="s"><el-input v-model="s.row.description" size="mini" /></template>
          </el-table-column>
          <el-table-column label="绑定语言" width="120">
            <template slot-scope="s"><el-input v-model="s.row.language" size="mini" placeholder="留空用 {0} 占位" /></template>
          </el-table-column>
          <el-table-column label="来源 / URL" min-width="200">
            <template slot-scope="s"><el-input v-model="s.row.source" size="mini" /></template>
          </el-table-column>
          <el-table-column label="操作" width="70">
            <template slot-scope="s">
              <el-button type="text" size="mini" style="color:#f56c6c" @click="model.language.sources.splice(s.$index,1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="hint" style="margin-top:8px">language 留空时 source 必须含 {0} 占位符（加载时替换为当前语言代码）；来源可为文件/文件夹/网络 URL。</div>
      </el-card>
  </config-layout>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigLayout from '../components/ConfigLayout.vue'

export default {
  name: 'SettingsLanguage',
  mixins: [configPage],
  components: { ConfigLayout },
  data() {
    return { fileId: 'language' }
  },
  methods: {
    addSource() {
      if (!Array.isArray(this.model.language.sources)) this.$set(this.model.language, 'sources', [])
      this.model.language.sources.push({ name: '', description: '', language: '', source: '' })
    }
  }
}
</script>

<style scoped>
.mb { margin-bottom: 14px; }
.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }
</style>
