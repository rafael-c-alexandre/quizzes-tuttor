<template>
  <div class="container ">
    <v-row no-gutters>
      <v-col md="5" data-cy="changeButton"
        ><v-select
          filled
          background-color="#FFFFFF"
          label="Change Dashboard View"
          v-model="defaultView"
          :items="['Questions Statistics', 'Tournaments Statistics']"
          dense
          @change="changeMenu()"
        >
        </v-select
      ></v-col>
      <v-spacer></v-spacer>
      <v-col
        ><v-tooltip v-if="isPublic" bottom>
          <template v-slot:activator="{ on }">
            <v-btn data-cy="public" @click="changePrivacy" color="green" dark v-on="on"
              ><v-icon left>visibility</v-icon> Public</v-btn
            >
          </template>
          <span>Change info privacy</span>
        </v-tooltip>
        <v-tooltip v-if="!isPublic" bottom>
          <template v-slot:activator="{ on }">
            <v-btn data-cy="private" @click="changePrivacy" color="red" dark v-on="on"
              ><v-icon left>visibility_off</v-icon> Private</v-btn
            >
          </template>
          <span>Change info privacy </span>
        </v-tooltip>
      </v-col>
      <v-spacer></v-spacer>
      <v-col data-cy="users_dashboard" md="5">
        <v-select
          filled
          background-color="#FFFFFF"
          label="Search student"
          :items="names"
          v-model="selected"
          dense
          @change="changeUser()"
        >
        </v-select
      ></v-col>
    </v-row>

    <h1>Statistics</h1>
    <div v-if="tStats != null && !firstView" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="tStats.totalSignedTournaments" />
        </div>
        <div class="project-name">
          <p>Total Signed Tournaments</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="tStats.totalCreatedTournaments" />
        </div>
        <div class="project-name">
          <p>Total Created Tournaments</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="tStats.attendedTournaments" />
        </div>
        <div class="project-name">
          <p>Total Attended Tournaments</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageOfSeenQuestions">
          <animated-number
            :number="
              (tStats.totalCorrectAnswers * 100) / tStats.answersInTournaments
            "
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Average Score</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="correctAnswers">
          <animated-number
            :number="tStats.totalCorrectAnswers"
          ></animated-number>
        </div>
        <div class="project-name">
          <p>Total Correct Answers</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="improvedCorrectAnswers">
          <animated-number
            :number="tStats.answersInTournaments"
          ></animated-number>
        </div>
        <div class="project-name">
          <p>Total Answers</p>
        </div>
      </div>
    </div>
    <div v-if="qStats != null && firstView" class="stats-container">
      <div class="items">
        <div
          class="icon-wrapper"
          ref="totalQuestionSubmitted"
          data-cy="submitted"
        >
          <animated-number :number="qStats.totalQuestionsSubmitted" />
        </div>
        <div class="project-name">
          <p>Total Questions Submitted</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionsApproved">
          <animated-number :number="qStats.totalQuestionsApproved" />
        </div>
        <div class="project-name">
          <p>Total Questions Approved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionOnHold">
          <animated-number :number="qStats.totalQuestionsOnHold" />
        </div>
        <div class="project-name">
          <p>Total Questions on hold</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionOnHold">
          <animated-number :number="qStats.totalQuestionsRejected" />
        </div>
        <div class="project-name">
          <p>Total Questions Rejected</p>
        </div>
      </div>

      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionsAvailable">
          <animated-number :number="qStats.totalQuestionsAvailable" />
        </div>
        <div class="project-name">
          <p>Total Questions Available in Quizzes Tutor</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageQuestionsApproved">
          <animated-number :number="qStats.percentageQuestionsApproved"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>% Questions Approved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageQuestionsRejected">
          <animated-number :number="qStats.percentageQuestionsRejected"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>% Questions Rejected</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatsUser from '@/models/user/StatsUser';
import StudentSubmissionStats from '@/models/statement/StudentSubmissionStats';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import UserTournamentStats from '@/models/statement/UserTournamentStats';

@Component({
  components: { AnimatedNumber }
})
export default class DashboardView extends Vue {
  qStats: StudentSubmissionStats | null = null;
  tStats: UserTournamentStats | null = null;
  users: StatsUser[] = [];
  names: string[] = [];
  user: string | null = null;
  selected: string | null = null;
  currentUser: boolean = true;
  isPublic: boolean = true;
  firstView: boolean = true;
  defaultView: string = 'Questions';

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.qStats = await RemoteServices.getStudentQuestionStats();
      this.isPublic = (
        await RemoteServices.getUserPrivacyStatus()
      ).privacyStatus;
      if (!this.isPublic) {
        await this.changePrivacy();
        this.users = await RemoteServices.getPublicDashboardUsers();
        await this.changePrivacy();
      } else this.users = await RemoteServices.getPublicDashboardUsers();
      for (let u of this.users) this.names.push(u.name);
      this.names.sort();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeMenu() {
    await this.cleanStats();
    if (this.firstView) this.tStats = await RemoteServices.getTournamentStats();
    else this.qStats = await RemoteServices.getStudentQuestionStats();
    this.firstView = !this.firstView;
    console.log(this.firstView);
  }

  async changePrivacy() {
    this.isPublic = !this.isPublic;
    await RemoteServices.changeUserDashboardPrivacy();
  }

  async changeUser() {
    for (let i = 0; i < this.users.length; i++) {
      if (this.users[i].name == this.selected) {
        this.cleanStats();
        if (this.firstView)
          this.qStats = await RemoteServices.getOtherStudentQuestionStats(
            this.users[i].id
          );
        else
          this.tStats = await RemoteServices.getUserTournamentStats(
            this.users[i].id
          );
        this.currentUser = false;
      }
    }
  }

  async cleanStats() {
    this.qStats = this.tStats = null;
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
