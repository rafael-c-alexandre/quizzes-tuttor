describe('Tournament Tests', () => {
  beforeEach(() => {});

  afterEach(() => {});

  it('login checks tournaments list', () => {
    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments (id,available_date,conclusion_date,creation_date,title,course_execution_id,user_id) VALUES (1001 , \'2020-04-20 14:19:00\' , \'2020-08-20 14:20:00\' , \'2020-08-19 15:18:25\' , \'Torneio1\' , 11 , 676);" '
    );

    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments (id,available_date,conclusion_date,creation_date,title,course_execution_id,user_id) VALUES (1002 , \'2020-08-18 14:19:00\' , \'2020-08-25 14:20:00\' , \'2020-08-17 14:19:00\' , \'Torneio2\' , 11, 676);" '
    );
    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments (id,available_date,conclusion_date,creation_date,title,course_execution_id,user_id) VALUES (1004 , \'2020-08-18 18:00:00\' , \'2022-08-25 14:20:00\' , \'2020-08-17 14:19:00\' , \'Torneio5\' , 11, 676);" '
    );
    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments (id,available_date,conclusion_date,creation_date,title,course_execution_id,user_id) VALUES (1003 , \'2020-08-18 14:19:00\' , \'2020-08-19 14:20:00\' , \'2020-08-18 14:19:00\' , \'Torneio3\' , 11, 676);" '
    );
    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments (id,available_date,conclusion_date,creation_date,title,course_execution_id,user_id) VALUES (1005 , \'2020-04-17 10:19:00\' , \'2020-04-19 14:20:00\' , \'2020-04-16 14:19:00\' , \'Torneio4\' , 11, 676);" '
    );

    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments_signed_users VALUES (1001 , 700);" '
    );

    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments_signed_users VALUES (1002 , 676);" '
    );
    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments_signed_users VALUES (1003 , 676);" '
    );
    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments_signed_users VALUES (1004 , 676);" '
    );
    cy.exec(
      'PGPASSWORD=123 psql -d tutordb -U afonso -h localhost -c "INSERT INTO tournaments_signed_users VALUES (1005 , 676);" '
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
      'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "update quizzes set associated_tournament_id = NULL where associated_tournament_id = 2;" '
    );

    cy.exec(
      'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "delete from tournaments_questions where tournament_id = 2;" '
    );

    cy.exec(
      'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "delete from tournaments_signed_users where tournament_id = 2;" '
    );

    cy.exec(
      'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "delete from tournaments_topics where tournament_id = 2;" '
    );

    cy.exec(
      'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "delete from users_created_tournaments where created_tournaments_id = 2;" '
    );

    cy.exec(
      'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "delete from users_signed_tournaments where signed_tournaments_id = 2;" '
    );

    cy.exec(
      'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "delete from tournaments where title =\'Tournament Title2\';" '
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
    cy.exec(
        'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "update tournaments set available_date = \'2020-05-6 12:12\' where id =3;" '
    );
    cy.wait(2000);
    cy.exec(
        'PGPASSWORD=tgllvg99 psql -d tutordb -U pedro -h localhost -c "INSERT INTO tournaments_signed_users VALUES (3, 700);" '
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
