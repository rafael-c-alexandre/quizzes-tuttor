package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament ;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz ;


import java.sql.SQLException;
import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class StatsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TournamentRepository tournamentRepository;


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizStatsDto getQuizStats(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        QuizStatsDto quizStatsDto = new QuizStatsDto();

        int totalQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .count();

        int totalAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int uniqueQuestions = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .distinct().count();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect).count();

        int uniqueCorrectAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

        int totalAvailableQuestions = questionRepository.getAvailableQuestionsSize(course.getId());

        quizStatsDto.setTotalQuizzes(totalQuizzes);
        quizStatsDto.setTotalAnswers(totalAnswers);
        quizStatsDto.setTotalUniqueQuestions(uniqueQuestions);
        quizStatsDto.setTotalAvailableQuestions(totalAvailableQuestions);
        if (totalAnswers != 0) {
            quizStatsDto.setCorrectAnswers(((float)correctAnswers)*100/totalAnswers);
            quizStatsDto.setImprovedCorrectAnswers(((float)uniqueCorrectAnswers)*100/uniqueQuestions);
        }
        return quizStatsDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentStatsDto getTournamentStats(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        TournamentStatsDto tournamentStatsDto = new TournamentStatsDto();

        int totalSignedTournaments = user.getSignedTournaments().size();
        int createdTournaments = user.getNumberCreatedTournaments();


        int totalAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getAssociatedTournament() != null)
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        //signed tournaments in which answered at least 1 question
        int attendedTournaments = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getAssociatedTournament() != null)
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getQuestionAnswers().size() > 1)
                .count();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getAssociatedTournament() != null)
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        tournamentStatsDto.setTotalSignedTournaments(totalSignedTournaments);
        tournamentStatsDto.setTotalCreatedTournaments(createdTournaments);
        tournamentStatsDto.setAttendedTournaments(attendedTournaments);
        tournamentStatsDto.setTotalCorrectAnswers(correctAnswers);
        tournamentStatsDto.setAnswersInTournaments(totalAnswers);

        return tournamentStatsDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionStatsDto getStudentQuestionsStats(int userId) {


        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        StudentQuestionStatsDto statsDto = new StudentQuestionStatsDto();

        int totalQuestionsSubmitted = user.getSubmissions().size();

        int totalQuestionsApproved = (int) user.getSubmissions().stream()
                .filter(submission -> submission.getSubmissionStatus() == Submission.Status.APPROVED)
                .count();

        int totalQuestionsOnHold = (int) user.getSubmissions().stream()
                .filter(submission -> submission.getSubmissionStatus() == Submission.Status.ONHOLD)
                .count();

        int totalQuestionsRejected = (int) user.getSubmissions().stream()
                .filter(submission -> submission.getSubmissionStatus() == Submission.Status.REJECTED)
                .count();

        int totalQuestionsAvailable = (int) user.getSubmissions().stream()
                .filter(submission -> submission.getSubmissionStatus() == Submission.Status.APPROVED && submission.isMadeAvailable())
                .count();


        statsDto.setTotalQuestionsApproved(totalQuestionsApproved);
        statsDto.setTotalQuestionsOnHold(totalQuestionsOnHold);
        statsDto.setTotalQuestionsRejected(totalQuestionsRejected);
        statsDto.setTotalQuestionsAvailable(totalQuestionsAvailable);
        statsDto.setTotalQuestionsSubmitted(totalQuestionsSubmitted);

        if (totalQuestionsSubmitted != 0) {
            statsDto.setPercentageQuestionsApproved(((float)totalQuestionsApproved)*100/totalQuestionsSubmitted);
            statsDto.setPercentageQuestionsRejected(((float)totalQuestionsRejected)*100/totalQuestionsSubmitted);
        }
        return statsDto;
    }
}
