import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";
import Return from "./Return";
import "./styles.css";

const Tela = window.location.pathname === "/return" ? Return : App;

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <Tela />
  </StrictMode>,
);
