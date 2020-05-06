<template>
  <div class="container">
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
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="stats.totalSignedTournaments" />
        </div>
        <div class="project-name">
          <p>Total Signed Tournaments</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.totalCreatedTournaments" />
        </div>
        <div class="project-name">
          <p>Total Created Tournaments</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.attendedTournaments" />
        </div>
        <div class="project-name">
          <p>Total Attended Tournaments</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageOfSeenQuestions">
          <animated-number
            :number="
              (stats.totalCorrectAnswers * 100) / stats.answersInTournaments
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
            :number="stats.totalCorrectAnswers"
          ></animated-number>
        </div>
        <div class="project-name">
          <p>Total Correct Answers</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="improvedCorrectAnswers">
          <animated-number
            :number="stats.answersInTournaments"
          ></animated-number>
        </div>
        <div class="project-name">
          <p>Total Answers</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import UserStats from '@/models/user/UserStats';
import UserTournamentStats from '@/models/statement/UserTournamentStats';
@Component({
  components: { AnimatedNumber }
})
export default class DashboardView extends Vue {
  stats: UserTournamentStats | null = null;
  users: UserStats[] = [];
  selected: string | null = null;
  names: string[] = [];
  user: string | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = null;
      this.users = await RemoteServices.getPublicDashboardUsers();
      for (let u of this.users) this.names.push(u.name);
      this.stats = await RemoteServices.getTournamentStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeUser() {
    await this.$store.dispatch('loading');
    try {
      for (let i = 0; i < this.users.length; i++) {
        if (this.users[i].name == this.selected) {
          await this.cleanStats();
          this.stats = await RemoteServices.getUserTournamentStats(
            this.users[i].id
          );
        }
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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
