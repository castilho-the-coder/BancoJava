// Arquivo: AcaoTransferencia.java

/**
 * Thread que executa uma transferencia.
 */
public class AcaoTransferencia implements Runnable {
    private final ContaBancaria origem;
    private final ContaBancaria destino;
    private final double valor;

    public AcaoTransferencia(ContaBancaria origem, ContaBancaria destino, double valor) {
        this.origem = origem;
        this.destino = destino;
        this.valor = valor;
    }

    @Override
    public void run() {
        origem.transferir(destino, valor);
    }
}