describe('Tournament Tests', () => {
  beforeEach(() => {});

  afterEach(() => {});

  it('login checks tournaments list', () => {
    cy.exec(
      'psql tutordb < tests/e2e/specs/student/tournaments/sql/addTournaments.sql'
    );

    cy.demoStudentLogin();
    cy.listTournaments();
  });

  it('login checks closed tournaments list, ', () => {
    cy.listClosedTournaments();

    //asserts
    cy.contains('Torneio4')
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 3);
  });

  it('login tries to cancel a  closed tournament', () => {
    cy.listTournaments();
    cy.cancelTournament('Torneio4');
    cy.closeErrorMessage();
  });

  it('login tries to sign into a  closed tournament', () => {
    cy.listTournaments();
    cy.enrollTournament('Torneio4');
    cy.closeErrorMessage();
  });

  it('login tries to sign into an open tournament', () => {
    cy.listTournaments();
    cy.enrollTournament('Torneio2');
    cy.closeErrorMessage();
  });

  it('login creates two tournaments with same name and deletes it', () => {
    cy.listTournaments();

    cy.createTournament(
      'Tournament Title2',
      '2020-09-22 12:12',
      '2020-10-22 12:12',
      '5',
      ['Adventure Builder']
    );

    cy.log('try to create with the same name');

    cy.createTournament(
      'Tournament Title2',
      '2020-09-23 12:12',
      '2020-10-24 12:12',
      '5',
      ['Adventure Builder']
    );

    cy.closeErrorMessage();

    cy.log('close dialog');

    cy.get('[data-cy="closeButton"]').click();

    cy.listTournaments();

    cy.cancelTournament('Tournament Title2');
  });

  it('login creates tournament and tries to enroll twice', () => {
    cy.listTournaments();

    cy.createTournament(
      'Tournament Title2',
      '2020-09-22 12:12',
      '2020-10-22 12:12',
      '5',
      ['Adventure Builder']
    );

    cy.enrollTournament('Tournament Title2');

    cy.log('try to enroll twice');

    cy.enrollTournament('Tournament Title2');

    cy.closeErrorMessage();

    cy.log('close dialog');

    cy.exec(
      'psql tutordb < tests/e2e/specs/student/tournaments/sql/deleteTournaments.sql'
    );
  });

  it('login creates a tournament, enrolls in it and resolves its quizz', () => {
    cy.demoStudentLogin();

    cy.wait(2000);

    cy.listTournaments();

    cy.createTournament(
      'Tournament Title2',
      '2020-09-22 12:12',
      '2020-10-22 12:12',
      '5',
      ['Adventure Builder']
    );
    cy.wait(2000);
    cy.exec('psql tutordb < tests/e2e/specs/student/tournaments/sql/setDate.sql');
    cy.wait(2000);
    cy.exec(
      'psql tutordb < tests/e2e/specs/student/tournaments/sql/insertUser.sql'
    );

    cy.wait(2000);

    cy.listTournaments();

    cy.wait(1000);

    cy.enrollTournament('Tournament Title2');

    cy.wait(1000);

    cy.listOpenTournaments();
    cy.wait(1000);

    cy.startQuiz();

    cy.wait(1000);

    cy.get('[data-cy="logoutButton"]').click();
  });
});
