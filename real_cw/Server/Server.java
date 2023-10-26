import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Server extends UnicastRemoteObject implements Auction {
    private HashMap<Integer, SealedObject> itemMap = new HashMap<>();
    private SecretKey secretKey;

    public Server() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, IOException {
        super();
        

            String dirpath = new File(System.getProperty("user.dir")).getParent()+"/keys";
            String filename = "testKey.aes";
            String filepath = Paths.get(dirpath,filename).toString();
            File keyfile = new File(filepath);

            if(!keyfile.exists()||keyfile.length()==0){
                KeyGenerator keygen = KeyGenerator.getInstance("AES");
                keygen.init(128);
                secretKey = keygen.generateKey();
                String encodedkey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE,secretKey);
                try(PrintWriter out = new PrintWriter((filepath))){
                    out.println(encodedkey);

                }catch(IOException e){
                    e.printStackTrace();
                }
            }else{
                try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
                String encodedKey = br.readLine();
                byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
                secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
            AuctionItem expensiveitem = new AuctionItem();
            expensiveitem.itemID = 1;
            expensiveitem.name = "expensive";
            expensiveitem.description = "its over 9000";
            expensiveitem.highestBid = 700;

            AuctionItem cheapItem = new AuctionItem();
            cheapItem.itemID = 30;
            cheapItem.name = "cheap";
            cheapItem.description = "its less than 9000";
            cheapItem.highestBid = 70;
            

            AuctionItem middleitem = new AuctionItem();
            middleitem.itemID = 40;
            middleitem.name = "ok";
            middleitem.description = "meh";
            middleitem.highestBid = 100;
            
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE,secretKey);
            SealedObject sealedexitem = new SealedObject(expensiveitem, c);
            SealedObject sealedcheapitem = new SealedObject(cheapItem, c);
            SealedObject sealedmiditem = new SealedObject(middleitem,c);
            itemMap.put(expensiveitem.itemID, sealedexitem);
            itemMap.put(middleitem.itemID, sealedmiditem);
            itemMap.put(cheapItem.itemID, sealedcheapitem);
        }

    public SealedObject getSpec(int itemID) throws RemoteException {
        SealedObject sealeditem = itemMap.get(itemID);
        if (sealeditem != null) {
            return sealeditem;
        } else {
            throw new RemoteException("Item with ID " + itemID + " not found.");
        }
    }

    public static void main(String[] args) throws Exception {
    try {
        Server server = new Server();
        String name = "Auction";
        Auction stub = (Auction) server;
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(name, stub);
        System.out.println("Server ready");
    } catch (RemoteException e) {
        System.err.println("Exception:");
        e.printStackTrace();
    }
}
}
