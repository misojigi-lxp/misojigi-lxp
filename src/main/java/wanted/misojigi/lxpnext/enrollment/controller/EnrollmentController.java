package wanted.misojigi.lxpnext.enrollment.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.misojigi.lxpnext.common.auth.LoginMember;
import wanted.misojigi.lxpnext.enrollment.dto.EnrollRequest;
import wanted.misojigi.lxpnext.enrollment.dto.EnrollmentResponse;
import wanted.misojigi.lxpnext.enrollment.service.EnrollmentService;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * 수강 신청.
     */
    @PostMapping
    public ResponseEntity<Void> enroll(
        @LoginMember Long memberId,
        @Valid @RequestBody EnrollRequest request
    ) {
        enrollmentService.enroll(memberId, request.lectureId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 본인 수강 전체 조회.
     */
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(memberId));
    }
}