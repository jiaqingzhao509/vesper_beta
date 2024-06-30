<script lang="ts" setup>
  import { useRouter } from 'vue-router';
  import { useUserStore } from '@/store';
  import { SvgIcon } from '@/components/common';
  import { useBasicLayout } from '@/hooks/useBasicLayout';
  import { ref } from 'vue';
  import { useMessage } from 'naive-ui';

  const userStore = useUserStore();
  const message = useMessage();
  const { isMobile } = useBasicLayout();
  const router = useRouter();

  const options = [
    {
      label: 'Profile',
      value: 'profile',
    },
    {
      label: 'Logout',
      value: 'logout',
    },
  ];

  let open = ref(false);
  function menuOpen() {
    open.value = !open.value;
  }

  async function onLogout() {
    await userStore.logout();
    await router.push({ name: 'Login' });
    message.success('Logout successful');
  }

  async function onSelect(val: string) {
    if (val === 'profile') {
      await router.push({ name: 'Profile' });
    }
    if (val === 'logout') {
      await onLogout();
    }
  }
</script>

<template>
  <nav class="w-full border-b md:border-0 md:static">
    <div class="items-center max-w-screen-xl mx-auto md:flex md:px-8">
      <div
        :class="isMobile ? 'px-3' : 'px-2'"
        class="flex items-center justify-between py-3 md:py-5 md:block"
      >
        <n-button class="!text-xl" text @click="router.push('/')">Vesper</n-button>
        <div class="md:hidden flex justify-center">
          <button class="text-gray-700 outline-none rounded-md text-xl" @click="menuOpen()">
            <SvgIcon :class="[open ? 'hidden' : 'block']" icon="material-symbols:menu" />
            <SvgIcon :class="[open ? 'block' : 'hidden']" icon="material-symbols:close" />
          </button>
        </div>
      </div>
      <div :class="[open ? 'block' : 'hidden']" class="flex-1 pb-3 mt-1 md:block md:pb-0 md:mt-0">
        <ul
          :class="isMobile ? 'px-3' : 'px-2'"
          class="justify-end items-center space-y-2 md:flex md:space-x-2 md:space-y-0"
        >
          <template v-if="userStore.user">
            <template v-if="isMobile">
              <li class="text-gray-600 cursor-pointer text-lg cursor-pointer">
                {{ userStore.user.nickname }}
              </li>
              <li class="text-gray-600 cursor-pointer pt-3" @click="onSelect('profile')">
                Profile
              </li>
              <li class="text-gray-600 cursor-pointer pt-1" @click="onSelect('logout')">
                Logout
              </li>
            </template>

            <n-popselect v-else :options="options" class="!cursor-pointer" @update:value="onSelect">
              <div>
                <li class="text-gray-600 cursor-pointer text-lg">
                  {{ userStore.user.nickname }}
                </li>
                <li v-if="isMobile" class="text-gray-600 cursor-pointer pt-3"> Profile </li>
                <li v-if="isMobile" class="text-gray-600 cursor-pointer pt-1"> Logout </li>
              </div>
            </n-popselect>
          </template>

          <template v-else>
            <template v-if="isMobile">
              <li
                class="text-gray-600 hover:text-indigo-600 text-lg"
                @click="router.push('/works')"
              >
                How it Works
              </li>
              <li
                class="text-gray-600 hover:text-indigo-600 text-lg"
                @click="router.push('/login')"
              >
                Login
              </li>
            </template>
            <template v-else>
              <n-button
                class="!rounded-md"
                color="#5678DE"
                ghost
                size="large"
                @click="router.push('/works')"
              >
                How it Works
              </n-button>
              <n-button
                class="!rounded-md"
                color="#5678DE"
                size="large"
                @click="router.push('/login')"
              >
                Login
              </n-button>
            </template>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>

<style lang="less">
  ::v-deep(.v-binder-follower-container) {
    width: 100% !important;
  }
</style>
