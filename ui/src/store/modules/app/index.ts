import { defineStore } from 'pinia';
import type { AppState, Theme } from './helper';
import { getLocalSetting, setLocalSetting } from './helper';
import { store } from '@/store/helper';

export const useAppStore = defineStore('app-store', {
  state: (): AppState => getLocalSetting(),
  actions: {
    setSiderCollapsed(collapsed: boolean) {
      this.siderCollapsed = collapsed;
      this.recordState();
    },

    setTheme(theme: Theme) {
      this.theme = theme;
      this.recordState();
    },

    recordState() {
      setLocalSetting(this.$state);
    },
  },
});

export function useAppStoreWithOut() {
  return useAppStore(store);
}
