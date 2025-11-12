// Arquivo: SimulacaoBanco.java

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Classe principal que orquestra a simulacao bancaria.
 */
public class SimulacaoBanco {

    public static void main(String[] args) {
    System.out.println("Iniciando simulacao bancaria...");

        // Cria duas contas com saldos iniciais.
        ContaBancaria conta1 = new ContaBancaria(1, 1000.00);
        ContaBancaria conta2 = new ContaBancaria(2, 500.00);

        // ExecutorService gerencia o pool de threads de forma eficiente.
        ExecutorService executor = Executors.newFixedThreadPool(10); // 10 threads

    // --- Criacao das Acoes (Threads) ---
        executor.submit(new AcaoDeposito(conta1, 100.00));
        executor.submit(new AcaoSaque(conta1, 200.00));
        executor.submit(new AcaoJuros(conta1, 0.05));
        executor.submit(new AcaoDeposito(conta1, 50.00));
        
        executor.submit(new AcaoDeposito(conta2, 300.00));
        executor.submit(new AcaoSaque(conta2, 100.00));

        executor.submit(new AcaoTransferencia(conta1, conta2, 150.00));
    executor.submit(new AcaoTransferencia(conta2, conta1, 200.00)); // Transferencia cruzada
        executor.submit(new AcaoSaque(conta1, 50.00));
        executor.submit(new AcaoTransferencia(conta1, conta2, 50.00));
    executor.submit(new AcaoTransferencia(conta2, conta1, 100.00)); // Transferencia cruzada
        executor.submit(new AcaoJuros(conta2, 0.02));


        // Encerra o executor (para de aceitar novas tarefas)
        executor.shutdown();
        
        try {
            // Aguarda todas as threads terminarem
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("As tarefas nao terminaram a tempo, forcando o encerramento.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

    System.out.println("\n--- SIMULACAO CONCLUIDA ---");
        System.out.println("Saldo final " + conta1.getInfo() + ": " + String.format("R$%.2f", conta1.getSaldo()));
        System.out.println("Saldo final " + conta2.getInfo() + ": " + String.format("R$%.2f", conta2.getSaldo()));
    }
}