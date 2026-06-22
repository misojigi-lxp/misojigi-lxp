"use client";

import { useState } from "react";
import type { Question } from "@/components/questions/QnaTab";
import QuestionItem from "@/components/questions/QuestionItem";

type QuestionListProps = {
  questions: Question[];
  currentUserId: string;
  onSelect: (id: number) => void;
};

export default function QuestionList({
  questions,
  currentUserId,
  onSelect,
}: QuestionListProps) {
  const [searchQuery, setSearchQuery] = useState("");

  const visibleQuestions = searchQuery
    ? questions.filter((q) => {
        const isHidden = q.authorId !== currentUserId && !q.isPublic;
        if (isHidden) return false;
        return q.title.toLowerCase().includes(searchQuery.toLowerCase());
      })
    : questions;

  return (
    <div>
      {/* Search + Register */}
      <div className="flex gap-3 mb-4">
        <div className="relative flex-1">
          <span className="absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="15"
              height="15"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              className="text-gray-400"
            >
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </span>
          <input
            type="text"
            placeholder="제목으로 검색 (예: use)"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2.5 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition"
          />
        </div>
        <button className="px-4 py-2.5 bg-violet-600 text-white text-sm font-medium rounded-lg hover:bg-violet-700 transition-colors flex-shrink-0">
          질문 등록
        </button>
      </div>

      {/* Count */}
      <p className="text-sm text-gray-500 mb-3">
        전체 질문{" "}
        <span className="font-semibold text-gray-700">{visibleQuestions.length}</span>개
      </p>

      {/* List */}
      {visibleQuestions.length === 0 ? (
        <div className="flex items-center justify-center py-16 text-sm text-gray-400">
          검색 결과가 없습니다.
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {visibleQuestions.map((q) => {
            const isHidden = q.authorId !== currentUserId && !q.isPublic;
            const isMine = q.authorId === currentUserId;
            return (
              <QuestionItem
                key={q.id}
                question={q}
                isHidden={isHidden}
                isMine={isMine}
                onClick={isHidden ? undefined : () => onSelect(q.id)}
              />
            );
          })}
        </div>
      )}
    </div>
  );
}
