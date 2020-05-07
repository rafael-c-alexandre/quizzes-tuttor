describe('Student views questions dashboard walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.accessStudentQuestionsPage()
    cy.submitQuestion('Demo Question','What is the best subject in the Computer engineering course at IST?',['ES','AMS', 'GESTAO', 'LP'], true)
    cy.log('close dialog')
    cy.demoTeacherLogin()
    cy.accessTeacherSubmissionsPage()
    cy.evaluateSubmission('Demo Question',true,'Question well structured and scientifically correct.', [false,false,false,false])
    cy.logout();
    cy.demoStudentLogin()

  })

  afterEach(() => {
    cy.logout();
    cy.exec( 'psql tutordb < tests/e2e/specs/sql/deleteSubmission.sql')
  })

  it('login checks his own dashboard', () => {
    cy.accessDashboardPage()

    //hack to make DOM become available to click logout
    cy.get('[data-cy="submitted"]').click()
    //cy.checkDashboardInfo()



  });




});
