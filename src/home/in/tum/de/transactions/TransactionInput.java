package home.in.tum.de.transactions;

public class TransactionInput {
    public String transactionOutputID;
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutputID){
        this.transactionOutputID = transactionOutputID;
    }
}
