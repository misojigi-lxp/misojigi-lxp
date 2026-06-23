export interface LoginRequest {
  loginId: string;
  password: string;
}

export interface MemberResponse {
  memberId: number;
  loginId: string;
  nickname: string;
}
