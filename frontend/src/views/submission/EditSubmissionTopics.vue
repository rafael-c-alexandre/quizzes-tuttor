<template>
  <v-form>
    <v-autocomplete
      v-model="submissionTopics"
      :items="topics"
      multiple
      return-object
      item-text="name"
      item-value="name"
      @change="saveTopics"
      data-cy="topics"
    >
      <template v-slot:selection="data" >
        <v-chip
          v-bind="data.attrs"
          :input-value="data.selected"
          close
          @click="data.select"
          @click:close="removeTopic(data.item)"
        >
          {{ data.item.name }}
        </v-chip>
      </template>
      <template v-slot:item="data">
        <v-list-item-content>
          <v-list-item-title v-html="data.item.name" />
        </v-list-item-content>
      </template>
    </v-autocomplete>
  </v-form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import Submission from '@/models/management/Submission';

@Component
export default class EditSubmissionTopics extends Vue {
  @Prop({ type: Submission, required: true }) readonly submission!: Submission;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];

  submissionTopics: Topic[] = JSON.parse(
    JSON.stringify(this.submission.topics)
  );

  async saveTopics() {
    if (this.submission.id) {
      try {
        await RemoteServices.updateSubmissionTopics(
          this.submission.id,
          this.submissionTopics
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    this.$emit(
      'submission-changed-topics',
      this.submission.id,
      this.submissionTopics
    );
  }

  removeTopic(topic: Topic) {
    this.submissionTopics = this.submissionTopics.filter(
      element => element.id != topic.id
    );
    this.saveTopics();
  }
}
</script>
