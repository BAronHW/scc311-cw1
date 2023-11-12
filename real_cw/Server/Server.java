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

public class Server implements Auction {
    private static int itemID = 0;
    private static int userID = 0;
    private HashMap<Integer, AuctionItem> itemMap = new HashMap<>();
    private HashMap<Integer,String> userHashMap = new HashMap<>();
    private HashMap<Integer,AuctionSaleItem> auctionSaleItemmap = new HashMap<>();
    
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
        
        itemMap.put(expensiveitem.itemID, expensiveitem);
        itemMap.put(middleitem.itemID, middleitem);
        itemMap.put(cheapItem.itemID, cheapItem);
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

    public AuctionItem getSpec(int itemID) throws RemoteException {
        AuctionItem item = itemMap.get(itemID);
        if (item != null) {
            return item;
        } else {
            throw new RemoteException("Item with ID " + itemID + " not found.");
        }
    }

    @Override
    public Integer register(String email) throws RemoteException {
        //get the user to input email address
        // increase userid by 1
        // after taking user email give unique userID and put int hashmap
        userID++;
        userHashMap.put(userID,email);
        return userID;
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item) throws RemoteException {
        // when given an exiting userid and auctionsaleitem
        // if userhashmap contains userid meaning if that the userid is already registered 
        // put the item auctionsaleitem into a hashmap as the value and itemid as the key
        // return itemid 
        if (userHashMap.containsKey(userID)) {
            itemID++;
            auctionSaleItemmap.put(itemID, item);
        }else{
            System.out.println("you are not registered!");
        }
        return itemID;
        
    }

    @Override
    public AuctionItem[] listItems() throws RemoteException {
        AuctionItem[] itemArray = itemMap.values().toArray(new AuctionItem[0]);
        return itemArray;
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeAuction'");
    }

    @Override
    public boolean bid(int userID, int itemID, int price) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bid'");
    }
}

/*
 * TODO:
 * 1. create auction
 * 2. close auction
 * 3. bid 
 */