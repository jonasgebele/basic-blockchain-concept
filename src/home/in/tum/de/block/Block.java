package home.in.tum.de.block;

import home.in.tum.de.cryptography.StringUtility;
import org.jetbrains.annotations.Contract;

import java.util.Date;

public class Block {

    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    Block (String data, String previousHash){
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        return StringUtility.applySHA256(
                getPreviousHash() +
                        Long.toString(getTimeStamp()) +
                        Integer.toString(getNonce()) +
                        getData()
        );
    }

    public String getHash() {
        return hash;
    }

    @Contract(pure = true)
    private String getData() {
        return data;
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
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!getHash().substring(0, difficulty).equals(target)){
            setNonce(getNonce()+1);
            setHash(calculateHash());
            System.out.println(getHash());
        }
        System.out.println("Block Mined: " + getHash());
    }
}