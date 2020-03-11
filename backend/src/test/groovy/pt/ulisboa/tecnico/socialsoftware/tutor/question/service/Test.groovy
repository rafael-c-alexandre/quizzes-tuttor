package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import spock.lang.Specification
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Test

class Test extends Specification {

    def adminService
    def setup(){
        adminService = new Test()
    }
    def "putinha"(){
        expect: false
    }
}
