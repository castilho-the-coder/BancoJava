# BancoJava — Simulação Bancária (exemplo)

Projeto Java que simula operações bancárias concorrentes entre contas (depósitos, saques, juros e transferências).

Conteúdo principal
- `ContaBancaria.java` — representação da conta com operações sincronizadas.
- `AcaoDeposito.java`, `AcaoSaque.java`, `AcaoJuros.java`, `AcaoTransferencia.java` — `Runnable`s que executam operações sobre as contas.
- `SimulacaoBanco.java` — classe `main` que cria contas e submete ações a um `ExecutorService`.

Requisitos
- JDK instalado (Java 8+). No Windows, um caminho comum é `C:\Program Files (x86)\Java\jdk1.8.0_101\bin`.

# BancoJava — Simulação Bancária (exemplo)

Este repositório contém uma pequena simulação bancária multi-threaded em Java. O objetivo é demonstrar concorrência sobre recursos compartilhados (contas) e técnicas básicas para evitar problemas como condições de corrida e deadlocks.

Resumo rápido
- Linguagem: Java (JDK 8+ compatível)
- Propósito: demonstrar operações concorrentes (depósito, saque, juros, transferência) sobre instâncias de `ContaBancaria`.

Estrutura de arquivos
- `ContaBancaria.java` — modelo da conta bancária (id, saldo) com métodos que implementam operações atômicas e a operação de transferência entre contas.
- `AcaoDeposito.java` — `Runnable` que chama `ContaBancaria.depositar(double)`.
- `AcaoSaque.java` — `Runnable` que chama `ContaBancaria.sacar(double)`.
- `AcaoJuros.java` — `Runnable` que chama `ContaBancaria.creditarJuros(double)`.
- `AcaoTransferencia.java` — `Runnable` que chama `ContaBancaria.transferir(ContaBancaria destino, double valor)`.
- `SimulacaoBanco.java` — classe `main` que cria contas, submete várias ações a um `ExecutorService` (pool fixo) e imprime saldos finais.

Especificação de cada classe

1) `ContaBancaria.java`

- Campos:
   - `private final int idConta` — identificador da conta (usado para ordenar locks em transferências).
   - `private double saldo` — saldo atual (nota: o uso de `double` é simples mas pode causar problemas de precisão; ver seção melhorias).

- Construtor:
   - `ContaBancaria(int idConta, double saldoInicial)` — inicializa id e saldo.

- Métodos principais (visão funcional):
   - `synchronized void depositar(double valor)` — adiciona ao saldo de forma atômica; ignora valores <= 0.
   - `synchronized boolean sacar(double valor)` — remove do saldo se houver fundos; retorna `true` em sucesso, `false` caso contrário.
   - `synchronized void creditarJuros(double taxa)` — calcula juros (saldo * taxa) e adiciona.
   - `void transferir(ContaBancaria destino, double valor)` — implementação da transferência entre contas:
      - Ordena os locks por `idConta` (conta com menor id é travada primeiro) para evitar deadlocks.
      - Usa dois blocos `synchronized` aninhados (`synchronized(lock1) { synchronized(lock2) { ... } }`).
      - Modifica diretamente os campos `saldo` de ambas as contas (não chama `sacar`/`depositar` internos para reduzir overhead de locking).

   Observações sobre concorrência: a estratégia atual é adequada para este exemplo. Se você expandir o sistema para operações mais complexas ou integrar com persistência, considere usar `ReentrantLock` com timeout, transações, ou um serviço central (ex.: `Banco`) para coordenar transferências.

2) `AcaoDeposito`, `AcaoSaque`, `AcaoJuros`, `AcaoTransferencia`

- Cada classe implementa `Runnable` e simplesmente encapsula uma chamada ao método correspondente em `ContaBancaria`.
- Isso torna fácil submeter operações a um `ExecutorService` e simular concorrência.

3) `SimulacaoBanco.java`

- Padrão: cria duas contas e submete várias ações ao `ExecutorService` (pool fixo de 10 threads). Ao final, aguarda término (awaitTermination) e imprime os saldos finais.

Como compilar e executar (Windows / PowerShell)

1) Abra PowerShell no diretório do projeto:

    cd 'C:\\Users\\aluno\\BancoJava'

2) Compilar (recomendado: forçar encoding UTF-8):

    "C:\\caminho\\para\\jdk\\bin\\javac.exe" -encoding UTF-8 *.java

Exemplo (com JDK localizado em `C:\\Program Files (x86)\\Java\\jdk1.8.0_101`):

    & 'C:\\Program Files (x86)\\Java\\jdk1.8.0_101\\bin\\javac.exe' -encoding UTF-8 *.java

3) Executar:

    & 'C:\\Program Files (x86)\\Java\\jdk1.8.0_101\\bin\\java.exe' -cp . SimulacaoBanco

Observações sobre encoding

- Problema comum: compilador reclama de "unmappable character for encoding Cp1252" quando arquivos fonte estão em UTF-8 mas `javac` lê no encoding de plataforma.

- Soluções:
   - Compilar com `-encoding UTF-8` (simples e portátil).
   - Configurar o editor (VS Code) para salvar arquivos em UTF-8: `"files.encoding": "utf8"`.
   - Adicionar JDK ao PATH para facilitar uso do `javac` sem caminho absoluto.

Limpeza (remover classes compiladas)

No PowerShell:

    Get-ChildItem -Path . -Filter '*.class' -Recurse | Remove-Item -Force

Sugestões de melhorias (próximos passos)

- Usar `BigDecimal` para valores monetários: `double` traz erros de arredondamento; `BigDecimal` com escala fixa e `RoundingMode` é o padrão para aplicações financeiras.
- Tornar APIs consistentes: decidir entre retorno booleano ou exceções para falhas (por exemplo `depositar` atualmente não retorna nada enquanto `sacar` retorna `boolean`).
- Tratar `transferir` quando `origem == destino` (no-op) e validar `destino != null`.
- Substituir `System.out.println` por um `Logger` (por exemplo `java.util.logging` ou SLF4J) para controle de níveis, timestamps e fácil desativação em produção.
- Adicionar testes JUnit: testes unitários para métodos atômicos e testes de estresse concorrente que verificam a invariante da soma total dos saldos.
- Considerar uso de `ReentrantLock` com `tryLock` e timeout em cenários que podem travar ou bloquear por muito tempo.

Exemplo de teste de invariância (manual)

- Crie N contas com saldos iniciais conhecidos, execute muitas transferências aleatórias entre elas em paralelo e verifique que a soma total dos saldos permaneceu igual (se não houver depósitos/saques externos).

Scripts úteis (opcionais)

- Posso adicionar `build.ps1` (compila com UTF-8), `clean.ps1` (remove .class) e `run.ps1` (compila+executa). Quer que eu os crie e commite?

Licença

- Este é um exemplo educacional. Sinta-se livre para usar/alterar conforme precisar.

-----

Arquivo gerado/atualizado por assistência — se quiser, eu posso também commitar este README e adicionar scripts auxiliares (`build.ps1`, `clean.ps1`, `run.ps1`) automaticamente.
