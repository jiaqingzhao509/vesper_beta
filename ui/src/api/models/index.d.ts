export type TokenInfo = {
  token?: string;
  expires?: number;
  user?: User;
} & R;

export type User = {
  id?: number;
  username?: string;
  nickname?: string;
  avatar?: string;
  status?: boolean;
  email?: string;
  chatLimit?: number;
  createTime?: string;
  birthdate?: string;
  place?: string;
  lat?: string;
  lng?: string;
  chart?: string;
};

export type R = {
  code: number;
  message: string;
  data: Object;
};

export interface ChatR {
  chatId?: string;
  message?: string;
  role?: 'user' | 'assistant' | 'system';
  createTime?: string;
}
