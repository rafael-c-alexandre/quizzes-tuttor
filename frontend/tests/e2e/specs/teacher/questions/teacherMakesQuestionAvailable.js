describe('Teacher makes submission available walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.accessStudentQuestionsPage();
    cy.submitQuestion(
      'Demo Question',
      'What is the best subject in the Computer engineering course at IST?',
      ['ES', 'AMS', 'GESTAO', 'LP'],
      true
    );
    cy.log('close dialog');
    cy.demoTeacherLogin();
    cy.accessTeacherSubmissionsPage();
  });

  afterEach(() => {
    cy.logout();
    cy.exec(
      'psql tutordb < tests/e2e/specs/sql/deleteSubmissionAndQuestion.sql'
    );
  });

  it('login makes an approved question available to the students', () => {
    cy.evaluateSubmission(
      'Demo Question',
      true,
      'Question well structured and scientifically correct.',
      [false, false, false, false]
    );
    cy.makeQuestionAvailable('Demo Question', true);

    cy.assertQuestionOnAvailable('Demo Question');
  });

  it('login tries make an on hold question  available', () => {
    cy.makeQuestionAvailable('Demo Question', true);
    cy.closeErrorMessage();

    // exit popup
    cy.get('[data-cy="notButton"]').click();

    cy.log('close dialog');
  });

  it('login tries make an already available question available', () => {
    cy.evaluateSubmission(
      'Demo Question',
      true,
      'Question well structured and scientifically correct.',
      [false, false, false, false]
    );
    cy.log('close dialog');
    cy.makeQuestionAvailable('Demo Question', true);
    cy.makeQuestionAvailable('Demo Question', true);
    cy.closeErrorMessage();

    // exit popup
    cy.get('[data-cy="notButton"]').click();

    cy.log('close dialog');
  });
});
