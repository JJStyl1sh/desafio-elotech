# Desafio Técnico - Elotech

Sistema de gestão de biblioteca com cadastro de livros, usuários, empréstimos, devoluções e recomendações de livros.

---

## Tecnologias

**Backend**
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven

**Frontend**
- React 18
- Vite
- CSS puro

**Infraestrutura**
- Docker
- Docker Compose

---

## Pré-requisitos

- Docker e Docker Compose instalados
- Chave de API do Google Books ([obter aqui](https://console.cloud.google.com))

---

## Como executar

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/desafio-elotech-api.git
cd desafio-elotech-api
```

### 2. Configure a chave do Google Books

Abra o `docker-compose.yml` e substitua `COLE_SUA_CHAVE_AQUI` pela sua chave:

```yaml
- GOOGLE_BOOKS_API_KEY=sua_chave_aqui
```

### 3. Suba os containers

```bash
docker-compose up --build
```

Aguarde o build finalizar. Os serviços estarão disponíveis em:

| Serviço  | URL                        |
|----------|----------------------------|
| Frontend | http://localhost:5173       |
| Backend  | http://localhost:8081       |
| Banco    | localhost:5432              |

### Executar sem Docker (desenvolvimento local)

Certifique-se de ter PostgreSQL rodando localmente e um arquivo `application.properties` configurado, então:

```bash
# Backend
mvn spring-boot:run

# Frontend (em outro terminal)
cd frontend-elotech
npm install
npm run dev
```

---

## Endpoints da API

### Usuários `/usuarios`

| Método | Endpoint         | Descrição             |
|--------|------------------|-----------------------|
| GET    | /usuarios        | Lista todos (paginado)|
| GET    | /usuarios/{id}   | Busca por ID          |
| POST   | /usuarios        | Cria novo usuário     |
| PUT    | /usuarios/{id}   | Atualiza usuário      |
| DELETE | /usuarios/{id}   | Remove usuário        |

**Exemplo de body (POST/PUT):**
```json
{
  "nome": "João",
  "email": "joao@email.com",
  "telefone": "44999777777"
}
```

---

### Livros `/livros`

| Método | Endpoint                  | Descrição                        |
|--------|---------------------------|----------------------------------|
| GET    | /livros                   | Lista todos (paginado)           |
| GET    | /livros/{id}              | Busca por ID                     |
| POST   | /livros                   | Cria novo livro                  |
| PUT    | /livros/{id}              | Atualiza livro                   |
| DELETE | /livros/{id}              | Remove livro                     |
| GET    | /livros/google?titulo=... | Busca livros na API Google Books |
| POST   | /livros/google?isbn=...   | Adiciona livro do Google Books   |
| GET    | /livros/recomendacoes/{usuarioId} | Retorna recomendações      |

**Exemplo de body (POST/PUT):**
```json
{
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "isbn": "9780132350884",
  "dataPublicacao": "2008-08-01",
  "categoria": "Technology"
}
```

---

### Empréstimos `/emprestimos`

| Método | Endpoint            | Descrição                        |
|--------|---------------------|----------------------------------|
| GET    | /emprestimos        | Lista todos (paginado)           |
| POST   | /emprestimos        | Registra novo empréstimo         |
| PUT    | /emprestimos/{id}   | Atualiza status e data devolução |

**Exemplo de body (POST):**
```json
{
  "usuarioId": 1,
  "livroId": 2
}
```

**Exemplo de body (PUT):**
```json
{
  "status": "DEVOLVIDO",
  "dataDevolucao": "2026-03-20"
}
```

Status disponíveis: `EMPRESTADO`, `DEVOLVIDO`, `ATRASADO`

---

## Recomendação de Livros

O sistema recomenda livros com base nas categorias dos empréstimos do usuário. Se o usuário já emprestou livros da categoria "Fiction", o sistema recomendará outros livros da mesma categoria que ele ainda não emprestou.

> A recomendação utiliza correspondência exata de categoria. Livros adicionados via Google Books podem ter variações de categoria (ex: "Fiction" vs "Literary Fiction") que afetam os resultados.

---

## Testes

Os testes unitários cobrem os serviços principais da aplicação utilizando JUnit 5 e Mockito.

```bash
mvn test
```

---

## Observações

- A chave da API do Google Books não está inclusa no repositório. Configure conforme instruções acima.
- Datas parciais retornadas pela API do Google Books (ex: `"1990-08"` ou `"1959"`) são normalizadas para o primeiro dia do período.
- O campo `autor` armazena múltiplos autores separados por vírgula para manter compatibilidade com a especificação. Em produção, recomendaríamos uma tabela dedicada para autores.