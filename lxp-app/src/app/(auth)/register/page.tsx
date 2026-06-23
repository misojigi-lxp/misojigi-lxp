"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Button from "@/components/ui/Button";
import Input from "@/components/ui/Input";
import { signup } from "@/lib/api/auth";
import useAuthStore from "@/store/authStore";
import type { ErrorResponse } from "@/types/auth";
import axios from "axios";

const LOGIN_ID_REGEX = /^[a-zA-Z0-9]+$/;

function validateForm(
  nickname: string,
  id: string,
  password: string,
  passwordConfirm: string
): Record<string, string> {
  const errors: Record<string, string> = {};

  if (!nickname) errors.nickname = "닉네임을 입력해 주세요.";
  else if (nickname.length > 20) errors.nickname = "닉네임은 20자 이하로 입력해 주세요.";

  if (!id) errors.loginId = "아이디를 입력해 주세요.";
  else if (id.length < 4 || id.length > 20) errors.loginId = "아이디는 4~20자로 입력해 주세요.";
  else if (!LOGIN_ID_REGEX.test(id)) errors.loginId = "아이디는 영문과 숫자만 사용할 수 있습니다.";

  if (!password) errors.password = "비밀번호를 입력해 주세요.";
  else if (password.length < 8) errors.password = "비밀번호는 8자 이상으로 입력해 주세요.";

  if (!passwordConfirm) errors.passwordConfirm = "비밀번호 확인을 입력해 주세요.";
  else if (password !== passwordConfirm) errors.passwordConfirm = "비밀번호가 일치하지 않습니다.";

  return errors;
}

export default function RegisterPage() {
  const router = useRouter();
  const setUser = useAuthStore((state) => state.setUser);

  const [nickname, setNickname] = useState("");
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirm, setPasswordConfirm] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(false);

  const isDisabled =
    !nickname.trim() || !id.trim() || !password.trim() || !passwordConfirm.trim() || isLoading;

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    const errors = validateForm(nickname, id, password, passwordConfirm);
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    setFieldErrors({});
    setIsLoading(true);

    try {
      const user = await signup({ loginId: id, password, nickname });
      setUser(user);
      router.push("/courses");
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const data = err.response?.data as ErrorResponse | undefined;
        if (data?.fieldErrors) {
          setFieldErrors(data.fieldErrors);
        } else {
          setFieldErrors({ _form: data?.message ?? "회원가입 중 오류가 발생했습니다." });
        }
      } else {
        setFieldErrors({ _form: "회원가입 중 오류가 발생했습니다." });
      }
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="flex-1 flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-1">회원가입</h1>
          <p className="text-sm text-gray-500">개발자 성장 여정을 시작하세요.</p>
        </div>

        {/* Form */}
        <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
          <Input
            label="닉네임"
            id="nickname"
            type="text"
            placeholder="닉네임을 입력하세요"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            error={fieldErrors.nickname}
          />
          <Input
            label="아이디"
            id="user-id"
            type="text"
            placeholder="아이디를 입력하세요"
            value={id}
            onChange={(e) => setId(e.target.value)}
            error={fieldErrors.loginId}
          />
          <Input
            label="비밀번호"
            id="password"
            type="password"
            placeholder="영문·숫자 포함 8자 이상"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={fieldErrors.password}
          />
          <Input
            label="비밀번호 확인"
            id="password-confirm"
            type="password"
            placeholder="비밀번호를 다시 입력하세요"
            value={passwordConfirm}
            onChange={(e) => setPasswordConfirm(e.target.value)}
            error={fieldErrors.passwordConfirm}
          />

          {fieldErrors._form && (
            <p className="text-sm text-red-500">{fieldErrors._form}</p>
          )}

          <Button type="submit" fullWidth disabled={isDisabled} className="mt-2 py-3">
            {isLoading ? "가입 중..." : "가입하기"}
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
