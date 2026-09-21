<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="16" style="padding-left: 20px">
        <h2>SOYSHTTPOverMC ERP 管理台</h2>
        <p>
          欢迎回来，<b>{{ nickName || name }}</b>。这里是 SOYSHTTPOverMC 的 ERP 专属管理界面，
          提供用户权限、权限组与 X-API-KEY 的统一管理。
        </p>
        <p>
          <el-tag type="info" size="mini">数据存储</el-tag>
          <span style="margin-left: 8px">权限 4 表 + APIKEY + 凭证元数据（YAML / SQLite / MySQL 双后端同构）</span>
        </p>
      </el-col>
      <el-col :sm="24" :lg="8">
        <el-card shadow="hover" style="margin-top: 20px">
          <div slot="header"><b>快捷入口</b></div>
          <div style="line-height: 2.4">
            <router-link to="/erp/user"><el-button type="primary" size="mini" plain icon="el-icon-user">用户列表</el-button></router-link>
            <router-link to="/erp/group"><el-button type="success" size="mini" plain icon="el-icon-key">权限组列表</el-button></router-link>
            <router-link to="/erp/apikey"><el-button type="warning" size="mini" plain icon="el-icon-lock">APIKEY 管理</el-button></router-link>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Index',
  computed: {
    ...mapGetters([
      'name',
      'nickName'
    ])
  }
}
</script>
