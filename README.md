# Order Management System

## Objetivo
Este projeto implementa um serviço "order" que gerencia e calcula os produtos dos pedidos. Os pedidos são recebidos de um produto externo A, processados e calculados, e então disponibilizados para um produto externo B.

## Descrição
O sistema recebe pedidos, realiza a gestão e cálculo do valor total dos produtos, somando o valor de cada produto dentro do pedido. Também disponibiliza uma consulta dos pedidos e produtos, junto com seu status, para o produto externo B.

## Tecnologias Utilizadas
- Java
- Spring Boot
- Redis
- JPA/Hibernate
- Banco de dados (PostgreSQL)
- Kafka

## Funcionalidades
- Recebimento de pedidos do produto externo A
- Processamento e cálculo do valor total dos pedidos
- Armazenamento dos pedidos no banco de dados
- Disponibilização dos pedidos processados para o produto externo B
- Verificação de duplicação de pedidos
- Garantia de disponibilidade e consistência dos dados

## Estrutura do Projeto
- `config`: Configurações do Redis e Kafka
- `controller`: Controladores REST para receber e disponibilizar pedidos
- `service`: Serviços para processar e calcular pedidos
- `repository`: Repositórios JPA para armazenar pedidos
- `model`: Modelos de dados para pedidos e produtos

## Como Executar
1. Clone o repositório:
   ```bash
   git clone <URL_DO_REPOSITORIO>
   ```
2. Navegue até o diretório do projeto:
   ```bash
   cd ntt
   ```
3. Configure o banco de dados no arquivo `application.properties`.
4. Execute o projeto:
   ```bash
   ./mvnw spring-boot:run
   ```

## Endpoints
- `POST /api/orders`: Recebe um pedido do produto externo A
- `GET /api/orders`: Retorna todos os pedidos processados com seu status

## Considerações
- O sistema foi projetado para suportar uma volumetria de 150 mil a 200 mil pedidos por dia.
- Utiliza Redis para cache e gerenciamento de sessões.
- Utiliza Kafka para comunicação assíncrona e processamento de mensagens.
- Implementa mecanismos de retry e circuit breaker para garantir a resiliência do serviço.
- Utiliza transações no banco de dados para garantir a consistência dos dados.

## Pontos Extras
- Verificação de duplicação de pedidos.
- Garantia de disponibilidade do serviço com alta volumetria.
- Consistência dos dados e concorrência.
