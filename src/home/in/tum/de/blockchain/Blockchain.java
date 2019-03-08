package home.in.tum.de.blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import com.google.gson.GsonBuilder;
import home.in.tum.de.block.Block;
import home.in.tum.de.cryptography.StringUtility;
import home.in.tum.de.transactions.Transaction;
import home.in.tum.de.transactions.TransactionInput;
import home.in.tum.de.transactions.TransactionOutput;
import home.in.tum.de.wallet.Wallet;
import org.jetbrains.annotations.NotNull;

public class Blockchain {

    private ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>(); // List of unspend transactions

    private static int difficulty = 4;

    public static float minimumTransaction = 0.01f;

    private static Wallet walletA;
    private static Wallet walletB;

    public static Transaction genesisTransaction;

    public static void main(String[] args){

        Blockchain bitcoin = new Blockchain();

        // add our blocks to the blockchain ArrayList
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // create wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // create genesis transaction, which sends 100 Coins to walletA
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionID = "0";
        genesisTransaction.outputs.add(
                new TransactionOutput(
                        genesisTransaction.reciepient,
                        genesisTransaction.value,
                        genesisTransaction.transactionID
                )
        );
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // creating and mining genesis block
        System.out.println("Creating and Mining Genesis block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        bitcoin.addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        bitcoin.addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        bitcoin.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        bitcoin.isChainValid();

        /*
        // test public and private keys
        System.out.println("Private and public keys: ");
        System.out.println(StringUtility.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtility.getStringFromKey(walletA.publicKey));

        // create a test transaction from WalletA to WalletB
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        // verify the signature works and verify it from the public key
        System.out.print("Is signature verified: " + transaction.verifySignature());

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
        */
    }

    private void addBlock (Block b) {
        b.mineBlock(difficulty);
        blockchain.add(b);
    }

    @NotNull
    private Block createBlock(String previousBlockHash){
        return new Block(previousBlockHash);
    }

    private String getPreviousBlockHash () {
        return this.blockchain.get(blockchain.size()-1).getHash();
    }

    @NotNull
    private Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

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
            // loop through blockchain transactions
            TransactionOutput tempOutput;
            for(int t = 0; t < currentBlock.transactions.size(); t++){
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()){
                    System.out.println("#Signature on Transaction (" + t + ") is invalid.");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()){
                    System.out.println("#Inputs are not equal to outputs on Transaction (" + t + ")");
                    return false;
                }
                for(TransactionInput input : currentTransaction.inputs){
                    tempOutput = tempUTXOs.get(input.transactionOutputID);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputID);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }
        }

        System.out.println("Blockchain is valid");
        return true;
    }
}
