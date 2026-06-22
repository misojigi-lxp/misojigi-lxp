import type { LectureListResponse } from "@/types/lecture";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export async function getLectures(): Promise<LectureListResponse[]> {
  const response = await fetch(`${API_BASE_URL}/lectures`, {
    method: "GET",
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error("강의 목록을 불러오지 못했습니다.");
  }

  return response.json();
}