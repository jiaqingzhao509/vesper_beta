<script lang="ts" setup>
  import { onMounted } from 'vue';
  import { router } from '@/router';
  import { useUserStore } from '@/store';
  import { SvgIcon } from '@/components/common';
  import { googleLogin } from '@/api/auth';

  const userStore = useUserStore();

  onMounted(async () => {
    const { token } = router.currentRoute.value.query;
    if (token !== undefined) {
      console.log('设置token');
      userStore.setToken(token as string);
      await router.push('/');
    }
  });

  async function onGoogle() {
    const { url } = await googleLogin();
    window.open(url, '_self');
  }
</script>

<template>
  <section class="py-28 relative">
    <div class="relative z-10 max-w-screen-xl mx-auto px-4 md:text-center md:px-8">
      <div class="max-w-xl md:mx-auto">
        <p class="text-white text-3xl font-semibold sm:text-4xl"> SSO Authorize </p>
        <p class="text-blue-100 mt-3"> Single Sign-On（SSO） </p>
      </div>
      <div class="max-w-sm mt-4 md:mx-auto flex flex-col gap-4">
        <a
          class="inline-block py-2 px-4 text-gray-800 font-medium cursor-pointer bg-white duration-150 hover:bg-gray-100 active:bg-gray-200 rounded-full"
          @click="router.push('/')"
        >
          Go Home
        </a>
        <n-button
          block
          class="!bg-white !text-black !rounded-full hover:bg-gray-100 active:bg-gray-200"
          size="large"
          @click="onGoogle"
        >
          <SvgIcon icon="devicon:google" />
          &nbsp; Continue with Google
        </n-button>
      </div>
    </div>
    <div
      class="absolute top-0 w-full h-screen"
      style="
        background: linear-gradient(
          268.24deg,
          rgba(59, 130, 246, 0.76) 50%,
          rgba(59, 130, 246, 0.545528) 80.61%,
          rgba(55, 48, 163, 0) 117.35%
        );
      "
    ></div>
  </section>
</template>

<style lang="less" scoped></style>
