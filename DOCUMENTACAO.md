# Arena Vibe Sports - Documentação do sistema

## 1. Visão geral

O Arena Vibe Sports é um sistema de gerenciamento de quadras esportivas.
Ele possui:

- um telão público para acompanhar o estado das quadras em tempo real;
- um painel administrativo para iniciar partidas, agendar reservas, consultar
  disponibilidade, registrar pagamentos e cancelar reservas;
- uma API REST em Spring Boot;
- persistência dos dados em MySQL;
- documentação interativa da API com Swagger/OpenAPI.

O frontend está no projeto irmão `..\arena-vibe-frontend` e o backend está
neste projeto (`arena-vibe-volei`).

## 2. Arquitetura

### Frontend

O frontend foi implementado com HTML5, CSS3 e JavaScript puro:

| Arquivo | Responsabilidade |
| --- | --- |
| `index.html` | Telão público com status das quadras e cronômetros |
| `admin.html` | Painel administrativo |
| `admin.js` | Operações administrativas e renderização de tabelas |
| `config.js` | URL da API, endpoints e funções de formatação |
| `package.json` | Configuração opcional para servir os arquivos estáticos |

O frontend consulta o backend usando `fetch`. A URL padrão é:

```text
http://localhost:8081
```

Para alterar a URL sem editar o arquivo:

```javascript
localStorage.setItem('arena_api_base', 'http://192.168.0.10:8081');
```

### Backend

O backend usa:

- Java 21;
- Spring Boot 4.0.8;
- Spring MVC;
- Spring Data JPA/Hibernate;
- MySQL Connector/J;
- SpringDoc OpenAPI;
- Maven.

Organização principal:

| Pacote/arquivo | Responsabilidade |
| --- | --- |
| `Controller` | Endpoints HTTP |
| `Service/QuadraService` | Regras de reservas, pagamentos e cronômetros |
| `Model` | Entidades persistidas no banco |
| `api/Repository` | Repositórios JPA |
| `api/Dto` | Objetos de entrada e saída da API |
| `api/Enum` | Estados de quadra, reserva e pagamento |
| `api/SecurityConfig` | CORS e regras de acesso |
| `api/OpenApiConfig` | Título e metadados do Swagger |

## 3. Banco de dados

A aplicação usa MySQL:

```text
Banco: arena_vibe_volei
Host: localhost
Porta: 3306
Usuário padrão: root
```

As credenciais podem ser sobrescritas por variáveis de ambiente:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

O arquivo `application.properties` usa `spring.jpa.hibernate.ddl-auto=update`,
portanto o Hibernate cria ou atualiza as tabelas conforme as entidades.

Tabelas:

- `tb_quadras`: cadastro e estado atual de cada quadra;
- `tb_reservas`: histórico de reservas, valores, pagamentos, cancelamentos e
  horas excedentes.

Relacionamento:

```text
tb_quadras 1 ---- N tb_reservas
```

As três quadras permanentes atualmente utilizadas são:

- Quadra Sol;
- Quadra Mar;
- Quadra Areia.

## 4. Fluxo de uso

### Iniciar uma partida

1. O administrador informa o cliente e a duração.
2. O frontend chama `POST /api/admin/quadras/{id}/iniciar`.
3. O backend cria uma reserva com início imediato.
4. A reserva fica `EM_ANDAMENTO`.
5. A quadra fica `OCUPADA`.
6. O telão passa a exibir o cronômetro.

### Agendar uma reserva futura

1. O administrador escolhe a quadra, cliente, data e duração.
2. O frontend chama `POST /api/admin/quadras/{id}/reservas`.
3. O backend valida conflitos de horário.
4. A reserva fica `AGENDADA`.
5. Ela aparece em `proximasReservas` e na lista de agendamentos futuros.
6. Quando o horário chega, o backend pode promovê-la para `EM_ANDAMENTO`.

### Encerrar uma partida

1. O administrador clica em liberar/encerrar.
2. O frontend chama `POST /api/admin/quadras/{id}/liberar`.
3. A reserva ativa é marcada como `CONCLUIDA`.
4. Horas excedentes são calculadas.
5. A quadra volta para `LIVRE`.

### Pagamento

O endpoint `POST /api/admin/reservas/{id}/pagar` altera o pagamento para
`PAGO`. O valor final considera o valor-base e eventual taxa de hora extra.

### Cancelamento

O endpoint `POST /api/admin/reservas/{id}/cancelar` altera a reserva para
`CANCELADA`. Se o pagamento já estava `PAGO`, ele passa para `ESTORNADO`.
Também é registrada a data em `canceladaEm`.

## 5. Regras de negócio

- Cliente e duração são obrigatórios.
- A duração deve ser maior que zero.
- Uma reserva não pode terminar no passado.
- Reservas ativas não podem se sobrepor na mesma quadra.
- Reservas canceladas não bloqueiam disponibilidade.
- Reservas concluídas não podem ser canceladas.
- Reservas canceladas não podem ser pagas.
- A consulta de disponibilidade aceita períodos de até 90 dias.
- A taxa extra usa o multiplicador configurado em:

```properties
app.reserva.multiplicador-hora-extra=1.5
```

- A hora extra é calculada depois do término previsto.
- A quadra é destacada como `HORA_EXTRA` quando o excedente chega a
  pelo menos 10 minutos.

## 6. Status utilizados

### Quadra

| Status | Significado |
| --- | --- |
| `LIVRE` | Sem partida em andamento |
| `OCUPADA` | Existe uma reserva em andamento |
| `HORA_EXTRA` | A partida ultrapassou o horário previsto |

### Reserva

| Status | Significado |
| --- | --- |
| `AGENDADA` | Reserva futura |
| `EM_ANDAMENTO` | Partida ativa |
| `CONCLUIDA` | Partida encerrada |
| `CANCELADA` | Reserva cancelada |

### Pagamento

| Status | Significado |
| --- | --- |
| `PENDENTE` | Ainda não registrado |
| `PAGO` | Pagamento recebido |
| `ESTORNADO` | Pagamento revertido após cancelamento |

## 7. Endpoints da API

Base URL:

```text
http://localhost:8081
```

### Painel

```http
GET /api/painel
```

Retorna as quadras com estado atual, reserva ativa, próximas reservas,
histórico, cronômetro e valores.

### Quadras

```http
POST /api/admin/quadras
POST /api/admin/quadras/{id}/iniciar
POST /api/admin/quadras/{id}/reservas
POST /api/admin/quadras/{id}/liberar
GET  /api/admin/quadras/{id}/disponibilidade?inicio=2026-09-22&fim=2026-09-30
```

Cadastro de quadra:

```json
{
  "nome": "Quadra Sol",
  "valorHora": 100
}
```

Payload para iniciar ou agendar:

```json
{
  "clienteResponsavel": "Nome do cliente",
  "inicioReserva": "2026-09-22T19:00:00",
  "duracaoMinutos": 60
}
```

Para iniciar imediatamente, `inicioReserva` pode ser omitido ou enviado como
`null`.

### Reservas

```http
GET  /api/admin/reservas
POST /api/admin/reservas/{id}/cancelar
POST /api/admin/reservas/{id}/pagar
```

## 8. Swagger/OpenAPI

Com o backend em execução, acessar:

```text
http://localhost:8081/swagger-ui/index.html
```

Também estão disponíveis:

```text
http://localhost:8081/swagger-ui.html
http://localhost:8081/v3/api-docs
```

O Swagger é fornecido pela dependência
`springdoc-openapi-starter-webmvc-ui` e descreve automaticamente os endpoints
dos controllers.

## 9. Como executar

### Banco

Certifique-se de que o serviço MySQL esteja em execução e que o banco
`arena_vibe_volei` esteja acessível.

### Backend

Na pasta `arena-vibe-volei`:

```powershell
mvn spring-boot:run
```

Ou:

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrão, a API inicia na porta `8081`.

Para alterar:

```powershell
$env:SERVER_PORT="8082"
mvn spring-boot:run
```

### Frontend

Na pasta `arena-vibe-frontend`, abra `index.html` para o telão ou
`admin.html` para o painel administrativo. Para evitar restrições do navegador,
é recomendado servir a pasta por um servidor HTTP estático.

## 10. Teste automatizado do fluxo

O backend possui o script:

```text
scripts\testar-fluxo.ps1
```

Executar com:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\testar-fluxo.ps1
```

O script:

1. verifica o painel;
2. cria uma quadra temporária;
3. cria e inicia uma reserva;
4. consulta a disponibilidade;
5. registra o pagamento;
6. libera a quadra;
7. confirma a reserva concluída no histórico.

O script cria dados de teste no banco. Após executá-lo, remova os registros
temporários ou execute-o em uma base de desenvolvimento.

## 11. Segurança e CORS

Atualmente os endpoints estão liberados pelo `SecurityConfig`, sem autenticação
de usuário. Isso é adequado para desenvolvimento local, mas deve ser revisto
antes de publicar o sistema.

O CORS permite as origens locais:

```text
http://localhost:5500
http://127.0.0.1:5500
```

## 12. Observações importantes

- O frontend deve apontar para a porta `8081`, que é a porta configurada no
  backend.
- O banco precisa permanecer ativo para que o painel carregue dados.
- O frontend não possui tela de cadastro de quadras; o cadastro existe na API.
- Os dados de teste devem ser mantidos separados dos dados reais.
- Para produção, configure credenciais seguras, autenticação e uma política de
  CORS restrita.
