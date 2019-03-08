package home.in.tum.de.transactions;

import home.in.tum.de.blockchain.Blockchain;
import home.in.tum.de.cryptography.StringUtility;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    private String transactionID; // hash of the transaction
    private PublicKey sender; // public key of sender
    private PublicKey reciepient; // public key of reciever
    private float value; // value
    private byte [] signature; // security signature

    private ArrayList<TransactionInput> inputs = new ArrayList<>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int transaction_counter = 0;

    public Transaction (PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    @NotNull
    private String calculateHash() {
        transaction_counter++; // avoid that 2 identical transactions have the same hash value
        return StringUtility.applySHA256(
                StringUtility.getStringFromKey(sender) +
                        StringUtility.getStringFromKey(reciepient) +
                        Float.toString(value) +
                        transaction_counter
        );
    }

    // signs all the data we do not wish to tampered with
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtility.getStringFromKey(sender) + StringUtility.getStringFromKey(reciepient) + Float.toString(value);
        signature = StringUtility.applyECDSASig(privateKey, data);
    }

    // verifies the data we signed hasnt been tampered with
    public boolean verifySignature(){
        String data = StringUtility.getStringFromKey(sender) + StringUtility.getStringFromKey(reciepient) + Float.toString(value);
        return StringUtility.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction () {
        if(!verifySignature()){
            System.out.println("Transaction Signature failed to verify");
            return false;
        }

        // gather transaction inputs (Make sure they are unspent)
        for(TransactionInput input : inputs) {
            input.UTXO = Blockchain.UTXOs.get(input.transactionOutputID);
        }

        if(getInputsValue() < Blockchain.minimumTransaction){
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

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
    @Contract(pure = true)
    private float getInputsValue(){
        float total = 0;
        for(TransactionInput i : inputs){
            if(i.UTXO == null) {
                continue;
                // if transaction can not be found, skip it
            }
            total = total +  i.UTXO.value;
        }
        return total;
    }

    // returns sum of outputs
    public float getOutputsValue(){
        float total = 0;
        for (TransactionOutput o : outputs){
            total = total + o.value;
        }
        return total;
    }
}