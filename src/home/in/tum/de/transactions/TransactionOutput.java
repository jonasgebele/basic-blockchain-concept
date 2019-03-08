package home.in.tum.de.transactions;

import home.in.tum.de.cryptography.StringUtility;

import java.security.PublicKey;

// transactionOutput will show the final amount sent to each party from the transaction. These act as the proof
// that the coins have been send.
public class TransactionOutput {

    public String id;
    public PublicKey reciepient;
    public float value;
    public String parentTransactionID; // the id of the transaction this output was created in

    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionID){
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionID = parentTransactionID;
        this.id = StringUtility.applySHA256(
                StringUtility.getStringFromKey(reciepient) +
                        Float.toString(value) +
                        parentTransactionID
        );
    }

    public boolean isMine(PublicKey publicKey){
        return (publicKey == reciepient);
    }
}
