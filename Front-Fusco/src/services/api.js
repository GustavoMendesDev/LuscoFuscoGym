import axios from "axios";

/**
 * Cliente HTTP único da aplicação.
 * Em desenvolvimento, o Vite encaminha /exercicios para localhost:8080.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_EXERCICIOS_API_URL ?? "/exercicios",
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
