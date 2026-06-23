"use client";

import { useEffect, useState } from "react";
import { getReviews } from "@/lib/api/reviews";
import type { ReviewListResponse } from "@/types/review";
import LectureReviewForm from "@/components/lectures/LectureReviewForm";

type LectureReviewSectionProps = {
  lectureId: number;
};

export default function LectureReviewSection({
  lectureId,
}: LectureReviewSectionProps) {
  const [reviews, setReviews] = useState<ReviewListResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  async function fetchReviews() {
    try {
      setErrorMessage("");
      const reviewList = await getReviews(lectureId);
      setReviews(reviewList);
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "후기 목록을 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    fetchReviews();
  }, [lectureId]);

  function renderStars(rating: number) {
    return "★".repeat(rating) + "☆".repeat(5 - rating);
  }

  return (
    <section className="mt-8 rounded-2xl border border-gray-100 bg-white p-8 shadow-sm">
      <div className="mb-5 flex items-center justify-between">
        <h2 className="text-xl font-bold text-gray-900">후기</h2>
        <span className="text-sm text-gray-400">총 {reviews.length}개</span>
      </div>

      <LectureReviewForm lectureId={lectureId} onCreated={fetchReviews} />

      {isLoading ? (
        <p className="text-sm text-gray-400">후기를 불러오는 중입니다.</p>
      ) : errorMessage ? (
        <p className="text-sm text-red-500">{errorMessage}</p>
      ) : reviews.length > 0 ? (
        <div className="flex flex-col gap-4">
          {reviews.map((review) => (
            <article
              key={review.reviewId}
              className="rounded-xl border border-gray-100 px-4 py-4"
            >
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="text-sm font-medium text-gray-900">
                    작성자 ID {review.writerId}
                  </p>

                  <p className="mt-1 text-sm text-yellow-500">
                    {renderStars(review.rating)}
                  </p>
                </div>

                <div className="flex items-center gap-1 text-sm">
                  <span
                    className={
                      review.likedByMe ? "text-blue-500" : "text-gray-300"
                    }
                  >
                    ♥
                  </span>
                  <span className="text-gray-500">{review.likeCount}</span>
                </div>
              </div>

              <p className="mt-4 text-sm leading-6 text-gray-700">
                {review.content}
              </p>

              <p className="mt-3 text-xs text-gray-400">
                {new Date(review.createdAt).toLocaleDateString("ko-KR")}
              </p>
            </article>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-xl bg-gray-50 py-12">
          <p className="text-sm text-gray-400">작성된 후기가 없습니다.</p>
        </div>
      )}
    </section>
  );
}