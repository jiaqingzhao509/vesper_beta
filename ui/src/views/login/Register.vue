<script lang="ts" setup>
  import { SvgIcon } from '@/components/common';
  import { onMounted, reactive, ref, toRaw } from 'vue';
  import { useMessage } from 'naive-ui';
  import { useUserStore } from '@/store/modules/user';
  import { useRouter } from 'vue-router';
  import { checkPlace, googleLogin, register } from '@/api/auth';

  const formRef = ref();
  const message = useMessage();
  const loading = ref(false);
  const form = reactive({
    username: '',
    password: '',
    rePassword: '',
    nickname: '',
    birthdate: undefined,
    place: '',
    lat: '',
    lng: '',
  });

  const rules = {
    username: { required: true, message: 'Enter your email', trigger: 'blur' },
    password: { required: true, message: 'Enter your password', trigger: 'blur' },
    nickname: { required: true, message: 'Enter your nickname', trigger: 'blur' },
    birthdate: { required: true, message: 'Enter your birthdate', trigger: 'blur' },
    place: {
      required: true,
      message: 'Enter your birth place',
      trigger: 'blur',
      validator(rule: any, value: string) {
        if (value === '') {
          return false;
        }
        checkPlace(form.place)
          .then((res: any) => {
            console.log('checkPlace response:', res)
            if (res == null) {
              message.error('Please check whether the geographical location entered is incorrect');
              return false;
            }
            console.log('Latitude:', res.lat, 'Longitude:', res.lng);
            form.lat = res.lat;
            form.lng = res.lng;
            message.success('Data acquisition success');
            return true;
          })
          .catch((err) => {
            console.log('err', err);
            return false;
          });
      },
    },
  };

  const userStore = useUserStore();
  const router = useRouter();

  onMounted(() => {
    message.destroyAll();
  });

  const handleSubmit = (e: any) => {
    e.preventDefault();
    formRef.value.validate(async (errors: any) => {
      if (!errors) {
        if (form.password.length < 6) {
          message.error('The password must contain at least six characters');
          return;
        }
        if (form.password !== form.rePassword) {
          message.error('The two passwords are different');
          return;
        }

        loading.value = true;
        try {
          const { token } = await register(toRaw(form));
          userStore.setToken(token as string);
          message.success('Registered successfully!');
          await router.replace('/');
        } finally {
          loading.value = false;
        }
      } else {
        message.error('Please fill in the complete information');
      }
    });
  };

  async function onGoogle() {
    const { url } = await googleLogin();
    window.open(url, '_self');
  }
</script>

<template>
  <div class="w-full flex justify-center flex-1 h-full items-start pt-[4vh]">
    <div class="w-[380px]">
      <div class="mt-4 login-content-form">
        <div class="text-4xl text-center mb-6">Create an account</div>
        <n-form ref="formRef" :model="form" :rules="rules" :show-feedback="false" size="large">
          <n-form-item class="login-animation1" label="Email" path="username">
            <n-input v-model:value="form.username" placeholder="enter your email">
              <template #prefix>
                <n-icon color="#808695" size="18">
                  <SvgIcon icon="material-symbols:person-outline" />
                </n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item class="login-animation1" label="Nickname" path="nickname">
            <n-input v-model:value="form.nickname" placeholder="enter your nickname">
              <template #prefix>
                <n-icon color="#808695" size="18">
                  <SvgIcon icon="material-symbols:person-outline" />
                </n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item class="login-animation2" label="Create a password" path="password">
            <div class="flex flex-col gap-2 w-full">
              <n-input
                v-model:value="form.password"
                placeholder="Enter your password"
                showPasswordOn="click"
                type="password"
              >
                <template #prefix>
                  <n-icon color="#808695" size="18">
                    <SvgIcon icon="mdi:lock-outline" />
                  </n-icon>
                </template>
              </n-input>
              <n-input
                v-model:value="form.rePassword"
                placeholder="Re-enter password"
                showPasswordOn="click"
                type="password"
              >
                <template #prefix>
                  <n-icon color="#808695" size="18">
                    <SvgIcon icon="mdi:lock-outline" />
                  </n-icon>
                </template>
              </n-input>
            </div>
          </n-form-item>
          <n-form-item class="login-animation1" label="Birthdate" path="birthdate">
            <n-date-picker
              v-model:formatted-value="form.birthdate"
              class="w-full"
              format="yyyy-MM-dd HH:mm"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm"
            />
          </n-form-item>
          <n-form-item class="login-animation1" label="Place of birth" path="place">
            <n-input v-model:value="form.place" placeholder="Enter your birth place" />
          </n-form-item>

          <div class="login-animation2 space-y-2 mb-3 mt-3">
            <n-button
              :loading="loading"
              block
              class="!rounded-md !py-6"
              color="#5678DE"
              size="large"
              @click="handleSubmit"
            >
              Sign up
            </n-button>
          </div>
          <div class="login-animation3 space-y-4">
            <n-button block class="!rounded-md !justify-start !py-6" size="large" @click="onGoogle">
              <SvgIcon icon="devicon:google" />
              &nbsp; Continue with Google
            </n-button>
            <div class="text-[#0A0A0A] font-700 text-center">
              Already have an account?
              <n-button class="!font-bold" text @click="router.push('/login')">Log in</n-button>
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
  ::v-deep(.n-form-item .n-form-item-label) {
    padding-top: 10px;
    margin-bottom: 0 !important;
    height: auto !important;
    min-height: auto !important;
  }
</style>
