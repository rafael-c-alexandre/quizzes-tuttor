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
          {{ 'Evaluate question " ' + submission.title + ' " ' }}
        </span>
      </v-card-title>
      <v-card-text class="text-left">
        <div>
          <span
            v-html="convertMarkDown(submission.content, submission.image)"
          />
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
      </v-card-text>
      <v-card-text>
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-row align="center">
                <v-col cols="6">
                  <v-subheader> Evaluation </v-subheader>
                </v-col>
                <v-col cols="6">
                  <v-select
                    :items="['Approve', 'Reject']"
                    menu-props="auto"
                    label="Select"
                    hint="Pick your decision here"
                    v-model="evaluateSubmission.status"
                    persistent-hint
                    single-line
                  ></v-select>
                </v-col>
              </v-row>
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="5"
                v-model="evaluateSubmission.justification"
                label="Justification"
                placeholder="Explain your decision here"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveSubmission">Mark</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';

import RemoteServices from '@/services/RemoteServices';
import Submission from '../../../models/management/Submission';
import Image from '@/models/management/Image';

@Component
export default class EvaluateSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;

  evaluateSubmission!: Submission;

  created() {
    this.evaluateSubmission = new Submission(this.submission);
  }

  async saveSubmission() {

    if (this.evaluateSubmission.status == 'Approve') {
      this.evaluateSubmission.teacherDecision = true;
      this.evaluateSubmission.status='APPROVED';
    }
    else if (this.evaluateSubmission.status == 'Reject') {
      this.evaluateSubmission.teacherDecision = false;
      this.evaluateSubmission.status='REJECTED';
    }
    if (
      this.evaluateSubmission &&
      (!this.evaluateSubmission.justification ||
        !this.evaluateSubmission.teacherDecision)
    ) {
      await this.$store.dispatch(
        'error',
        'Evaluation must have justification and new status'
      );
      return;
    }

    if (this.evaluateSubmission && this.evaluateSubmission.id != null) {
      try {
        const result = await RemoteServices.evaluateSubmission(
          this.evaluateSubmission
        );
        this.$emit('save-submission', result);
        confirm(
          'Question "' +
            this.evaluateSubmission.title +
            '" successfully marked!'
        );

      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
