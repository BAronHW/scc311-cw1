import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class AuctionData {
    private ArrayList<Integer> itemidlist = new ArrayList<>();
    private ConcurrentHashMap<Integer, AuctionItem> itemMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> useridanditem = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> userHashMap = new ConcurrentHashMap<>();
    
    public int registerUser(int userID, String email) {
        userHashMap.put(userID, email);
        return userID;
    }

    public int createNewAuction(int userID, AuctionSaleItem item) {
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
    }

    public AuctionItem[] listItems() {
        return itemMap.values().toArray(new AuctionItem[0]);
    }

    public AuctionResult closeAuction(int userID, int itemID) {
        AuctionItem closeItem = itemMap.get(itemID);
        if (closeItem != null && useridanditem.containsKey(userID) && useridanditem.get(userID) == itemID) {
            String winemail = userHashMap.get(userID);
            AuctionResult result = new AuctionResult();
            result.winningEmail = winemail;
            result.winningPrice = closeItem.highestBid;
            itemMap.remove(itemID);
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
}
