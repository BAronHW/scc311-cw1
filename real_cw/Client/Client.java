import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
public class Client {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Client n");
            return;
        }
        int id = Integer.parseInt(args[0]);

        
        
        try{
            String name = "Auction";
            Registry registry = LocateRegistry.getRegistry("localhost");
            Auction server = (Auction) registry.lookup(name);

            AuctionItem[] array = server.listItems();
            array.toString();
            System.out.println(array);
            
            try {
                String configfilepath = new File(System.getProperty("user.dir")).getParent()+"/keys/testKey.aes";
                BufferedReader br = new BufferedReader(new FileReader(configfilepath));
                String encodedkey = br.readLine();
                byte[] decodedkey = Base64.getDecoder().decode(encodedkey);
                SecretKey secretKey = new SecretKeySpec(decodedkey, 0, decodedkey.length, "AES");
                SealedObject sealedObject = server.getSpec(id);
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                AuctionItem auitem = (AuctionItem) sealedObject.getObject(secretKey);
                System.out.println("ItemID:"+auitem.itemID);
                System.out.println("Name:"+auitem.name);
                System.out.println("description:"+auitem.description);
                System.out.println("highestBid:"+auitem.highestBid);
                
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            
            

        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
    
}
