package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {
    public static String generateSHA256(String plainText){
        StringBuilder output = new StringBuilder(64);
        try{
            MessageDigest digester = MessageDigest.getInstance("SHA-256");
            byte[] digest = digester.digest(plainText.getBytes(StandardCharsets.UTF_8));

            //turn into a 64 char hex string
            for(byte b : digest){
                String hexString = Integer.toHexString(0xff & b);
                if(hexString.length() == 1){//each byte should be represented by 2 char;
                    output.append("0");
                }
                output.append(hexString);
            }
        }catch(NoSuchAlgorithmException ex){
            System.out.println("couldn't find algorithm for hashing" + ex.getMessage());
        }
        return output.toString();
    }
}
