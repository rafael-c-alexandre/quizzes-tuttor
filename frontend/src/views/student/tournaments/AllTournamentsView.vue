<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="tournaments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            to="/student/tournaments/create"
            dark
            data-cy="createButton"
            >New Tournament</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="cancel(item)"
              color="red"
              data-cy="cancelTournament"
              >delete</v-icon
            >
          </template>
          <span>Cancel Tournament</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="signTournament(item.id)"
              data-cy="signTournament"
              >edit</v-icon
            >
          </template>
          <span>Sign in Tournament</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class AllTournamentsView extends Vue {
  tournaments: Tournament[] = [];
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'title', align: 'left', width: '30%' },
    {
      text: 'Available Date',
      value: 'availableDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Conclusion Date',
      value: 'conclusionDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Topics',
      value: 'numberOfTopics',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%'
    },
    {
      text: 'State',
      value: 'state',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false,
      width: '20%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.listTournaments()).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async cancel(tournament: Tournament) {
    try {
      if (confirm('Are you sure you want to cancel this tournament?')) {
        await RemoteServices.cancelTournament(tournament.id);
        this.tournaments = (await RemoteServices.listTournaments()).reverse();
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async signTournament(tournamentId: number) {
    try {
      if (confirm('Are you sure you want to sign to this tournament?')) {
        await RemoteServices.enrollInTournament(tournamentId);
        this.tournaments = (
          await RemoteServices.listSignableTournaments()
        ).reverse();
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
