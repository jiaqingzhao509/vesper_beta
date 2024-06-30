<script lang="ts" setup>
  import Chat from '../modules/chat/index.vue';
  import { useUserStore } from '@/store';
  import { router } from '@/router';
  import { onMounted } from 'vue';
  import { useMessage } from 'naive-ui';

  const message = useMessage();
  const userStore = useUserStore();

  onMounted(() => {});
</script>

<template>
  <div class="h-full">
    <div
      v-if="!userStore.user?.username"
      class="flex h-full w-full flex-1 justify-center items-start pt-[20vh]"
    >
      <div class="inline-flex flex-col items-center gap-10">
        <div class="w-60 h-60 bg-[#EBE2F3]"></div>
        <n-button class="!rounded-md" color="#5678DE" size="large" @click="router.push('/login')">
          Sign up and ask now
        </n-button>
      </div>
    </div>

    <Chat v-else />
  </div>
</template>

<style lang="less" scoped></style>
