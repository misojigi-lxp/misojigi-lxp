"use client";

import { useState } from "react";
import Link from "next/link";

type EnrolledCourse = {
  id: number;
  title: string;
  instructor: string;
  category: string;
  watermark: string;
  progress: number;
};

const enrolledCourses: EnrolledCourse[] = [
  {
    id: 1,
    title: "실전 React 18 완전 정복",
    instructor: "김지훈",
    category: "프론트엔드",
    watermark: "실전",
    progress: 65,
  },
  {
    id: 3,
    title: "TypeScript 타입 마스터",
    instructor: "이도현",
    category: "언어",
    watermark: "Ty",
    progress: 30,
  },
  {
    id: 5,
    title: "알고리즘 코딩테스트 대비",
    instructor: "한예슬",
    category: "CS",
    watermark: "알고",
    progress: 12,
  },
];

export default function EnrollmentsPage() {
  const [hiddenIds, setHiddenIds] = useState<number[]>([]);

  const visible = enrolledCourses.filter((c) => !hiddenIds.includes(c.id));

  return (
    <div className="max-w-3xl mx-auto px-6 py-10 w-full">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-1">내 학습</h1>
        <p className="text-sm text-gray-500">
          신청한 강의 {enrolledCourses.length}개 · 꾸준히 완강해봐요.
        </p>
      </div>

      {/* Course List */}
      <div className="flex flex-col gap-4">
        {visible.map((course) => (
          <div key={course.id} className="relative group">
            <Link href={`/courses/${course.id}`}>
              <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 flex items-center gap-5 hover:shadow-md transition-shadow cursor-pointer">
                {/* Thumbnail */}
                <div className="w-20 h-20 rounded-xl bg-violet-100 flex items-center justify-center flex-shrink-0 overflow-hidden">
                  <span className="text-2xl font-extrabold text-violet-300 select-none">
                    {course.watermark}
                  </span>
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <h3 className="font-bold text-gray-900 text-base mb-0.5 truncate">
                    {course.title}
                  </h3>
                  <p className="text-sm text-gray-400 mb-3">
                    {course.instructor} · {course.category}
                  </p>
                  {/* Progress bar */}
                  <div className="flex items-center gap-3">
                    <div className="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-violet-600 rounded-full transition-all"
                        style={{ width: `${course.progress}%` }}
                      />
                    </div>
                    <span className="text-sm font-semibold text-violet-600 flex-shrink-0 w-9 text-right">
                      {course.progress}%
                    </span>
                  </div>
                </div>

                {/* Hide button */}
                <button
                  onClick={(e) => {
                    e.preventDefault();
                    setHiddenIds((prev) => [...prev, course.id]);
                  }}
                  className="flex-shrink-0 px-3 py-1.5 border border-gray-200 text-gray-400 text-xs rounded-lg hover:border-gray-300 hover:text-gray-600 transition-colors"
                >
                  숨김
                </button>
              </div>
            </Link>
          </div>
        ))}

        {visible.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20 gap-3">
            <p className="text-sm text-gray-400">표시할 강의가 없습니다.</p>
            <Link
              href="/courses"
              className="text-sm text-violet-600 font-medium hover:underline"
            >
              강의 둘러보기 →
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
