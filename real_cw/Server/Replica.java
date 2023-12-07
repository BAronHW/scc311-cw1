import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.NoSuchPaddingException;

public class Replica implements Replication{
    private int userID;
    public int replicaID;
    private AuctionData auctionData;
    ReplicaState currentstate;
    private Map<Integer, Replication> replicationMap; //store the existence of other replicas
    private boolean isprimary;
    private Long lastupdate;

    
    public Replica(int replicaID) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        super();
        this.auctionData = new AuctionData(replicaID);
        this.userID = 0;
        this.replicaID = replicaID;
        this.replicationMap = new HashMap<Integer, Replication>();
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
            server.syncWithPrimaryReplica();

            //foreachloop all replicas and run notify replicas on them 
            //get every replica in the hashmap except for the primary replica
            //for each of the replicas that is not primary call server.getstate()
            // server.getState(replicationMap.get(1));
            // for (int i : server.replicationMap.keySet()) {
            //     try {
            //         Replication otherreplica = server.replicationMap.get(i);
            //         System.out.println(otherreplica.getReplicaID());
            //         otherreplica.getState(replicaID);
            //     } catch (Exception e) {
                    
            //     }
                
            // }
            //foreach replica that exist in the rmi room find the one where isprimary is set to true and then get its replicaid and use it to call getstate function
            
        
            System.out.println("Server ready");
            System.out.println("This Replica's ID is " + replicaID);
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }

    //need to make a timestamp everytime a replica shares its state which tells which one has been updated most recently
    //afterthis using syncwithprimaryreplica you list out all the replicas in the map and find the one with the most recently updated replica and take its replica id and use it to run getstate.
    public void syncWithPrimaryReplica() {
        try {
            long maxTimestamp = Long.MIN_VALUE;
            int maxTimestampReplicaID = 1;
            // Iterate through all replicas in the RMI room
            for (Replication otherreplica : getReplicationMap().values()) {
                
                    Long timestamp = otherreplica.getLastupdate();
                    if (timestamp>maxTimestamp) {
                        maxTimestamp = timestamp;
                        maxTimestampReplicaID = otherreplica.getReplicaID();
                    }
                }
            getState(maxTimestampReplicaID);
            System.out.println("Synced with most recent replica : " + maxTimestampReplicaID);
    
            // If no primary replica is found, print a message or handle it as needed
            System.out.println("No primary replica found.");
        } catch (Exception e) {
            // Handle other exceptions if needed
            e.printStackTrace(); // Consider logging the exception
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
        return null;
    }

    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        // System.out.println("userID: " + userID+"suthenticate");
        // TokenInfo tinfo = auctionData.authenticate(userID, signature);
        // // for (Replication replica : getReplicationMap().values()) {
        // //     try {
        // //         replica.getState(replica);
        // //     } catch (NotBoundException e) {
        // //         // TODO Auto-generated catch block
        // //         e.printStackTrace();
        // //     }
        // // }
        // System.out.println(tinfo);
        return null;
    }
    
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        userID++;
        int regid = auctionData.registerUser(userID, email, pubKey);
        System.out.println("regid: " + regid); // Add this line to check the value
            for (int i : getReplicationMap().keySet()) {
                try {
                    Replication otherreplica = replicationMap.get(i);
                    System.out.println(otherreplica.getReplicaID());
                    otherreplica.getState(this.replicaID);
                } catch (Exception e) {
                    // TODO: handle exception
                    // e.printStackTrace();
                }
                
            }
        System.out.println("REGID is returning");
        System.out.println(regid);
        return regid;
    }
    
    

    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException {
        for (int i : getReplicationMap().keySet()) {
                try {
                    Replication otherreplica = replicationMap.get(i);
                System.out.println(otherreplica.getReplicaID());
                otherreplica.getState(this.replicaID);
                } catch (Exception e) {
                    // TODO: handle exception
                    // e.printStackTrace();
                }
                
            }
        return auctionData.getSpec(itemID,userID,token);
    }

    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        setIsprimary(true);
        int id = auctionData.createNewAuction(userID, item, token);
        for (int i : getReplicationMap().keySet()) {
                try {
                    Replication otherreplica = replicationMap.get(i);
                System.out.println(otherreplica.getReplicaID());
                otherreplica.getState(this.replicaID);
                } catch (Exception e) {
                    // TODO: handle exception
                    // e.printStackTrace();
                }
                
            }
        return id;
    }

    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        setIsprimary(true);
        System.out.println("I am primary is "+isprimary);
        return auctionData.listItems(userID, token);
    }
    
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        setIsprimary(true);
        AuctionResult ac = auctionData.closeAuction(userID, itemID,token);
        for (int i : getReplicationMap().keySet()) {
                try {
                    Replication otherreplica = replicationMap.get(i);
                System.out.println(otherreplica.getReplicaID());
                otherreplica.getState(this.replicaID);
                } catch (Exception e) {
                    // TODO: handle exception
                    // e.printStackTrace();
                }
                
            }
        return ac;
    }
    

    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        setIsprimary(true);
        boolean bool = auctionData.placeBid(userID, itemID, price,token);
        for (int i : getReplicationMap().keySet()) {
                try {
                    Replication otherreplica = replicationMap.get(i);
                System.out.println(otherreplica.getReplicaID());
                otherreplica.getState(this.replicaID);
                } catch (Exception e) {
                    // TODO: handle exception
                    // e.printStackTrace();
                }
                
            }
        
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
    public ReplicaState returncurrState() throws RemoteException{
        ConcurrentHashMap<Integer, AuctionItem> itemMap = auctionData.getItemMap();
        ConcurrentHashMap<Integer, Integer> useridanditem = auctionData.getUseridanditem();
        ConcurrentHashMap<Integer, String> userHashMap = auctionData.getUserHashMap();
        ConcurrentHashMap<Integer, Integer> highestBidders = auctionData.getHighestBidders();
        Long currenttime = Instant.now().getEpochSecond();
        // HashMap<Integer, PublicKey> everyuserpubkey = auctionData.getEveryuserpubkey();
        // HashMap<Integer, String> randomstringhashmap = auctionData.getRandomstringhashmap();
        // ConcurrentHashMap<Integer, TokenInfo> usertokenmap = auctionData.getUsertokenmap();
        // KeyPair pair = auctionData.getPair();
        int userID = getUserID();
        int itemID = auctionData.getId();
        // PrivateKey privateKey = pair.getPrivate();
        // PublicKey publicKey = pair.getPublic();
        // System.out.println("returncurrstate's"+itemMap);

        ReplicaState currentstate = new ReplicaState(
            itemMap, useridanditem, userHashMap, highestBidders
            ,userID,itemID,currenttime
        );
        return currentstate;
    }
    
    
    public void getState(int myreplicaid) throws RemoteException, NotBoundException {
        // for the secondary replicas so that they can set take apart the currentstate variable and put it into their own states
        try {
            Registry registry = LocateRegistry.getRegistry();
            // Get the current state from the primary replica
            Replication replica = (Replication) registry.lookup("Replica "+myreplicaid);
            ReplicaState state = (ReplicaState) replica.returncurrState();
            System.out.println("getstate"+state.getItemMap());
            setCurrentstate(state);
            auctionData.setItemMap(state.getItemMap());
            auctionData.setHighestBidders(state.getHighestBidders());
            auctionData.setUseridanditem(state.getUseridanditem());
            auctionData.setUserHashMap(state.getUserHashMap());
            this.setUserID(state.getUserID());
            auctionData.setId(state.getItemid());
            this.setLastupdate(state.getCurrenttime());
            System.out.println("states item map "+ state.getUserID());
            System.out.println("Last update"+this.lastupdate);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
        }
    
    // public void unwrap(ReplicaState state){
    //     auctionData.setItemMap(state.getItemMap());
    //     auctionData.setHighestBidders(state.getHighestBidders());
    //     auctionData.setUseridanditem(state.getUseridanditem());
    //     auctionData.setUserHashMap(state.getUserHashMap());
    //     auctionData.setId(state.getId());
    // }
    


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
                    try {
                        //replica belongs other replicas stored in the hashmap
                        Replication replica = (Replication) registry.lookup(replicaName);
                        replicationMap.put(replica.getReplicaID(), replica);
                        //this replica is the one that matches with the current replica name
                        Replication myselfreplica = (Replication) registry.lookup("Replica "+this.getReplicaID());
                        replica.addToReplicationMap(replicaID, myselfreplica);
                        System.out.println(replica.getReplicaID());
                        // myselfreplica.getState(replicaID);
                        
                        System.out.println(" Added replica " + replica.getReplicaID());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }
    public Long getLastupdate() {
        return lastupdate;
    }
    public void setLastupdate(Long lastupdate) {
        this.lastupdate = lastupdate;
    }
    
    
    
}