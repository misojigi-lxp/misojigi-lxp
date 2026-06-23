import axios from "axios";

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080",
  withCredentials: true, // JSESSIONID 쿠키 자동 전송
  headers: {
    "Content-Type": "application/json",
  },
});

export default apiClient;
