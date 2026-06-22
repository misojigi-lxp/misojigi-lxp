package wanted.misojigi.lxpnext.lecture.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.misojigi.lxpnext.lecture.dto.LectureListResponse;
import wanted.misojigi.lxpnext.lecture.service.LectureService;

import java.util.List;

@RestController
@RequestMapping("/lectures")
public class LectureController {

	private final LectureService lectureService;

	public LectureController(LectureService lectureService) {
		this.lectureService = lectureService;
	}

	@GetMapping
	public ResponseEntity<List<LectureListResponse>> findAllLectures() {
		return ResponseEntity.ok(lectureService.findAllLectures());
	}
}