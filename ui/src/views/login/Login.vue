<script lang="ts" setup>
  import { SvgIcon } from '@/components/common';
  import { onMounted, reactive, ref, toRaw } from 'vue';
  import { useMessage } from 'naive-ui';
  import { useUserStore } from '@/store/modules/user';
  import { useRouter } from 'vue-router';
  import { googleLogin } from '@/api/auth';

  const formRef = ref();
  const message = useMessage();
  const loading = ref(false);
  const form = reactive({
    username: '',
    password: '',
  });
  const rules = {
    username: { required: true, message: 'Enter your email', trigger: 'blur' },
    password: { required: true, message: 'Enter your password', trigger: 'blur' },
  };
  const userStore = useUserStore();
  const router = useRouter();

  onMounted(() => {
    message.destroyAll();
    const { error } = router.currentRoute.value.query;
    if (error !== undefined) {
      message.error(String(error));
    }
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    formRef.value.validate(async (errors: any) => {
      if (!errors) {
        loading.value = true;

        try {
          await userStore.login(toRaw(form));
          message.success('Login success');
          await router.push('/');
        } finally {
          loading.value = false;
        }
      }
    });
  };

  async function onGoogle() {
    const { url } = await googleLogin();
    window.open(url, '_self');
  }

  function onForget() {
    router.push('/forget');
  }
</script>

<template>
  <div class="w-full flex justify-center flex-1 h-full items-start pt-[10vh]">
    <div class="w-[380px]">
      <div class="mt-4 login-content-form">
        <div class="text-4xl text-center mb-6">Login</div>
        <n-form ref="formRef" :model="form" :rules="rules" size="large">
          <n-form-item class="login-animation1" label="Email" path="username">
            <n-input v-model:value="form.username" placeholder="Enter your email">
              <template #prefix>
                <n-icon color="#808695" size="18">
                  <SvgIcon icon="material-symbols:person-outline" />
                </n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item class="login-animation2" label="Password" path="password">
            <n-input
              v-model:value="form.password"
              placeholder="Enter you password"
              showPasswordOn="click"
              type="password"
            >
              <template #prefix>
                <n-icon color="#808695" size="18">
                  <SvgIcon icon="mdi:lock-outline" />
                </n-icon>
              </template>
            </n-input>
          </n-form-item>
          <div class="login-animation2 space-y-2 mb-3">
            <!--            <n-button text @click="onForget">Forgot your password?</n-button>-->
            <n-button
              :loading="loading"
              block
              class="!rounded-md !py-6"
              color="#5678DE"
              size="large"
              @click="handleSubmit"
            >
              Login
            </n-button>
          </div>
          <div class="login-animation3 space-y-4">
            <n-button block class="!rounded-md !justify-start !py-6" size="large" @click="onGoogle">
              <SvgIcon icon="devicon:google" />
              &nbsp; Continue with Google
            </n-button>
            <div class="text-[#0A0A0A] font-700 text-center">
              Don’t have an account yet?
              <n-button class="!font-bold" text @click="router.push('/register')">Sign up</n-button>
            </div>
          </div>
        </n-form>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
  @import '@/styles/login';
  ::v-deep(.n-input .n-input-wrapper) {
    padding-top: 4px;
    padding-bottom: 4px;
  }
</style>
