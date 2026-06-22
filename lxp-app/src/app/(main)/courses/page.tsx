import CourseCard from "@/components/courses/CourseCard";

const courses = [
  {
    id: 1,
    title: "실전 React 18 완전 정복",
    instructor: "김지훈",
    level: "중급",
    rating: 4.8,
    students: "3.2k",
    lessons: 24,
    category: "프론트엔드",
    enrolled: true,
    watermark: "실전",
  },
  {
    id: 2,
    title: "백엔드 시스템 디자인 입문",
    instructor: "박서연",
    level: "중급",
    rating: 4.7,
    students: "1.9k",
    lessons: 18,
    category: "백엔드",
    enrolled: false,
    watermark: "백엔",
  },
  {
    id: 3,
    title: "TypeScript 타입 마스터",
    instructor: "이도현",
    level: "중급",
    rating: 4.9,
    students: "2.8k",
    lessons: 16,
    category: "언어",
    enrolled: true,
    watermark: "Ty",
  },
  {
    id: 4,
    title: "Docker & Kubernetes 실무",
    instructor: "정민우",
    level: "고급",
    rating: 4.6,
    students: "1.4k",
    lessons: 30,
    category: "데브옵스",
    enrolled: false,
    watermark: "Do",
  },
  {
    id: 5,
    title: "알고리즘 코딩테스트 대비",
    instructor: "한예슬",
    level: "입문",
    rating: 4.8,
    students: "5.1k",
    lessons: 40,
    category: "CS",
    enrolled: true,
    watermark: "알고",
  },
  {
    id: 6,
    title: "AWS 클라우드 아키텍트",
    instructor: "최준영",
    level: "고급",
    rating: 4.5,
    students: "2.0k",
    lessons: 28,
    category: "클라우드",
    enrolled: false,
    watermark: "AW",
  },
];

export default function CoursesPage() {
  return (
    <div className="max-w-6xl mx-auto px-6 py-10 w-full">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900 mb-1">강의 둘러보기</h1>
        <p className="text-sm text-gray-500">
          개발자로 성장하는 가장 빠른 길. 관심 있는 강의를 수강 신청하세요.
        </p>
      </div>

      {/* Course Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {courses.map((course) => (
          <CourseCard key={course.id} {...course} />
        ))}
      </div>
    </div>
  );
}
