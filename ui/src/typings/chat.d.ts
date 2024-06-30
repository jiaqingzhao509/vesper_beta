export type Message = {
  id?: string;
  chatId?: string;
  role?: 'user' | 'assistant' | 'system';
  message?: string;
  isError?: boolean;
  createTime: string;
};
