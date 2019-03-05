package home.in.tum.de.cryptography;

import org.jetbrains.annotations.NotNull;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Base64;

public class StringUtility {

    @NotNull
    public static String applySHA256(String input) {
        try{
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            // MessageDigst provides secure one-way hash functions
            byte [] data = input.getBytes("UTF-8");
            // converts a string into an array of bytes

            byte [] encodedhash = msgDigest.digest(data);
            // generates from the data the message digest
            StringBuilder hexString = new StringBuilder();

            for (int hashValue : encodedhash){
                String hex = Integer.toHexString(0xff & hashValue);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            // builds a hexadezimal string of the encoded hash-value
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e){
            System.out.println("Message digest instantiating did not work.");
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e){
            System.out.println("Generating of hash-value did not work.");
            throw new RuntimeException(e);
        }
    }

    public static byte [] applyECDSASig(PrivateKey privateKey, String input){
        Signature dsa;
        byte [] output = new byte [0];
        try{
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte [] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte [] signature){
        try{
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}