package home.in.tum.de;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;
import home.in.tum.de.block.Block;

public class Blockchain {

    private ArrayList<Block> blockchain = new ArrayList<>();

    private static int difficulty = 4;

    public static void main(String[] args){
        Blockchain bitcoin = new Blockchain();

        bitcoin.addBlock(bitcoin.createBlock("Genesis Block", "0"));
        System.out.println("Trying to Mine block 1...");
        bitcoin.blockchain.get(0).mineBlock(difficulty);

        bitcoin.addBlock(bitcoin.createBlock("fill in data here", bitcoin.getPreviousBlockHash()));
        System.out.println("Trying to Mine block " + Integer.toString(2) + "...");
        bitcoin.blockchain.get(1).mineBlock(difficulty);

        bitcoin.addBlock(bitcoin.createBlock("fill in data here", bitcoin.getPreviousBlockHash()));
        System.out.println("Trying to Mine block " + Integer.toString(3) + "...");
        bitcoin.blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + bitcoin.isChainValid());

        System.out.println("\nThe block chain: ");
        String BitcoinJsonFormat = new GsonBuilder().setPrettyPrinting().create().toJson(bitcoin);
        System.out.println(BitcoinJsonFormat);
    }

    private void addBlock (Block b) {
        blockchain.add(b);
    }

    private Block createBlock(String data, String previousBlockHash){
        return new Block(data, previousBlockHash);
    }

    private String getPreviousBlockHash () {
        return this.blockchain.get(blockchain.size()-1).getHash();
    }

    private Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //go through the whole blockchain
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            //compare registered hash and calculated hash
            if(!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
