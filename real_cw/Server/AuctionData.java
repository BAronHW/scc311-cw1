import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class AuctionData {
    private static int id = 0;
    private ConcurrentHashMap<Integer, AuctionItem> itemMap;
    private ConcurrentHashMap<Integer, Integer> useridanditem;
    private ConcurrentHashMap<Integer, String> userHashMap;
    private ConcurrentHashMap<Integer, Integer> highestBidders; //ItemID -> HighestBidderID
    private HashMap<Integer,PublicKey> everyuserpubkey = new HashMap<Integer, PublicKey>();

    public AuctionData() {
        super();
        this.itemMap = new ConcurrentHashMap<>();
        this.useridanditem = new ConcurrentHashMap<>();
        this.userHashMap = new ConcurrentHashMap<>();
        this.highestBidders = new ConcurrentHashMap<>();
    }

    public int registerUser(int userID, String email, PublicKey publicKey) {
        userHashMap.put(userID, email);
        everyuserpubkey.put(userID, publicKey);
        System.out.println(everyuserpubkey.get(userID));
        return userID;
    }

    public int createNewAuction(int userID, AuctionSaleItem item) {
        id++;
        AuctionItem auctionItem = new AuctionItem();
        auctionItem.itemID = id;
        auctionItem.name = item.name;
        auctionItem.description = item.description;
        auctionItem.highestBid = item.reservePrice;

        itemMap.put(id, auctionItem);
        useridanditem.put(userID, auctionItem.itemID);

        return id;
    }

    public AuctionItem[] listItems() {
        return itemMap.values().toArray(new AuctionItem[0]);
    }

    public AuctionResult closeAuction(int userID, int itemID) {
        AuctionItem closeItem = itemMap.get(itemID);
        if (closeItem != null && useridanditem.containsKey(userID) && useridanditem.get(userID) == itemID) {
            int highestBidderID = highestBidders.get(itemID);
            String winemail = userHashMap.get(highestBidderID);
            AuctionResult result = new AuctionResult();
            result.winningEmail = winemail;
            result.winningPrice = closeItem.highestBid;
            itemMap.remove(itemID);
            highestBidders.remove(itemID);
            return result;
        } else {
            return null; // or throw an exception indicating permission issue
        }
    }

    public boolean placeBid(int userID, int itemID, int price) {
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
            highestBidders.put(itemID, userID);
            System.out.println("Bid successful. New highest bid: " + price);
            return true;
        } else {
            System.out.println("Bid not successful. Bid price must be greater than the current highest bid.");
            return false;
        }
    }

    public AuctionItem getSpec(int itemID) {
        AuctionItem item = itemMap.get(itemID);
        if (item != null) {
            return item;
        } else {
            return null; // or throw an exception indicating item not found
        }
    }

    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException {
        throw new UnsupportedOperationException("STFU");

    }

    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        throw new UnsupportedOperationException("Unimplemented method 'authenticate'");
    }
}
