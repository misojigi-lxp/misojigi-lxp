import type { QuestionListResponse } from "@/types/question";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export async function getQuestions(
  lectureId: number
): Promise<QuestionListResponse[]> {
  const res = await fetch(`${BASE_URL}/questions?lectureId=${lectureId}`, {
    credentials: "include", // 세션 쿠키 전송 → 백엔드가 visibility 판단
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error("질문 목록을 불러오지 못했습니다.");
  }

  return res.json();
}