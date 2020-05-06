describe('Student resubmits questions walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
    cy.submitQuestion('Demo Question','What is the best subject in the Computer engineering course at IST?',['ES','AMS', 'GESTAO', 'LP'], true)
    cy.log('close dialog')
    cy.logout()
    cy.demoTeacherLogin()
    cy.accessTeacherSubmissionsPage()
  })

  afterEach(() => {
    cy.contains('Logout').click()
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM options WHERE content = \'ES\' or content = \'AMS\' or content = \'GESTAO\' or content = \'LP\'" ')
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM submissions WHERE title = \'Demo Question\'"')

  })
/*
  it('login submits question, then it is rejected by teacher, then resubmits', () => {
    cy.evaluateSubmission('Demo Question',false,'Question sucks.')
    cy.log('close dialog')
    cy.logout()
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
    cy.reSubmitSubmission('Demo Question','Demo Question edited', 'What is the best subject in the Computer engineering course at IST-Alameda?',['ES','AMS', 'SD', 'LP'])
    cy.log('close dialog')

  });*/

  it('login submits question, then it is approved by teacher, then tries to resubmit', () => {
    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.')
    cy.log('close dialog')
    cy.logout()
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
    cy.reSubmitSubmission('Demo Question','Demo Question edited', 'What is the best subject in the Computer engineering course at IST-Alameda?',['ES','AMS', 'SD', 'LP'])
    cy.closeErrorMessage()
    cy.log('close dialog')
    cy.get('[data-cy="cancelSubmissionButton"]').click()

  });


});
