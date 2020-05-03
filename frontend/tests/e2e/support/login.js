Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/')
  cy.get('[data-cy="adminButton"]').click()
  cy.contains('Administration').click()
  cy.contains('Manage Courses').click()
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/')
  cy.get('[data-cy="teacherButton"]').click()
  cy.contains('Management').click()
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/')
  cy.get('[data-cy="studentButton"]').click()
});

Cypress.Commands.add('logout', () => {
  cy.get('[data-cy="logoutButton"]').click();
});
