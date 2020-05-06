<template>

  <div class="container ">
    <v-switch inset
            prepend-icon="fas fa-lock-open"
              color="#FFFFFF"
              label="Public stats?"
              v-if="currentUser "
              v-model="isPublic"
              single-line></v-switch>
    <v-select
      filled
      background-color="#FFFFFF"
      label="Search student"
      :items="names"
      v-model="selected"
      dense
      @change="changeUser()"
    >
    </v-select>

    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionSubmitted" data-cy="submitted">
          <animated-number :number="this.stats.totalQuestionsSubmitted" />
        </div>
        <div class="project-name">
          <p>Total Questions Submitted</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionsApproved">
          <animated-number :number="stats.totalQuestionsApproved" />
        </div>
        <div class="project-name">
          <p>Total Questions Approved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionOnHold">
          <animated-number :number="stats.totalQuestionsOnHold" />
        </div>
        <div class="project-name">
          <p>Total Questions on hold</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionOnHold">
          <animated-number :number="stats.totalQuestionsRejected" />
        </div>
        <div class="project-name">
          <p>Total Questions Rejected</p>
        </div>
      </div>

      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionsAvailable">
          <animated-number :number="stats.totalQuestionsAvailable" />
        </div>
        <div class="project-name">
          <p>Total Questions Available in Quizzes Tutor</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageQuestionsApproved">
          <animated-number :number="stats.percentageQuestionsApproved"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>% Questions Approved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageQuestionsRejected">
          <animated-number :number="stats.percentageQuestionsRejected"
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

  @Component({
  components: { AnimatedNumber }
})
export default class DashboardView extends Vue {
  stats: StudentSubmissionStats | null = null;
  users: StatsUser[] = [];
  names: string[] = [];
  user: string | null = null;
  selected: string | null = null;
  currentUser: boolean = true;
  isPublic: boolean = true;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.users = await RemoteServices.getPublicQuestionDashboardUsers();
      for (let u of this.users) this.names.push(u.name);
      this.stats = await RemoteServices.getStudentQuestionStats();
      this.names.sort();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeUser() {
    for (let i = 0; i < this.users.length; i++) {
      if (this.users[i].name == this.selected) {
        this.cleanStats();
        this.stats = await RemoteServices.getOtherStudentQuestionStats(this.users[i].id);
        this.currentUser = false;
      }
    }
  }

  async cleanStats() {
    this.stats = null;
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
