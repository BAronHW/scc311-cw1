import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.NoSuchPaddingException;

public class Replica implements Auction, Replication {
    private static int userID;
    private static int replicaID;
    private AuctionData auctionData;
    private boolean isPrimary;

    public Replica(int replicaID, boolean isPrimary) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
        this.auctionData = new AuctionData(replicaID);
        Replica.userID = 0;
        Replica.replicaID = replicaID;
        this.isPrimary = isPrimary;

        if(isPrimary){
            //replicate to other replicas.
        }
    }

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Java Replica Requires Replica ID");
            System.exit(1);
        }
        replicaID = Integer.parseInt(args[0]);

        try {
            Replica server = new Replica(replicaID);
            Auction stub = (Auction) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            String replicaname = Integer.toString(replicaID);
            registry.rebind(replicaname, stub);
            System.out.println("Server ready");
            System.out.println("This Replica's ID is " + replicaID);
            System.out.println(server.getPrimaryReplicaID());
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

    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        return replicaID;
    }

    @Override
    public void replicateData(AuctionData data) throws RemoteException {
        this.auctionData = data;
    }

}

/*
 * TODO: 
 * 1. fix getspec
 */