describe('Teacher edits submissions walkthrough', () => {

  beforeEach(() => {
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
    cy.submitQuestion('Demo Question','What is the best subject in the Computer engineering course at IST?',['ES','AMS', 'GESTAO', 'LP'], true)
    cy.log('close dialog')
    cy.demoTeacherLogin()
    cy.accessTeacherSubmissionsPage()
  })

  afterEach(() => {
    cy.contains('Logout').click()
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM options WHERE content = \'ES\' or content = \'AMS\' or content = \'GESTAO\' or content = \'LP\'" ')
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM submissions WHERE title = \'Demo Question\'"')
  })

  it('login approves question and edits it', () => {
    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.')
    cy.log('close dialog')
    cy.editSubmissionByTeacher('Demo Question','Demo Question edited', 'What is the best subject in the Computer engineering course at IST-Alameda?',['ES','AMS', 'SD', 'LP'])
    cy.log('close dialog')
    cy.get('[data-cy="cancelSubmissionButton"]').click()
  });


  it('login rejects question and tries to edit it', () => {
    cy.evaluateSubmission('Demo Question',false,'Question is dumb.')
    cy.log('close dialog')
    cy.editSubmissionByTeacher('Demo Question','Demo Question edit', 'What is the best subject in the Computer engineering course at IST-Alameda?',['ES','AMS', 'SD', 'LP'])
    cy.closeErrorMessage()
    cy.log('close dialog')
    cy.get('[data-cy="cancelSubmissionButton"]').click()
  });
});
