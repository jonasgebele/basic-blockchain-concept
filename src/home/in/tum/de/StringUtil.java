package home.in.tum.de;

import java.security.MessageDigest;

public class StringUtil {

    public static String appySHA256(String input){
        try{
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            byte [] hash = msgDigest.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();

            for (int hashValue : hash){
                String hex = Integer.toHexString(0xff & hashValue);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}