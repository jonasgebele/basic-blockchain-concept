package home.in.tum.de.wallet;

import home.in.tum.de.blockchain.Blockchain;
import home.in.tum.de.transactions.TransactionOutput;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;

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
        for(Map.Entry<String, TransactionOutput> item : Blockchain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)){
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    /*

    Here will be sendsFunds Function

     */

}
