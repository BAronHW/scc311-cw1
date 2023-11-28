import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.NoSuchPaddingException;

public class Server implements Auction {
    private static int userID;
    private AuctionData auctionData;
    

    public Server() throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
        this.auctionData = new AuctionData();
        Server.userID = 0;

        
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

    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException{
        ChallengeInfo cInfo = auctionData.challenge(userID, clientChallenge);
        System.out.println(cInfo);
        return cInfo;
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        TokenInfo tinfo = auctionData.authenticate(userID, signature);
        System.out.println(tinfo);
        return tinfo;
    }
    
    @Override
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        userID++;
        System.out.println(pubKey);
        return auctionData.registerUser(userID, email, pubKey);
    }

    @Override
    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException {
        return auctionData.getSpec(itemID,userID,token);
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        int id = auctionData.createNewAuction(userID, item, token);
        return id;
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        return auctionData.listItems(userID, token);
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        return auctionData.closeAuction(userID, itemID,token);
    }

    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        return auctionData.placeBid(userID, itemID, price,token);
    }

}

/*
 * TODO: 
 * 1. fix getspec
 */