import { LoginRequest, MemberResponse } from "@/types/auth";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export async function loginApi(body: LoginRequest): Promise<MemberResponse> {
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    throw new Error("로그인 정보가 일치하지 않습니다.");
  }

  return res.json() as Promise<MemberResponse>;
}

export async function logoutApi(): Promise<void> {
  const res = await fetch(`${BASE_URL}/auth/logout`, {
    method: "POST",
    credentials: "include",
  });

  if (!res.ok) {
    throw new Error("로그아웃에 실패했습니다. 다시 시도해 주세요.");
  }
}
