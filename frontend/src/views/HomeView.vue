<template>
  <div class="stack">
    <section class="hero-banner hero-banner--dynamic">
      <div class="hero-orb hero-orb--a"></div>
      <div class="hero-orb hero-orb--b"></div>
      <div class="hero-orb hero-orb--c"></div>

      <div class="hero-cat" aria-hidden="true">
        <span class="hero-cat__ear hero-cat__ear--left"></span>
        <span class="hero-cat__ear hero-cat__ear--right"></span>
        <span class="hero-cat__face">
          <span class="hero-cat__eye hero-cat__eye--left"></span>
          <span class="hero-cat__eye hero-cat__eye--right"></span>
          <span class="hero-cat__nose"></span>
          <span class="hero-cat__blush hero-cat__blush--left"></span>
          <span class="hero-cat__blush hero-cat__blush--right"></span>
          <span class="hero-cat__whisker hero-cat__whisker--left"></span>
          <span class="hero-cat__whisker hero-cat__whisker--right"></span>
        </span>
        <span class="hero-cat__paw hero-cat__paw--left"></span>
        <span class="hero-cat__paw hero-cat__paw--right"></span>
      </div>

      <div class="hero-banner__content hero-banner__content--animated">
        <span class="section-kicker">Campus snack station</span>
        <h1>校园零食，轻松下单，宿舍生活的每一次嘴馋都有人接住。</h1>
        <p>
          从日常解馋的小零食，到熬夜提神的饮料速食，这里把校园里最常需要的味道收在一起。
          选购、下单、收货都尽量做得简单直接，让补货这件事不再打断你的节奏。
        </p>
        <div class="hero-banner__meta">
          <RouterLink class="primary-btn" to="/products">立即选购</RouterLink>
          <RouterLink class="ghost-btn" to="/cart">查看购物车</RouterLink>
        </div>
      </div>
    </section>

    <BasePanel title="为什么大家都在这里下单" subtitle="把常买、好找、好下单这三件事做到位。">
      <div class="stack">
        <div class="hero-card hero-card--accent reveal-card" style="--delay: 0s">
          <p>一站式补货</p>
          <h3>零食、饮料、速食集中展示，想买什么不用来回找。</h3>
          <span>围绕校园生活里最常见的即时需求整理商品，让浏览路径更短，补货动作更轻。</span>
        </div>
        <div class="hero-grid">
          <div class="hero-card reveal-card" style="--delay: 0.08s">
            <p>挑选更省心</p>
            <h3>分类清晰，筛选直接</h3>
            <span>想找膨化、糖果、饮料还是速食，都能快速定位，不被冗余信息干扰。</span>
          </div>
          <div class="hero-card reveal-card" style="--delay: 0.16s">
            <p>下单更顺手</p>
            <h3>从加入购物车到提交订单，一路简洁流畅</h3>
            <span>把复杂步骤藏起来，把真正需要你做的选择保留下来，减少来回跳转和重复操作。</span>
          </div>
        </div>
      </div>
    </BasePanel>

    <BasePanel title="商城公告" subtitle="活动安排、商品提醒和服务说明都会更新在这里。">
      <div v-if="notices.length" class="stack">
        <article
          v-for="(notice, index) in notices"
          :key="notice.id"
          class="notice-item notice-item--dynamic"
          :style="{ '--delay': `${index * 0.06}s` }"
        >
          <h3>{{ notice.title }}</h3>
          <p>{{ notice.content }}</p>
        </article>
      </div>
      <AppEmpty v-else description="当前暂无最新公告。" />
    </BasePanel>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import BasePanel from '@/components/common/BasePanel.vue'
import AppEmpty from '@/components/common/AppEmpty.vue'
import { fetchNoticeListApi } from '@/api/notice'

const notices = ref([])

onMounted(async () => {
  try {
    const res = await fetchNoticeListApi()
    notices.value = Array.isArray(res.data) ? res.data : []
  } catch {
    notices.value = []
  }
})
</script>
