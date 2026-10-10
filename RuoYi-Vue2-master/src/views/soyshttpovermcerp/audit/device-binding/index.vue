<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="玩家名 / UUID / 设备 / IP" clearable size="small" style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="revoked">
        <el-select v-model="queryParams.revoked" placeholder="全部" clearable size="small" style="width: 120px">
          <el-option label="有效" :value="0" />
          <el-option label="已吊销" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-alert type="info" :closable="false" style="margin-bottom: 10px"
      title="说明：设备记录由玩家勾选"记住我"后自动写入（仅存 SHA-256 指纹哈希，不存原始指纹）。吊销后该设备的自动登录立即失效；删除则彻底清除绑定。" />

    <el-table v-loading="loading" :data="bindingList">
      <el-table-column label="玩家" align="center" prop="player" width="120" />
      <el-table-column label="UUID" align="center" prop="uuid" width="280" show-overflow-tooltip />
      <el-table-column label="设备指纹" align="center" width="160" show-overflow-tooltip>
        <template slot-scope="scope"><code>{{ scope.row.fingerprintShort || scope.row.fingerprintHash }}</code></template>
      </el-table-column>
      <el-table-column label="设备描述" align="center" prop="deviceLabel" min-width="180" show-overflow-tooltip />
      <el-table-column label="最近 IP" align="center" prop="lastIp" width="130" />
      <el-table-column label="最后绑定" align="center" width="150">
        <template slot-scope="scope">
          <span v-if="scope.row.lastBindAt">{{ parseTime(scope.row.lastBindAt, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.revoked === 1 ? 'danger' : 'success'">{{ scope.row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" :icon="scope.row.revoked === 1 ? 'el-icon-refresh-right' : 'el-icon-circle-close'" @click="handleToggle(scope.row)">
            {{ scope.row.revoked === 1 ? '恢复' : '吊销' }}
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#f56c6c" @click="handleRemove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listDeviceBinding, toggleDeviceBinding, removeDeviceBinding } from '@/api/erp'

export default {
  name: 'ErpDeviceBinding',
  data() {
    return {
      loading: true,
      showSearch: true,
      bindingList: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, keyword: undefined, player: undefined, revoked: undefined }
    }
  },
  created() {
    const player = this.$route.query.player
    if (player) {
      this.queryParams.player = player
    }
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDeviceBinding(this.queryParams).then(res => {
        this.bindingList = res.rows
        this.total = res.total
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleToggle(row) {
      toggleDeviceBinding(row.id).then(res => {
        this.$modal.msgSuccess(res.msg || '操作成功')
        this.getList()
      }).catch(() => {})
    },
    handleRemove(row) {
      this.$modal.confirm('确认删除玩家 ' + (row.player || '') + ' 的这台设备绑定？').then(() => {
        return removeDeviceBinding(row.id)
      }).then(() => {
        this.$modal.msgSuccess('已删除')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>
