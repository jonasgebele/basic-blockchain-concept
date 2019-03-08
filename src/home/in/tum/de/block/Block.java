package home.in.tum.de.block;

import home.in.tum.de.cryptography.StringUtility;
import home.in.tum.de.transactions.Transaction;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Date;

public class Block {

    private String hash;
    private String previousHash;
    private String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private long timeStamp;
    private int nonce;

    public Block (String data, String previousHash){
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        // important to calculate the hash after we set the other values
        this.hash = calculateHash();
    }

    public String calculateHash(){
        return StringUtility.applySHA256(
                getPreviousHash() +
                        Long.toString(getTimeStamp()) +
                        Integer.toString(getNonce()) +
                        getMerkleRoot()
        );
    }

    public String getHash() {
        return hash;
    }

    @Contract(pure = true)
    private String getMerkleRoot() {
        return merkleRoot;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    @Contract(pure = true)
    private long getTimeStamp() {
        return timeStamp;
    }

    @Contract(pure = true)
    private int getNonce() {
        return nonce;
    }

    private void setHash(String hash) {
        this.hash = hash;
    }

    private void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtility.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        // String target = StringUtility.getDifficultyString(difficulty);
        while(!getHash().substring(0, difficulty).equals(target)){
            setNonce(getNonce()+1);
            setHash(calculateHash());
            // System.out.println(getHash());
        }
        System.out.println("Block Mined: " + getHash());
    }

    public boolean addTransaction(Transaction transaction){
        //process transaction and check if valid, unless block is genesis block then ignore
        if(transaction == null) {
            return false;
        }
        if(getPreviousHash() != "0"){
            if(transaction.processTransaction() != true){
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}