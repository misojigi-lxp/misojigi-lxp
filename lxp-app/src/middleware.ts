import { NextRequest, NextResponse } from "next/server";

const AUTH_PAGES = ["/login", "/register"];

// 로그인이 필요한 페이지
const PROTECTED_PAGES = ["/enrollments", "/goals", "/reviews", "/questions"];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const isLoggedIn = request.cookies.has("JSESSIONID");

  // 로그인 상태 → /login, /register 접근 시 /courses로 리다이렉트
  if (isLoggedIn && AUTH_PAGES.some((p) => pathname.startsWith(p))) {
    return NextResponse.redirect(new URL("/courses", request.url));
  }

  // 비로그인 상태 → 보호된 페이지 접근 시 /login으로 리다이렉트
  if (!isLoggedIn && PROTECTED_PAGES.some((p) => pathname.startsWith(p))) {
    return NextResponse.redirect(new URL("/login", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/login", "/register", "/enrollments/:path*", "/goals/:path*", "/reviews/:path*", "/questions/:path*"],
};
