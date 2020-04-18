describe('Tournament Tests', () => {
    beforeEach(()=> {
        cy.demoStudentLogin()
    })

    afterEach( () => {
        cy.contains('Logout').click()
    })

    it('create tournament', () => {
        cy.createTournament('Tournament Title','2020-09-22 12:12','2020-10-22 12:12','5',['Adventure Builder'])

    });
/*
    it('login creates two tournaments and deletes it', () => {
        cy.createTournament('Tournament Title','2020-09-22 12:12','2020-10-22 12:12','5','Adventure Builder')

        cy.log('try to create with the same name')

        cy.createTournament('Tournament Title','2020-09-23 12:12','2020-10-24 12:12','5','Adventure Builder')

        cy.closeErrorMessage()

        cy.log('close dialog')
        //TODO mudar o botao de close
        cy.get('[data-cy="closeButton"]').click()

        cy.cancelTournament('Tournament Title')
    }); */
});
