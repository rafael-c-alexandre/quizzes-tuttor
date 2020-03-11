package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NOT_FOUND;


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

    public List<QuestionDto> findQuestionsSubmittedByStudent(int userID) {
        return questionRepository.findSubmittedQuestions(userID).stream().map(QuestionDto::new).collect(Collectors.toList());
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
