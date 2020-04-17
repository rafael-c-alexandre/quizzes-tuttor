import Tournament from '@/models/management/Tournament';

export const signabletournament = new Tournament({
  id: 123,
  title: 'Torneio com inscricoes abertas de teste',
  creationDate: '',
  availableDate: '',
  conclusionDate: '',
  numberOfSignedUsers: 7,
  numberOfTopics: 0,
  numberOfQuestions: 0,

  questions: [],
  topics: [],
  signedUsers: [1,2,3,4,5,123]
});

export const openTournament = new Tournament({
  id: 124,
  title: 'Torneio Aberto de teste',
  creationDate: '',
  availableDate: '',
  conclusionDate: '',
  numberOfSignedUsers: 7,
  numberOfTopics: 0,
  numberOfQuestions: 0,

  questions: [],
  topics: [],
  signedUsers: [1,2,3,4,5,124]
});

export const closedTouranment = new Tournament({
  id: 123,
  title: 'Torneio Fechado de teste',
  creationDate: '',
  availableDate: '',
  conclusionDate: '',
  numberOfSignedUsers: 7,
  numberOfTopics: 0,
  numberOfQuestions: 0,

  questions: [],
  topics: [],
  signedUsers: [1,2,3,4,5,124]
});
