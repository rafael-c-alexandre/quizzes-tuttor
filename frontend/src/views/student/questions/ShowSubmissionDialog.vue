<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
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
        <v-btn dark color="blue darken-1" @click="$emit('dialog')" data-cy = "closeSubmissionButton">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
  import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Submission from '../../../models/management/Submission';
import ShowSubmission from '@/views/student/questions/ShowSubmission.vue';

@Component({
  components: {
    'show-submission': ShowSubmission
  }
})
export default class ShowSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;



}
</script>
