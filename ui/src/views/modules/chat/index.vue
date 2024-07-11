<script lang="ts" setup>
  import { computed, nextTick, onMounted, onUnmounted, onUpdated, Ref, ref } from 'vue';
  import { SvgIcon } from '@/components/common';
  import { v4 as uuidv4 } from 'uuid';
  import { chat, genSuggestion, getSuggestion } from '@/api/chat';
  import Message from './message/Message.vue';
  import { useBasicLayout } from '@/hooks/useBasicLayout';
  import { useScroll } from './store/useScroll';
  import { useChatStore } from './store/useChatStore';
  import { NButton } from 'naive-ui';
  import { router } from '@/router';

  const { isMobile } = useBasicLayout();
  const { scrollRef, contentRef, scrollToBottom, scrollToBottomIfAtBottom } = useScroll();
  const chatStore = useChatStore();
  let controller = new AbortController();
  const showPlanModal = ref<boolean>(false);
  const loading = ref<boolean>(false);
  const pageLoading = ref<boolean>(true);
  const suggestionLoading = ref<boolean>(false);
  const prompt = ref<string>('');
  const chatId = ref<string>('');
  const aiChatId = ref<string>('');
  const inputRef = ref<Ref | null>(null);
  const suggestions = ref<any>([
    "What does the stars say about me?",
    'Will I meet my true love soon?',
    "What should I manifest today?",
  ]);

  onMounted(async () => {
    await chatStore.loadData();
    suggestionLoading.value = true;
    getSuggestion()
      .then((res: any) => {
        if (res != null) {
          suggestions.value = res;
        }
      })
      .finally(() => {
        suggestionLoading.value = false;
      });
    await scrollToBottom();

    await nextTick(() => {
      pageLoading.value = false;
    });
  });

  const dataSources = computed(() => {
    scrollToBottom();
    return chatStore.messages;
  });

  async function onSuggestionChat(userMessage: string) {
    prompt.value = userMessage;
    await handleSubmit();
  }

  async function handleSubmit() {
    let message = prompt.value;
    if (loading.value) {
      return;
    }
    if (!message || message.trim() === '') {
      return;
    }
    controller = new AbortController();

    // user
    chatId.value = uuidv4();
    await chatStore.addMessage(message, 'user', chatId.value);

    loading.value = true;
    prompt.value = '';

    try {
      // ai
      await scrollToBottom();
      aiChatId.value = uuidv4();
      await scrollToBottom();
      await chatStore.addMessage('', 'assistant', aiChatId.value);
      await scrollToBottomIfAtBottom();
      await onChat(message);
    } finally {
      loading.value = false;
    }
  }

  async function onChat(userMessage: string) {
    try {
      await chat(
        {
          chatId: chatId.value,
          message: userMessage,
          role: 'user',
        },
        async ({ event }) => {
          const list = event.target.responseText.split('\n\n');

          let text = '';
          let isRun = true;
          list.forEach((i: any) => {
            if (i.startsWith('data:Error')) {
              isRun = false;
              text += i.substring(5, i.length);
              chatStore.updateMessage(aiChatId.value, text, true);
              return;
            }
            if (!i.startsWith('data:{')) {
              return;
            }

            const { done, message } = JSON.parse(i.substring(5, i.length));
            if (done) {
              suggestionLoading.value = true;
              console.log("Message being passed to genSuggestion:", userMessage);
              genSuggestion({ message: userMessage})
                .then((sugs: any) => {
                  if (sugs != null) {
                    suggestions.value = sugs;
                  }
                })
                .finally(() => {
                  suggestionLoading.value = false;
                });
              return;
            }
            text += message;
          });
          if (!isRun) {
            await scrollToBottomIfAtBottom();
            return;
          }
          await chatStore.updateMessage(aiChatId.value, text, false);
          await scrollToBottomIfAtBottom();
        }
      )
        .catch((e: any) => {
          loading.value = false;
          if (e.msg !== undefined) {
            chatStore.updateMessage(aiChatId.value, e.msg, true);
          }
          // if (e.startsWith('data:Error')) {
          //   chatStore.updateMessage(aiChatId.value, e.substring(5, e.length), true);
          // }

          if (e.msg !== undefined && e.msg.includes('plan')) {
            showPlanModal.value = true;
          }
        })
        .finally(() => {
          scrollToBottomIfAtBottom();
        });
    } catch {
      loading.value = false;
    }
  }

  function handleEnter(event: KeyboardEvent) {
    if (!isMobile.value) {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        handleSubmit();
      }
    } else {
      if (event.key === 'Enter' && event.ctrlKey) {
        event.preventDefault();
        handleSubmit();
      }
    }
  }

  function handleStop() {
    if (loading.value) {
      controller.abort();
      loading.value = false;
    }
  }

  const buttonDisabled = computed(() => {
    return loading.value;
  });

  const chatIsLoading = computed(() => {
    return chatStore.chatIsLoading;
  });

  onMounted(() => {
    chatStore.loadData();
    if (inputRef.value && !isMobile.value) {
      inputRef.value?.focus();
    }
  });

  onUnmounted(() => {
    if (loading.value) {
      controller.abort();
    }
  });

  onUpdated(() => {
    scrollToBottomIfAtBottom();
  });

  function onPlan() {
    showPlanModal.value = false;
    router.push('/plan');
  }
</script>

<template>
  <div
    :style="isMobile ? 'height: calc(100vh - 80px)' : 'height: calc(100vh - 140px)'"
    class="flex flex-col w-full overflow-hidden"
  >
    <main ref="contentRef" class="flex-1 overflow-hidden overflow-y-auto">
      <n-spin :show="pageLoading" class="min-h-full flex-1 py-4" description="Loading...">
        <div v-if="chatIsLoading" class="w-full h-full flex items-center justify-center">
          <n-spin :show="chatIsLoading" size="large" />
        </div>
        <div v-else ref="scrollRef" class="h-full w-full m-auto">
          <Message
            v-for="(item, index) of dataSources"
            :key="index"
            :date-time="item.createTime"
            :error="item.isError"
            :inversion="item.role !== 'assistant'"
            :loading="loading"
            :text="item.message"
          />
          <div class="sticky bottom-0 left-0 flex justify-center">
            <NButton v-if="loading" type="warning" @click="handleStop">
              <template #icon>
                <SvgIcon icon="ri:stop-circle-line" />
              </template>
              Stop Responding
            </NButton>
          </div>
        </div>
      </n-spin>
    </main>

    <n-modal v-model:show="showPlanModal">
      <n-card
        :bordered="false"
        aria-modal="true"
        closable
        role="dialog"
        size="huge"
        style="width: 500px"
        title="Upgrade to ask"
        @close="showPlanModal = false"
      >
        You’ve asked all 3 questions today. Upgrade to a premium account to ask unlimited questions!
        <template #footer>
          <div class="w-full">
            <n-button block type="primary" @click="onPlan">Upgrade Plan</n-button>
          </div>
        </template>
      </n-card>
    </n-modal>

    <footer :class="isMobile ? '' : 'mb-[2vh]'">
      <n-spin :show="suggestionLoading" class="mb-2 mt-2">
        <div :class="isMobile ? 'grid-cols-2' : 'grid-cols-3'" class="grid gap-3">
          <template v-for="(item, idx) in suggestions" :key="item">
            <n-button
              v-if="isMobile ? idx <= 1 : true"
              block
              class="!rounded-lg !py-6 cursor-pointer"
              color="#5678DE"
              ghost
              size="large"
              @click="onSuggestionChat(item)"
            >
              <n-ellipsis>{{ item }} →</n-ellipsis>
            </n-button>
          </template>
        </div>
      </n-spin>

      <div class="w-full m-auto">
        <div class="flex flex-col gap-2 w-full pt-3">
          <div>Ask a question</div>
          <n-input
            ref="inputRef"
            v-model:value="prompt"
            placeholder="Vesper could answer something about your future..."
            size="large"
            @keypress="handleEnter"
          >
            <template #suffix>
              <n-button
                :disabled="buttonDisabled"
                :loading="loading"
                size="small"
                text
                @click="handleSubmit"
              >
                <template #icon>
                  <SvgIcon icon="material-symbols:send" />
                </template>
              </n-button>
            </template>
          </n-input>
        </div>
      </div>
    </footer>
  </div>
</template>

<style lang="less" scoped>
  ::v-deep(.n-input .n-input-wrapper) {
    padding-top: 4px;
    padding-bottom: 4px;
  }
  ::v-deep(.n-layout) {
    background: transparent !important;
  }
</style>
