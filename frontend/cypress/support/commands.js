// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />
Cypress.Commands.add('demoAdminLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
    cy.contains('Administration').click()
    cy.contains('Manage Courses').click()
})

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
})

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('listTournaments', ()=>{
    cy.contains('Tournaments').click()
    cy.contains('List').click()
    cy.contains('List').trigger('mouseover')
});

Cypress.Commands.add('listOpenTournaments', ()=>{
    cy.contains('Tournaments').click({force: true})
    cy.contains('Open').click({force: true})
});

Cypress.Commands.add('listClosedTournaments', ()=>{
    cy.contains('Tournaments').click()
    cy.contains('Closed').click()
    cy.contains('Closed').trigger('mouseover')
});


Cypress.Commands.add('createTournament',(tournamentName, availableDate, conclusionDate
    , numberOfQuestions, topics) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="tournamentName"]').type(tournamentName)
    cy.get('[data-cy="manADate"]').click()
    cy.get('[data-cy="availableDateText"]').type(availableDate)
    cy.get('[data-cy="manCDate"]').click()
    cy.get('[data-cy="conclusionDateText"]').type(conclusionDate)
    cy.get('[data-cy="topics"]').click()
    for(let i = 0 ; i < topics.length; i++)
        cy.contains(topics[i]).click()
    cy.get('[data-cy="5questions"]').click()
    cy.get('[data-cy="createTournament"]').click()
})

Cypress.Commands.add('enrollTournament',(tournamentName) => {
    cy.contains(tournamentName)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="signTournament"]')
        .click()
})

Cypress.Commands.add('cancelTournament', (tournamentName) => {
    cy.contains(tournamentName)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="cancelTournament"]')
        .click()

});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click()
})

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})


