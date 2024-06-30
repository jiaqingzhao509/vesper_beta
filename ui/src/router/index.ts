import type { App } from 'vue';
import type { RouteRecordRaw } from 'vue-router';
import { createRouter, createWebHistory } from 'vue-router';
import { Layout } from '@/layout';
import { setupPageGuard } from '@/router/permission';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Root',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        name: 'Index',
        meta: {
          label: 'Index',
        },
        component: () => import('@/views/home/index.vue'),
      },
      {
        path: '/plan',
        name: 'Plan',
        meta: {
          label: 'Plan',
        },
        component: () => import('@/views/modules/plan/index.vue'),
      },
      {
        path: '/profile',
        name: 'Profile',
        meta: {
          label: 'Profile',
        },
        component: () => import('@/views/profile/index.vue'),
      },
      {
        path: '/login',
        name: 'Login',
        meta: {
          label: 'Login',
        },
        component: () => import('@/views/login/Login.vue'),
      },
      {
        path: '/register',
        name: 'Register',
        meta: {
          label: 'Register',
        },
        component: () => import('@/views/login/Register.vue'),
      },
      {
        path: '/forget',
        name: 'Forget',
        meta: {
          label: 'Forget',
        },
        component: () => import('@/views/login/Forget.vue'),
      },
      {
        path: '/chat/:uuid?',
        name: 'Chat',
        meta: {
          label: 'Chat',
          icon: 'bx:chat',
        },
        component: () => import('@/views/modules/chat/index.vue'),
      },
    ],
  },
];

const baseRoutes: RouteRecordRaw[] = [
  {
    path: '/sso',
    name: 'SSO',
    component: () => import('@/views/login/SSO.vue'),
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/exception/404/index.vue'),
  },

  {
    path: '/500',
    name: '500',
    component: () => import('@/views/exception/500/index.vue'),
  },

  {
    path: '/:pathMatch(.*)*',
    name: 'notFound',
    redirect: '/404',
  },
];

export const routesConst: RouteRecordRaw[] = routes.flatMap((route) => route.children ?? []);

export const router = createRouter({
  history: createWebHistory(),
  routes: [...baseRoutes, ...routes],
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

export async function setupRouter(app: App) {
  app.use(router);
  setupPageGuard(router);
}
