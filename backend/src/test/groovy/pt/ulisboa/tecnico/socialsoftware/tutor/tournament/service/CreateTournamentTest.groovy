package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.*

import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@DataJpaTest
class CreateTournamentTest extends Specification{

    @Autowired
    TournamentRepository tournamentRepository


    def tournamentDto
    def creationDate
    def availableDate
    def conclusionDate
    def formatter
    def adminService

    def setup(){
        /*formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        tournamentDto = new TournamentDto()
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now()

        tournamentDto.setCreationDate(creationDate.format(formatter))
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))

        tournamentDto.setState()

        */

    }

    def "sucsessfully create a tournament"(){
        given: "a tournamentDto"

        expect: false
    }

    def "empty tournament title"(){

        expect: false
    }

    def "invalid tournament date"(){

        expect: false
    }

    def "create tournament with no topics"(){

        expect: false
    }

    def "empty creation date"(){

        expect: false
    }

    def "empty available date"(){

        expect: false
    }

    def "empty conclusion date"(){

        expect: false
    }

}