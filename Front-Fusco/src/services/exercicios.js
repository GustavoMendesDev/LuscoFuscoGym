import api from "./api";

export const exerciciosApi = {
  listar: () => api.get(""),
  buscarPorId: (id) => api.get(String(id)),
  criar: (exercicio) => api.post("", exercicio),
};
