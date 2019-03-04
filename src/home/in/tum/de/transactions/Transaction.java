package home.in.tum.de.transactions;

import home.in.tum.de.StringUtility;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionID; // this is also the hash of the transaction
    private PublicKey sender;
    private PublicKey reciepient;
    private float value;
    public byte [] signature; // this is to prevent anybody else from spending funds in our wallet

    private ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int transaction_counter = 0; // a rough count of how many transaction have been generated

    public Transaction (PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        transaction_counter++;
        return StringUtility.applySHA256(
                StringUtility.getStringFromKey(sender) +
                        StringUtility.getStringFromKey(reciepient) +
                        Float.toString(value) +
                        transaction_counter
        );
    }

    // signs all the data we do not wish to tampered with
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtility.getStringFromKey(sender) + StringUtility.getStringFromKey(reciepient);
        signature = StringUtility.applyECDSASig(privateKey, data);
    }

    // verifies the data we signed hasnt been tampered with
    public boolean verifySignature(){
        String data = StringUtility.getStringFromKey(sender) + StringUtility.getStringFromKey(reciepient);
        return StringUtility.verifyECDSASig(sender, data, signature);
    }
}
