import type { Question } from "@/components/questions/QnaTab";

type QuestionItemProps = {
  question: Question;
  isHidden: boolean;
  isMine: boolean;
  onClick?: () => void;
};

export default function QuestionItem({
  question,
  isHidden,
  isMine,
  onClick,
}: QuestionItemProps) {
  const displayTitle = isHidden ? "비공개 질문입니다." : question.title;
  const displayAuthor = isHidden ? "비공개" : question.authorName;

  return (
    <div
      role={onClick ? "button" : undefined}
      tabIndex={onClick ? 0 : undefined}
      onClick={onClick}
      onKeyDown={onClick ? (e) => e.key === "Enter" && onClick() : undefined}
      className={`bg-white rounded-xl border border-gray-100 p-4 transition-all ${
        onClick
          ? "cursor-pointer hover:shadow-sm hover:border-violet-200"
          : "cursor-default"
      }`}
    >
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 min-w-0">
          {/* Badges */}
          <div className="flex gap-2 mb-2 flex-wrap">
            {question.isPublic ? (
              <span className="inline-flex items-center gap-1 text-xs font-medium text-green-600 bg-green-50 px-2.5 py-0.5 rounded-full">
                🔓 공개
              </span>
            ) : (
              <span className="inline-flex items-center gap-1 text-xs font-medium text-gray-500 bg-gray-100 px-2.5 py-0.5 rounded-full">
                🔒 비공개
              </span>
            )}
            {isMine && (
              <span className="inline-flex items-center text-xs font-medium text-violet-600 bg-violet-100 px-2.5 py-0.5 rounded-full">
                내 질문
              </span>
            )}
          </div>

          {/* Title */}
          <h4
            className={`text-sm font-semibold leading-snug ${
              isHidden ? "text-gray-400" : "text-gray-800"
            }`}
          >
            {displayTitle}
          </h4>

          {/* Meta — hidden 질문은 노출 안 함 */}
          {!isHidden && (
            <div className="flex items-center gap-2 text-xs text-gray-400 mt-1.5 flex-wrap">
              <span>{displayAuthor}</span>
              <span>·</span>
              <span>{question.date}</span>
              <span className="text-violet-500 font-medium">
                답변 {question.answers.length}
              </span>
            </div>
          )}
        </div>

        {/* 남의 비공개: 우측 레이블 */}
        {isHidden && (
          <span className="text-xs text-gray-400 flex-shrink-0 mt-0.5">
            비공개 질문
          </span>
        )}
      </div>
    </div>
  );
}
