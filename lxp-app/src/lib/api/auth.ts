import apiClient from "@/lib/axios";
import type { LoginRequest, SignupRequest, MemberResponse } from "@/types/auth";

export async function login(data: LoginRequest): Promise<MemberResponse> {
  const response = await apiClient.post<MemberResponse>("/auth/login", data);
  return response.data;
}

export async function signup(data: SignupRequest): Promise<MemberResponse> {
  const response = await apiClient.post<MemberResponse>("/members", data);
  return response.data;
}

export async function logout(): Promise<void> {
  await apiClient.post("/auth/logout");
}

export async function withdraw(): Promise<void> {
  await apiClient.delete("/members/me");
}
