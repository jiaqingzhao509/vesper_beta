import { defineStore } from 'pinia';
import { ChatState } from './chat';
import { formatToDateTime } from '@/utils/dateUtil';
import { Message } from '@/typings/chat';
import { getMessages } from '@/api/chat';

export const useChatStore = defineStore('chat-store', {
  state: (): ChatState =>
    <ChatState>{
      chatIsLoading: true,
      messages: [],
    },

  getters: {},

  actions: {
    async loadData() {
      try {
        getMessages()
          .then((res: any) => {
            this.messages = res.reverse();
          })
          .finally(() => {
            this.chatIsLoading = false;
          });
      } finally {
        this.chatIsLoading = false;
      }
    },

    /**
     * 新增消息
     */
    async addMessage(message: string, role: 'user' | 'assistant' | 'system', chatId: string) {
      const data = {
        chatId,
        role: role,
        message,
        createTime: formatToDateTime(new Date()),
      };
      this.messages.push(data);
      return data;
    },

    /**
     * 更新消息
     */
    async updateMessage(chatId: string | undefined, message: string, isError?: boolean) {
      const index = this.messages.findIndex((item) => item?.chatId == chatId);
      if (index !== -1) {
        this.messages[index].message = message;
        this.messages[index].isError = isError;
      }
    },

    /**
     * 删除消息
     */
    async delMessage(item: Message) {
      this.messages = this.messages.filter((i) => i.chatId !== item.chatId);
    },
  },
});
