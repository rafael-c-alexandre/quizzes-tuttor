<template>
  <v-card class="table">
    <v-card-title>
      <span>Create Tournament</span>

      <v-spacer />

      <v-btn color="primary" dark to="/">
        Close
      </v-btn>

      <v-btn color="primary" dark v-if="canCreate" @click="create">
        Create
      </v-btn>
    </v-card-title>
    <v-card-text>
      <v-text-field v-model="tournament.title" label="*Title" />
      <v-row>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="*Available Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.availableDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
          >
          </v-datetime-picker>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="*Conclusion Date"
            format="yyyy-MM-dd HH:mm"
            v-model="tournament.conclusionDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
          >
          </v-datetime-picker>
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
              <v-btn text value="5">5</v-btn>
              <v-btn text value="10">10</v-btn>
              <v-btn text value="20">20</v-btn>
            </v-btn-toggle>
          </v-container>
        </v-col>
        <v-col cols="12" sm="6">
          <v-form>
            TOPICS
            <v-autocomplete
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
  resp!: Tournament;
  topics: Topic[] = [];

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

  async create() {
    try {
      this.tournament.tournamentCreator = this.$store.getters.getUser;
      this.resp = await RemoteServices.createTournament(this.tournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  close() {
    return;
  }
}
</script>
