import { defineConfig } from "vite";

export default defineConfig({
  server: {
    proxy: {
      "/exercicios": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
