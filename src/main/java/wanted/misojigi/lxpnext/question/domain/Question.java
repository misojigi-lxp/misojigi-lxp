package wanted.misojigi.lxpnext.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import wanted.misojigi.lxpnext.common.domain.BaseEntity;

@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private QuestionVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuestionStatus status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Question(){}

    private Question(Long lectureId, Long writerId, String title, String content,
            QuestionVisibility visibility) {
        this.lectureId = lectureId;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.visibility = visibility;
        this.status = QuestionStatus.ACTIVE;
    }

    public static Question create(Long lectureId, Long writerId, String title, String content,
            QuestionVisibility visibility){
        return new Question(lectureId, writerId, title, content, visibility);
    }

    public boolean isDeleted() {
        return this.status == QuestionStatus.DELETED;
    }

    public boolean isPrivate() {
        return this.visibility == QuestionVisibility.PRIVATE;
    }

    public boolean isWrittenBy(Long memberId) {
        return this.writerId.equals(memberId);
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public QuestionVisibility getVisibility() {
        return visibility;
    }

    public Long getWriterId() {
        return writerId;
    }

    public Long getLectureId() {
        return lectureId;
    }
}
