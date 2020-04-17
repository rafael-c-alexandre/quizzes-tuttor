<template>
  <div>
    <span v-html="convertMarkDown(submission.content, submission.image)" />
    <ul>
      <li v-for="option in submission.options" :key="option.number">
        <span
                v-if="option.correct"
                v-html="convertMarkDown('**[★]** ', null)"
        />
        <span
                v-html="convertMarkDown(option.content, null)"
                v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
      </li>
    </ul>
    <br />
  </div>
</template>

<script lang="ts">
  import { Component, Vue, Prop } from 'vue-property-decorator';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';

  import Image from '@/models/management/Image';
  import Submission from '@/models/management/Submission';

  @Component
  export default class ShowSubmission extends Vue {
    @Prop({ type: Submission, required: true }) readonly submission!: Submission;

    convertMarkDown(text: string, image: Image | null = null): string {
      return convertMarkDown(text, image);
    }
  }
</script>

