import { http } from '@/utils/request';
import { TokenInfo, User } from '@/api/models/index';

/**
 * @description: 用户登录
 */
export function login(data: any): Promise<TokenInfo> {
  return http.post({
    url: `/api/auth/login`,
    data,
  });
}

export function register(data: any): Promise<TokenInfo> {
  return http.post({
    url: `/api/auth/register`,
    data,
  });
}

/**
 * @description: 用户信息
 */
export function info(): Promise<User> {
  return http.get({
    url: `/api/auth/info`,
  });
}

export function updateUser(data: any) {
  return http.put({
    url: `/api/auth/update`,
    data,
  });
}

export function checkPlace(place: string) {
  return http.get({
    url: `/api/auth/checkPlace?place=${place}`,
  });
}

/**
 * @description: 用户登出
 */
export function logout() {
  return http.delete({
    url: '/api/auth/logout',
  });
}

/**
 * @description: google登录地址
 */
export function googleLogin() {
  return http.delete({
    url: '/api/auth/oauth/google/authorize',
  });
}
