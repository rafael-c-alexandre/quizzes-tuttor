<template>
  <div class="container "  >

    <v-select
            label="Search student "   :items="names" v-model="defaultSelected" dense @change="changeUser(user)">
    </v-select>

    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestionSubmitted">
          <animated-number :number="stats.totalQuestionSubmitted" />
        </div>
        <div class="project-name">
          <p>Total Question Submitted</p>
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
        <div class="icon-wrapper" ref="totalQuestionsAvailable">
          <animated-number
                  :number="stats.totalQuestionsAvailable
             "
          />
        </div>
        <div class="project-name">
          <p>PTotal Questions Available in Quizzes Tutor</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageQuestionsApproved">
          <animated-number :number="stats.percentageQuestionsApproved">%</animated-number>
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
    defaultSelected: string | null = null;

    async created() {

      await this.$store.dispatch('loading');
      try {
        this.users = await RemoteServices.getPublicQuestionDashboardUsers();
        for (let u of this.users) this.names.push(u.name);
        this.stats = await RemoteServices.getStudentQuestionStats();
        this.defaultSelected = this.users[0].name
        this.names.sort()
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    async changeUser(newUser: StatsUser) {
      this.stats = await RemoteServices.getOtherStudentQuestionStats(newUser.id);
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