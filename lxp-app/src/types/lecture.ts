export type LectureListResponse = {
  lectureId: number;
  instructorId: number;
  instructorNickname: string;
  title: string;
  description: string | null;
};