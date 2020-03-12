package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class QuestionsByStudentService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @PersistenceContext
    EntityManager entityManager;

    //PpA - Feature 1
    @Transactional( isolation = Isolation.REPEATABLE_READ)
    public QuestionDto studentSubmitQuestion(int courseId, QuestionDto questionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        if (questionDto.getKey() == null) {
            int maxQuestionNumber = questionRepository.getMaxQuestionNumber() != null ?
                    questionRepository.getMaxQuestionNumber() : 0;
            questionDto.setKey(maxQuestionNumber + 1);
        }

        Question question = new Question(course, questionDto);
        question.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(question);
        QuestionDto qDto = new QuestionDto(question);
        qDto.setStatus("ONHOLD");
        return qDto;
    }

    //PpA - Feature 3
    public List<QuestionDto> findQuestionsSubmittedByStudent(int userID) {
        return questionRepository.findSubmittedQuestions(userID).stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    //PpA - Feature 2
    public void makeQuestionAvailable(QuestionDto question){
        question.setStatus("AVAILABLE");

    }
    public void makeQuestionRemoved(QuestionDto question){

        question.setStatus("REMOVED");
    }


    public QuestionDto teacherEvaluatesQuestion(User user, int questionId) {
        //user é prof?
        //o prof e a question estao no mesmo curso? -- nao ha course na questionDto...
        //AVALIAR COMO?: 2 funcoes

        if (!user.getRole().toString().equals("TEACHER")) {
            throw new TutorException(NOT_TEACHER_ERROR);
        }


        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId)); //erro errado
        QuestionDto qDto = new QuestionDto(question);

        Course course = question.getCourse();
        Set cexec = user.getCourseExecutions();

        for (CourseExecution f : (Iterable<CourseExecution>) cexec) {

            if (f.getCourse().equals(course)) {
                makeQuestionAvailable(qDto);

                return qDto;
            }

        }
        makeQuestionRemoved(qDto);
        return qDto;
    }
}
