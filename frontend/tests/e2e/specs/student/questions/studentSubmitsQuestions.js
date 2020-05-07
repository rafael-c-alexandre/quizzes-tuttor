describe('Student submits questions walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login submits question and checks question information', () => {
    cy.submitQuestion('Demo Question','What is the best subject in the Computer engineering course at IST?',['ES','AMS', 'GESTAO', 'LP'], true)
    cy.showSubmission('Demo Question')

    cy.log('close dialog')

    //asserts
    cy.contains('Demo Question')
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 8)

    cy.exec('psql tutordb < tests/e2e/specs/sql/deleteSubmission.sql');
  });

  it('login tries to submit question without correct option', () => {
    cy.submitQuestion('Demo Question','What is the best subject in the Computer engineering course at IST?',['AMS', 'GESTAO','ES', 'LP'], false)
    cy.closeErrorMessage()

    cy.log('close dialog')

    cy.get('[data-cy="cancelSubmissionButton"]').click()
  });

  it('login tries to submit question without title', () => {
    cy.submitQuestion(' ','What is the best subject in the Computer engineering course at IST?',['AMS', 'GESTAO','ES', 'LP'], true)
    cy.closeErrorMessage()

    cy.log('close dialog')

    cy.get('[data-cy="cancelSubmissionButton"]').click()
  });



});
