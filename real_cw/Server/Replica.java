import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.NoSuchPaddingException;

public class Replica implements Replication{
    private static int userID;
    public int replicaID;
    private AuctionData auctionData;
    private boolean isPrimary;
    ReplicaState currentstate;
    private Map<Integer, Replication> replicationMap; //store the existence of other replicas
    
    public Replica(int replicaID) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
        this.auctionData = new AuctionData(replicaID);
        Replica.userID = 0;
        this.replicaID = replicaID;
        this.isPrimary = isPrimary;
        this.replicationMap = new HashMap<Integer, Replication>();
        // Replication replicastub = (Replication) UnicastRemoteObject.exportObject(this, 0);
        // Registry registry = LocateRegistry.getRegistry();
        // registry.rebind(Integer.toString(replicaID), replicastub)
    }

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Java Replica Requires Replica ID");
            System.exit(1);
        }
        int replicaID = Integer.parseInt(args[0]);

        try {
            Replica server = new Replica(replicaID);

            Replication stub = (Replication) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            String replicaname = "Replica "+Integer.toString(replicaID);
            registry.rebind(replicaname, stub);
            server.broadcastReplica();
            System.out.println("Server ready");
            System.out.println("This Replica's ID is " + replicaID);
            
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }

    
    public int getReplicaID() throws RemoteException {
        return replicaID;
    }

    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException{
        ChallengeInfo cInfo = auctionData.challenge(userID, clientChallenge);
        System.out.println(cInfo);
        return cInfo;
    }

    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        TokenInfo tinfo = auctionData.authenticate(userID, signature);
        System.out.println(tinfo);
        return tinfo;
    }
    
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        userID++;
        System.out.println(pubKey);
        return auctionData.registerUser(userID, email, pubKey);
    }

    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException {
        return auctionData.getSpec(itemID,userID,token);
    }

    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        int id = auctionData.createNewAuction(userID, item, token);
        return id;
    }

    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        return auctionData.listItems(userID, token);
    }
    
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        return auctionData.closeAuction(userID, itemID,token);
    }

    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        return auctionData.placeBid(userID, itemID, price,token);
    }

    public int getPrimaryReplicaID() throws RemoteException {
        return replicaID;
    }

    public void replicateData(AuctionData data) throws RemoteException {
        this.auctionData = data;
    }

    public boolean getisPrimary(){
        return this.isPrimary;
    }

    public Map<Integer, Replication> getReplicationMap() {
        return replicationMap;
    }

    public ReplicaState returncurrState(){
        auctionData.getHighestBidders();
        ReplicaState currentstate = new ReplicaState(null, null, null, null, null, null, null, null, null, userID);
        return currentstate;
    }

    // public ConcurrentHashMap<Integer, Integer> highestBid(){
    //     System.out.println(auctionData.getHighestBidders());
    //     return auctionData.getHighestBidders();
    // }

    public void getState(){
        // for the secondary replicas so that they can set take apart the currentstate variable and put it into their own states
        returncurrState();
    }

    public static String[] listAllReplicas() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        String[] list = registry.list();
        ArrayList<String> replicalist = new ArrayList<>();
        for (String string : list) {
            if (string.startsWith("Replica ")) {
                replicalist.add(string);
            }
        }
        return replicalist.toArray(new String[0]);
    }

    private void broadcastReplica(){
        try {
            Registry registry = LocateRegistry.getRegistry();
            for(int i = 1; i<=listAllReplicas().length; i++){
                if (i != replicaID) {
                    String replicaname = "Replica " + Integer.toString(i);
                    Replication replica = (Replication) registry.lookup(replicaname);
                    replicationMap.put(i, replica);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/*
 * TODO: 
 * 1. fix getspec
 */