import { useEffect, useState } from "react";
import { exerciciosApi } from "./services/exercicios";

export default function Return() {
  const [resposta, setResposta] = useState("Buscando exercícios...");
  const [buscando, setBuscando] = useState(false);

  async function buscarExercicios() {
    setBuscando(true);

    try {
      const respostaApi = await exerciciosApi.listar();
      setResposta(JSON.stringify(respostaApi.data, null, 2));
    } catch (erro) {
      const detalhe =
        erro.response?.data?.message ??
        erro.response?.data ??
        erro.message ??
        "Erro desconhecido";
      setResposta(`Não foi possível buscar os exercícios na API: ${detalhe}`);
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
            <label htmlFor="resposta-api">Resposta da API</label>
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
