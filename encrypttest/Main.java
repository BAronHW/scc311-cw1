import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class Main{
    public static void main(String[] args) {
        // KeyGenerator generator = KeyGenerator.getInstance("AES");
        //     SecureRandom secureRandom = new SecureRandom();
        //     generator.init(128,secureRandom);
        //     Key keytowrap = generator.generateKey();
        //     System.out.println("input:"+new String(keytowrap.getEncoded()));
        //     Cipher cipher = Cipher.getInstance("AES");
        //     generator.init(256);
        //     Key key = generator.generateKey();
        //     cipher.init(Cipher.WRAP_MODE, key);
        //     byte[] wrappedkey = cipher.wrap(keytowrap);
        //     System.out.println("wrapped: "+new String(wrappedkey));
        //     cipher.init(Cipher.WRAP_MODE, key);
        //     Key newKey = cipher.unwrap(wrappedkey, "AES", Cipher.SECRET_KEY);
        //     System.out.println("unwrapped:" + new String(newKey.getEncoded()));
    }
}