<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="票据串 / 玩家 / 签发服 / IP" clearable size="small" style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable size="small" style="width: 130px">
          <el-option label="未消费" value="active" />
          <el-option label="已消费" value="consumed" />
          <el-option label="已过期" value="expired" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" size="small" value-format="yyyy-MM-dd" style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" @click="openClean">批量清理</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-alert type="info" :closable="false" style="margin-bottom: 10px"
      title="说明：票据由系统自动签发/消费（TTL 默认 60s），此处仅作审计查看与运维清理。过期/已消费行由读取方惰性清理，长期不清理会导致 YAML 后端数据文件膨胀。" />

    <el-table v-loading="loading" :data="ticketList">
      <el-table-column label="票据串" align="center" prop="ticket" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope"><code>{{ scope.row.ticket }}</code></template>
      </el-table-column>
      <el-table-column label="玩家" align="center" prop="subject" width="120" />
      <el-table-column label="签发服" align="center" prop="issuedServer" width="120" />
      <el-table-column label="客户端 IP" align="center" prop="clientIp" width="130" />
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusTagType(scope.row.statusLabel)">{{ scope.row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" align="center" width="150" prop="expiresAtText" />
      <el-table-column label="消费时间" align="center" width="150">
        <template slot-scope="scope">
          <span v-if="scope.row.consumedAtText">{{ scope.row.consumedAtText }}</span>
          <span v-else style="color:#909399">未消费</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" width="150">
        <template slot-scope="scope">
          <span v-if="scope.row.createTime">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="90" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#f56c6c" @click="handleRemove(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- ============ 批量清理 ============ -->
    <el-dialog title="批量清理票据" :visible.sync="cleanOpen" width="480px">
      <el-radio-group v-model="cleanMode" style="display: block">
        <el-radio label="expired">仅清理已过期（expiresAt &lt; now）</el-radio>
        <el-radio label="consumed">仅清理已消费（consumedAt 非空）</el-radio>
        <el-radio label="all">清理已过期 + 已消费（推荐）</el-radio>
      </el-radio-group>
      <el-alert type="warning" :closable="false" style="margin-top: 10px" title="此操作不可恢复，请确认后再提交。" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="cleanOpen = false">取 消</el-button>
        <el-button type="danger" :loading="cleanLoading" @click="submitClean">确认清理</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSsoTicket, cleanSsoTicket, removeSsoTicket } from '@/api/erp'

export default {
  name: 'ErpSsoTicket',
  data() {
    return {
      loading: true,
      showSearch: true,
      ticketList: [],
      total: 0,
      dateRange: [],
      cleanOpen: false,
      cleanMode: 'all',
      cleanLoading: false,
      queryParams: { pageNum: 1, pageSize: 10, keyword: undefined, status: undefined, subject: undefined }
    }
  },
  created() {
    // 从用户列表跳转时携带 ?subject=xxx
    const subject = this.$route.query.subject
    if (subject) {
      this.queryParams.subject = subject
    }
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = { ...this.queryParams }
      if (this.dateRange && this.dateRange.length === 2) {
        params.from = new Date(this.dateRange[0]).getTime()
        params.to = new Date(this.dateRange[1]).getTime() + 86399999
      }
      listSsoTicket(params).then(res => {
        this.ticketList = res.rows
        this.total = res.total
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    statusTagType(label) {
      if (label === '已消费') return 'info'
      if (label === '已过期') return 'warning'
      return 'success'
    },
    openClean() { this.cleanMode = 'all'; this.cleanOpen = true },
    submitClean() {
      this.cleanLoading = true
      cleanSsoTicket(this.cleanMode).then(res => {
        this.cleanLoading = false
        this.cleanOpen = false
        this.$modal.msgSuccess(res.msg || '清理完成')
        this.getList()
      }).catch(() => { this.cleanLoading = false })
    },
    handleRemove(row) {
      this.$modal.confirm('确认删除票据 ' + row.ticket + '？').then(() => {
        return removeSsoTicket(row.id)
      }).then(() => {
        this.$modal.msgSuccess('已删除')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>
