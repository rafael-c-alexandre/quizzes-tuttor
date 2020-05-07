describe('Teacher evaluates submissions walkthrough', () => {

  beforeEach(() => {
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
    cy.submitQuestion('Demo Question','What is the best subject in the Computer engineering course at IST?',['ES','AMS', 'GESTAO', 'LP'], true)
    cy.log('close dialog')
    cy.demoTeacherLogin()
    cy.accessTeacherSubmissionsPage()
  })

  afterEach(() => {
    cy.logout();
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM options WHERE content = \'ES\' or content = \'AMS\' or content = \'GESTAO\' or content = \'LP\'" ')
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM submissions WHERE title = \'Demo Question\'"')
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM questions WHERE title = \'Demo Question\'"')


  })

  it('login makes an approved question available to the students', () => {
    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.', [false,false,false,false])
    cy.makeQuestionAvailable('Demo Question', true);

    cy.assertQuestionOnAvailable('Demo Question');
  });


  it('login tries make an on hold question  available', () => {

    cy.makeQuestionAvailable('Demo Question', true);
    cy.closeErrorMessage()

    // exit popup
    cy.get('[data-cy="notButton"]').click()

    cy.log('close dialog')

  });

  it('login tries make an already available question available', () => {

    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.',[false,false,false,false])
    cy.log('close dialog')
    cy.makeQuestionAvailable('Demo Question', true);
    cy.makeQuestionAvailable('Demo Question', true);
    cy.closeErrorMessage()

    // exit popup
    cy.get('[data-cy="notButton"]').click()

    cy.log('close dialog')

  });



});
