describe('Teacher edits submissions walkthrough', () => {
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
    cy.contains('Logout').click();
  });

  it('login approves question and edits it', () => {
    cy.evaluateSubmission(
      'Demo Question',
      true,
      'Question well structured and scientifically correct.',
      [false, false, false, false]
    );
    cy.log('close dialog');
    cy.editSubmissionByTeacher(
      'Demo Question',
      'Demo Question edited',
      'What is the best subject in the Computer engineering course at IST-Alameda?',
      ['ES', 'AMS', 'SD', 'SO']
    );
    cy.log('close dialog');
    cy.exec(
      'psql tutordb < tests/e2e/specs/sql/deleteResubmittedSubmission.sql'
    );
  });

  it('login rejects question and tries to edit it', () => {
    cy.evaluateSubmission('Demo Question', false, 'Question is dumb.', [
      true,
      false,
      true,
      false
    ]);
    cy.log('close dialog');
    cy.editSubmissionByTeacher(
      'Demo Question',
      'Demo Question edit',
      'What is the best subject in the Computer engineering course at IST-Alameda?',
      ['ES', 'AMS', 'SD', 'SO']
    );
    cy.closeErrorMessage();
    cy.log('close dialog');
    cy.get('[data-cy="cancelSubmissionButton"]').click();
    cy.exec('psql tutordb < tests/e2e/specs/sql/deleteSubmission.sql');
  });
});
