import { getLectures } from "@/lib/api/lecture";
import LectureCard from "@/components/lectures/LectureCard";

export default async function LecturesPage() {
  const lectures = await getLectures();

  return (
    <div className="max-w-6xl mx-auto px-6 py-10 w-full">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-1">강의 둘러보기</h1>
        <p className="text-sm text-gray-500">
          총 {lectures.length}개의 강의가 있습니다.
        </p>
      </div>

      {lectures.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {lectures.map((lecture) => (
            <LectureCard
              key={lecture.lectureId}
              id={lecture.lectureId}
              title={lecture.title}
              instructor={lecture.instructorNickname}
              level="중급"
              rating={0}
              students="-"
              lessons={0}
              category="강의"
              enrolled={false}
              watermark={lecture.title.slice(0, 2)}
            />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center py-20">
          <p className="text-sm text-gray-400">등록된 강의가 없습니다.</p>
        </div>
      )}
    </div>
  );
}