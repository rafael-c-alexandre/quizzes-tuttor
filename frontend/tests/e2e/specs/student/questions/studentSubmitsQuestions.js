describe('Student submits questions walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login submits question and checks question information', () => {
    cy.submitQuestion('Demo Question1','What is the best subject in the Computer engineering course at IST?',['ES','AMS', 'GESTAO', 'LP'], true)
    cy.showSubmission('Demo Question1')

    cy.log('close dialog')

    //asserts
    cy.contains('Demo Question1')
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 8)

    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM options WHERE content = \'ES\' or content = \'AMS\' or content = \'GESTAO\' or content = \'LP\'" ')
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM submissions WHERE title = \'Demo Question1\'"')
  });

  it('login tries to submit question without correct option', () => {
    cy.submitQuestion('Demo Question1','What is the best subject in the Computer engineering course at IST?',['AMS', 'GESTAO','ES', 'LP'], false)
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
