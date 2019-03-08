package home.in.tum.de.transactions;

import home.in.tum.de.cryptography.StringUtility;

import java.security.PublicKey;

/*
show the amount relevant adresses recieved in the transaction
the outputs are referenced as inputs in new transactions
 */
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
