"use client";

import { createContext, useContext, useState, ReactNode } from "react";
import { MemberResponse } from "@/types/auth";

interface AuthState {
  member: MemberResponse | null;
  setMember: (member: MemberResponse | null) => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [member, setMember] = useState<MemberResponse | null>(null);

  return (
    <AuthContext.Provider value={{ member, setMember }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuthContext(): AuthState {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuthContext must be used within AuthProvider");
  }
  return ctx;
}
