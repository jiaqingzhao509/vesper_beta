import { Message } from '@/typings/chat';

export interface ChatState {
  chatIsLoading: boolean;
  messages: Message[];
}
