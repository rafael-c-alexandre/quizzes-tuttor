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
              : 'Re-submit Question'
          }}
        </span>
      </v-card-title>
      <v-sheet color="grey lighten-3">
      <v-card-text class="text-left" v-if="editSubmission && editSubmission.id !== null && editSubmission.fieldsToImprove.length !== 0">
        <v-icon>fas fa-exclamation-triangle</v-icon>
        <p></p>
        <div>
          <span >The teacher suggested you to change:</span>
          <p></p>
          <ul>
            <li v-for="field in editSubmission.fieldsToImprove" :key="field.number">
              <span
                      v-html="convertMarkDown(field, null)"
                      v-bind:class="[field ? 'font-weight-bold' : '']"
              />
            </li>
          </ul>
          <br />
        </div>
      </v-card-text>
      </v-sheet>

      <v-card-text class="text-left" v-if="editSubmission">
        <v-container grid-list-md fluid>
          <v-flex xs24 sm12 md8>
            <v-textarea
              clearable
              v-model="editSubmission.title"
              label="*Title"
              data-cy="Title"
            />
          </v-flex>
          <v-flex xs24 sm12 md12>
            <v-textarea
              clearable
              outline
              rows="10"
              v-model="editSubmission.content"
              label="*Content"
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
              clearable
              outline
              rows="10"
              v-model="editSubmission.options[index - 1].content"
              label="Content"
            ></v-textarea>
          </v-flex>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <v-btn
          @click="$emit('dialog', false)"
          data-cy="cancelSubmissionButton">Cancel
        </v-btn>
        <v-btn
          color="blue darken-1"
          @click="saveSubmission"
          data-cy="saveSubmissionButton"
          >Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Submission from '../../../models/management/Submission';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';

@Component
export default class EditSubmissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;

  editSubmission!: Submission;

  created() {
    console.log(this.submission.fieldsToImprove);
   this.updateSubmission();

  }

  @Watch('submission', { immediate: true, deep: true })
  updateSubmission() {
    this.editSubmission = new Submission(this.submission);
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async saveSubmission() {
    if (this.editSubmission && (!this.editSubmission.title || !this.editSubmission.content)) {
      await this.$store.dispatch('error','Question must have title and content');
      return;
    }

    if (this.editSubmission && this.editSubmission.id != null) {
      try {
        const result = await RemoteServices.reSubmitSubmission(this.editSubmission);
        this.$emit('save-submission', result);
        alert('Question "' + this.editSubmission.title + '" re-submitted successfully!');
      } catch (error) {await this.$store.dispatch('error', error);}
    } else if (this.editSubmission) {
      try {
        const result = await RemoteServices.createSubmission(this.editSubmission);
        this.$emit('save-submission', result);
        alert('Question "' + this.editSubmission.title + '" submitted successfully!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
