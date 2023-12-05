import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Frontend implements Auction{
    private static String primaryReplica;
    private static int PrimaryReplicaID;
    public Frontend() {
        super();

        
    }

    public static void main(String[] args) {
        try {
            Frontend frontend = new Frontend();
            Auction stub = (Auction) UnicastRemoteObject.exportObject(frontend, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Frontend", stub);
            System.out.println("Frontend Ready");
            Frontend.primaryReplica = choosePrimary();  // Assign the selected primary replica
        } catch (Exception e) {
            System.err.println("Error starting Frontend " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    

    private Replication getReplica(){
        try{
            Registry registry = LocateRegistry.getRegistry();
            System.out.println("this is primary replica "+primaryReplica);
            Replication replica = (Replication) registry.lookup(primaryReplica);
            replica.setIsprimary(true);
            return replica;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
public Integer register(String email, PublicKey pubKey) throws RemoteException {
    Replication replica = getAliveReplica();
    if (replica != null) {
        try {
            Integer regid = replica.register(email, pubKey);
            if (regid != null) {
                return regid;
            } else {
                System.err.println("Registration failed. Received null registration ID.");
            }
        } catch (Exception e) {
            System.err.println("There was a problem registering: " + e.getMessage());
            e.printStackTrace();
        }
    }
    return null;  // Handle registration failure
}


    @Override
    public ChallengeInfo challenge(int userID, String clientChallenge)
            throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Replication replica = getAliveReplica();
        if (replica!=null) {
            try{
                return replica.challenge(userID, clientChallenge);
            }catch(Exception e){
                System.err.println("there was a problem with the challenge" + e.getMessage());
            }
            
        }
        return null;
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        Replication replica = getAliveReplica();
        if (replica!=null) {
            try{
                return replica.authenticate(userID, signature);
            }catch(Exception e){
                System.err.println("there was a problem with the authenticate" + e.getMessage());
            }
            
        }
        return null;
    }

    @Override
    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException {
        Replication replica = getAliveReplica();
        if (replica!=null) {
            try{
                return replica.getSpec(userID, itemID, token);
            }catch(Exception e){
                System.err.println("there was a problem with the getspec" + e.getMessage());
            }
            
        }
        return null;
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        Replication replica = getAliveReplica();
        if (replica!=null) {
            try{
                return replica.newAuction(userID, item, token);
            }catch(Exception e){
                System.err.println("there was a problem with the challenge" + e.getMessage());
            }
            
        }
        return null;
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        Replication replica = getAliveReplica();
        if (replica!=null) {
            try{
                return replica.listItems(userID, token);
            }catch(Exception e){
                System.err.println("there was a problem with the listitem" + e.getMessage());
            }
            
        }
        return null;
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        Replication replica = getAliveReplica();
        if (replica!=null) {
            try{
                return replica.closeAuction(userID, itemID, token);
            }catch(Exception e){
                System.err.println("there was a problem with the closeauction" + e.getMessage());
            }
            
        }
        return null;
    }

    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        Replication replica = getAliveReplica();
        if (replica!=null) {
            try{
                return replica.bid(userID, itemID, price, token);
            }catch(Exception e){
                System.err.println("there was a problem with the bid" + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        return Frontend.PrimaryReplicaID;
    }

    public static void setPrimaryReplicaID(int primaryReplicaid) {
        Frontend.PrimaryReplicaID = primaryReplicaid;
    }

    private static String choosePrimary() throws RemoteException {
        try {
            List<String> newlist = new ArrayList<String>();
            Registry registry = LocateRegistry.getRegistry();
            String[] reglist = registry.list();
            for (String string : reglist) {
                if (string.startsWith("Replica")) {
                    newlist.add(string);
                    System.out.println(string);
                }
            }
    
            int sizeofnewlist = newlist.size();
            Random random = new Random();
            int randomIndex = random.nextInt(sizeofnewlist);
            String primaryReplica = newlist.get(randomIndex);
    
            // Extract the replica ID from the replica name
            int primaryReplicaID = Integer.parseInt(primaryReplica.split(" ")[1]);
    
            // Set the primary replica ID in the Frontend class
            Frontend.setPrimaryReplicaID(primaryReplicaID);
    
            System.out.println("Chosen Primary Replica: " + primaryReplica);
    
            return primaryReplica;
        } catch (Exception e) {
            System.err.print(e);
        }
        return null;
    }

    
    private boolean pingReplica(Replication replica) {
        try {
            return replica.ping();
        } catch (RemoteException e) {
            // Replica is not reachable, consider it as not alive
            return false;
        }
    }

    private Replication getAliveReplica() throws RemoteException{
        Replication replica = getReplica();
        if (replica!=null && pingReplica(replica)) {
            return replica;
        }else{
            // check what the current primary replica is and then choose one that isnt that one
            Registry registry = LocateRegistry.getRegistry();
            String[] rmiarr =  registry.list();
            ArrayList<String> rmiarArrayList = new ArrayList<String>();
            for (String string : rmiarr) {
                if (string.equals(Frontend.primaryReplica)) {
                    System.out.println("found primary replica in rmiregistry "); 
                }
                rmiarArrayList.add(string);
            }
            Replication anotherReplica = getReplica();
            return anotherReplica;
        }
        
    }



    
    
}
