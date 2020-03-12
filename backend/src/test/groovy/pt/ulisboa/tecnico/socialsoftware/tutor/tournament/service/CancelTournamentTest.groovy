package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class CancelTournamentTest extends Specification{

    def setup(){

    }

    def "cancel existing tournament by creator"(){


        expect:false
    }

    def "cancel non existing tournamet"(){

        expect:false
    }

    def "tournament canceled by non creator"(){

        expect: false
    }

    def "cancel non open tournament"(){

        expect: false
    }





}