<template>
  <div>
    <span v-html="convertMarkDown(submission.content, submission.image)" />
    <br />
    <ul>
      <li v-for="option in submission.options" :key="option.number">
        <span
          v-if="option.correct"
          v-html="convertMarkDown('**[★]** ' + option.content)"
          v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
        <span
          v-else
          v-html="convertMarkDown(option.content)"

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
