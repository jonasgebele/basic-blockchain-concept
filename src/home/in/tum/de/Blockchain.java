package home.in.tum.de;

import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class Blockchain {

    private ArrayList<Block> BlockListe;

    public Blockchain(){
        this.BlockListe = new ArrayList<>();
    }

    public static void main(String[] args){

        Blockchain bitcoin = new Blockchain();

        bitcoin.addBlock(bitcoin.createBlock("das ist der erste Block", "0"));
        bitcoin.addBlock(bitcoin.createBlock("das ist der zweite Block", bitcoin.getPreviousBlockHash()));
        bitcoin.addBlock(bitcoin.createBlock("das ist der dritte Block", bitcoin.getPreviousBlockHash()));

        String bitcoinJson = new GsonBuilder().setPrettyPrinting().create().toJson(bitcoin);
        System.out.println(bitcoinJson);
    }

    private Block createBlock(String data, String previousBlockHash){
        return new Block(data, previousBlockHash);
    }

    private String getPreviousBlockHash () {
        return this.BlockListe.get(BlockListe.size()-1).getHash();
    }

    private void addBlock (Block b) {
        getBlockListe().add(b);
    }

    public ArrayList<Block> getBlockListe() {
        return BlockListe;
    }

    public void setBlockListe(ArrayList<Block> blockListe) {
        BlockListe = blockListe;
    }

}
