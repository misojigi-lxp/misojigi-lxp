package wanted.misojigi.lxpnext.lecture.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;
import wanted.misojigi.lxpnext.lecture.dto.LectureListResponse;
import wanted.misojigi.lxpnext.lecture.repository.LectureRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LectureService {

	private final LectureRepository lectureRepository;

	public LectureService(LectureRepository lectureRepository) {
		this.lectureRepository = lectureRepository;
	}

	public List<LectureListResponse> findAllLectures() {
		return lectureRepository.findByStatusOrderByCreatedAtDesc(LectureStatus.PUBLIC)
			.stream()
			.map(LectureListResponse::from)
			.toList();
	}
}