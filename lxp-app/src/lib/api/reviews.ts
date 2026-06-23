import type {
  ErrorResponse,
  ReviewCreateRequest,
  ReviewListResponse,
  ReviewResponse,
} from "@/types/review";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function getErrorMessage(response: Response, fallbackMessage: string) {
  try {
    const errorBody = (await response.json()) as ErrorResponse;

    if (errorBody.fieldErrors) {
      const firstFieldError = Object.values(errorBody.fieldErrors)[0];

      if (firstFieldError) {
        return firstFieldError;
      }
    }

    if (typeof errorBody.message === "string") {
      return errorBody.message;
    }

    return fallbackMessage;
  } catch {
    return fallbackMessage;
  }
}

export async function getReviews(
  lectureId: number
): Promise<ReviewListResponse[]> {
  const response = await fetch(`${API_BASE_URL}/lectures/${lectureId}/reviews`, {
    method: "GET",
    credentials: "include",
    cache: "no-store",
  });

  if (!response.ok) {
    const message = await getErrorMessage(
      response,
      "후기 목록을 불러오지 못했습니다."
    );

    throw new Error(message);
  }

  return response.json();
}

export async function createReview(
  lectureId: number,
  data: ReviewCreateRequest
): Promise<ReviewResponse> {
  const response = await fetch(`${API_BASE_URL}/lectures/${lectureId}/reviews`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const message = await getErrorMessage(
      response,
      "후기 등록에 실패했습니다."
    );

    throw new Error(message);
  }

  return response.json();
}