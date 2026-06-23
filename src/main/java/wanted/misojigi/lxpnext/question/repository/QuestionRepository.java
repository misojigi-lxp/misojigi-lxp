package wanted.misojigi.lxpnext.question.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wanted.misojigi.lxpnext.question.domain.Question;
import wanted.misojigi.lxpnext.question.domain.QuestionStatus;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByLectureIdAndStatus(Long lectureId, QuestionStatus status);
}
