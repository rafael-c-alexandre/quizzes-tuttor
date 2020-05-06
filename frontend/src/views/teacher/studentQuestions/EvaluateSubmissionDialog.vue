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
          {{ 'Evaluate question " ' + evaluateSubmission.title + ' " ' }}
        </span>
      </v-card-title>
      <v-card-text class="text-left" v-if="evaluateSubmission">
        <div>
          <span
            v-html="
              convertMarkDown(
                evaluateSubmission.content,
                evaluateSubmission.image
              )
            "
          />
          <ul>
            <li v-for="option in evaluateSubmission.options" :key="option.number">
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
              <v-row>
                  <v-subheader> *Approve? </v-subheader>

                  <v-btn v-if='hasDecided && hasApproved' color="blue darken-1"  @click="approve" data-cy="approve1">
                      Yes
                  </v-btn>
                  <v-btn v-if='!hasApproved' @click="approve" data-cy="approve">
                    Yes
                  </v-btn>


                  <v-btn v-if='hasDecided && !hasApproved' color="blue darken-1" @click="reject" data-cy="reject1">
                    No
                  </v-btn>

                  <v-btn v-if='!hasDecided || hasApproved' @click="reject" data-cy="reject">
                    No
                  </v-btn>
              </v-row>
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                clearable
                outline
                rows="5"
                v-model="evaluateSubmission.justification"
                label="*Justification"
                placeholder="Explain your decision here"
                data-cy="justification"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

        <v-container >
          <v-col cols="3"  >Suggestions to change:</v-col>
      <v-row  no-gutters>
        <v-col >
          <v-checkbox v-model="titleInput" label="Title" value="yes" data-cy="sugestionTitle"></v-checkbox>
        </v-col>
        <v-col >
          <v-checkbox v-model="contentInput" label="Content" value="yes" data-cy="sugestionContent"></v-checkbox>
        </v-col >
        <v-col >
          <v-checkbox v-model="optionsInput" label="Options" value="yes" data-cy="sugestionOptions"></v-checkbox>
        </v-col >
        <v-col >
          <v-checkbox v-model="correctInput" label="Correct Option" value="yes" data-cy="sugestionCorrectOption"></v-checkbox>
        </v-col >
      </v-row>
        </v-container>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="cancelEvaluateButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="saveSubmission"
          data-cy="saveEvaluationButton"
          >Mark</v-btn
        >
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
export default class submissionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;

  evaluateSubmission!: Submission;

  titleInput: string | null = null;
  contentInput: string | null = null;
  optionsInput: string | null = null;
  correctInput: string | null = null;

  changeTitle: boolean = false;
  changeContent: boolean = false;
  changeOptions: boolean = false;
  changeCorrect: boolean = false;

  hasDecided : boolean = false;
  hasApproved: boolean = false;

  created() {
    this.evaluateSubmission = new Submission(this.submission);
  }
  async approve(){
    this.hasDecided = true;
    this.hasApproved=true;
    this.evaluateSubmission.status = 'APPROVED';
    this.evaluateSubmission.teacherDecision = true;
  }


  async reject(){
    this.hasDecided = true;
    this.hasApproved=false;
    this.evaluateSubmission.status = 'REJECTED';
    this.evaluateSubmission.teacherDecision = false;
  }

  async saveSubmission() {

    if(this.evaluateSubmission && !this.hasDecided && !this.evaluateSubmission.justification) {
      await this.$store.dispatch('error', 'Error: Evaluation must have a decision and justification');
      return;
    }

    if(this.evaluateSubmission && !this.hasDecided) {
      await this.$store.dispatch('error', 'Error: Evaluation must have a decision');
      return;
    }

    if (this.evaluateSubmission && !this.evaluateSubmission.justification) {
      await this.$store.dispatch('error', 'Error: Evaluation must have justification');
      return;
    }

    if(this.evaluateSubmission && this.hasApproved && (this.correctInput || this.titleInput || this.contentInput || this.optionsInput)) {
      await this.$store.dispatch('error', 'Error: Evaluation cannot have suggestions when submission is approved');
      return;
    }

    this.getSuggestions()

    if (this.evaluateSubmission && this.evaluateSubmission.id != null) {
      try {
        const result = await RemoteServices.evaluateSubmission(
          this.evaluateSubmission
        );
        this.$emit('save-submission', result);
        alert(
          'Question "' +
            this.evaluateSubmission.title +
            '" successfully marked!'
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  getSuggestions() {
    this.evaluateSubmission.fieldsToImprove = [];

    if (this.titleInput == 'yes') {
      this.changeTitle = true;
      this.evaluateSubmission.fieldsToImprove.push('title');
    }
    if (this.contentInput == 'yes') {
      this.changeContent = true;
      this.evaluateSubmission.fieldsToImprove.push('content');

    }
    if (this.optionsInput == 'yes') {
      this.changeOptions = true;
      this.evaluateSubmission.fieldsToImprove.push('options');

    }
    if (this.correctInput == 'yes') {
      this.changeCorrect = true;
      this.evaluateSubmission.fieldsToImprove.push('correct option');


    }


  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
