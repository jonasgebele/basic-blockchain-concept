package home.in.tum.de;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class StringUtility {

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
}