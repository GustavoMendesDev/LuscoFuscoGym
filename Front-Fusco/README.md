# LuscoFuscoGym

Front-end React independente do Lusco-Fusco original. Os exercícios são
enviados ao backend pela camada Axios em `src/services` — não há persistência
em `localStorage`.

## API

Durante o desenvolvimento, o Vite encaminha automaticamente as requisições de
`/exercicios` para `http://localhost:8080/exercicios`. Isso evita bloqueios de
CORS no navegador. Para produção, copie `.env.example` para `.env` e informe a
URL pública da API:

```bash
VITE_EXERCICIOS_API_URL=https://api.exemplo.com/exercicios
```

O formulário cria um exercício com `POST /exercicios`, encaminhado ao
`POST http://localhost:8080/exercicios` pelo proxy. A camada também expõe
`listar()` (`GET /exercicios`) e `buscarPorId(id)` (`GET /exercicios/:id`) em
`src/services/exercicios.js`.

## Executar

```bash
npm install
npm run dev
```

Abra o endereço exibido pelo Vite (normalmente `http://localhost:5173`).
