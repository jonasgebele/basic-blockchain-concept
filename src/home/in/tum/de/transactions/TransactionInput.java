package home.in.tum.de.transactions;

/*
references to previous transactions that prove the sender has funds to send
 */
public class TransactionInput {

    public String transactionOutputID; // reference to TranactionOutput -> TransactionID
    public TransactionOutput UTXO; // contains the unspent transaction output

    public TransactionInput(String transactionOutputID){
        this.transactionOutputID = transactionOutputID;
    }
}
