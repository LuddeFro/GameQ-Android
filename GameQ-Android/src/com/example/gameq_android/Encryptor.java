package com.example.gameq_android;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


/**
 * Used to Hash not encrypt passwords
 * this is to be done before storing them
 * and before sending them over the network.
 * 
 */
public class Encryptor{
	/**
	 * HashSHA256
	 * @param password the password that should be hashed.
	 * @return a hashed hex encoded version of the string, returns null in case of error.
	 * returned hex hash has lowercase letters
	 */
	public static String hashSHA256(String password) {
        try {
        	MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
 
			byte byteData[] = md.digest();
			return bytesToHex(byteData);
        } catch (NoSuchAlgorithmException e) {
        	System.out.println("Error: " + e);
        	return null;
        }
	}
	
	//TODO Make sense of this function
   private static String bytesToHex(byte[] b) {
      char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                         '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      StringBuffer buf = new StringBuffer();
      for (int j=0; j<b.length; j++) {
         buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
         buf.append(hexDigit[b[j] & 0x0f]);
      }
      return buf.toString().toLowerCase(Locale.US);
   }
}
