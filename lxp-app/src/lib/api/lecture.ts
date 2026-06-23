import type {
  LectureDetailResponse,
  LectureListResponse,
} from "@/types/lecture";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function getErrorMessage(response: Response, fallbackMessage: string) {
  try {
    const errorBody = await response.json();

    if (typeof errorBody.message === "string") {
      return errorBody.message;
    }

    return fallbackMessage;
  } catch {
    return fallbackMessage;
  }
}

export async function getLectures(): Promise<LectureListResponse[]> {
  const response = await fetch(`${API_BASE_URL}/lectures`, {
    method: "GET",
    cache: "no-store",
  });

  if (!response.ok) {
    const message = await getErrorMessage(
      response,
      "강의 목록을 불러오지 못했습니다."
    );

    throw new Error(message);
  }

  return response.json();
}

export async function getLecture(
  lectureId: number
): Promise<LectureDetailResponse> {
  const response = await fetch(`${API_BASE_URL}/lectures/${lectureId}`, {
    method: "GET",
    cache: "no-store",
  });

  if (!response.ok) {
    const message = await getErrorMessage(
      response,
      "강의 정보를 불러오지 못했습니다."
    );

    throw new Error(message);
  }

  return response.json();
}