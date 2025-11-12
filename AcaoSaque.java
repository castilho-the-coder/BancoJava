// Arquivo: AcaoSaque.java

/**
 * Thread que executa um saque.
 */
public class AcaoSaque implements Runnable {
    private final ContaBancaria conta;
    private final double valor;

    public AcaoSaque(ContaBancaria conta, double valor) {
        this.conta = conta;
        this.valor = valor;
    }

    @Override
    public void run() {
        conta.sacar(valor);
    }
}