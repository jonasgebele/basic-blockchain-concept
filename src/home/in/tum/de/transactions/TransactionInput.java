package home.in.tum.de.transactions;

// TransactionInput will reference TransactionOutputs that have not yet been spent. The transactionOutputID
// will be used to find the relevant TransactionOutput, allowing miners to check the ownership
public class TransactionInput {

    public String transactionOutputID; // reference to TranactionOutput -> TransactionID
    public TransactionOutput UTXO; // contains the unspent transaction output

    public TransactionInput(String transactionOutputID){
        this.transactionOutputID = transactionOutputID;
    }
}
