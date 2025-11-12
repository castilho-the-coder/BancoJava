// Arquivo: AcaoDeposito.java

/**
 * Thread que executa um deposito.
 */
public class AcaoDeposito implements Runnable {
    private final ContaBancaria conta;
    private final double valor;

    public AcaoDeposito(ContaBancaria conta, double valor) {
        this.conta = conta;
        this.valor = valor;
    }

    @Override
    public void run() {
        conta.depositar(valor);
    }
}