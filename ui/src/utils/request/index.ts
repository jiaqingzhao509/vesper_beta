import type { AxiosProgressEvent, AxiosResponse, GenericAbortSignal } from 'axios';
import request from './axios';
import { router } from '@/router';
import { useUserStore } from '@/store';

export interface HttpOption {
  url: string;
  data?: any;
  params?: any;
  method?: string;
  headers?: any;
  onDownloadProgress?: (progressEvent: AxiosProgressEvent) => void;
  onUploadProgress?: (progressEvent: AxiosProgressEvent) => void;
  signal?: GenericAbortSignal;
  beforeRequest?: () => void;
  afterRequest?: () => void;
  isTransformResponse?: boolean;
}

export interface Response<T = any> {
  result: T;
  msg: string | null;
  code: number;
}

function axios<T = any>({
  url,
  data,
  method,
  headers,
  onDownloadProgress,
  signal,
  beforeRequest,
  afterRequest,
  isTransformResponse,
}: HttpOption): any {
  const successHandler = (res: AxiosResponse<Response<T>>) => {
    const $message = window['$message'];

    if (res.status !== 200) {
      const message = res.data.msg ?? res.statusText;
      $message!.error(message);
      return Promise.reject(res.data);
    }

    if (isTransformResponse) {
      return res.data;
    }

    if (res.data.code === 401) {
      const message = res.data.msg ?? 'No operation permission';
      $message!.error(message);
      return Promise.reject(res.data);
    }

    if (res.data.code === 200) return res.data.result;

    // if (res.data.status === 'Unauthorized') {
    //   authStore.removeToken();
    //   window.location.reload();
    // }

    return Promise.reject(res.data);
  };

  const failHandler = async (error: any) => {
    console.error(error);
    const userStore = useUserStore();
    const $message = window['$message'];
    const { status } = error.response;
    if (status === 401) {
      $message!.error('Login is invalid, please login again');
      userStore.resetToken();
      userStore.resetUser();
      await router.push({ name: 'Login' });
      return;
    }

    if (error.response !== undefined && error.response.data != undefined) {
      $message!.error(error.response.data.msg ?? 'Interface request exception');
    }

    afterRequest?.();
    // throw new Error(error?.response.data.msg || 'Error');
    return Promise.reject(error?.response.data);
  };

  beforeRequest?.();

  method = method || 'GET';

  const params = Object.assign(typeof data === 'function' ? data() : data ?? {}, {});

  switch (method) {
    case 'get':
      return request
        .get(url, { params, signal, onDownloadProgress })
        .then(successHandler, failHandler);
    case 'post':
      return request
        .post(url, params, { headers, signal, onDownloadProgress })
        .then(successHandler, failHandler);
    case 'delete':
      return request
        .delete(url, { headers, signal, onDownloadProgress })
        .then(successHandler, failHandler);
    case 'put':
      return request
        .put(url, params, { headers, signal, onDownloadProgress })
        .then(successHandler, failHandler);
    default:
      break;
  }
}

export function get<T = any>(options: HttpOption): Promise<T | Response<T>> {
  options.method = 'get';
  return axios<T>(options);
}

export function post<T = any>(options: HttpOption): Promise<T | Response<T>> {
  options.method = 'post';
  return axios<T>(options);
}

export class http {
  constructor() {}
  static get(options: HttpOption) {
    options.method = 'get';
    return axios(options);
  }
  static post(options: HttpOption) {
    options.method = 'post';
    return axios(options);
  }
  static delete(options: HttpOption) {
    options.method = 'delete';
    return axios(options);
  }
  static put(options: HttpOption) {
    options.method = 'put';
    return axios(options);
  }
}
