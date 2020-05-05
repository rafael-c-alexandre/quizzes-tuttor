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

import MakeQuestionAvailableDialog from '../../../src/views/teacher/studentQuestions/MakeQuestionAvailableDialog';

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
});

Cypress.Commands.add('listOpenTournaments', ()=>{
    cy.contains('Tournaments').click({force: true})
    cy.get('[data-cy="openButton"]').click({force: true})
});

Cypress.Commands.add('listClosedTournaments', ()=>{
    cy.contains('Tournaments').click({force: true})
    cy.get('[data-cy="closedButton"]').click({force: true})
});


Cypress.Commands.add('createTournament',(tournamentName, availableDate, conclusionDate
    , numberOfQuestions, topics) => {
    cy.get('[data-cy="createButton"]').click({force:true})
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
        .click({force:true})
})

Cypress.Commands.add('cancelTournament', (tournamentName) => {
    cy.contains(tournamentName)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="cancelTournament"]')
        .click({force:true})

});

Cypress.Commands.add('closeErrorMessage', () => {
    cy.get('[data-cy="error"]')
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


Cypress.Commands.add('accessStudentQuestionsPage', () => {
    cy.contains('Questions').click()
    cy.contains('Manage').click()
})

Cypress.Commands.add('accessTeacherSubmissionsPage', () => {
    cy.contains('Student Questions').click()
})

Cypress.Commands.add('submitQuestion', (title, content, options, checkCorrect) => {

    cy.get('[data-cy="createSubmissionButton"]').click()
    if (title !== '')cy.get('[data-cy="Title"]').type(title)
    if (content !== '')cy.get('[data-cy="Content"]').type(content)
    let optionFields = cy.get('[data-cy="options"]').first().type(options[0])
    for (let i=1; i < options.length ; i++) {
        optionFields.next().type(options[i])
    }
    if (checkCorrect) cy.get('[data-cy="options"]').get('[type="checkbox"]').first().check({force:true})
    cy.get('[data-cy="saveSubmissionButton"]').click()
})

Cypress.Commands.add('editSubmission', (oldTitle,newTitle, content) => {

    cy.contains(oldTitle)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 8)
        .find('[data-cy="editSubmission"]')
        .click()

    if (newTitle !== '')cy.get('[data-cy="Title"]').clear().type(newTitle)
    if (content !== '')cy.get('[data-cy="Content"]').clear().type(content)

    cy.get('[data-cy="saveSubmissionButton"]').click()
})



Cypress.Commands.add('showSubmission', (title) => {
    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 8)
        .find('[data-cy="showSubmission"]')
        .click()
    cy.get('[data-cy = "closeSubmissionButton"]').click()

})

Cypress.Commands.add('evaluateSubmission', (title, isApproved, justification) => {

    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 9)
        .find('[data-cy="evaluateSubmissionButton"]')
        .click()

    if (isApproved) cy.get('[data-cy="status"]').check( {force:true})
    if (justification !== '') cy.get('[data-cy="justification"]').type(justification)
    cy.get('[data-cy="saveEvaluationButton"]').click()
})

Cypress.Commands.add('makeQuestionAvailable', (title,makeAvailable) => {

    cy.contains(title)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 9)
      .find('[data-cy="makeQuestionAvailableButton"]')
      .click()

    if (makeAvailable) cy.get('[data-cy="yesButton"]').click()
    else cy.get('[data-cy="notButton"]').click()
})

Cypress.Commands.add('assertQuestionOnAvailable', (title) => {

    cy.get('[data-cy="managementButton"]').click()
    cy.contains('Questions').click()

    //assert
    cy.contains(title)
      .parent()
      .should('have.length', 1)
      .click()
    cy.get('[data-cy="closeQuestionButton"]').click()

})





