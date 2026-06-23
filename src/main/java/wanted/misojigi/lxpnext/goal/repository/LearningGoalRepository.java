package wanted.misojigi.lxpnext.goal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.misojigi.lxpnext.goal.domain.LearningGoal;

public interface LearningGoalRepository extends JpaRepository<LearningGoal, Long> {
}
