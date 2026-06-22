type ContentItemProps = {
  index: number;
  type: "영상" | "파일";
  title: string;
  info: string;
  status: "완료" | "재생" | "다운로드";
};

function PlayIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M3 1.5L12 7L3 12.5V1.5Z" fill="#7C3AED" />
    </svg>
  );
}

function FileIcon() {
  return (
    <svg
      width="14"
      height="14"
      viewBox="0 0 24 24"
      fill="none"
      stroke="#7C3AED"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" />
      <polyline points="14 2 14 8 20 8" />
      <line x1="8" y1="13" x2="16" y2="13" />
      <line x1="8" y1="17" x2="12" y2="17" />
    </svg>
  );
}

export default function ContentItem({
  index,
  type,
  title,
  info,
  status,
}: ContentItemProps) {
  const num = String(index).padStart(2, "0");

  return (
    <div className="bg-white rounded-xl border border-gray-100 px-5 py-4 flex items-center gap-4">
      {/* Number */}
      <span className="text-sm text-gray-400 w-6 flex-shrink-0 text-center">
        {num}
      </span>

      {/* Icon */}
      <div className="w-9 h-9 rounded-full bg-violet-100 flex items-center justify-center flex-shrink-0">
        {type === "영상" ? <PlayIcon /> : <FileIcon />}
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-gray-800 truncate">{title}</p>
        <p className="text-xs text-gray-400 mt-0.5">
          {type} · {info}
        </p>
      </div>

      {/* Status */}
      {status === "완료" ? (
        <span className="text-sm text-green-500 font-medium flex-shrink-0">
          ✓ 완료
        </span>
      ) : (
        <span className="text-sm text-violet-600 font-medium flex-shrink-0">
          {status}
        </span>
      )}
    </div>
  );
}
