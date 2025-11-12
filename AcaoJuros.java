// Arquivo: AcaoJuros.java

/**
 * Thread que executa um credito de juros.
 */
public class AcaoJuros implements Runnable {
    private final ContaBancaria conta;
    private final double taxa; // ex: 0.05 para 5%

    public AcaoJuros(ContaBancaria conta, double taxa) {
        this.conta = conta;
        this.taxa = taxa;
    }

    @Override
    public void run() {
        conta.creditarJuros(taxa);
    }
}