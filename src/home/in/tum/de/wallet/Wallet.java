package home.in.tum.de.wallet;

import home.in.tum.de.blockchain.Blockchain;
import home.in.tum.de.transactions.Transaction;
import home.in.tum.de.transactions.TransactionInput;
import home.in.tum.de.transactions.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public PublicKey publicKey;
    public PrivateKey privateKey;
    private HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet () {
        generateKeyPair();
    }

    private void generateKeyPair () {
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            // returns a keyPairGenerator Objekt that can generate public and private keys
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // returns a SecureRandom object that implements the specified Random Number Generator
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // class specifies the set of parameters used for generating elliptic curve domain parameters

            keyGen.initialize(ecSpec, random);
            // Initializes the key pair generator using the specified parameter set and the SecureRandom implementation
            // of the highest-priority installed provider as the source of randomness
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }
        catch (InvalidAlgorithmParameterException e){
            System.out.println("Initializing of keyGen did not work");
            throw new RuntimeException(e);
        }
        catch(Exception e) {
            System.out.println("Instantiation of KeyPairGenerator did not work.");
            throw new RuntimeException(e);
        }
    }

    // returns balance and stores the UTXOs owned by this wallet in this.UTXOs
    public float getBalance(){
        float total = 0;
        for(HashMap.Entry<String, TransactionOutput> item : Blockchain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)){
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    // generates and returns a new transaction from this wallet
    public Transaction sendFunds (PublicKey _recipient, float value) {
        if(getBalance() < value){
            System.out.println("#Not enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total = total + UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs){
            UTXOs.remove(input.transactionOutputID);
        }

        return newTransaction;
    }


}
