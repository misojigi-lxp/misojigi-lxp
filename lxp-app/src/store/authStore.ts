import { create } from "zustand";
import type { MemberResponse } from "@/types/auth";

interface AuthState {
  user: MemberResponse | null;
  setUser: (user: MemberResponse) => void;
  clearUser: () => void;
}

const useAuthStore = create<AuthState>((set) => ({
  user: null,
  setUser: (user) => set({ user }),
  clearUser: () => set({ user: null }),
}));

export default useAuthStore;
