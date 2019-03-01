package home.in.tum.de;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public String data;
    public long timeStamp;
    private int nonce;

    Block (String data, String previousHash){
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash(){
        return StringUtil.appySHA256("" + previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)){
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined: " + hash);
    }
}