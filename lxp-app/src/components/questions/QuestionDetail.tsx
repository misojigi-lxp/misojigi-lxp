import type { Question } from "@/components/questions/QnaTab";

type QuestionDetailProps = {
  question: Question;
  onBack: () => void;
};

export default function QuestionDetail({ question, onBack }: QuestionDetailProps) {
  return (
    <div>
      {/* Back */}
      <button
        onClick={onBack}
        className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-5 transition-colors"
      >
        <span>‹</span>
        <span>질문 목록</span>
      </button>

      {/* Question Card */}
      <div className="bg-white rounded-xl border border-gray-100 p-6 mb-4">
        {/* Badge */}
        <div className="mb-3">
          {question.isPublic ? (
            <span className="inline-flex items-center gap-1 text-xs font-medium text-green-600 bg-green-50 px-2.5 py-0.5 rounded-full">
              🔓 공개
            </span>
          ) : (
            <span className="inline-flex items-center gap-1 text-xs font-medium text-gray-500 bg-gray-100 px-2.5 py-0.5 rounded-full">
              🔒 비공개
            </span>
          )}
        </div>

        {/* Title */}
        <h2 className="text-lg font-bold text-gray-900 mb-2">{question.title}</h2>

        {/* Meta */}
        <div className="flex items-center gap-2 text-xs text-gray-400 mb-5">
          <span>{question.authorName}</span>
          <span>·</span>
          <span>{question.date}</span>
        </div>

        {/* Content */}
        <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
          {question.content}
        </p>
      </div>

      {/* Answers Header */}
      <h3 className="text-sm font-semibold text-gray-700 mb-3">
        답변 {question.answers.length}
      </h3>

      {/* Answers */}
      {question.answers.length === 0 ? (
        <div className="flex items-center justify-center py-12 text-sm text-gray-400 bg-white rounded-xl border border-gray-100">
          아직 답변이 없습니다.
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {question.answers.map((answer) => (
            <div
              key={answer.id}
              className="bg-white rounded-xl border border-gray-100 p-5"
            >
              {/* Answer Header */}
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2">
                  <div
                    className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold text-white flex-shrink-0 ${
                      answer.isInstructor ? "bg-violet-500" : "bg-gray-400"
                    }`}
                  >
                    {answer.authorName.charAt(0)}
                  </div>
                  <span className="text-sm font-medium text-gray-800">
                    {answer.authorName}
                  </span>
                  {answer.isInstructor && (
                    <span className="text-xs font-medium text-green-600 bg-green-50 px-2 py-0.5 rounded-full">
                      강사
                    </span>
                  )}
                </div>
                <span className="text-xs text-gray-400">{answer.date}</span>
              </div>

              {/* Answer Content */}
              <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
                {answer.content}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
