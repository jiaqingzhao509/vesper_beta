import { storage } from '@/utils/storage';

const LOCAL_NAME = 'appSetting';

export type Theme = 'light' | 'dark' | 'auto';

export interface AppState {
  siderCollapsed: boolean;
  theme: Theme;
}

export function defaultSetting(): AppState {
  return { siderCollapsed: false, theme: 'light' };
}

export function getLocalSetting(): AppState {
  const localSetting: AppState | undefined = storage.get(LOCAL_NAME);
  return { ...defaultSetting(), ...localSetting };
}

export function setLocalSetting(setting: AppState): void {
  storage.set(LOCAL_NAME, setting);
}
