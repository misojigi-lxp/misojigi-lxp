"use client";

import { useEffect, useState } from "react";
import QuestionList from "@/components/questions/QuestionList";
import QuestionDetail from "@/components/questions/QuestionDetail";
import QuestionFormModal from "@/components/questions/QuestionFormModal";
import { getQuestions } from "@/lib/api/questions";
import type { QuestionListResponse } from "@/types/question";

export default function QnaTab({ lectureId }: { lectureId: number }) {
  const [questions, setQuestions] = useState<QuestionListResponse[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadQuestions = () => {
  setLoading(true);
  setError("");
  getQuestions(lectureId)
    .then(setQuestions)
    .catch((e) =>
      setError(e instanceof Error ? e.message : "질문 목록을 불러오지 못했습니다."),
    )
    .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadQuestions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [lectureId]);

  // 상세 보기
  if (selectedId !== null) {
    return (
      <QuestionDetail
        questionId={selectedId}
        onBack={() => setSelectedId(null)}
        onDeleted={() => {
          setSelectedId(null);
          loadQuestions(); // 삭제 후 목록 새로고침
        }}
      />
    );
  }

  return (
  <div>
    {/* 헤더: 제목 + 질문 등록 버튼 한 줄 */}
    <div className="mb-5 flex items-center justify-between">
      <h2 className="text-xl font-bold text-gray-900">Q&amp;A</h2>
      <button
        type="button"
        onClick={() => setShowForm(true)}
        className="rounded-lg bg-violet-600 px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-violet-700"
      >
        질문 등록
      </button>
    </div>

    {loading ? (
      <p className="py-16 text-center text-sm text-gray-400">불러오는 중...</p>
    ) : error ? (
      <p className="py-16 text-center text-sm text-red-500">{error}</p>
    ) : (
      <QuestionList questions={questions} onSelect={setSelectedId} />
    )}

    {showForm && (
      <QuestionFormModal
        lectureId={lectureId}
        onClose={() => setShowForm(false)}
        onCreated={() => {
          setShowForm(false);
          loadQuestions();
        }}
      />
    )}
  </div>
  );
}