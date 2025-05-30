import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    client: {
      overlay: false,
    },
    port: 3000,
    proxy: {
      '/api': {
        target: "http://localhost:9000",
        changeOrigin: true,
        secure: false,
        ws: true,
      }
    },
  },
})
