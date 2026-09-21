<template>
  <div class="app-container" style="padding:0" v-loading="loading">
    <config-top-bar title="页面与资源" file="pages.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" />
    <div style="padding:16px 20px; max-width:1000px">

      <el-card shadow="never" class="mb">
        <div slot="header">Web 站点</div>
        <el-form label-width="200px" size="small">
          <el-form-item label="静态资源根目录">
            <el-input v-model="model.web.root" />
            <div class="hint">dist / 前端构建产物所在目录。</div>
          </el-form-item>
          <el-form-item label="首页路径">
            <el-input v-model="model.web.home" />
          </el-form-item>
          <el-form-item label="大文件阈值（字节）">
            <el-input-number v-model="model.web['large-file-threshold']" :step="1048576" />
          </el-form-item>
          <el-form-item label="大文件上限（字节）">
            <el-input-number v-model="model.web['large-file-max-bytes']" :step="1048576" />
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">缓存策略</div>
        <el-form label-width="200px" size="small">
          <el-form-item label="缓存总大小上限（字节）">
            <el-input-number v-model="model.web.cache['max-bytes']" :step="1048576" />
          </el-form-item>
          <el-form-item label="缓存条目数上限">
            <el-input-number v-model="model.web.cache['max-entries']" :min="1" />
          </el-form-item>
          <el-form-item label="缓存 TTL（秒）">
            <el-input-number v-model="model.web.cache['ttl-seconds']" :min="0" />
          </el-form-item>
          <el-form-item label="固定缓存（pinned）">
            <div v-for="(p,i) in model.web.cache.pinned" :key="i" class="srow">
              <el-input :value="p" size="small" @change="v => $set(model.web.cache.pinned, i, v)" />
              <el-button type="text" size="mini" icon="el-icon-delete" @click="model.web.cache.pinned.splice(i,1)" />
            </div>
            <el-button size="mini" type="text" icon="el-icon-plus" @click="model.web.cache.pinned.push('')">添加 pinned 路径</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">已登记页面（pages.page）</div>
        <el-table :data="pageList" size="mini" border>
          <el-table-column label="路径" width="140">
            <template slot-scope="s"><el-input v-model="s.row._key" size="mini" /></template>
          </el-table-column>
          <el-table-column label="显示名 nicknames">
            <template slot-scope="s">
              <el-input :value="(s.row.nicknames||[]).join(', ')" size="mini"
                        @change="v => $set(s.row, 'nicknames', v.split(/[,，]/).map(x=>x.trim()).filter(Boolean))"
                        placeholder="逗号分隔" />
            </template>
          </el-table-column>
          <el-table-column label="描述" min-width="180">
            <template slot-scope="s"><el-input v-model="s.row.description" size="mini" /></template>
          </el-table-column>
          <el-table-column label="资源文件" width="180">
            <template slot-scope="s"><el-input v-model="s.row.resource" size="mini" /></template>
          </el-table-column>
          <el-table-column label="操作" width="70">
            <template slot-scope="s">
              <el-button type="text" size="mini" style="color:#f56c6c" @click="removePage(s.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button size="mini" type="text" icon="el-icon-plus" style="margin-top:8px" @click="addPage">添加页面</el-button>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">自动路由（pages.auto）</div>
        <div class="hint" style="margin-bottom:8px">路径 → 静态资源来源；按路径自动映射文件。</div>
        <div v-for="(v,k,i) in model.pages.auto" :key="i" class="kvrow">
          <el-input :value="k" size="small" @change="nv => renameKey(model.pages.auto, k, nv)" />
          <el-input :value="v" size="small" @change="nv => $set(model.pages.auto, k, nv)" placeholder="来源路径" />
          <el-button type="text" size="mini" icon="el-icon-delete" @click="deleteKey(model.pages.auto, k)" />
        </div>
        <el-button size="mini" type="text" icon="el-icon-plus" @click="addMapEntry(model.pages.auto)">添加路由</el-button>
      </el-card>

      <el-card shadow="never" class="mb">
        <div slot="header">页面权限（pages.permissions）</div>
        <div class="hint" style="margin-bottom:8px">路径 → 权限节点数组；访问该路径需持有任一节点。</div>
        <div v-for="(v,k,i) in model.pages.permissions" :key="i" class="kvrow">
          <el-input :value="k" size="small" @change="nv => renameKey(model.pages.permissions, k, nv)" />
          <el-input :value="(v||[]).join(', ')" size="small"
                    @change="nv => $set(model.pages.permissions, k, nv.split(/[,，]/).map(x=>x.trim()).filter(Boolean))"
                    placeholder="权限节点，逗号分隔" />
          <el-button type="text" size="mini" icon="el-icon-delete" @click="deleteKey(model.pages.permissions, k)" />
        </div>
        <el-button size="mini" type="text" icon="el-icon-plus" @click="addPermEntry">添加权限映射</el-button>
      </el-card>

    </div>
  </div>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigTopBar from '../components/ConfigTopBar.vue'

export default {
  name: 'SettingsPages',
  mixins: [configPage],
  components: { ConfigTopBar },
  data() {
    return { fileId: 'pages' }
  },
  computed: {
    // pages.page 是 {路径: {nicknames,description,resource}} 对象，转成行数组便于表格编辑，保存时回写
    pageList() {
      const obj = (this.model.pages && this.model.pages.page) || {}
      return Object.keys(obj).map(k => ({ _key: k, ...obj[k] }))
    }
  },
  methods: {
    addPage() {
      if (!this.model.pages) this.$set(this.model, 'pages', {})
      if (!this.model.pages.page) this.$set(this.model.pages, 'page', {})
      this.$set(this.model.pages.page, '/new-' + Date.now(), { nicknames: [], description: '', resource: '' })
    },
    removePage(i) {
      const row = this.pageList[i]
      this.$delete(this.model.pages.page, row._key)
    },
    renameKey(obj, oldK, newK) {
      if (oldK === newK || !obj[oldK] && obj[newK]) return
      const v = obj[oldK]
      this.$delete(obj, oldK)
      this.$set(obj, newK, v)
    },
    deleteKey(obj, k) { this.$delete(obj, k) },
    addMapEntry(obj) { this.$set(obj, '/new-' + Date.now(), '') },
    addPermEntry() {
      if (!this.model.pages.permissions) this.$set(this.model.pages, 'permissions', {})
      this.$set(this.model.pages.permissions, '/new-' + Date.now(), [])
    }
  }
}
</script>

<style scoped>
.mb { margin-bottom: 14px; }
.hint { font-size: 12px; color: #a8abb2; line-height: 1.5; }
.srow { display: flex; align-items: center; margin-bottom: 6px; }
.srow .el-input { flex: 1; }
.kvrow { display: flex; gap: 8px; margin-bottom: 6px; }
.kvrow .el-input { flex: 1; }
</style>
