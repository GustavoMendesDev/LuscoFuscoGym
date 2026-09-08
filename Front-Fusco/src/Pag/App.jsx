import { useState } from "react";
import { exerciciosApi } from "../services/exercicios";

const CAMPOS = [
  { nome: "nome", rotulo: "Nome do exercício", tipo: "text", placeholder: "Ex.: Supino reto" },
  { nome: "grupo", rotulo: "Grupo muscular", tipo: "text", placeholder: "Ex.: Peito" },
  { nome: "series", rotulo: "Séries", tipo: "number", placeholder: "Ex.: 4", min: "1" },
  { nome: "repeticoes", rotulo: "Repetições", tipo: "number", placeholder: "Ex.: 12", min: "1" },
  { nome: "carga", rotulo: "Carga", tipo: "text", placeholder: "Ex.: 20 kg" },
];

export default function App() {
  const [exercicio, setExercicio] = useState({});
  const [mensagem, setMensagem] = useState("");
  const [salvando, setSalvando] = useState(false);

  function atualizarCampo(evento) {
    const { name, value } = evento.target;
    setExercicio((atual) => ({ ...atual, [name]: value }));
    setMensagem("");
  }

  async function salvar() {
    setSalvando(true);
    setMensagem("");

    try {
      await exerciciosApi.criar(exercicio);
      setMensagem("Exercício enviado para a API!");
      setExercicio({});
      window.location.assign("/return");
    } catch (erro) {
      const detalhe =
        erro.response?.data?.message ??
        erro.response?.data ??
        erro.message ??
        "Erro desconhecido";
      setMensagem(`Não foi possível salvar: ${detalhe}`);
    } finally {
      setSalvando(false);
    }
  }

  return (
    <main className="page">
      <section className="card" aria-labelledby="titulo">
        <header className="brand">
          <img className="sunrise-icon" src="/assets/icon/sunrise.png" alt="" />

          <span>LuscoFusco<span>Gym</span></span>
        </header>

        <h1 id="titulo">Aproveite seu treino junto ao<br />LuscoFusco</h1>

        <form onSubmit={(evento) => { evento.preventDefault(); salvar(); }}>
          <div className="fields">
            {CAMPOS.map((campo) => (
              <label className="field" key={campo.nome}>
                <span>{campo.rotulo}</span>
                <input
                  name={campo.nome}
                  type={campo.tipo}
                  min={campo.min}
                  value={exercicio[campo.nome] ?? ""}
                  onChange={atualizarCampo}
                  placeholder={campo.placeholder}
                  required
                />
              </label>
            ))}
          </div>

          <button id="salvando" className="submit" type="submit" disabled={salvando}>
            {salvando ? "Salvando..." : "Salvar exercício"}
          </button>
          {mensagem && <p className="message" role="status">{mensagem}</p>}
        </form>
      </section>
    </main>
  );
}
