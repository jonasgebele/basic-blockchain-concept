package home.in.tum.de.transactions;

import home.in.tum.de.blockchain.Blockchain;
import home.in.tum.de.cryptography.StringUtility;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    private String transactionID; // this is also the hash of the transaction
    private PublicKey sender;
    private PublicKey reciepient;
    private float value;
    private byte [] signature; // this is to prevent anybody else from spending funds in our wallet

    private ArrayList<TransactionInput> inputs = new ArrayList<>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

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

    public boolean processTransaction () {
        if(!verifySignature()){
            System.out.println("Transaction Signature failed to verify");
            return false;
        }

        // gather transaction inputs (Make sure they are unspent)
        for(TransactionInput i : inputs) {
            i.UTXO = Blockchain.UTXOs.get(i.transactionOutputID);
        }

        // check if transaction is valid
        if(getInputsValue() < Blockchain.minimumTransaction){
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
        }
        return false;

        // generate transaction outputs
        float leftOver = getInputsValue() - value;
        transactionID = calculateHash();
        outputs.add(new TransactionOutput(this.reciepient, value, transactionID));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionID));

        // add outputs to Unspent list
        for(TransactionOutput o : outputs){
            Blockchain.UTXOs.put(o.id, o);
        }

        // remove transaction inputs from UTXO lists as spent
        for (TransactionInput i : inputs) {
            if (i.UTXO == null){
                continue;
                // If transaction can not be found skip it
            }
            Blockchain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    // returns sum of inputs (UTXOs) values
    private float getInputsValue(){
        float total = 0;
        for(TransactionInput i : inputs){
            if(i.UTXO == null) {
                continue;
                // if transaction can not be found, skip it
            }
            total += i.UTXO.value;
        }
        return total;
    }

    // returns sum of outputs
    public float getOutputsValue(){
        float total = 0;
        for (TransactionOutput o : outputs){
            total += o.value;
        }
        return total;
    }
}
