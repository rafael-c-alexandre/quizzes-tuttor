<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="closeSubmissionDialog"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ submission.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-submission :submission="submission" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeSubmissionDialog"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Submission from '../../../models/management/Submission';
import ShowSubmission from '@/views/student/questions/ShowSubmission.vue';

@Component({
  components: {
    'show-submission': ShowSubmission
  }
})
export default class ShowSubmissionDialog extends Vue {
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeSubmissionDialog() {
    this.$emit('close-show-submission-dialog');
  }
}
</script>
