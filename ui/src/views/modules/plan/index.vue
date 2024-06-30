<script lang="ts" setup>
  import { useBasicLayout } from '@/views/modules/chat/store/useBasicLayout';
  import { ref } from 'vue';

  const { isMobile } = useBasicLayout();
  const active = ref('Basic');

  const plans = [
    {
      name: 'Basic',
      price: 12,
      features: [
        'Curabitur faucibus',
        'massa ut pretium maximus',
        'Sed posuere nisi',
        'Pellentesque eu nibh et neque',
        'Suspendisse a leo',
        'Praesent quis venenatis ipsum',
        'Duis non diam vel tortor',
      ],
    },
    {
      name: 'Startup',
      price: 35,
      features: [
        'Curabitur faucibus',
        'massa ut pretium maximus',
        'Sed posuere nisi',
        'Pellentesque eu nibh et neque',
        'Suspendisse a leo',
        'Praesent quis venenatis ipsum',
        'Duis non diam vel tortor',
      ],
    },
    {
      name: 'Enterprise',
      price: 60,
      features: [
        'Curabitur faucibus',
        'massa ut pretium maximus',
        'Sed posuere nisi',
        'Pellentesque eu nibh et neque',
        'Suspendisse a leo',
        'Praesent quis venenatis ipsum',
        'Duis non diam vel tortor',
      ],
    },
  ];
</script>

<template>
  <div
    :class="isMobile ? '' : 'mt-[2vh]'"
    class="flex h-full w-full flex-1 justify-center items-start gap-4"
  >
    <section class="py-14">
      <div class="max-w-screen-xl mx-auto px-4 text-gray-600 md:px-8">
        <div class="relative max-w-xl mx-auto sm:text-center">
          <h3 class="text-gray-800 text-3xl font-semibold sm:text-4xl"> Pricing for all sizes </h3>
          <div class="mt-3 max-w-xl">
            <p>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam efficitur consequat
              nunc.
            </p>
          </div>
        </div>
        <div
          class="mt-16 space-y-6 justify-center gap-6 sm:grid sm:grid-cols-2 sm:space-y-0 lg:grid-cols-3"
        >
          <div
            v-for="(plan, index) in plans"
            :key="index"
            class="relative flex-1 flex items-stretch flex-col p-8 rounded-xl border-2"
          >
            <div>
              <span class="text-[#5678DE] font-medium">
                {{ plan.name }}
              </span>
              <div class="mt-4 text-gray-800 text-3xl font-semibold">
                ${{ plan.price }} <span class="text-xl text-gray-600 font-normal">/mo</span>
              </div>
            </div>
            <ul class="py-8 space-y-3">
              <li
                v-for="(feature, idx) in plan.features"
                :key="idx"
                class="flex items-center gap-5"
              >
                <svg
                  class="h-5 w-5 text-[#5678DE]"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    clip-rule="evenodd"
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                    fill-rule="evenodd"
                  />
                </svg>
                {{ feature }}
              </li>
            </ul>
            <div class="flex-1 flex items-end">
              <n-button
                :disabled="active === plan.name"
                block
                class="!rounded-lg !py-5"
                color="#5678DE"
              >
                Select this plan
              </n-button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style lang="less" scoped></style>
