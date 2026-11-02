import { createRouter, createWebHistory } from 'vue-router'
import { USER_ROLE } from '@/constants/app'
import { useAuthStore } from '@/stores/auth'
import MainLayout from '@/layouts/MainLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import MerchantLayout from '@/layouts/MerchantLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', name: 'home', component: () => import('@/views/HomeView.vue'), meta: { title: '首页' } },
        { path: 'products', name: 'products', component: () => import('@/views/product/ProductListView.vue'), meta: { title: '商品列表' } },
        { path: 'products/:id', name: 'product-detail', component: () => import('@/views/product/ProductDetailView.vue'), meta: { title: '商品详情' } },
        { path: 'cart', name: 'cart', component: () => import('@/views/user/CartView.vue'), meta: { title: '购物车', requiresAuth: true } },
        { path: 'checkout', name: 'checkout', component: () => import('@/views/user/CheckoutView.vue'), meta: { title: '提交订单', requiresAuth: true } },
        { path: 'orders', name: 'orders', component: () => import('@/views/user/OrdersView.vue'), meta: { title: '我的订单', requiresAuth: true } },
        { path: 'profile', name: 'profile', component: () => import('@/views/user/ProfileView.vue'), meta: { title: '个人中心', requiresAuth: true } },
        { path: 'addresses', name: 'addresses', component: () => import('@/views/user/AddressesView.vue'), meta: { title: '地址管理', requiresAuth: true } },
      ],
    },
    {
      path: '/',
      component: AuthLayout,
      children: [
        { path: 'login', name: 'login', component: () => import('@/views/auth/LoginView.vue'), meta: { title: '登录' } },
        { path: 'register', name: 'register', component: () => import('@/views/auth/RegisterView.vue'), meta: { title: '注册' } },
      ],
    },
    {
      path: '/merchant',
      component: MerchantLayout,
      meta: { requiresAuth: true, role: USER_ROLE.MERCHANT, title: '商家后台' },
      children: [
        { path: '', name: 'merchant-dashboard', component: () => import('@/views/merchant/MerchantDashboardView.vue'), meta: { title: '商家总览' } },
        { path: 'products', name: 'merchant-products', component: () => import('@/views/merchant/MerchantProductsView.vue'), meta: { title: '商品管理' } },
        { path: 'orders', name: 'merchant-orders', component: () => import('@/views/merchant/MerchantOrdersView.vue'), meta: { title: '订单管理' } },
        { path: 'statistics', name: 'merchant-statistics', component: () => import('@/views/merchant/MerchantStatisticsView.vue'), meta: { title: '经营分析' } },
      ],
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true, role: USER_ROLE.ADMIN, title: '管理控制台' },
      children: [
        { path: '', name: 'admin-dashboard', component: () => import('@/views/admin/AdminDashboardView.vue'), meta: { title: '管理控制台' } },
        { path: 'categories', name: 'admin-categories', component: () => import('@/views/admin/AdminCategoriesView.vue'), meta: { title: '分类管理' } },
        { path: 'products', name: 'admin-products', component: () => import('@/views/admin/AdminProductsView.vue'), meta: { title: '商品管理' } },
        { path: 'orders', name: 'admin-orders', component: () => import('@/views/admin/AdminOrdersView.vue'), meta: { title: '订单管理' } },
        { path: 'users', name: 'admin-users', component: () => import('@/views/admin/AdminUsersView.vue'), meta: { title: '用户管理' } },
        { path: 'notices', name: 'admin-notices', component: () => import('@/views/admin/AdminNoticesView.vue'), meta: { title: '公告管理' } },
        { path: 'statistics', name: 'admin-statistics', component: () => import('@/views/admin/AdminStatisticsView.vue'), meta: { title: '统计分析' } },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { title: '404' },
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  authStore.hydrate()

  if (to.meta.title) {
    document.title = `${to.meta.title} - ${import.meta.env.VITE_APP_TITLE || 'Campus Snack Mall'}`
  }

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath,
      },
    }
  }

  if (to.meta.role && authStore.userInfo?.role !== to.meta.role) {
    return '/'
  }

  return true
})

export default router
