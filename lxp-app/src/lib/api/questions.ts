import type {
  QuestionListResponse,
  QuestionDetailResponse,
} from "@/types/question";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export async function getQuestions(
  lectureId: number
): Promise<QuestionListResponse[]> {
  const res = await fetch(`${BASE_URL}/questions?lectureId=${lectureId}`, {
    credentials: "include",
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error("질문 목록을 불러오지 못했습니다.");
  }

  return res.json();
}

export async function getQuestion(
  questionId: number
): Promise<QuestionDetailResponse> {
  const res = await fetch(`${BASE_URL}/questions/${questionId}`, {
    credentials: "include",
    cache: "no-store",
  });

  if (!res.ok) {
    if (res.status === 403) {
      throw new Error("비공개 질문입니다. 접근 권한이 없습니다.");
    }
    if (res.status === 404) {
      throw new Error("질문을 찾을 수 없거나 삭제된 질문입니다.");
    }
    throw new Error("질문을 불러오지 못했습니다.");
  }

  return res.json();
}