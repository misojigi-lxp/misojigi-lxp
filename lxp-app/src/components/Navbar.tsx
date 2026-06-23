"use client";

import { useState, useRef, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import useAuthStore from "@/store/authStore";
import { logout } from "@/lib/api/auth";

export default function Navbar() {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const clearUser = useAuthStore((state) => state.clearUser);

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [showWithdraw, setShowWithdraw] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  async function handleLogout() {
    try {
      await logout();
    } finally {
      clearUser();
      router.push("/courses");
    }
  }

  // showWithdraw는 커밋 6에서 WithdrawModal과 연결
  void showWithdraw;

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 h-14 bg-white border-b border-gray-100 flex items-center px-8">
      {/* Logo */}
      <Link href="/lectures" className="flex items-center gap-2 mr-8">
        <span className="w-8 h-8 bg-violet-600 rounded-lg flex items-center justify-center text-white font-bold text-sm select-none">
          L
        </span>
        <span className="font-bold text-lg tracking-tight">Loop.</span>
      </Link>

      {/* Center nav links */}
      <div className="flex items-center gap-6 flex-1">
        <Link
          href="/lectures"
          className="text-sm text-gray-600 hover:text-gray-900 transition-colors"
        >
          강의 둘러보기
        </Link>
        <Link
          href="/enrollments"
          className="text-sm text-gray-600 hover:text-gray-900 transition-colors"
        >
          내 학습
        </Link>
      </div>

      {/* Right: 로그인 상태 분기 */}
      {user ? (
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={() => setDropdownOpen((prev) => !prev)}
            className="flex items-center gap-2 text-sm font-medium text-gray-700 hover:text-gray-900 transition-colors"
          >
            <span className="w-7 h-7 rounded-full bg-violet-100 text-violet-700 flex items-center justify-center text-xs font-bold select-none">
              {user.nickname[0]}
            </span>
            {user.nickname}
            <svg
              className={`w-4 h-4 text-gray-400 transition-transform ${dropdownOpen ? "rotate-180" : ""}`}
              fill="none" viewBox="0 0 24 24" stroke="currentColor"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
          </button>

          {dropdownOpen && (
            <div className="absolute right-0 mt-2 w-36 bg-white rounded-xl border border-gray-100 shadow-lg py-1 z-50">
              <button
                onClick={handleLogout}
                className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
              >
                로그아웃
              </button>
              <button
                onClick={() => { setDropdownOpen(false); setShowWithdraw(true); }}
                className="w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-red-50 transition-colors"
              >
                회원탈퇴
              </button>
            </div>
          )}
        </div>
      ) : (
        <Link
          href="/login"
          className="inline-flex items-center justify-center px-4 py-1.5 rounded-lg border border-violet-600 text-violet-600 text-sm font-medium bg-white hover:bg-violet-50 transition-colors"
        >
          로그인
        </Link>
      )}
    </nav>
  );
}
