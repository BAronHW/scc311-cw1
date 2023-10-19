import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

public class Server extends UnicastRemoteObject  implements Auction {
    private HashMap<Integer, SealedObject> itemMap = new HashMap<>();
    private SecretKey secretKey;

    public Server() throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
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
        
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, secretKey);
            SealedObject sealedexitem = new SealedObject(expensiveitem, c);
            SealedObject sealedcheapitem = new SealedObject(cheapItem, c);
            SealedObject sealedmiditem = new SealedObject(middleitem,c);
            itemMap.put(expensiveitem.itemID, sealedexitem);
            itemMap.put(middleitem.itemID, sealedmiditem);
            itemMap.put(cheapItem.itemID, sealedcheapitem);
        } catch (IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        }
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
            String name = "myserver";
            Auction stub = (Auction) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Server ready");
        } catch (RemoteException e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
}
