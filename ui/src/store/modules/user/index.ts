import { defineStore } from 'pinia';
import { storage } from '@/utils/storage';
import { login, logout } from '@/api/auth';
import { TokenInfo, User } from '@/api/models';

const TokenStoreKey = 'SECRET_TOKEN';
const UserStoreKey = 'USER_INFO';

export interface UserState {
  user: User | undefined;
  token: string | undefined;
}

export const useUserStore = defineStore('user-store', {
  state: (): UserState => ({
    user: storage.get(UserStoreKey),
    token: storage.get(TokenStoreKey),
  }),
  actions: {
    setUser(user: Partial<User>) {
      this.user = user;
      storage.set(UserStoreKey, user);
    },
    resetUser() {
      this.user = undefined;
      storage.set(UserStoreKey, undefined);
    },

    setToken(token: string) {
      this.token = token;
      storage.set(TokenStoreKey, token);
    },
    resetToken() {
      this.token = undefined;
      storage.set(TokenStoreKey, undefined);
    },

    // 登录
    async login(params: User): Promise<TokenInfo> {
      const response = await login(params);
      if (response.token == undefined) {
        throw Error('Error');
      }
      this.setToken(response.token);

      if (response.user != null) {
        this.setUser(response.user);
      }
      return response;
    },

    // 登出
    async logout() {
      await logout();
      this.resetToken();
      this.resetUser();
    },
  },
});
