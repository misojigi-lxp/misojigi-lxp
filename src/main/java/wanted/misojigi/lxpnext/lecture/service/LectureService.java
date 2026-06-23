package wanted.misojigi.lxpnext.lecture.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import wanted.misojigi.lxpnext.member.domain.Member;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class LectureService {

	private static final String UNKNOWN_INSTRUCTOR_NICKNAME = "강사 정보 없음";
	private static final String DELETED_INSTRUCTOR_NICKNAME = "탈퇴한 강사";

	private final LectureRepository lectureRepository;
	private final ContentRepository contentRepository;
	private final MemberRepository memberRepository;

	public LectureService(
		LectureRepository lectureRepository,
		ContentRepository contentRepository,
		MemberRepository memberRepository
	) {
		this.lectureRepository = lectureRepository;
		this.contentRepository = contentRepository;
		this.memberRepository = memberRepository;
	}

	public List<LectureListResponse> findAllLectures() {
		List<Lecture> lectures = lectureRepository.findByStatusOrderByCreatedAtDesc(
			LectureStatus.PUBLIC
		);

		Set<Long> instructorIds = lectures.stream()
			.map(Lecture::getInstructorId)
			.collect(Collectors.toSet());

		Map<Long, String> nicknameByInstructorId = memberRepository.findAllById(instructorIds)
			.stream()
			.collect(Collectors.toMap(
				Member::getMemberId,
				this::getInstructorNickname
			));

		return lectures.stream()
			.map(lecture -> LectureListResponse.of(
				lecture,
				nicknameByInstructorId.getOrDefault(
					lecture.getInstructorId(),
					UNKNOWN_INSTRUCTOR_NICKNAME
				)
			))
			.toList();
	}

	public LectureDetailResponse findLecture(Long lectureId) {
		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

		if (lecture.getStatus() != LectureStatus.PUBLIC) {
			throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
		}

		String instructorNickname = memberRepository.findById(lecture.getInstructorId())
			.map(this::getInstructorNickname)
			.orElse(UNKNOWN_INSTRUCTOR_NICKNAME);

		List<ContentResponse> contents = contentRepository
			.findByLectureIdOrderBySortOrderAscIdAsc(lectureId)
			.stream()
			.map(ContentResponse::from)
			.toList();

		return LectureDetailResponse.of(
			lecture,
			instructorNickname,
			contents
		);
	}

	private String getInstructorNickname(Member instructor) {
		if (!instructor.isActive()) {
			return DELETED_INSTRUCTOR_NICKNAME;
		}

		return instructor.getNickname();
	}
}