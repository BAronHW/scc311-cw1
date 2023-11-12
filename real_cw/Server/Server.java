import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.NoSuchPaddingException;

public class Server implements Auction {
    private static int userID = 0;
    private ArrayList<Integer> itemidlist = new ArrayList<Integer>();
    private ConcurrentHashMap<Integer, AuctionItem> itemMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> useridanditem = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> userHashMap = new ConcurrentHashMap<>();

    public Server() throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
    }

    public static void main(String[] args) throws Exception {
        try {
            Server server = new Server();
            Auction stub = (Auction) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("myserver", stub);
            System.out.println("Server ready");
        } catch (RemoteException e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized AuctionItem getSpec(int itemID) throws RemoteException {
        AuctionItem item = itemMap.get(itemID);
        if (item != null) {
            return item;
        } else {
            throw new RemoteException("Item with ID " + itemID + " not found.");
        }
    }

    @Override
    public synchronized Integer register(String email) throws RemoteException {
        userID++;
        userHashMap.put(userID, email);
        return userID;
    }

@Override
public synchronized Integer newAuction(int userID, AuctionSaleItem item) throws RemoteException {
    if (userHashMap.containsKey(userID)) {
        Random rand = new Random();
        int id;

        // Generate a unique item ID
        do {
            id = rand.nextInt(100);
        } while (itemidlist.contains(id));

        itemidlist.add(id);  // Add the new ID to the list

        AuctionItem auctionItem = new AuctionItem();
        auctionItem.itemID = id;
        auctionItem.name = item.name;
        auctionItem.description = item.description;
        auctionItem.highestBid = item.reservePrice;

        itemMap.put(id, auctionItem);
        useridanditem.put(userID, auctionItem.itemID);

        return id;
    } else {
        System.out.println("You are not registered!");
        return null;
    }
}


    @Override
    public synchronized AuctionItem[] listItems() throws RemoteException {
        AuctionItem[] itemArray = itemMap.values().toArray(new AuctionItem[0]);
        return itemArray;
    }

    @Override
    public synchronized AuctionResult closeAuction(int userID, int itemID) throws RemoteException {
        try {
            AuctionItem closeItem = itemMap.get(itemID);
            if (closeItem != null && useridanditem.containsKey(userID) && useridanditem.get(userID) == itemID) {
                String winemail = userHashMap.get(userID);
                AuctionResult result = new AuctionResult();
                result.winningEmail = winemail;
                result.winningPrice = closeItem.highestBid;
                itemMap.remove(itemID);
                return result;
            } else {
                throw new RemoteException("You don't have permission to close this auction.");
            }
        } catch (NullPointerException e) {
            throw new RemoteException("An error occurred while closing the auction.", e);
        }
    }

    @Override
    public synchronized boolean bid(int userID, int itemID, int price) throws RemoteException {
        if (!userHashMap.containsKey(userID)) {
            System.out.println("User not registered.");
            return false;
        }

        AuctionItem item = itemMap.get(itemID);
        if (item == null) {
            System.out.println("Item not found.");
            return false;
        }

        if (price > item.highestBid) {
            item.highestBid = price;
            System.out.println("Bid successful. New highest bid: " + price);
            return true;
        } else {
            System.out.println("Bid not successful. Bid price must be greater than the current highest bid.");
            return false;
        }
    }
}
