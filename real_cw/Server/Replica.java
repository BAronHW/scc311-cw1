import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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
    ReplicaState currentstate;
    private Map<Integer, Replication> replicationMap; //store the existence of other replicas
    private boolean isprimary = false;

    
    public Replica(int replicaID) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
        this.auctionData = new AuctionData(replicaID);
        Replica.userID = 0;
        this.replicaID = replicaID;
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
            server.notifyReplicas();
            //get every replica in the hashmap except for the primary replica
            //for each of the replicas that is not primary call server.getstate()
            // server.getState(replicationMap.get(1));
            for (Replication replica : server.getReplicationMap().values()) {
                    replica.getState(replicaID);
                }
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
        System.out.println("userID:"+userID+" clientChallenge:"+clientChallenge	);
        ChallengeInfo cInfo = auctionData.challenge(userID, clientChallenge);
        // for (Replication replica : getReplicationMap().values()) {
        //     try {
        //         replica.getState(replica);
        //     } catch (NotBoundException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }
        System.out.println(cInfo);
        return cInfo;
    }

    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        System.out.println("userID: " + userID+"suthenticate");
        TokenInfo tinfo = auctionData.authenticate(userID, signature);
        // for (Replication replica : getReplicationMap().values()) {
        //     try {
        //         replica.getState(replica);
        //     } catch (NotBoundException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }
        System.out.println(tinfo);
        return tinfo;
    }
    
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        userID++;
        System.out.println(pubKey);
        for (Replication replica : getReplicationMap().values()) {
            try {
                replica.getState(this.replicaID);
            } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        int regid = auctionData.registerUser(userID, email, pubKey);
        
        return regid;
    }

    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException {
        return auctionData.getSpec(itemID,userID,token);
    }

    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        int id = auctionData.createNewAuction(userID, item, token);
        ConcurrentHashMap<Integer, Integer> map = currentstate.getHighestBidders();
        map.forEach((key, value) -> {
            if (key != null && value != null) {
                System.out.println("ItemID: " + key + ", HighestBidderID: " + value);
            } else {
                System.out.println("Null entry detected.");
            }
        });
        for (Replication otherreplica : getReplicationMap().values()) {
            try {

                otherreplica.getState(this.replicaID);
            } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(map.size());

        
        return id;
    }

    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        return auctionData.listItems(userID, token);
    }
    
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        for (Replication replica : getReplicationMap().values()) {
            try {
                replica.getState(this.replicaID);
            } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        AuctionResult ac = auctionData.closeAuction(userID, itemID,token);
        return ac;
    }

    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        for (Replication replica : getReplicationMap().values()) {
            try {
                replica.getState(this.replicaID);
            } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        boolean bool = auctionData.placeBid(userID, itemID, price,token);
        return bool;
    }

    public int getPrimaryReplicaID() throws RemoteException {
        return replicaID;
    }

    public void replicateData(AuctionData data) throws RemoteException {
        this.auctionData = data;
    }


    public Map<Integer, Replication> getReplicationMap() {
        return replicationMap;
    }

    //everytime the primary state changes get the replicas from the primary replica hashmap and update their state
    //to return the primary replicas current state to the secondary replicas
    public ReplicaState returncurrState(){
        ConcurrentHashMap<Integer, AuctionItem> itemMap = auctionData.getItemMap();
        ConcurrentHashMap<Integer, Integer> useridanditem = auctionData.getUseridanditem();
        ConcurrentHashMap<Integer, String> userHashMap = auctionData.getUserHashMap();
        ConcurrentHashMap<Integer, Integer> highestBidders = auctionData.getHighestBidders();
        HashMap<Integer, PublicKey> everyuserpubkey = auctionData.getEveryuserpubkey();
        HashMap<Integer, String> randomstringhashmap = auctionData.getRandomstringhashmap();
        ConcurrentHashMap<Integer, TokenInfo> usertokenmap = auctionData.getUsertokenmap();
        KeyPair pair = auctionData.getPair();
        int userID = getUserID();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        System.out.println("returncurrstate's"+itemMap);

        ReplicaState currentstate = new ReplicaState(
            itemMap, useridanditem, userHashMap, highestBidders,
            everyuserpubkey, randomstringhashmap, usertokenmap
            , pair, userID,publicKey,privateKey
        );
        return currentstate;
    }
    
    
    public void getState(int myreplicaid) throws RemoteException, NotBoundException {
        // for the secondary replicas so that they can set take apart the currentstate variable and put it into their own states
        Registry registry = LocateRegistry.getRegistry();
    
        // Get the current state from the primary replica
        Replication replica = (Replication) registry.lookup("Replica "+myreplicaid);
        ReplicaState state = replica.returncurrState();
        System.out.println("getstate"+state.getItemMap());
        setCurrentstate(state);
        auctionData.setItemMap(state.getItemMap());
        auctionData.setHighestBidders(state.getHighestBidders());
        auctionData.setUseridanditem(state.getUseridanditem());
        auctionData.setUserHashMap(state.getUserHashMap());
        auctionData.setId(state.getId());
        System.out.println(auctionData.getItemMap().size());
        System.out.println(auctionData.getHighestBidders().size());
        System.out.println(auctionData.getUseridanditem().size());
        System.out.println(auctionData.getUserHashMap().size());
        System.out.println(auctionData.getId());
        }
    
    


    private String[] listAllReplicas(Registry registry) throws RemoteException {
        String[] list = registry.list();
        ArrayList<String> replicalist = new ArrayList<>();
        for (String string : list) {
            if (string.startsWith("Replica ")) {
                replicalist.add(string);
            }
        }
        return replicalist.toArray(new String[0]);
    }

    private void notifyReplicas() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            String[] replicas = listAllReplicas(registry);
            
            for (String replicaName : replicas) {
                if (!replicaName.equals("Replica " + replicaID)) {
                    Replication replica = (Replication) registry.lookup(replicaName);
                    replicationMap.put(replica.getReplicaID(), replica);
                    replica.addToReplicationMap(replicaID, this);
                    System.out.println(" Added replica " + replica.getReplicaID());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToReplicationMap(int replicaID, Replication replica) throws RemoteException {
        replicationMap.put(replicaID, replica);
        System.out.println("new replica in the room Added replica " + replica.getReplicaID());
    }

    public AuctionData getAuctionData() {
        return auctionData;
    }

    public static int getUserID() {
        return userID;
    }
    public void setCurrentstate(ReplicaState currentstate) {
        this.currentstate = currentstate;
    }
    public ReplicaState getCurrentstate() {
        return currentstate;
    }
    public void setIsprimary(boolean isprimary) {
        this.isprimary = isprimary;
    }
    
    public boolean getisprimary(){
        return this.isprimary;
    }
    public Map<Integer, Replication> getreplicationmap(){
        return this.replicationMap;
    }
    
    
    
}

/*
 * TODO: 
 * 1. fix getspec
 */