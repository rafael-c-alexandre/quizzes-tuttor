describe('Tournament Tests', () => {
    beforeEach(() => {
        cy.demoStudentLogin()
    })

    afterEach(() => {
        cy.contains('Logout').click()
    })

    it('create tournament', () => {
        cy.createTournament('TEST','2020-04-22 07:32','2020-04-23 07:32',['Adventure Builder','Amazon Silk','Allocation viewtype'])
    });

    it('sign in tournament', () => {
        cy.contains('Sign In').click()
        cy.contains().click()
    });
});
