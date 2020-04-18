describe('Tournament Tests', () => {
    beforeEach(()=> {
        cy.demoStudentLogin()
        cy.listTournaments()
    })

    afterEach( () => {
        cy.contains('Logout').click()
    })

    it('login creates a tournament and deletes it', () => {
        cy.createTournament('Tournament Title1','2020-09-22 12:12','2020-10-22 12:12','5',['Adventure Builder'])

        cy.cancelTournament('Tournament Title1')

    });

    it('login creates two tournaments with same name and deletes it', () => {
        cy.createTournament('Tournament Title2','2020-09-22 12:12','2020-10-22 12:12','5',['Adventure Builder'])

        cy.log('try to create with the same name')

        cy.createTournament('Tournament Title2','2020-09-23 12:12','2020-10-24 12:12','5',['Adventure Builder'])

        cy.closeErrorMessage()

        cy.log('close dialog')

        cy.get('[data-cy="closeButton"]').click()

        cy.listTournaments()
        cy.cancelTournament('Tournament Title2')
    });
});
