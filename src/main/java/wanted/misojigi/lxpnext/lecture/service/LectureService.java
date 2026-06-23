package wanted.misojigi.lxpnext.lecture.service;

import java.util.List;

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
import wanted.misojigi.lxpnext.member.domain.MemberStatus;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class LectureService {

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
		return lectureRepository.findByStatusOrderByCreatedAtDesc(LectureStatus.PUBLIC)
			.stream()
			.map(lecture -> LectureListResponse.of(
				lecture,
				findActiveInstructor(lecture.getInstructorId()).getNickname()
			))
			.toList();
	}

	public LectureDetailResponse findLecture(Long lectureId) {
		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

		if (lecture.getStatus() != LectureStatus.PUBLIC) {
			throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
		}

		Member instructor = findActiveInstructor(lecture.getInstructorId());

		List<ContentResponse> contents = contentRepository
			.findByLectureIdOrderBySortOrderAscIdAsc(lectureId)
			.stream()
			.map(ContentResponse::from)
			.toList();

		return LectureDetailResponse.of(
			lecture,
			instructor.getNickname(),
			contents
		);
	}

	private Member findActiveInstructor(Long instructorId) {
		return memberRepository
			.findByMemberIdAndStatus(instructorId, MemberStatus.ACTIVE)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
	}
}