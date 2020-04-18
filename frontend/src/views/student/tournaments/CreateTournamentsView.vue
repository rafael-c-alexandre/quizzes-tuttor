<template>
  <v-card class="table">
    <v-card-title>
      <span>Create Tournament</span>

      <v-spacer />

      <v-btn color="primary" dark to="/">
        Close
      </v-btn>

      <v-btn
        color="primary"
        dark
        v-if="canCreate"
        @click="create"
        data-cy="createTournament"
      >
        Create
      </v-btn>
    </v-card-title>
    <v-card-text>
      <v-text-field
        data-cy="tournamentName"
        v-model="tournament.title"
        label="*Title"
      />
      <v-row>
        <v-col cols="12" sm="6">
          <v-text-field
            data-cy="availableDateText"
            v-if="maADate"
            v-model="tournament.availableDate"
            label="*Manual Available Date"
          />
          <v-datetime-picker
            v-if="!maADate"
            label="*Available Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.availableDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
          >
          </v-datetime-picker>
        </v-col>
        <v-col sm="1">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="manualADate"
                data-cy="manADate"
                >edit</v-icon
              >
            </template>
            <span>Manually Insert Date</span>
          </v-tooltip>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <v-text-field
            data-cy="conclusionDateText"
            v-if="maCDate"
            v-model="tournament.conclusionDate"
            label="*Manual Conclusion Date"
          />
          <v-datetime-picker
            v-if="!maCDate"
            label="*Conclusion Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.conclusionDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
          >
          </v-datetime-picker>
        </v-col>
        <v-col cols="12" sm="1">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="manualCDate"
                data-cy="manCDate"
                >edit</v-icon
              >
            </template>
            <span>Manually Insert Date</span>
          </v-tooltip>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" sm="6">
          <v-container>
            <p class="pl-0">Number of Questions</p>
            <v-btn-toggle
              v-model="tournament.numberOfQuestions"
              mandatory
              class="button-group"
            >
              <v-btn text value="5" data-cy="5questions">5</v-btn>
              <v-btn text value="10" data-cy="10questions">10</v-btn>
              <v-btn text value="20" data-cy="20questions">20</v-btn>
            </v-btn-toggle>
          </v-container>
        </v-col>
        <v-col cols="12" sm="6">
          <v-form>
            TOPICS
            <v-autocomplete
              data-cy="topics"
              v-model="tournament.topics"
              :items="topics"
              multiple
              return-object
              item-text="name"
              item-value="name"
            >
              <template v-slot:selection="data">
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
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/management/Tournament';
import Topic from '@/models/management/Topic';

@Component
export default class CreateTournamentsView extends Vue {
  tournament: Tournament = new Tournament();
  topics: Topic[] = [];
  maADate: boolean = false;
  maCDate: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  get canCreate(): boolean {
    //CHECKS IF ITS READY FOR Create
    return (
      !!this.tournament.conclusionDate &&
      !!this.tournament.conclusionDate &&
      !!this.tournament.availableDate &&
      this.tournament.topics.length > 0
    );
  }

  removeTopic(topic: Topic) {
    this.tournament.topics = this.tournament.topics.filter(
      element => element.id != topic.id
    );
  }

  manualADate() {
    this.maADate = !this.maADate;
  }

  manualCDate() {
    this.maCDate = !this.maCDate;
  }

  async create() {
    try {
      await RemoteServices.createTournament(this.tournament);
      await this.$router.push({ name: 'all-tournaments' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  close() {
    return;
  }
}
</script>
