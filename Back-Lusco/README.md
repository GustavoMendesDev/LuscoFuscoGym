# 🎵 Atividade – CRUD de Músicas (Spring Boot + JDBC)

## 🎯 Objetivo

Desenvolver uma **API REST** simples para gerenciamento de músicas, implementando um **CRUD completo**, utilizando **Spring Boot**, **JdbcTemplate** e **banco H2**.

---

## ⚠️ Atenção

Antes de iniciar o desenvolvimento:

* Leia todo o enunciado com atenção antes de começar;
* Utilize **apenas o que foi visto em aula**;
* **Não utilize bibliotecas ou dependências externas**, além das já configuradas no projeto base;
* O foco da atividade é a **correta implementação dos endpoints**, validações e códigos HTTP.

---

## 📌 Requisitos Gerais

* A aplicação deve rodar em `http://localhost:8080`
* O recurso principal da API é **/musicas**
* As respostas devem utilizar **códigos HTTP adequados**
* Em casos de erro, **não é necessário retornar objetos detalhados**, apenas o status correto

---

## 📂 Estrutura do Modelo

A tabela **Musica** deve possuir os seguintes atributos:

* `id` : inteiro
* `nome` : string
* `artista` : string
* `album` : string
* `duracao` : inteiro

---

## 🔧 Endpoints da API


### 1️⃣   Listagem de músicas

**Objetivo:** Retornar todas as músicas cadastradas.

* **Método HTTP:** GET
* **URL:** `http://localhost:8080/musicas`

#### Exemplo de resposta:

```json
[
  {
    "id": 1,
    "nome": "Bohemian Rhapsody",
    "artista": "Queen",
    "album": "A Night at the Opera",
    "duracao": 354
  }
]
```

* Caso não existam músicas cadastradas, retorne **200 (OK)** com uma lista vazia.

---

### 2️⃣ Busca de música por ID

**Objetivo:** Buscar uma música específica pelo seu identificador.

* **Método HTTP:** GET

* **URL:** `http://localhost:8080/musicas/{id}`

* Caso o ID não exista, retorne o **status HTTP adequado**.

---

### 3️⃣ Cadastro de música

**Objetivo:** Cadastrar uma nova música no sistema.

* **Método HTTP:** POST
* **URL:** `http://localhost:8080/musicas`

#### Validações obrigatórias:

* Nome, artista e álbum não podem ser vazios ou nulos;
* Duração deve ser um número positivo;
* Não deve existir outra música com o **mesmo nome e artista** cadastrados.

#### Exemplo de requisição:

```json
{
  "nome": "Bohemian Rhapsody",
  "artista": "Queen",
  "album": "A Night at the Opera",
  "duracao": 354
}
```

#### Observações:

* Em caso de sucesso, retorne a música cadastrada com **ID gerado**;
* Caso já exista música com mesmo nome e artista, retorne o **status HTTP adequado**.

**Exemplo de resposta:**

```json
{
  "id": 1,
  "nome": "Bohemian Rhapsody",
  "artista": "Queen",
  "album": "A Night at the Opera",
  "duracao": 354
}
```

---


### 4️⃣ Atualização de música por ID

**Objetivo:** Atualizar os dados de uma música existente.

* **Método HTTP:** PUT
* **URL:** `http://localhost:8080/musicas/{id}`

#### Regras:

* O ID informado deve existir;
* Todas as validações de cadastro devem ser reaplicadas;
* O ID da música não deve ser alterado.

---

### 5️⃣ Exclusão de música por ID

**Objetivo:** Remover uma música do sistema.

* **Método HTTP:** DELETE

* **URL:** `http://localhost:8080/musicas/{id}`

* Caso o ID não exista, retorne o **status HTTP adequado**;

* Não é necessário retornar o objeto removido.

---

### 6️⃣ Busca dinâmica de músicas 🔥

**Objetivo:** Permitir a busca de músicas por diferentes critérios.

* **Método HTTP:** GET
* **URL:** `http://localhost:8080/musicas/search`

#### Parâmetros opcionais:

* `nome`
* `artista`
* `album`
* `duracao`

#### Exemplos:

```
/musicas/search?artista=Queen
/musicas/search?nome=love
/musicas/search?album=opera&duracao=354
```

* Todos os filtros são opcionais;
* Caso nenhum registro seja encontrado, retorne **200 (OK)** com uma lista vazia.
* Todos os filtros devem ser combinados, ou seja, a busca deve retornar apenas músicas que atendam a **todos os critérios** informados.
* A busca deve ser **case-insensitive** e permitir buscas parciais (ex: `nome=love` deve retornar músicas com "Love" no nome).

---

## 💡 Dicas Importantes

* Utilize corretamente os verbos HTTP (`GET`, `POST`, `PUT`, `DELETE`);
* Garanta o uso correto dos **códigos de status HTTP**;
* Evite duplicação de código;
* Validações devem ser centralizadas sempre que possível;
* Teste os endpoints usando o **Bruno**.

---

Boa atividade e bons estudos! 🎧📚
