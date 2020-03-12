package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;

@RestController
public class QuestionsByStudentController {

    @Autowired
    private  QuestionsByStudentService studentService;
}
