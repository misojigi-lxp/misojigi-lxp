package wanted.misojigi.lxpnext.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.misojigi.lxpnext.lecture.domain.Lecture;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

	List<Lecture> findByStatusOrderByCreatedAtDesc(LectureStatus status);
}