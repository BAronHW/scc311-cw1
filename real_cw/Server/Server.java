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
    private AuctionData auctionData;

    public Server() throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
        auctionData = new AuctionData();
    }
    
    public static void main(String[] args) {
        try {
            Server server = new Server();
            Auction stub = (Auction) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("myserver", stub);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Integer register(String email) throws RemoteException {
        userID++;
        return auctionData.registerUser(userID, email);
    }

    @Override
    public synchronized Integer newAuction(int userID, AuctionSaleItem item) throws RemoteException {
        return auctionData.createNewAuction(userID, item);
    }

    @Override
    public synchronized AuctionItem[] listItems() throws RemoteException {
        return auctionData.listItems();
    }

    @Override
    public synchronized AuctionResult closeAuction(int userID, int itemID) throws RemoteException {
        return auctionData.closeAuction(userID, itemID);
    }

    @Override
    public synchronized boolean bid(int userID, int itemID, int price) throws RemoteException {
        return auctionData.placeBid(userID, itemID, price);
    }

    @Override
    public synchronized AuctionItem getSpec(int itemID) throws RemoteException {
        return auctionData.getSpec(itemID);
    }

}