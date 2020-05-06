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

      <template>
        <v-list-item>
          <v-list-item-content>
            <v-chip dark> </v-chip>
          </v-list-item-content>
        </v-list-item>
      </template>
      <template v-slot:item.status="{ item }">
        <v-chip v-if="item.status" :color="getStatusColor(item.status)" dark>{{
          item.status
        }}</v-chip>
      </template>
      <template v-slot:item.isAvailable="{ item }">
        <v-chip v-if="item.isAvailable" :color="getAvailableColor()" dark>{{
          item.isAvailable
          }}</v-chip>
      </template>
      <template v-slot:item.topics="{ item }">
        <edit-submission-topics
                :submission="item"
                :topics="topics"
                v-on:submission-changed-topics="onSubmissionChangedTopics"
        />
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
              >fas fa-pen-fancy</v-icon
            >
          </template>
          <span>Evaluate Submission</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="editSubmission(item)" data-cy="editSubmissionTeacher">edit</v-icon>
          </template>
          <span>Edit Submission</span>
        </v-tooltip>
            <v-tooltip bottom>
                <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="makeQuestionAvailable(item)"
              data-cy="makeQuestionAvailableButton"
              >open_in_browser</v-icon
            >
          </template>
          <span>Make question available</span>
            </v-tooltip>
      </template>
    </v-data-table>
    <edit-submission-by-teacher-dialog
      v-if="currentSubmission"
      v-model="editSubmissionByTeacherDialog"
      :submission="currentSubmission"
      v-on:save-submission="onSaveSubmission"
    />
    <show-submission-dialog
      v-if="currentSubmission"
      v-model="submissionDialog"
      :submission="currentSubmission"
      v-on:close-show-submission-dialog="onCloseShowSubmissionDialog"
    />
    <evaluate-submission-dialog
      v-if="currentSubmission"
      v-model="evaluateSubmissionDialog"
      :submission="currentSubmission"
      v-on:save-submission="onSaveSubmission"
    />
    <make-question-available-dialog
      v-if="currentSubmission"
      v-model="makeQuestionAvailableDialog"
      :submission="currentSubmission"
      v-on:save-submission="onExitQuestionAvailableDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Submission from '@/models/management/Submission';
import Question from '@/models/management/Question';
import Topic from '@/models/management/Topic';
import Image from '@/models/management/Image';
import ShowSubmissionDialog from '@/views/submission/ShowSubmissionDialog.vue';
import EvaluateSubmissionDialog from '@/views/teacher/studentQuestions/EvaluateSubmissionDialog.vue';
import MakeQuestionAvailableDialog from '@/views/teacher/studentQuestions/MakeQuestionAvailableDialog.vue';
import EditSubmissionByTeacherDialog from '@/views/teacher/studentQuestions/EditSubmissionByTeacherDialog.vue';
import EditSubmissionTopics from '@/views/submission/EditSubmissionTopics.vue';


@Component({
  components: {
    'show-submission-dialog': ShowSubmissionDialog,
    'make-question-available-dialog': MakeQuestionAvailableDialog,
    'evaluate-submission-dialog': EvaluateSubmissionDialog,
    'edit-submission-by-teacher-dialog': EditSubmissionByTeacherDialog,
    'edit-submission-topics': EditSubmissionTopics
  }
})
export default class StudentQuestionsView extends Vue {
  submissions: Submission[] = [];
  topics: Topic[] = [];
  currentSubmission: Submission | null = null;
  search: string = '';
  submissionDialog: boolean = false;
  evaluateSubmissionDialog: boolean = false;
  editSubmissionByTeacherDialog: boolean = false;
  makeQuestionAvailableDialog: boolean = false;

  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'left' },
    {
      text: 'Topics',
      value: 'topics',
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
      text: 'Image',
      value: 'imageUrl',
      align: 'center',
      sortable: false
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center'
    },
    {
      text: 'Available?',
      value: 'isAvailable',
      align: 'center'
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

  @Watch('makeQuestionAvailableDialog')
  closeAvailableDialog() {
    if (!this.makeQuestionAvailableDialog) {
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

  onSubmissionChangedTopics(submissionId: Number, changedTopics: Topic[]) {
    let submission = this.submissions.find(
            (submission: Submission) => submission.id == submissionId
    );
    if (submission) {
      submission.topics = changedTopics;
    }
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'ONHOLD') return 'orange';
    else return 'green';
  }
  getAvailableColor() {

    return 'blue';
  }

  evaluateSubmission(submission: Submission) {
    this.currentSubmission = submission;
    this.evaluateSubmissionDialog = true;
  }

  async onSaveSubmission(submission: Submission) {
    this.submissions = this.submissions.filter(q => q.id !== submission.id);
    this.submissions.unshift(submission);
    this.editSubmissionByTeacherDialog = false;
    this.currentSubmission = null;
    this.customSorter();
  }

  editSubmission(submission: Submission) {
    this.currentSubmission = submission;
    this.editSubmissionByTeacherDialog = true;
  }
  async onExitQuestionAvailableDialog() {
    this.currentSubmission = null;
    this.makeQuestionAvailableDialog = false;
    this.submissions = await RemoteServices.getAllSubmissions();
    this.customSorter();
  }

  makeQuestionAvailable(submission: Submission) {
    this.currentSubmission = submission;
    this.makeQuestionAvailableDialog = true;
  }

  customSorter() {
    let aux, a, b;
    for (let i = 0; i < this.submissions.length - 1; i++) {
      for (let j = i + 1; j < this.submissions.length; j++) {
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
