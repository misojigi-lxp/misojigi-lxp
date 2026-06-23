package wanted.misojigi.lxpnext.lecture.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.lecture.domain.Lecture;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;
import wanted.misojigi.lxpnext.lecture.dto.ContentResponse;
import wanted.misojigi.lxpnext.lecture.dto.LectureDetailResponse;
import wanted.misojigi.lxpnext.lecture.dto.LectureListResponse;
import wanted.misojigi.lxpnext.lecture.repository.ContentRepository;
import wanted.misojigi.lxpnext.lecture.repository.LectureRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LectureService {

	private final LectureRepository lectureRepository;
	private final ContentRepository contentRepository;

	public LectureService(
		LectureRepository lectureRepository,
		ContentRepository contentRepository
	) {
		this.lectureRepository = lectureRepository;
		this.contentRepository = contentRepository;
	}

	public List<LectureListResponse> findAllLectures() {
		return lectureRepository.findByStatusOrderByCreatedAtDesc(LectureStatus.PUBLIC)
			.stream()
			.map(LectureListResponse::from)
			.toList();
	}

	public LectureDetailResponse findLecture(Long lectureId) {
		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

		if (lecture.getStatus() != LectureStatus.PUBLIC) {
			throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
		}

		List<ContentResponse> contents = contentRepository.findByLectureIdOrderBySortOrderAsc(lectureId)
			.stream()
			.map(ContentResponse::from)
			.toList();

		return LectureDetailResponse.of(lecture, contents);
	}
}