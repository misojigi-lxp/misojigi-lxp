"use client";

type ButtonProps = {
  children: React.ReactNode;
  variant?: "primary" | "outline";
  type?: "button" | "submit" | "reset";
  fullWidth?: boolean;
  className?: string;
  onClick?: () => void;
};

export default function Button({
  children,
  variant = "primary",
  type = "button",
  fullWidth = false,
  className = "",
  onClick,
}: ButtonProps) {
  const base =
    "inline-flex items-center justify-center px-4 py-2 rounded-lg font-medium text-sm transition-colors cursor-pointer focus:outline-none";
  const variants = {
    primary: "bg-violet-600 text-white hover:bg-violet-700",
    outline:
      "border border-violet-600 text-violet-600 bg-white hover:bg-violet-50",
  };

  return (
    <button
      type={type}
      onClick={onClick}
      className={`${base} ${variants[variant]} ${fullWidth ? "w-full" : ""} ${className}`}
    >
      {children}
    </button>
  );
}
