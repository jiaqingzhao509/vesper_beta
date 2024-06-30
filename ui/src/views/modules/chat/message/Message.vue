<script lang="ts" setup>
  import { ref } from 'vue';
  import { useMessage } from 'naive-ui';
  import TextComponent from './TextComponent.vue';
  import { useBasicLayout } from '../store/useBasicLayout';

  interface Props {
    dateTime?: string;
    text?: string;
    inversion?: boolean;
    error?: boolean;
    loading?: boolean;
  }

  interface Emit {
    (ev: 'delete'): void;
  }
  const props = defineProps<Props>();
  const emit = defineEmits<Emit>();

  const { isMobile } = useBasicLayout();
  const message = useMessage();
  const textRef = ref<HTMLElement>();
  const asRawText = ref(props.inversion);
  const messageRef = ref<HTMLElement>();
</script>

<template>
  <div
    ref="messageRef"
    :class="[{ 'flex-row-reverse': inversion }]"
    class="flex w-full mb-3 overflow-hidden"
  >
    <!--    <div-->
    <!--      :class="[inversion ? 'ml-2' : 'mr-2']"-->
    <!--      class="flex items-center justify-center flex-shrink-0 h-8 overflow-hidden rounded-full basis-8"-->
    <!--    >-->
    <!--      <AvatarComponent :image="inversion" />-->
    <!--    </div>-->
    <div :class="[inversion ? 'items-end' : 'items-start']" class="overflow-hidden text-sm">
      <!--      <p :class="[inversion ? 'text-right' : 'text-left']" class="text-xs text-[#b4bbc4]">-->
      <!--        {{ dateTime }}-->
      <!--      </p>-->
      <div
        :class="[inversion ? 'flex-col items-end' : 'flex-col justify-start items-start']"
        class="flex gap-1 mt-2"
      >
        <TextComponent
          ref="textRef"
          :as-raw-text="asRawText"
          :error="error"
          :inversion="inversion"
          :loading="loading"
          :text="text"
        />
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped></style>
