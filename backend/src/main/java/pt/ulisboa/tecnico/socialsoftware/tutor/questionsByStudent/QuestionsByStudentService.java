package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

public class QuestionsByStudentService {

    public void studentSubmitQuestion() {

    }

    public void teacherEvaluatesQuestion(Question question){
        question.setNumberOfCorrect(5);
        return;
    }

}
