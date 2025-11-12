// Arquivo: ContaBancaria.java

/**
 * Classe da Conta Bancaria (O Recurso Compartilhado / Monitor)
 * Metodos sao 'synchronized' para garantir thread-safety.
 */
public class ContaBancaria {
    private final int idConta;
    private double saldo;

    public ContaBancaria(int idConta, double saldoInicial) {
        this.idConta = idConta;
        this.saldo = saldoInicial;
    }

    public int getIdConta() {
        return idConta;
    }

    public double getSaldo() {
        return saldo;
    }
    
    public String getInfo() {
        return "Conta " + idConta;
    }

    // --- Acoes Atomicas Simples ---
    // 'synchronized' garante que apenas UMA thread por vez
    // possa executar este metodo NESTE objeto de conta.

    /**
     * Adiciona valor ao saldo. (Thread-safe)
     */
    public synchronized void depositar(double valor) {
        if (valor <= 0) return;
        this.saldo += valor;
        System.out.println(getInfo() + ": Deposito de R$" + valor + ". Novo saldo: R$" + this.saldo);
    }

    /**
     * Remove valor do saldo. (Thread-safe)
     * Retorna true se o saque foi bem-sucedido.
     */
    public synchronized boolean sacar(double valor) {
        if (valor <= 0) return false;
        
        if (this.saldo >= valor) {
            this.saldo -= valor;
            System.out.println(getInfo() + ": Saque de R$" + valor + ". Novo saldo: R$" + this.saldo);
            return true;
        } else {
            System.out.println(getInfo() + ": Tentativa de saque de R$" + valor + " FALHOU (saldo insuficiente).");
            return false;
        }
    }
    
    /**
     * Adiciona juros ao saldo. (Thread-safe)
     */
    public synchronized void creditarJuros(double taxa) {
        if (taxa <= 0) return;
        double juros = this.saldo * taxa;
        this.saldo += juros;
        System.out.println(getInfo() + ": Juros de R$" + juros + " (taxa " + taxa*100 + "%). Novo saldo: R$" + this.saldo);
    }


    // --- Acao Atomica Complexa (Transferencia) ---
    
    /**
     * Transfere valor desta conta para a conta destino.
     * Esta e a parte mais critica, pois precisa travar DOIS monitores (contas).
     * Usamos ordenacao de locks (pelo ID da conta) para evitar DEADLOCK.
     */
    public void transferir(ContaBancaria destino, double valor) {
        if (valor <= 0) return;

        // 1. Define a ordem de travamento (lock)
        // Sempre trava primeiro a conta com o ID menor.
        ContaBancaria lock1 = (this.idConta < destino.idConta) ? this : destino;
        ContaBancaria lock2 = (this.idConta < destino.idConta) ? destino : this;

        // 2. Adquire os locks na ordem definida
        synchronized (lock1) {
            
            synchronized (lock2) {
                
                // 3. Executa a logica de transferencia
                // Importante: NAO chamamos this.sacar() e destino.depositar()
                
                if (this.saldo >= valor) {
                    this.saldo -= valor;
                    destino.saldo += valor;
                    System.out.println("Transferencia SUCESSO: R$" + valor + " de " 
                                       + this.getInfo() + " para " + destino.getInfo());
                } else {
                    System.out.println("Transferencia FALHOU: R$" + valor + " de " 
                                       + this.getInfo() + " para " + destino.getInfo() + " (saldo insuficiente).");
                }
            }
        }
    }
}