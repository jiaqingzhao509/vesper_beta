<script lang="ts" setup>
  import { SvgIcon } from '@/components/common';
  import { onMounted, ref } from 'vue';
  import { checkPlace, info, updateUser } from '@/api/auth';
  import { useMessage } from 'naive-ui';
  import { useUserStore } from '@/store';
  import { useRouter } from 'vue-router';
  import { useBasicLayout } from '@/hooks/useBasicLayout';
  import { isBlank } from '@/utils/is';

  const { isMobile } = useBasicLayout();
  onMounted(async () => {
    form.value = await info();
    loading.value = false;
  });
  const formRef = ref();
  const message = useMessage();
  const loading = ref(false);
  const placeLoading = ref(false);
  const form = ref<any>({});
  const userStore = useUserStore();
  const router = useRouter();

  const rules = {
    username: { required: true, message: 'Enter your email', trigger: 'blur' },
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
        placeLoading.value = true;
        checkPlace(form.value.place)
          .then((res: any) => {
            if (res == null) {
              message.error('Please check whether the geographical location entered is incorrect');
              return false;
            }
            form.value.lat = res.lat;
            form.value.lng = res.lng;
            message.success('Data acquisition success');
            placeLoading.value = false;
            return true;
          })
          .catch((err) => {
            console.log('err', err);
            return false;
          });
      },
    },
  };

  async function handleSubmit() {
    loading.value = true;
    if (form.value.newPassword !== form.value.rePassword) {
      loading.value = false;
      message.warning('The two passwords are inconsistent');
      return;
    }
    if (isBlank(form.value.place)) {
      message.warning('Please enter your birth place');
      loading.value = false;
      return;
    }
    await updateUser({
      ...form.value,
      password: form.value.newPassword,
    })
      .then(async () => {
        userStore.setUser({
          ...userStore.user,
          nickname: form.value.nickname,
          username: form.value.username,
        });
        message.success('Update user profile success,It takes effect upon re-login');
        await userStore.logout();
      })
      .catch(() => {
        message.success('Update user profile error');
      })
      .finally(() => {
        loading.value = false;
      });
  }
</script>

<template>
  <div
    :class="isMobile ? 'pt-10' : 'pt-[4vh]'"
    class="w-full h-full flex justify-center items-start"
  >
    <div class="w-[380px]">
      <div :class="isMobile ? 'text-xl' : 'text-3xl'" class="text-center mb-6 font-semibold">
        Account settings
      </div>
      <n-form ref="formRef" :model="form" :rules="rules" :show-feedback="false" size="large">
        <n-form-item class="login-animation1" label="Name" path="name">
          <n-input v-model:value="form.nickname" placeholder="Enter your name">
            <template #prefix>
              <n-icon color="#808695" size="18">
                <SvgIcon icon="material-symbols:person-outline" />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>
        <n-form-item class="login-animation1" label="Email" path="username">
          <n-input v-model:value="form.username" placeholder="Enter your email">
            <template #prefix>
              <n-icon color="#808695" size="18">
                <SvgIcon icon="material-symbols:person-outline" />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>
        <n-form-item class="login-animation2" label="Update password" path="password">
          <div class="flex flex-col gap-2 w-full">
            <n-input
              v-model:value="form.password"
              placeholder="Enter existing password"
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
              v-model:value="form.newPassword"
              placeholder="Enter new password"
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
              placeholder="Re-enter new password"
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
          <n-input
            v-model:value="form.place"
            :disabled="placeLoading"
            placeholder="Enter your birth place"
          />
        </n-form-item>

        <div class="login-animation2 mb-6 mt-3">
          <n-button
            :disabled="placeLoading"
            :loading="loading"
            block
            class="!rounded-md !py-6"
            color="#5678DE"
            size="large"
            @click="handleSubmit"
          >
            Save changes
          </n-button>
        </div>
        <div class="login-animation3 space-y-1">
          <div>Account type</div>
          <n-input disabled value="Basic" />
          <n-button
            block
            class="!rounded-md !py-6 !mt-3"
            color="#5678DE"
            size="large"
            @click="router.push('/plan')"
          >
            Upgrade account
          </n-button>
        </div>
      </n-form>
    </div>
  </div>
</template>

<style lang="less" scoped>
  ::v-deep(.n-input .n-input-wrapper) {
    padding-top: 3px;
    padding-bottom: 3px;
  }
  ::v-deep(.n-form-item .n-form-item-label) {
    padding-top: 10px;
    margin-bottom: 0 !important;
    height: auto !important;
    min-height: auto !important;
  }
</style>
