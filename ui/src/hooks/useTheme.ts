import type { GlobalThemeOverrides } from 'naive-ui';
import { darkTheme, useOsTheme } from 'naive-ui';
import { computed } from 'vue';
import { useAppStore } from '@/store';

export function useTheme() {
  const appStore = useAppStore();

  const OsTheme = useOsTheme();

  const isDark = computed(() => {
    if (appStore.theme === 'auto') return OsTheme.value === 'dark';
    else return appStore.theme === 'dark';
  });

  const theme = computed(() => {
    return isDark.value ? darkTheme : undefined;
  });

  const themeOverrides = computed<GlobalThemeOverrides>(() => {
    if (isDark.value) {
      return {};
    }
    return {
      common: {
        primaryColor: '#5678DE',
        primaryColorHover: '#5678DE',
      },
      Button: {
        textColor: '#0A0A0A',
      },
    };
  });

  // watch(
  //   () => isDark.value,
  //   (dark) => {
  //     if (dark) document.documentElement.classList.add('dark');
  //     else document.documentElement.classList.remove('dark');
  //   },
  //   { immediate: true }
  // );

  return { theme, themeOverrides };
}
