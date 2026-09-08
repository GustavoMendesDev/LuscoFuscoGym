import { useEffect, useState } from "react";
import { exerciciosApi } from "../services/exercicios";

function formatarExercicios(dados) {
  const lista = dados?.content ?? dados?.exercicios ?? dados;
  const exercicios = Array.isArray(lista) ? lista : lista ? [lista] : [];

  return exercicios
    .map((exercicio) => {
      const { nome, grupo, grupoMuscular, repeticoes, series, carga } = exercicio;

      return [
        `Exercício: ${nome}`,
        `Grupo Muscular: ${grupo ?? grupoMuscular}`,
        `Séries: ${series}`,
        `Repetições: ${repeticoes}`,
        `Carga: ${carga}`,
      ]
        .filter((linha) => !linha.endsWith("undefined") && !linha.endsWith("null"))
        .join("\n");
    })
    .filter(Boolean)
    .join("\n\n");
}

export default function Return() {
  const [resposta, setResposta] = useState("Buscando exercícios...");
  const [buscando, setBuscando] = useState(false);

  async function buscarExercicios() {
    setBuscando(true);

    try {
      const respostaApi = await exerciciosApi.listar();
      const textoFormatado = formatarExercicios(respostaApi.data);
      setResposta(textoFormatado || "Nenhum exercício cadastrado.");
    } catch (erro) {
      const detalhe =
        erro.response?.data?.message ??
        erro.response?.data ??
        erro.message ??
        "Erro desconhecido";
      setResposta(`Não foi possível buscar os exercícios! : ${detalhe}`);
    } finally {
      setBuscando(false);
    }
  }

  useEffect(() => {
    buscarExercicios();
  }, []);

  return (
    <main className="page">
      <section className="card return-card" aria-labelledby="titulo-retorno">
        <header className="brand">
          <img className="sunrise-icon" src="/assets/icon/sunrise.png" alt="" />
          <span>LuscoFusco<span>Gym</span></span>
        </header>

        <h1 id="titulo-retorno">Exercícios cadastrados</h1>

        <div className="response">
          <div className="response-header">
            <label htmlFor="resposta-api">Exercicios Cadastrados: </label>
            <button type="button" onClick={buscarExercicios} disabled={buscando}>
              {buscando ? "Buscando..." : "Atualizar GET"}
            </button>
          </div>
          <textarea
            id="resposta-api"
            value={resposta}
            readOnly
            rows="14"
            aria-label="Resposta do GET de exercícios"
          />
        </div>

        <a className="back-link" href="/">Cadastrar outro exercício</a>
      </section>
    </main>
  );
}
