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
          {{
            editSubmission && editSubmission.id === null
              ? 'New Question'
              : 'Edit Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editSubmission">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-textarea v-model="editSubmission.title" label="Title" data-cy="Title" />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editSubmission.content"
                label="Content"
                data-cy="Content"
              ></v-textarea>
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in editSubmission.options.length"
              :key="index"
              data-cy="options"
            >
              <v-switch
                v-model="editSubmission.options[index - 1].correct"
                class="ma-4"
                label="Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="editSubmission.options[index - 1].content"
                label="Content"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)" data-cy="cancelSubmissionButton"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveSubmission" data-cy="saveSubmissionButton" >Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';

import RemoteServices from '@/services/RemoteServices';
import Submission from '../../../models/management/Submission';
import Question from '@/models/management/Question';

@Component
export default class EditSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;

  editSubmission!: Submission;

  created() {
    this.editSubmission = new Submission(this.submission);


  }

  async saveSubmission() {
    if (
      this.editSubmission &&
      (!this.editSubmission.title || !this.editSubmission.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    if (this.editSubmission && this.editSubmission.id != null) {
      try {

        //const result = await RemoteServices.updateQuestion(this.editQuestion);
        //this.$emit('save-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editSubmission) {
      try {
        const result = await RemoteServices.createSubmission(this.editSubmission);
        this.$emit('save-submission', result);
        confirm('Question "' + this.editSubmission.title + '" submitted successfully!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

}
</script>
