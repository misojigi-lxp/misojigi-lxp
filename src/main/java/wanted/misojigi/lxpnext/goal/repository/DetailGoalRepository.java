package wanted.misojigi.lxpnext.goal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wanted.misojigi.lxpnext.goal.domain.DetailGoal;

public interface DetailGoalRepository extends JpaRepository<DetailGoal, Long> {

    List<DetailGoal> findByLearningGoalIdOrderBySortOrderAsc(Long learningGoalId);
}
