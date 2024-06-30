import { http } from '@/utils/request';
import { ChatR } from '@/api/models';
import { AxiosProgressEvent } from 'axios';

/**
 * @description: 聊天
 */
export function chat(
  data: ChatR,
  onDownloadProgress?: (progressEvent: AxiosProgressEvent) => void
) {
  return http.post({
    url: '/api/chat',
    data,
    onDownloadProgress: onDownloadProgress,
  });
}

export function getMessages() {
  return http.get({
    url: `/api/message/messages`,
  });
}

export function genSuggestion(data: ChatR) {
  return http.post({
    url: `/api/chat/auto-suggestion`,
    data,
  });
}

export function getSuggestion() {
  return http.get({
    url: `/api/chat/init-suggestion`,
  });
}
