import tailwindcssTypography from "@tailwindcss/typography";
import type { Config } from "tailwindcss";
import colors from "tailwindcss/colors";

export default {
  content: ["./src/**/*.{js,ts,jsx,tsx,mdx}"],
  theme: {
    extend: {
      colors: {
        primary: colors.cyan,
      },
    },
  },
  plugins: [tailwindcssTypography],
} satisfies Config;
