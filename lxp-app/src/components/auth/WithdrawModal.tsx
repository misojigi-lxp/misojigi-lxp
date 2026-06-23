"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import useAuthStore from "@/store/authStore";
import { withdraw } from "@/lib/api/auth";

type Props = {
  onClose: () => void;
};

export default function WithdrawModal({ onClose }: Props) {
  const router = useRouter();
  const clearUser = useAuthStore((state) => state.clearUser);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleWithdraw() {
    setIsLoading(true);
    setError("");
    try {
      await withdraw();
      clearUser();
      router.push("/courses");
    } catch {
      setError("탈퇴 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
      setIsLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/40"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative bg-white rounded-2xl shadow-xl w-full max-w-sm mx-4 p-6">
        <h2 className="text-lg font-bold text-gray-900 mb-2">회원탈퇴</h2>
        <p className="text-sm text-gray-500 mb-1">
          탈퇴 시 아래 사항을 확인해 주세요.
        </p>
        <ul className="text-sm text-gray-500 list-disc list-inside space-y-1 mb-6">
          <li>수강 중인 강의 및 학습 기록이 모두 삭제됩니다.</li>
          <li>삭제된 계정은 복구할 수 없습니다.</li>
          <li>동일한 아이디로 재가입이 불가합니다.</li>
        </ul>

        {error && <p className="text-sm text-red-500 mb-4">{error}</p>}

        <div className="flex gap-2">
          <button
            onClick={onClose}
            disabled={isLoading}
            className="flex-1 py-2.5 rounded-lg border border-gray-300 text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors disabled:opacity-50"
          >
            취소
          </button>
          <button
            onClick={handleWithdraw}
            disabled={isLoading}
            className="flex-1 py-2.5 rounded-lg bg-red-500 text-sm font-medium text-white hover:bg-red-600 transition-colors disabled:opacity-50"
          >
            {isLoading ? "탈퇴 중..." : "탈퇴하기"}
          </button>
        </div>
      </div>
    </div>
  );
}
