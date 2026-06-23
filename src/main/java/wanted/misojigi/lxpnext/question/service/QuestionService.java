package wanted.misojigi.lxpnext.question.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.lecture.domain.Lecture;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;
import wanted.misojigi.lxpnext.lecture.repository.LectureRepository;
import wanted.misojigi.lxpnext.member.domain.Member;
import wanted.misojigi.lxpnext.member.domain.MemberStatus;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;
import wanted.misojigi.lxpnext.question.domain.Question;
import wanted.misojigi.lxpnext.question.domain.QuestionStatus;
import wanted.misojigi.lxpnext.question.dto.QuestionCreateRequest;
import wanted.misojigi.lxpnext.question.dto.QuestionDetailResponse;
import wanted.misojigi.lxpnext.question.dto.QuestionListResponse;
import wanted.misojigi.lxpnext.question.repository.QuestionRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, LectureRepository lectureRepository, MemberRepository memberRepository) {
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<QuestionListResponse> getQuestions(Long lectureId, Long memberId){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        boolean isInstructor = isInstructorOf(lecture, memberId);

        List<Question> questions =
                questionRepository.findByLectureIdAndStatus(lectureId, QuestionStatus.ACTIVE);

        // 질문 비공개 노출 규칙으로 필터
        List<Question> visibleQuestions = questions.stream()
                .filter(q -> isVisibleTo(q, memberId, isInstructor))
                .toList();

        Map<Long, String> nicknameMap = getNicknameMap(visibleQuestions);

        return visibleQuestions.stream()
                .map(q -> QuestionListResponse.from(q, nicknameMap.get(q.getWriterId())))
                .toList();
    }

    private boolean isInstructorOf(Lecture lecture, Long memberId) {
        if (memberId == null) {
            return false;
        }
        return lecture.getInstructorId().equals(memberId);
    }

    private boolean isVisibleTo(Question question, Long memberId, boolean isInstructor) {
        if (!question.isPrivate()) {
            return true;
        }
        if (memberId == null) {
            return false;
        }
        return question.isWrittenBy(memberId) || isInstructor;
    }

    private Map<Long, String> getNicknameMap(List<Question> questions) {
        List<Long> writerIds = questions.stream()
                .map(Question::getWriterId)
                .distinct()
                .toList();

        return memberRepository.findAllById(writerIds).stream()
                .collect(Collectors.toMap(Member::getMemberId, Member::getNickname));
    }

    @Transactional(readOnly = true)
    public QuestionDetailResponse getQuestion(Long questionId, Long memberId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        if (question.isDeleted()) {
            throw new BusinessException(ErrorCode.QUESTION_DELETED);
        }

        if (question.isPrivate()) {
            if (memberId == null) {
                throw new BusinessException(ErrorCode.QUESTION_ACCESS_DENIED);
            }
            Lecture lecture = lectureRepository.findById(question.getLectureId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

            boolean canAccess = question.isWrittenBy(memberId) || isInstructorOf(lecture, memberId);
            if (!canAccess) {
                throw new BusinessException(ErrorCode.QUESTION_ACCESS_DENIED);
            }
        }

        String writerNickname = memberRepository.findById(question.getWriterId())
                .map(Member::getNickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // BusinessException(ErrorCode.MEMBER_NOT_FOUND)로 변경 예정

        return QuestionDetailResponse.from(question, writerNickname);
    }

    @Transactional
    public Long createQuestion(Long memberId, QuestionCreateRequest request){
        memberRepository.findByMemberIdAndStatus(memberId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(request.lectureId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));
        if (lecture.getStatus() != LectureStatus.PUBLIC) {
            throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
        }

        Question question = Question.create(request.lectureId(), memberId, request.title(), request.content(), request.toVisibility());

        return questionRepository.save(question).getQuestionId();
    }
}
