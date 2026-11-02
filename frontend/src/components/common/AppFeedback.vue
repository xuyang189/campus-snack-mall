<template>
  <p v-if="message" class="feedback" :class="variantClass">{{ message }}</p>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: {
    type: String,
    default: '',
  },
  type: {
    type: String,
    default: 'info',
  },
})

const variantClass = computed(() => `feedback--${props.type}`)
</script>
