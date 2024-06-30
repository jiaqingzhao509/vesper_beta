<script lang="ts" setup>
  import Header from './Header.vue';
  import { useBasicLayout } from '@/hooks/useBasicLayout';

  const { isMobile } = useBasicLayout();
</script>

<template>
  <div class="w-full h-screen flex flex-col bg-[#FBF6FF]">
    <Header />
    <div
      :class="isMobile ? 'px-4' : 'px-2'"
      class="bg-[#FBF6FF] flex-1 justify-center items-center flex"
    >
      <div class="w-full h-full max-w-screen-lg">
        <RouterView v-slot="{ Component, route }">
          <keep-alive>
            <component :is="Component" :key="route.fullPath" />
          </keep-alive>
        </RouterView>
      </div>
    </div>
    <div v-if="!isMobile" class="flex w-360 p-6 justify-center items-center gap-6 bg-[#FBF6FF]">
      <div class="text-[#0A0A0A] font-Inconsolata text-4 font-not-italic font-400 lh-[160%]"
        >Contact</div
      >
      <div class="text-[#0A0A0A] font-Inconsolata text-4 font-not-italic font-400 lh-[160%]"
        >Privacy Policy</div
      >
      <div class="text-[#0A0A0A] font-Inconsolata text-4 font-not-italic font-400 lh-[160%]"
        >Terms of services</div
      >
    </div>
  </div>
</template>
