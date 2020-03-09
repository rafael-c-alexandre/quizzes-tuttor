package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import spock.lang.Specification

class StudentSubmitQuestionServiceSpockTest extends Specification{
    def questionByStudentService

    def setup() {
        questionByStudentService = new QuestionsByStudentService()

    }

    def "the topic exists and create question"()  {
        //the question is created and status becomes on hold
        expect: false
    }

    def "the topic and question do not exist"()  {
        //the question and topic are created and status becomes on hold
        expect: false
    }

    def "the topic exists and question exists"()  {
        // exception is thrown
        expect: false
    }

    def "the topic name is empty"()  {
        // exception is thrown
        expect: false
    }

    def "the question name is empty"()  {
        // exception is thrown
        expect: false
    }

    def "option for question is empty"()  {
        // exception is thrown
        expect: false
    }


}