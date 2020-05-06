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
    cy.contains('Logout').click()
    /*cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM options WHERE content = \'ES\' or content = \'AMS\' or content = \'GESTAO\' or content = \'LP\'" ')
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM submissions WHERE title = \'Demo Question\'"')
*/
  })
/*
  it('login evaluates a question with on hold status', () => {
    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.',[false,false,false,false])
    cy.log('close dialog')
  });*/

  it('login tries to evaluate question without providing justification', () => {
    cy.evaluateSubmission('Demo Question',false,'',[true,false,false,true])
    cy.closeErrorMessage()

    cy.log('close dialog')

    cy.get('[data-cy="cancelEvaluateButton"]').click()
  });

  it('login tries to evaluate a previously evaluated question', () => {
    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.', [false,false,false,false])
    cy.log('close dialog')

    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.',[false,false,false,false])
    cy.closeErrorMessage()

    cy.log('close dialog')

    cy.get('[data-cy="cancelEvaluateButton"]').click()
   });

});
