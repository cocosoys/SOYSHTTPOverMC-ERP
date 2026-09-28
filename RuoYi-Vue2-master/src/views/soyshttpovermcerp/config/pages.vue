<template>
  <config-layout title="页面与资源" :help-map="helpMap" file="pages.yml" :dirty="dirty" :loading="loading" :saving="saving" @reload="reload" @save="save" >

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
  </config-layout>
</template>

<script>
import configPage from '../mixins/configPage'
import ConfigLayout from '../components/ConfigLayout.vue'

export default {
  name: 'SettingsPages',
  mixins: [configPage],
  components: { ConfigLayout },
  data() {
    return {
      fileId: 'pages',
      helpMap: {
        '静态资源根目录': 'dist/ 前端构建产物所在目录。Web 站点所有静态文件（HTML、JS、CSS、图片）的根路径。插件会把这个目录下的文件作为静态资源对外提供访问。一般保持默认 dist/ 即可，无需修改。',
        '首页路径': '默认首页文件路径，当用户访问根路径 / 时默认返回哪个文件。一般为 index.html。',
        '大文件阈值（字节）': '文件大小超过此阈值时视为大文件，启用分块传输（streaming），避免一次性把整个文件读入内存导致服务器卡顿。默认较大值（如 1MB），日常小文件无影响，大文件自动优化。',
        '大文件上限（字节）': '允许提供的静态文件最大大小，超过此大小的文件直接拒绝访问（返回 403）。防止有人上传超大文件拖垮服务器。',
        '缓存总大小上限（字节）': '静态资源内存缓存的总大小上限（LRU 缓存）。插件会把高频访问的 JS/CSS/图片缓存在内存里，加快访问速度。超过上限后自动淘汰最久未访问的缓存条目。内存紧张时可调小，访问量大时可调大。',
        '缓存条目数上限': '内存缓存的最大条目数量。超过后按 LRU 淘汰最久未访问的条目。配合缓存总大小一起限制内存占用。',
        '缓存 TTL（秒）': '每个缓存条目的存活时间（秒）。超过 TTL 后该条目自动过期，下次访问重新从磁盘加载。设为 0 表示永不过期，只靠 LRU 淘汰机制管理。',
        '固定缓存（pinned）': '永久驻留在缓存里的路径列表，不随 LRU 淘汰。适合放首页、核心 JS/CSS、favicon 等高频访问且很少变化的资源。这些资源即使很久没访问也不会被从缓存里踢出去，保证极速响应。',
        '已登记页面（pages.page）': '手动登记的页面路由表。每个页面一行：路径（如 /user）→ 昵称列表（在菜单里显示的名字）→ 描述 → 对应的静态资源文件。表格方式编辑，增删改都很方便。这是 ERP 菜单系统的基础——插件根据这里登记的路径生成侧边栏菜单。',
        '已登记页面路径': '页面的 URL 路径，如 /user、/group。访问这个路径时插件会返回对应的静态资源文件。',
        '已登记页面昵称': '在 ERP 侧边栏菜单里显示的页面名称。可以有多个昵称（别名），方便多语言场景。',
        '已登记页面描述': '页面的功能描述，鼠标悬停在菜单上时可能显示。',
        '已登记页面资源文件': '该页面对应的静态资源文件路径（相对于 dist/ 目录），如 index.html。',
        '自动路由（pages.auto）': '路径 → 静态资源来源的自动映射规则。按 URL 路径自动匹配对应的文件，无需手动逐个登记页面。适合静态网站那种一个路径对应一个 html 文件的场景。',
        '页面权限（pages.permissions）': '路径 → 权限节点数组的映射表。访问某个路径时需要持有列表中的任一权限节点才能访问。用于控制哪些用户能看哪些页面，是细粒度的访问控制。'
      }
    }
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
