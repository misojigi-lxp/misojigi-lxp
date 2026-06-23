"use client";

export default function LectureTabMenu() {
  const handlePreparingClick = (tabName: string) => {
    alert(`${tabName} 기능은 준비 중입니다.`);
  };

  return (
    <div className="mt-8 flex items-center gap-2 border-b border-gray-200">
      <button
        type="button"
        className="border-b-2 border-violet-600 px-5 py-3 text-sm font-semibold text-violet-600"
      >
        콘텐츠
      </button>

      <button
        type="button"
        onClick={() => handlePreparingClick("Q&A")}
        className="px-5 py-3 text-sm font-semibold text-gray-400 hover:text-gray-600"
      >
        Q&amp;A
      </button>

      <button
        type="button"
        onClick={() => handlePreparingClick("후기")}
        className="px-5 py-3 text-sm font-semibold text-gray-400 hover:text-gray-600"
      >
        후기
      </button>
    </div>
  );
}