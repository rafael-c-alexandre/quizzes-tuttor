<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="submissions"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>
      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content, null)"
          @click="showSubmissionDialog(item)"
      /></template>
      <template >
        <v-list-item>
          <v-list-item-content>
            <v-chip   dark>
              ola
              </v-chip>
          </v-list-item-content>
        </v-list-item>
      </template>
      <template v-slot:item.status="{ item }">
        <v-chip v-if="item.status" :color="getStatusColor(item.status)" dark>{{
          item.status
        }}</v-chip>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showSubmissionDialog(item)"
              >visibility</v-icon
            >
          </template>
          <span>Show Submission</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="evaluateSubmission(item)"
              data-cy="evaluateSubmissionButton"
              >fas fa-marker</v-icon
            >
          </template>
          <span>Evaluate Submission</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="editSubmission(item)" data-cy="editSubmission">edit</v-icon>
          </template>
          <span>Edit Question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-submission-dialog
      v-if="currentSubmission"
      v-model="editSubmissionDialog"
      :submission="currentSubmission"
      v-on:save-submission="onSaveSubmission"
    />
    <show-submission-dialog
      v-if="currentSubmission"
      :dialog="submissionDialog"
      :submission="currentSubmission"
      v-on:close-show-submission-dialog="onCloseShowSubmissionDialog"
    />
    <evaluate-submission-dialog
      v-if="currentSubmission"
      v-model="evaluateSubmissionDialog"
      :submission="currentSubmission"
      v-on:save-submission="onSaveSubmission"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Submission from '@/models/management/Submission';
import Question from '@/models/management/Question';
import Topic from '@/models/management/Topic';
import Image from '@/models/management/Image';
import ShowSubmissionDialog from '@/views/student/questions/ShowSubmissionDialog.vue';
import EvaluateSubmissionDialog from '@/views/teacher/studentQuestions/EvaluateSubmissionDialog.vue';

@Component({
  components: {
    'show-submission-dialog': ShowSubmissionDialog,
    'evaluate-submission-dialog': EvaluateSubmissionDialog
  }
})
export default class StudentQuestionsView extends Vue {
  submissions: Submission[] = [];
  topics: Topic[] = [];
  currentSubmission: Submission | null = null;
  search: string = '';
  submissionDialog: boolean = false;
  evaluateSubmissionDialog: boolean = false;
  editSubmissionDialog: boolean = false;

  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'left' },
    {
      text: 'Topics',
      value: 'topicNames',
      align: 'center',
      sortable: false
    },
    {
      text: 'Justification',
      value: 'justification',
      align: 'center'
    },
    { text: 'Status', value: 'status', align: 'center' },

    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
    },
    {
      text: 'Image',
      value: 'imageUrl',
      align: 'center',
      sortable: false
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  @Watch('evaluateSubmissionDialog')
  closeError() {
    if (!this.evaluateSubmissionDialog) {
      this.currentSubmission = null;
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.topics, this.submissions] = await Promise.all([
        RemoteServices.getTopics(),
        RemoteServices.getAllSubmissions()
      ]);
      this.customSorter();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string, submission: Submission) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(submission)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  async handleFileUpload(event: File, submission: Submission) {
    if (submission.id) {
      try {
        const imageURL = await RemoteServices.uploadSubmissionImage(
          event,
          submission.id
        );
        submission.image = new Image();
        submission.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  onCloseShowSubmissionDialog() {
    this.submissionDialog = false;
  }
  showSubmissionDialog(submission: Submission) {
    this.currentSubmission = submission;
    this.submissionDialog = true;
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'ONHOLD') return 'orange';
    else return 'green';
  }

  evaluateSubmission(submission: Submission) {
    this.currentSubmission = submission;
    this.evaluateSubmissionDialog = true;
  }

  async onSaveSubmission(submission: Submission) {
    this.submissions = this.submissions.filter(q => q.id !== submission.id);
    this.submissions.unshift(submission);
    this.evaluateSubmissionDialog = false;
    this.currentSubmission = null;
    this.customSorter()
  }

  editSubmission(submission: Submission) {
    this.currentSubmission = submission;
    this.editSubmissionDialog = true;
  }
  customSorter() {
    let aux, a, b;
    for (let i = 0; i < this.submissions.length - 1; i++) {
      for (let j = i + 1; j < this.submissions.length ; j++) {
        a = this.submissions[i];
        b = this.submissions[j];
        if (a.status === 'REJECTED') {
          if (b.status === 'ONHOLD') {
            aux = this.submissions[i];
            this.submissions[i] = b;
            this.submissions[j] = aux;
          } else if (b.status == 'APPROVED') {
            aux = this.submissions[i];
            this.submissions[i] = b;
            this.submissions[j] = aux;
          }
        } else if (a.status == 'APPROVED') {
          if (b.status === 'ONHOLD') {
            aux = this.submissions[i];
            this.submissions[i] = b;
            this.submissions[j] = aux;
          }
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
