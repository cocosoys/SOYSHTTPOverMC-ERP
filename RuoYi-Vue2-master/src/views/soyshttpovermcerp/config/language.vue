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
    return {
      fileId: 'language',
      helpMap: {
        '当前语言': '当前使用的语言代码（zh_cn 简体中文 / en_us English）。可用 /soyshttp lang <代码> 切换并自动持久化。切换后插件所有面向玩家的提示消息、命令输出、错误提示都会随之改变。',
        '加载策略': '语言文件加载时的合并策略：internationalization（默认）= 先加载 en_us 作为保底基础，再叠加当前语言的翻译，缺失的 key 自动回退到英文；clear = 清空所有已有翻译后重新加载，不会保留其他语言；overlay = 只覆盖同 key 的翻译，其余已加载的保留。一般保持默认即可。',
        '额外语言源': '额外的语言文件来源列表，用于加载第三方插件或自定义的翻译文件。每条源包含：名称（标识用）、描述、绑定语言（指定该源只加载到哪个语言，留空则用 {0} 占位符自动适配当前语言）、来源路径（可以是本地文件、文件夹或网络 URL）。支持从网络 URL 远程加载语言文件，方便集中管理多服翻译。',
        '额外语言源名称': '该语言源的标识名称，便于在日志和调试时识别是哪个源。',
        '额外语言源描述': '该语言源的说明描述，方便管理员理解这个源是做什么的。',
        '额外语言源绑定语言': '指定该源只加载到哪个语言代码（如 zh_cn）。留空时 source 路径中必须包含 {0} 占位符，插件会自动把 {0} 替换为当前语言代码，实现一个源适配所有语言。',
        '额外语言源路径': '语言文件的加载路径。可以是：①本地文件路径（相对于插件目录）；②本地文件夹路径（自动扫描文件夹下所有 .yml）；③网络 URL（http/https 开头，远程下载语言文件）。'
      }
    }
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
