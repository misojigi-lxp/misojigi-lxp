"use client";

import { useState } from "react";

// ─── Types ───────────────────────────────────────────
type SubItem = { id: number; text: string; done: boolean };
type Goal = { id: number; title: string; deadline: string; items: SubItem[] };
type View = "list" | "add" | "edit";
type FormItem = { id: number; text: string; done: boolean };

// ─── Dummy data ───────────────────────────────────────
const INITIAL_GOALS: Goal[] = [
  {
    id: 1,
    title: "React 핵심 개념 복습",
    deadline: "2026-06-22T18:48",
    items: [
      { id: 1, text: "useState / useEffect 다시 보기", done: true },
      { id: 2, text: "Custom Hook 직접 만들어보기", done: false },
      { id: 3, text: "Context API 실습 예제 따라하기", done: false },
    ],
  },
  {
    id: 2,
    title: "코딩테스트 문제 2개 풀기",
    deadline: "2026-06-22T23:48",
    items: [
      { id: 1, text: "프로그래머스 LV2 그래프 탐색", done: false },
      { id: 2, text: "백준 DP 기초 문제", done: false },
    ],
  },
];

// ─── Helpers ──────────────────────────────────────────
function formatDeadline(deadline: string): string {
  const d = new Date(deadline);
  const today = new Date();
  const hh = String(d.getHours()).padStart(2, "0");
  const mm = String(d.getMinutes()).padStart(2, "0");
  if (d.toDateString() === today.toDateString()) return `오늘 ${hh}:${mm}`;
  const mo = String(d.getMonth() + 1).padStart(2, "0");
  const da = String(d.getDate()).padStart(2, "0");
  return `${mo}.${da} ${hh}:${mm}`;
}

function getTodayStr(): string {
  const t = new Date();
  return `${t.getFullYear()}.${String(t.getMonth() + 1).padStart(2, "0")}.${String(t.getDate()).padStart(2, "0")}`;
}

function getNowDatetimeLocal(): string {
  const t = new Date();
  return [
    t.getFullYear(),
    String(t.getMonth() + 1).padStart(2, "0"),
    String(t.getDate()).padStart(2, "0"),
  ].join("-") + "T" + String(t.getHours()).padStart(2, "0") + ":" + String(t.getMinutes()).padStart(2, "0");
}

// ─── Icons ────────────────────────────────────────────
function CloseIcon() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
      <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
    </svg>
  );
}

function RemoveIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
      <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
    </svg>
  );
}

function PencilIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
      <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
    </svg>
  );
}

function TrashIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="3 6 5 6 21 6" />
      <path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6" />
      <path d="M10 11v6M14 11v6M9 6V4h6v2" />
    </svg>
  );
}

function CheckIcon() {
  return (
    <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="3.5" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="20 6 9 17 4 12" />
    </svg>
  );
}

// ─── Component ────────────────────────────────────────
export default function GoalModal({ onClose }: { onClose: () => void }) {
  const [view, setView] = useState<View>("list");
  const [goals, setGoals] = useState<Goal[]>(INITIAL_GOALS);
  const [editingGoalId, setEditingGoalId] = useState<number | null>(null);

  // Form state
  const [formTitle, setFormTitle] = useState("");
  const [formDeadline, setFormDeadline] = useState("");
  const [formItems, setFormItems] = useState<FormItem[]>([]);

  // ── Actions ──
  const openAdd = () => {
    setFormTitle("");
    setFormDeadline(getNowDatetimeLocal());
    setFormItems([{ id: Date.now(), text: "", done: false }]);
    setView("add");
  };

  const openEdit = (goalId: number) => {
    const goal = goals.find((g) => g.id === goalId);
    if (!goal) return;
    setEditingGoalId(goalId);
    setFormTitle(goal.title);
    setFormDeadline(goal.deadline);
    setFormItems(goal.items.map((i) => ({ ...i })));
    setView("edit");
  };

  const handleDelete = (goalId: number) => {
    setGoals((prev) => prev.filter((g) => g.id !== goalId));
  };

  const handleToggleItem = (goalId: number, itemId: number) => {
    setGoals((prev) =>
      prev.map((g) =>
        g.id !== goalId
          ? g
          : { ...g, items: g.items.map((it) => it.id === itemId ? { ...it, done: !it.done } : it) }
      )
    );
  };

  const handleSaveAdd = () => {
    if (!formTitle.trim()) return;
    setGoals((prev) => [
      ...prev,
      {
        id: Date.now(),
        title: formTitle.trim(),
        deadline: formDeadline,
        items: formItems.filter((i) => i.text.trim()).map((i) => ({ ...i, text: i.text.trim() })),
      },
    ]);
    setView("list");
  };

  const handleSaveEdit = () => {
    if (!formTitle.trim() || editingGoalId === null) return;
    setGoals((prev) =>
      prev.map((g) =>
        g.id !== editingGoalId
          ? g
          : {
              ...g,
              title: formTitle.trim(),
              deadline: formDeadline,
              items: formItems.filter((i) => i.text.trim()).map((i) => ({ ...i, text: i.text.trim() })),
            }
      )
    );
    setView("list");
  };

  const addFormItem = () =>
    setFormItems((prev) => [...prev, { id: Date.now(), text: "", done: false }]);

  const removeFormItem = (id: number) =>
    setFormItems((prev) => prev.filter((i) => i.id !== id));

  const updateFormItem = (id: number, text: string) =>
    setFormItems((prev) => prev.map((i) => (i.id === id ? { ...i, text } : i)));

  const isFormView = view === "add" || view === "edit";

  // ── Render ──
  return (
    <div
      className="fixed inset-0 z-[100] flex items-center justify-center bg-black/40"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-2xl shadow-2xl w-full max-w-lg mx-4 max-h-[85vh] flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-6 pt-6 pb-4 flex-shrink-0">
          <div className="flex items-start justify-between">
            <div>
              <h2 className="text-xl font-bold text-gray-900">
                {view === "list" ? "학습 목표" : view === "add" ? "새 목표 추가" : "목표 수정"}
              </h2>
              {view === "list" && (
                <p className="text-xs text-gray-400 mt-0.5">오늘 마감 · {getTodayStr()}</p>
              )}
            </div>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition-colors mt-0.5"
            >
              <CloseIcon />
            </button>
          </div>
        </div>

        {/* Scrollable Body */}
        <div className="flex-1 overflow-y-auto px-6 min-h-0">
          {/* ── LIST VIEW ── */}
          {view === "list" && (
            <div className="flex flex-col gap-4 pb-4">
              {goals.length === 0 && (
                <p className="text-center text-sm text-gray-400 py-10">
                  아직 등록된 학습 목표가 없어요.
                </p>
              )}
              {goals.map((goal) => {
                const doneCount = goal.items.filter((i) => i.done).length;
                return (
                  <div key={goal.id} className="bg-gray-50 rounded-xl p-4">
                    {/* Goal header */}
                    <div className="flex items-start justify-between mb-1">
                      <h3 className="font-semibold text-gray-900 text-sm leading-snug pr-2">
                        {goal.title}
                      </h3>
                      <div className="flex items-center gap-2 flex-shrink-0">
                        <button
                          onClick={() => openEdit(goal.id)}
                          className="text-gray-400 hover:text-violet-600 transition-colors"
                        >
                          <PencilIcon />
                        </button>
                        <button
                          onClick={() => handleDelete(goal.id)}
                          className="text-gray-400 hover:text-red-500 transition-colors"
                        >
                          <TrashIcon />
                        </button>
                      </div>
                    </div>
                    {/* Progress */}
                    <p className="text-xs text-gray-400 mb-3">
                      {doneCount}/{goal.items.length} 완료 · 마감 {formatDeadline(goal.deadline)}
                    </p>
                    {/* Sub-items */}
                    <div className="flex flex-col gap-2.5">
                      {goal.items.map((item) => (
                        <button
                          key={item.id}
                          onClick={() => handleToggleItem(goal.id, item.id)}
                          className="flex items-center gap-2.5 text-left w-full"
                        >
                          <span
                            className={`w-5 h-5 rounded flex items-center justify-center flex-shrink-0 transition-colors border-2 ${
                              item.done
                                ? "bg-violet-600 border-violet-600"
                                : "bg-white border-gray-300"
                            }`}
                          >
                            {item.done && <CheckIcon />}
                          </span>
                          <span
                            className={`text-sm transition-colors ${
                              item.done ? "text-gray-400 line-through" : "text-gray-700"
                            }`}
                          >
                            {item.text}
                          </span>
                        </button>
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          {/* ── ADD / EDIT FORM ── */}
          {isFormView && (
            <div className="flex flex-col gap-5 pb-4">
              {/* Title */}
              <div>
                <div className="flex items-center justify-between mb-1.5">
                  <label className="text-sm font-medium text-gray-700">학습 목표 제목</label>
                  <span className="text-xs text-gray-400">{formTitle.length}/30</span>
                </div>
                <input
                  type="text"
                  maxLength={30}
                  placeholder="예: React 핵심 개념 복습"
                  value={formTitle}
                  onChange={(e) => setFormTitle(e.target.value)}
                  className="w-full px-3 py-2.5 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition"
                />
              </div>

              {/* Deadline */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">마감일</label>
                <input
                  type="datetime-local"
                  value={formDeadline}
                  onChange={(e) => setFormDeadline(e.target.value)}
                  className="w-full px-3 py-2.5 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition"
                />
              </div>

              {/* Sub-items */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">세부 목표</label>
                <div className="flex flex-col gap-2 mb-2">
                  {formItems.map((item) => (
                    <div key={item.id} className="flex items-center gap-2">
                      <span className="text-violet-400 text-xl leading-none flex-shrink-0">·</span>
                      <input
                        type="text"
                        placeholder="세부 목표를 입력하세요"
                        value={item.text}
                        onChange={(e) => updateFormItem(item.id, e.target.value)}
                        className="flex-1 px-3 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition"
                      />
                      <button
                        onClick={() => removeFormItem(item.id)}
                        className="text-gray-400 hover:text-gray-600 flex-shrink-0 p-0.5"
                      >
                        <RemoveIcon />
                      </button>
                    </div>
                  ))}
                </div>
                <button
                  onClick={addFormItem}
                  className="w-full py-2.5 border border-dashed border-violet-300 text-violet-600 text-sm rounded-lg hover:bg-violet-50 transition-colors"
                >
                  + 항목 추가
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-6 pb-6 pt-3 flex-shrink-0">
          {view === "list" && (
            <button
              onClick={openAdd}
              className="w-full py-3 bg-violet-50 text-violet-600 text-sm font-medium rounded-xl hover:bg-violet-100 transition-colors"
            >
              + 새 목표 추가
            </button>
          )}
          {isFormView && (
            <div className="flex gap-3">
              <button
                onClick={() => setView("list")}
                className="flex-1 py-2.5 border border-gray-200 text-gray-600 text-sm font-medium rounded-xl hover:bg-gray-50 transition-colors"
              >
                취소
              </button>
              <button
                onClick={view === "add" ? handleSaveAdd : handleSaveEdit}
                disabled={!formTitle.trim()}
                className="flex-1 py-2.5 bg-violet-600 text-white text-sm font-medium rounded-xl hover:bg-violet-700 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
              >
                {view === "add" ? "목표 등록" : "수정하기"}
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
