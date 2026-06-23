"use client";

import { useEffect, useState } from "react";
import QuestionList from "@/components/questions/QuestionList";
import { getQuestions } from "@/lib/api/questions";
import type { QuestionListResponse } from "@/types/question";

export default function QnaTab({ lectureId }: { lectureId: number }) {
  const [questions, setQuestions] = useState<QuestionListResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getQuestions(lectureId)
      .then(setQuestions)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [lectureId]);

  if (loading) {
    return <p className="py-16 text-center text-sm text-gray-400">불러오는 중...</p>;
  }
  if (error) {
    return <p className="py-16 text-center text-sm text-red-500">{error}</p>;
  }

  return <QuestionList questions={questions} />;
}