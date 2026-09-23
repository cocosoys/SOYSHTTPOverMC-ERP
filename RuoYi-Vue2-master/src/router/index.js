import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout：wujie 嵌入时主应用 MCERP 提供侧边栏（空 Layout）；直接访问时用完整 Layout（自带侧边栏） */
import RealLayout from '@/layout'
import EmptyLayout from '@/layout/Empty'

// wujie 子应用标记（主应用加载时注入 window.__POWERED_BY_WUJIE__）
export const isWujie = typeof window !== 'undefined' && !!window.__POWERED_BY_WUJIE__
const Layout = isWujie ? EmptyLayout : RealLayout

// 运行时 base：生产 = 契约 pageFullPrefix（/web/plugins/SOYSHTTPOverMC-ERP）；dev = '/'
const ctx = (typeof window !== 'undefined' && window.SOYS_CONTEXT) || {}
const routeBase = process.env.NODE_ENV === 'production'
  ? (ctx.pageFullPrefix || '/web/plugins/SOYSHTTPOverMC-ERP/')
  : '/'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'index',
    children: [
      {
        path: 'index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  // ERP 业务路由：写死 /erp/*，与 wujie 加载的子应用 URL 一致（不从 getRouters 拉）
  {
    path: '/erp',
    component: Layout,
    redirect: '/erp/user',
    children: [
      { path: 'user', component: () => import('@/views/erp/user/index'), name: 'ErpUser', meta: { title: '游戏用户列表' } },
      { path: 'group', component: () => import('@/views/erp/group/index'), name: 'ErpGroup', meta: { title: '权限组列表' } },
      { path: 'apikey', component: () => import('@/views/erp/apikey/index'), name: 'ErpApiKey', meta: { title: 'APIKEY 管理' } },
      { path: 'lang', component: () => import('@/views/erp/lang'), name: 'ErpLang', meta: { title: '语言管理' } },
      { path: 'settings/config', component: () => import('@/views/erp/settings/main'), name: 'SettingsConfig', meta: { title: '核心配置' } },
      { path: 'settings/language', component: () => import('@/views/erp/settings/language'), name: 'SettingsLanguage', meta: { title: '国际化' } },
      { path: 'settings/pages', component: () => import('@/views/erp/settings/pages'), name: 'SettingsPages', meta: { title: '页面与资源' } },
      { path: 'settings/eula', component: () => import('@/views/erp/settings/eula'), name: 'SettingsEula', meta: { title: '用户协议' } },
      { path: 'settings/gw-config', component: () => import('@/views/erp/settings/gw-config'), name: 'SettingsGwConfig', meta: { title: '网关总开关' } },
      { path: 'settings/gw-https', component: () => import('@/views/erp/settings/gw-https'), name: 'SettingsGwHttps', meta: { title: 'HTTPS 设置' } },
      { path: 'settings/gw-auth', component: () => import('@/views/erp/settings/gw-auth'), name: 'SettingsGwAuth', meta: { title: '认证鉴权' } },
      { path: 'settings/gw-rate', component: () => import('@/views/erp/settings/gw-rate'), name: 'SettingsGwRate', meta: { title: '令牌桶限流' } },
      { path: 'settings/gw-access', component: () => import('@/views/erp/settings/gw-access'), name: 'SettingsGwAccess', meta: { title: '访问限制器' } },
      { path: 'settings/gw-allow', component: () => import('@/views/erp/settings/gw-allow'), name: 'SettingsGwAllow', meta: { title: 'IP 白黑名单' } },
      { path: 'settings/gw-tls', component: () => import('@/views/erp/settings/gw-tls'), name: 'SettingsGwTls', meta: { title: 'TLS 强制' } },
      { path: 'settings/gw-session', component: () => import('@/views/erp/settings/gw-session'), name: 'SettingsGwSession', meta: { title: '会话令牌' } }
    ]
  },
  {
    path: '/lock',
    component: () => import('@/views/lock'),
    hidden: true,
    meta: { title: '锁定屏幕' }
  }
]

// 动态路由（预留）：菜单由后端 getRouters 动态驱动（ERP 模块菜单），此处保持空
export const dynamicRoutes = []

// 防止连续点击多次路由报错
let routerPush = Router.prototype.push
let routerReplace = Router.prototype.replace
// push
Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}
// replace
Router.prototype.replace = function push(location) {
  return routerReplace.call(this, location).catch(err => err)
}

export default new Router({
  mode: 'history', // SOYS 1.4.0 已实现 spaFallback，深层 URL 刷新不会 404
  base: routeBase,
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
