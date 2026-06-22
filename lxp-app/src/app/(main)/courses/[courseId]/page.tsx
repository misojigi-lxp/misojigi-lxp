"use client";

import { useState } from "react";
import Link from "next/link";
import Button from "@/components/ui/Button";
import ContentItem from "@/components/courses/ContentItem";
import QnaTab from "@/components/questions/QnaTab";
import ReviewTab from "@/components/reviews/ReviewTab";

const course = {
  title: "실전 React 18 완전 정복",
  instructor: "김지훈",
  rating: 4.8,
  lessons: 24,
  level: "중급",
  category: "프론트엔드",
  watermark: "실전",
};

const contents = [
  { id: 1, type: "영상" as const, title: "강의 소개와 개발 환경 설정", info: "08:24", status: "완료" as const },
  { id: 2, type: "파일" as const, title: "실습 예제 소스코드 안내.txt", info: "2.4 KB", status: "완료" as const },
  { id: 3, type: "영상" as const, title: "React 18이 가져온 변화", info: "12:10", status: "완료" as const },
  { id: 4, type: "영상" as const, title: "useState 깊이 이해하기", info: "18:30", status: "완료" as const },
  { id: 5, type: "파일" as const, title: "Hooks 핵심 정리 노트.txt", info: "5.1 KB", status: "다운로드" as const },
  { id: 6, type: "영상" as const, title: "useEffect 완전 정복", info: "22:15", status: "재생" as const },
];

const TABS = ["컨텐츠", "Q&A", "후기"] as const;
type Tab = (typeof TABS)[number];

export default function CourseDetailPage() {
  const [activeTab, setActiveTab] = useState<Tab>("컨텐츠");

  return (
    <div className="max-w-4xl mx-auto px-6 py-8 w-full">
      {/* Back link */}
      <Link
        href="/courses"
        className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-6 transition-colors"
      >
        <span>‹</span>
        <span>목록으로</span>
      </Link>

      {/* Course Header Card */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex items-center gap-6 mb-6">
        {/* Thumbnail */}
        <div className="w-24 h-24 rounded-xl bg-violet-100 flex items-center justify-center flex-shrink-0 overflow-hidden relative">
          <span className="text-4xl font-extrabold text-violet-300 select-none">
            {course.watermark}
          </span>
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
          <span className="inline-block bg-violet-100 text-violet-600 text-xs font-medium px-3 py-1 rounded-full mb-2">
            {course.category}
          </span>
          <h1 className="text-xl font-bold text-gray-900 mb-2">
            {course.title}
          </h1>
          <div className="flex items-center gap-2 text-sm text-gray-500 flex-wrap">
            <span>{course.instructor} 강사</span>
            <span className="text-gray-300">·</span>
            <span className="flex items-center gap-1">
              <span className="text-amber-400">★</span>
              <span>{course.rating}</span>
            </span>
            <span className="text-gray-300">·</span>
            <span>{course.lessons}차시</span>
            <span className="text-gray-300">·</span>
            <span>{course.level}</span>
          </div>
        </div>

        {/* CTA */}
        <Button className="px-6 py-3 flex-shrink-0">이어서 학습하기</Button>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-gray-200 mb-6">
        {TABS.map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 pb-3 text-sm transition-colors border-b-2 -mb-px ${
              activeTab === tab
                ? "border-violet-600 text-violet-600 font-semibold"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {activeTab === "컨텐츠" && (
        <div className="flex flex-col gap-3">
          {contents.map((item, i) => (
            <ContentItem
              key={item.id}
              index={i + 1}
              type={item.type}
              title={item.title}
              info={item.info}
              status={item.status}
            />
          ))}
        </div>
      )}

      {activeTab === "Q&A" && <QnaTab />}

      {activeTab === "후기" && <ReviewTab />}
    </div>
  );
}
