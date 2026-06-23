"use client";

import { useEffect, useState } from "react";
import QuestionList from "@/components/questions/QuestionList";
import QuestionDetail from "@/components/questions/QuestionDetail";
import { getQuestions } from "@/lib/api/questions";
import type { QuestionListResponse } from "@/types/question";

export default function QnaTab({ lectureId }: { lectureId: number }) {
  const [questions, setQuestions] = useState<QuestionListResponse[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadQuestions = () => {
    setLoading(true);
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

  if (loading) {
    return <p className="py-16 text-center text-sm text-gray-400">불러오는 중...</p>;
  }
  if (error) {
    return <p className="py-16 text-center text-sm text-red-500">{error}</p>;
  }

  return <QuestionList questions={questions} onSelect={setSelectedId} />;
}