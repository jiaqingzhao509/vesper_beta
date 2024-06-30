import type { Router } from 'vue-router';
import { useUserStore } from '@/store';
import { info } from '@/api/auth';
import { User } from '@/api/models';

const whiteUrl = ['/login', '/register', '/forget', '/sso', '/index'];
function isWhiteUrl(path: string) {
  return whiteUrl.some((url) => new RegExp(`^${url}(/|\\?|$)`).test(path));
}

export function setupPageGuard(router: Router) {
  const userStore = useUserStore();
  const $message = window['$message'];

  router.beforeEach(async (to, from, next) => {
    if (!userStore.token) {
      try {
        if (!isWhiteUrl(to.path)) {
          next({ path: '/login' });
        } else {
          next();
        }
      } catch (error) {
        if (to.path !== '/500') next({ name: '500' });
        else next();
      }
    } else {
      if (!userStore.user) {
        // get user info
        const data = (await info()) as User;
        if (data === null) {
          await userStore.logout();
        } else {
          userStore.setUser(data);
          if (data.chart === null || data.lat === null || data.lng === null) {
            $message?.error('Please complete your birthdate coordinates first');
            router.push('/profile');
          }
        }
      }

      next();
    }
  });
}
