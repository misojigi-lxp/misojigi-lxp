"use client";

import { useState } from "react";
import Link from "next/link";
import Button from "@/components/ui/Button";
import Input from "@/components/ui/Input";

export default function RegisterPage() {
  const [nickname, setNickname] = useState("");
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");

  return (
    <div className="flex-1 flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-1">회원가입</h1>
          <p className="text-sm text-gray-500">개발자 성장 여정을 시작하세요.</p>
        </div>

        {/* Form */}
        <form className="flex flex-col gap-4">
          <Input
            label="닉네임"
            id="nickname"
            type="text"
            placeholder="닉네임을 입력하세요"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
          />
          <Input
            label="아이디"
            id="user-id"
            type="text"
            placeholder="아이디를 입력하세요"
            value={id}
            onChange={(e) => setId(e.target.value)}
          />
          <Input
            label="비밀번호"
            id="password"
            type="password"
            placeholder="영문·숫자 포함 8자 이상"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <Button type="submit" fullWidth className="mt-2 py-3">
            가입하기
          </Button>
        </form>

        {/* Footer */}
        <p className="mt-4 text-center text-sm text-gray-500">
          이미 계정이 있으신가요?{" "}
          <Link
            href="/login"
            className="text-violet-600 font-medium hover:underline"
          >
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}
