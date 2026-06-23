import Link from "next/link";
import { notFound } from "next/navigation";
import { getLecture } from "@/lib/api/lecture";
import type { LectureDetailResponse } from "@/types/lecture";
import LectureContentItem from "@/components/lectures/LectureContentItem";

type LectureDetailPageProps = {
  params: Promise<{
    lectureId: string;
  }>;
};

export default async function LectureDetailPage({
  params,
}: LectureDetailPageProps) {
  const { lectureId } = await params;
  const targetLectureId = Number(lectureId);

  if (Number.isNaN(targetLectureId)) {
    notFound();
  }

  let lecture: LectureDetailResponse | null = null;
  let errorMessage = "";

  try {
    lecture = await getLecture(targetLectureId);
  } catch (error) {
    errorMessage =
      error instanceof Error
        ? error.message
        : "강의 정보를 불러오지 못했습니다.";
  }

  return (
    <div className="max-w-4xl mx-auto px-6 py-10 w-full">
      <Link
        href="/lectures"
        className="text-sm text-violet-600 hover:text-violet-700"
      >
        ← 강의 목록으로 돌아가기
      </Link>

      {errorMessage ? (
        <div className="mt-10 rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
          <p className="text-sm text-red-500">{errorMessage}</p>
          <p className="mt-2 text-xs text-gray-400">
            강의 목록으로 돌아가 다시 확인해주세요.
          </p>
        </div>
      ) : lecture ? (
        <div className="mt-8">
          <section className="rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
            <span className="inline-flex rounded-full bg-violet-100 px-3 py-1 text-xs font-medium text-violet-700">
              강의
            </span>

            <h1 className="mt-4 text-3xl font-bold text-gray-900">
              {lecture.title}
            </h1>

            <p className="mt-3 text-sm text-gray-500">
              강사 ID {lecture.instructorId}
            </p>

            <p className="mt-6 leading-7 text-gray-700">
              {lecture.description ?? "강의 설명이 없습니다."}
            </p>
          </section>

          <section className="mt-8 rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
            <div className="mb-5 flex items-center justify-between">
              <h2 className="text-xl font-bold text-gray-900">콘텐츠 목록</h2>
              <span className="text-sm text-gray-400">
                총 {lecture.contents.length}개
              </span>
            </div>

            {lecture.contents.length > 0 ? (
              <div className="flex flex-col gap-3">
                {lecture.contents.map((content) => (
                  <LectureContentItem
                    key={content.contentId}
                    content={content}
                  />
                ))}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center rounded-xl bg-gray-50 py-12">
                <p className="text-sm text-gray-400">
                  등록된 콘텐츠가 없습니다.
                </p>
              </div>
            )}
          </section>
        </div>
      ) : null}
    </div>
  );
}