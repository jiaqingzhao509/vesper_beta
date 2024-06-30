<script lang="ts" setup>
  import { NaiveProvider } from '@/components/common';
  import { useTheme } from '@/hooks/useTheme';

  const { theme, themeOverrides } = useTheme();
</script>

<template>
  <n-config-provider
    :theme="theme"
    :theme-overrides="themeOverrides"
    class="h-full bg-[#FBF6FF] w-full"
  >
    <NaiveProvider>
      <RouterView />
    </NaiveProvider>
  </n-config-provider>
</template>
