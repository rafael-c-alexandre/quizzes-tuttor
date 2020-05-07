describe('Student changes dashboard privacy and see other dashboards', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.accessStudentDashboardPage();
  });

  afterEach(() => {
    cy.logout();
  });

  it('login checks his own dashboard', () => {
    //hack to make DOM become available to click logout
    cy.get('[data-cy="public"]').click();
    cy.get('[data-cy="private"]').click();
    //cy.checkDashboardInfo()
  });

  it('see other student dashboard info', () => {
    //hack to make DOM become available to click logout
    cy.get('[data-cy="users_dashboard"]').click();
    cy.contains('Student 616').click();
  });
});
