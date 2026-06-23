export type QuestionVisibility = "PUBLIC" | "PRIVATE";

export interface QuestionListResponse {
  questionId: number;
  title: string;
  writerNickname: string;
  createdAt: string; // LocalDateTime → JSON에선 문자열로 옴
  visibility: QuestionVisibility;
}