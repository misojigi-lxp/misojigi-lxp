"use client";

import { useState } from "react";
import QuestionList from "@/components/questions/QuestionList";
import QuestionDetail from "@/components/questions/QuestionDetail";

export type Answer = {
  id: number;
  authorName: string;
  isInstructor: boolean;
  date: string;
  content: string;
};

export type Question = {
  id: number;
  title: string;
  content: string;
  authorId: string;
  authorName: string;
  date: string;
  isPublic: boolean;
  answers: Answer[];
};

const CURRENT_USER_ID = "me";

const QUESTIONS: Question[] = [
  {
    id: 1,
    title: "useEffect 의존성 배열이 헷갈려요",
    content:
      "useEffect를 사용할 때 의존성 배열에 어떤 값을 넣어야 하는지 헷갈립니다. 빈 배열 []과 값이 있는 경우, 그리고 아예 생략하는 경우의 차이를 자세히 설명해주실 수 있나요?",
    authorId: "user1",
    authorName: "홍길동",
    date: "2024.01.15",
    isPublic: true,
    answers: [
      {
        id: 1,
        authorName: "김지훈",
        isInstructor: true,
        date: "2024.01.16",
        content:
          "빈 배열 []을 전달하면 컴포넌트 마운트 시에만 한 번 실행됩니다. 특정 값을 넣으면 해당 값이 바뀔 때마다 실행되고, 배열을 생략하면 모든 렌더링마다 실행되니 주의하세요.",
      },
      {
        id: 2,
        authorName: "이수진",
        isInstructor: false,
        date: "2024.01.16",
        content:
          "ESLint의 exhaustive-deps 규칙을 켜두면 누락된 의존성을 자동으로 경고해줘서 편했어요!",
      },
    ],
  },
  {
    id: 2,
    title: "클로저 관련 개인 질문",
    content: "비공개 질문 내용입니다.",
    authorId: "user2",
    authorName: "김철수",
    date: "2024.01.14",
    isPublic: false,
    answers: [],
  },
  {
    id: 3,
    title: "React 18 Concurrent Mode란 무엇인가요?",
    content:
      "강의에서 Concurrent Mode 얘기가 나왔는데, 정확히 어떤 개념인지 궁금합니다. 기존 방식과 어떻게 다른가요?",
    authorId: "me",
    authorName: "나",
    date: "2024.01.13",
    isPublic: true,
    answers: [
      {
        id: 3,
        authorName: "김지훈",
        isInstructor: true,
        date: "2024.01.14",
        content:
          "Concurrent Mode는 렌더링 작업을 중단하고 재개할 수 있게 해줍니다. 덕분에 무거운 렌더링 중에도 사용자 입력에 즉각 반응할 수 있어요.",
      },
    ],
  },
  {
    id: 4,
    title: "3강 실습 코드 오류 문의",
    content:
      "강의 3강 실습을 따라하다가 TypeError가 발생합니다. 혼자 확인하고 싶어서 비공개로 올립니다.",
    authorId: "me",
    authorName: "나",
    date: "2024.01.12",
    isPublic: false,
    answers: [],
  },
  {
    id: 5,
    title: "useState와 useReducer 언제 써야 하나요?",
    content:
      "상태 관리를 할 때 useState와 useReducer 중 어느 것을 선택해야 할지 기준이 있나요?",
    authorId: "user3",
    authorName: "박지영",
    date: "2024.01.11",
    isPublic: true,
    answers: [],
  },
  {
    id: 6,
    title: "비공개 개인 문의입니다",
    content: "비공개 내용입니다.",
    authorId: "user4",
    authorName: "최민수",
    date: "2024.01.10",
    isPublic: false,
    answers: [],
  },
];

export default function QnaTab() {
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const selectedQuestion =
    selectedId !== null
      ? QUESTIONS.find((q) => q.id === selectedId) ?? null
      : null;

  if (selectedQuestion) {
    return (
      <QuestionDetail
        question={selectedQuestion}
        onBack={() => setSelectedId(null)}
      />
    );
  }

  return (
    <QuestionList
      questions={QUESTIONS}
      currentUserId={CURRENT_USER_ID}
      onSelect={setSelectedId}
    />
  );
}
