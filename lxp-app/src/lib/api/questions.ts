import { apiRequest } from "@/lib/api/client";
import type {
  QuestionListResponse,
  QuestionDetailResponse,
} from "@/types/question";

/** 강의별 질문 목록 조회 */
export function getQuestions(lectureId: number) {
  return apiRequest<QuestionListResponse[]>(`/questions?lectureId=${lectureId}`);
}

/** 질문 단건 조회 */
export function getQuestion(questionId: number) {
  return apiRequest<QuestionDetailResponse>(`/questions/${questionId}`);
}

/** 질문 수정 (작성자 본인만 — 권한은 백엔드가 검증) */
export function updateQuestion(
  questionId: number,
  body: { title?: string; content?: string },
) {
  return apiRequest<void>(`/questions/${questionId}`, { method: "PATCH", body });
}

/** 질문 삭제 (작성자 또는 강사 — 권한은 백엔드가 검증, soft delete) */
export function deleteQuestion(questionId: number) {
  return apiRequest<void>(`/questions/${questionId}`, { method: "DELETE" });
}

export function createQuestion(body: {
  lectureId: number;
  title: string;
  content: string;
  isPublic: boolean;
}) {
  return apiRequest<number>("/questions", { method: "POST", body });
}