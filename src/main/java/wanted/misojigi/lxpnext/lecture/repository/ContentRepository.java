package wanted.misojigi.lxpnext.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.misojigi.lxpnext.lecture.domain.Content;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

	List<Content> findByLectureIdOrderBySortOrderAscIdAsc(Long lectureId);
}