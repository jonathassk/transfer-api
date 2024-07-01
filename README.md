# transferApi

## Visão Geral do Projeto

### Introdução
Esta API foi desenvolvida com o objetivo de verificação de qualificação tecnica. Como funcionalidade ela é capaz de executar transferência de fundos entre contas bancárias, oferecendo uma solução segura e eficiente para operações financeiras. A API de Transferência se integra com três outras APIs essenciais que são a de Cadastro, Contas e BACEN. Essa conexão entre os serviços foi feito nesse serviço inicial utilizando feign e para aumentar a disponibilidade do serviço e garantir a menor propagação de problemas causado por eventuais falhas, utilizei retry e circuit breaker.

### Arquitetura 
Nesse projeto, na arquitetura basica foi utilizada a seguinte.
Para integração entre os microserviços, utilizei feign e para salvar os dados das transações utilizei postgres como banco (apesar de cassandra tambem se mostrar uma opção com pontos positivos).
{img 1}
Em uma possivel melhora de projeto, eu faria as seguintes alterações 
{img 2}

### testes 
A aplicação conta com alguns testes unitarios, por questão de tempo (so tive o fim de semana para executar a tarefa) so cobri o service.

### Configuração e Instalação
para fazer a instalação da aplicação, voce pode usar o DOCKER, tem um dockerfile configurado, para executar ele dessa forma voce deve executar os seguintes comandos 
```
1 - mvn clean package
2 - docker-compose build
3 - docker-compose up
```

## .ENV
para a criação do .env, use as seguintes variaveis 
```
DB_URL=jdbc:postgresql://endereco:5433/dbname
DB_USER=username
DB_PASSWORD=password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

### documentação api 
a documentação do api pode ser vista no endereço quando a aplicação estiver rodando: [SWAGGER](http://localhost:8080/swagger-ui/index.html#/)

### proposta de design
*sistema na versao atual*

![image](https://github.com/jonathassk/transfer-api/assets/35012537/52762d56-e10b-4159-bdd3-364bb08b50db)

*Sistema completo com melhorias na disponibilidade, confiabilidade e robustez*

![image](https://github.com/jonathassk/transfer-api/assets/35012537/cf217736-fc85-4daf-a99d-981125ce05f8)

