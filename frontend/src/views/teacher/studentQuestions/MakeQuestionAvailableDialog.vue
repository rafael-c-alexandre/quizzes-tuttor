<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          Do you wish to make question '{{this.approvedSubmission.title}}' available?
        </span>
      </v-card-title>

      <p>
          </p>


      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy ="notButton"
          >No</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="makeQuestionAvailable"
          data-cy ="yesButton"
          >Yes</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Submission from '../../../models/management/Submission';

@Component
export default class MakeQuestionAvailableDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;

  approvedSubmission!: Submission;

  created() {
    this.approvedSubmission = new Submission(this.submission);

  }

  async makeQuestionAvailable() {

    if (this.approvedSubmission && this.approvedSubmission.id != null) {
      try {
        const result = await RemoteServices.makeSubmissionAvailable(
          this.approvedSubmission
        );
        this.$emit('save-submission', result);
        alert(
          'Question "' +
          this.approvedSubmission.title +
          '" successfully made public!'
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
