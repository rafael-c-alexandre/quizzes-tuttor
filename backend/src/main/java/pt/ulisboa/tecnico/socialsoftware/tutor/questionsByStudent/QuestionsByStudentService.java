package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;


@Service
public class QuestionsByStudentService {

    public QuestionDto studentSubmitQuestion(QuestionDto questionDto) {

        return  null;

    }

    public int teacherEvaluatesQuestion(Question question){
        //verificacoes discutivelmente pertinentes
        if(question == null){
            return -1;
        }
        if(question.getStatus()== Question.Status.DISABLED){
            return -1;
        }

        return 0;
    }
}
