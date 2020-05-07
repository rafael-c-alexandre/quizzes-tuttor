describe('Student views questions dashboard walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
    cy.submitQuestion('Demo Question','What is the best subject in the Computer engineering course at IST?',['ES','AMS', 'GESTAO', 'LP'], true)
    cy.log('close dialog')
    cy.demoTeacherLogin()
    cy.accessTeacherSubmissionsPage()
    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.')
    cy.logout();
    cy.demoStudentLogin()

  })

  afterEach(() => {
    cy.logout();
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM options WHERE content = \'ES\' or content = \'AMS\' or content = \'GESTAO\' or content = \'LP\'" ')
    cy.exec('PGPASSWORD= psql -d tutordb -U ist189528 -h localhost -c "DELETE FROM submissions WHERE title = \'Demo Question\'"')
  })

  it('login checks his own dashboard', () => {
    cy.accessStudentDashboardPage()

    //hack to make DOM become available to click logout
    cy.get('[data-cy="submitted"]').click()
    //cy.checkDashboardInfo()



  });




});
