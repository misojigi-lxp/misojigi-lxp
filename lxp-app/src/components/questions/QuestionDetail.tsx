"use client";

import { useEffect, useState } from "react";
import { getQuestion } from "@/lib/api/questions";
import type { QuestionDetailResponse } from "@/types/question";

type QuestionDetailProps = {
  questionId: number;
  onBack: () => void;
};

export default function QuestionDetail({ questionId, onBack }: QuestionDetailProps) {
  const [question, setQuestion] = useState<QuestionDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");
    getQuestion(questionId)
      .then(setQuestion)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [questionId]);

  return (
    <div>
      {/* Back */}
      <button
        onClick={onBack}
        className="mb-5 inline-flex items-center gap-1 text-sm text-gray-500 transition-colors hover:text-gray-700"
      >
        <span>‹</span>
        <span>질문 목록</span>
      </button>

      {loading ? (
        <p className="py-16 text-center text-sm text-gray-400">불러오는 중...</p>
      ) : error ? (
        <div className="flex items-center justify-center rounded-xl border border-gray-100 bg-white py-16 text-sm text-red-500">
          {error}
        </div>
      ) : question ? (
        <div className="rounded-xl border border-gray-100 bg-white p-6">
          {/* Badge + Actions */}
          <div className="mb-3 flex items-center justify-between">
            {question.visibility === "PUBLIC" ? (
              <span className="inline-flex items-center gap-1 rounded-full bg-green-50 px-2.5 py-0.5 text-xs font-medium text-green-600">
                🔓 공개
              </span>
            ) : (
              <span className="inline-flex items-center gap-1 rounded-full bg-gray-100 px-2.5 py-0.5 text-xs font-medium text-gray-500">
                🔒 비공개
              </span>
            )}

            <div className="flex items-center gap-2">
              <button
                type="button"
                className="rounded-md border border-gray-200 px-3 py-1 text-xs font-medium text-gray-600 transition-colors hover:bg-gray-50"
              >
                수정
              </button>
              <button
                type="button"
                className="rounded-md border border-red-200 px-3 py-1 text-xs font-medium text-red-500 transition-colors hover:bg-red-50"
              >
                삭제
              </button>
            </div>
          </div>

          {/* Title */}
          <h2 className="mb-2 text-lg font-bold text-gray-900">{question.title}</h2>

          {/* Meta */}
          <div className="mb-5 flex items-center gap-2 text-xs text-gray-400">
            <span>{question.writerNickname}</span>
            <span>·</span>
            <span>{new Date(question.createdAt).toLocaleDateString("ko-KR")}</span>
          </div>

          {/* Content */}
          <p className="whitespace-pre-wrap text-sm leading-relaxed text-gray-700">
            {question.content}
          </p>
        </div>
      ) : null}
    </div>
  );
}