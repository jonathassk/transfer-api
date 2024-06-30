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

### Configuração e Instalação

### documentação api 

### docker 
É possivel utilizar o docker-compose encontrado na raiz do sistema para fazer a configuração dos serviços necessarios para o bom funcionamento da aplicação.
