"use client";

import type { QuestionListResponse } from "@/types/question";
import QuestionItem from "@/components/questions/QuestionItem";

type QuestionListProps = {
  questions: QuestionListResponse[];
};

export default function QuestionList({ questions }: QuestionListProps) {
  return (
    <div>
      {/* Count */}
      <p className="text-sm text-gray-500 mb-3">
        전체 질문{" "}
        <span className="font-semibold text-gray-700">{questions.length}</span>개
      </p>

      {/* List */}
      {questions.length === 0 ? (
        <div className="flex items-center justify-center py-16 text-sm text-gray-400">
          등록된 질문이 없습니다.
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {questions.map((q) => (
            <QuestionItem
              key={q.questionId}
              question={q}
              onClick={() => {
                /* 상세 조회는 다음 단계 */
              }}
            />
          ))}
        </div>
      )}
    </div>
  );
}