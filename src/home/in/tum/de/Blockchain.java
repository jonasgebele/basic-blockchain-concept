package home.in.tum.de;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.google.gson.GsonBuilder;

public class Blockchain {

    private ArrayList<Block> blockchain;

    public static int difficulty = 4;

    public Blockchain(){
        this.blockchain = new ArrayList<>();
    }

    public static void main(String[] args){

        Blockchain bitcoin = new Blockchain();

        {
            bitcoin.addBlock(bitcoin.createBlock("fill in data here", "0"));
            System.out.println("Trying to Mine block 1...");
            bitcoin.blockchain.get(0).mineBlock(difficulty);

            System.out.println("\nThe block chain: ");

            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (Exception e){
                System.exit(0);
            }

            String bitcoinJson = new GsonBuilder().setPrettyPrinting().create().toJson(bitcoin);
            System.out.println(bitcoinJson);

            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (Exception e){
                System.exit(0);
            }
        }

        int i = 1;
        while(true){
            bitcoin.addBlock(bitcoin.createBlock("fill in data here", bitcoin.getPreviousBlockHash()));
            System.out.println("Trying to Mine block " + Integer.toString(i+1) + "...");
            bitcoin.blockchain.get(i).mineBlock(difficulty);
            i++;

            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (Exception e){
                System.exit(0);
            }

            System.out.println("\nBlockchain is Valid: " + bitcoin.isChainValid());

            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (Exception e){
                System.exit(0);
            }

            System.out.println("\nThe block chain: ");

            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (Exception e){
                System.exit(0);
            }

            String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(bitcoin);
            System.out.println(blockchainJson);
        }

    }

    private Block createBlock(String data, String previousBlockHash){
        return new Block(data, previousBlockHash);
    }

    private String getPreviousBlockHash () {
        return this.blockchain.get(blockchain.size()-1).hash;
    }

    private void addBlock (Block b) {
        blockchain.add(b);
    }

    public Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
