import Link from "next/link";

export default function Navbar() {
  return (
    <nav className="fixed top-0 left-0 right-0 z-50 h-14 bg-white border-b border-gray-100 flex items-center px-8">
      {/* Logo */}
      <Link href="/courses" className="flex items-center gap-2 mr-8">
        <span className="w-8 h-8 bg-violet-600 rounded-lg flex items-center justify-center text-white font-bold text-sm select-none">
          L
        </span>
        <span className="font-bold text-lg tracking-tight">Loop.</span>
      </Link>

      {/* Center nav links */}
      <div className="flex items-center gap-6 flex-1">
        <Link
          href="/courses"
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

      {/* Right: Login button */}
      <Link
        href="/login"
        className="inline-flex items-center justify-center px-4 py-1.5 rounded-lg border border-violet-600 text-violet-600 text-sm font-medium bg-white hover:bg-violet-50 transition-colors"
      >
        로그인
      </Link>
    </nav>
  );
}
