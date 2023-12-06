import java.rmi.NotBoundException;
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
    Replication replica;
    try {
        replica = getAliveReplica();
        System.out.println("I am able to get an alive replica");
        if (replica != null) {
            System.out.println("Replica that was found is not null");
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
    } catch (NotBoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    
    return null;  // Handle registration failure
}


    @Override
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        // Replication replica = getAliveReplica();
        // if (replica!=null) {
        //     try{
        //         return replica.challenge(userID, clientChallenge);
        //     }catch(Exception e){
        //         System.err.println("there was a problem with the challenge" + e.getMessage());
        //     }
            
        // }
        return null;
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        // Replication replica = getAliveReplica();
        // if (replica!=null) {
        //     try{
        //         return replica.authenticate(userID, signature);
        //     }catch(Exception e){
        //         System.err.println("there was a problem with the authenticate" + e.getMessage());
        //     }
            
        // }
        return null;
    }

    @Override
    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException {
        Replication replica;
        try {
            replica = getAliveReplica();
            if (replica!=null) {
            try{
                return replica.getSpec(userID, itemID, token);
            }catch(Exception e){
                System.err.println("there was a problem with the getspec" + e.getMessage());
            }
            
        }
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        return null;
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        Replication replica;
        try {
            replica = getAliveReplica();
            if (replica!=null) {
            try{
                return replica.newAuction(userID, item, token);
            }catch(Exception e){
                System.err.println("there was a problem with the challenge" + e.getMessage());
            }
            
        }
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        Replication replica;
        try {
            replica = getAliveReplica();
            if (replica!=null) {
            try{
                return replica.listItems(userID, token);
            }catch(Exception e){
                System.err.println("there was a problem with the listitem" + e.getMessage());
            }
            
        }
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        Replication replica;
        try {
            replica = getAliveReplica();
            if (replica!=null) {
            try{
                return replica.closeAuction(userID, itemID, token);
            }catch(Exception e){
                System.err.println("there was a problem with the closeauction" + e.getMessage());
            }
            
        }
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        Replication replica;
        try {
            replica = getAliveReplica();
            if (replica!=null) {
            try{
                return replica.bid(userID, itemID, price, token);
            }catch(Exception e){
                System.err.println("there was a problem with the bid" + e.getMessage());
            }
        }
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        System.out.println("PING: " + replica.getReplicaID());
        return replica.ping();
    } catch (RemoteException e) {
        // Replica is not reachable, consider it as not alive
        return false;
    }
}

private Replication getAliveReplica() throws RemoteException, NotBoundException {
    Registry registry = LocateRegistry.getRegistry();
    
    // Look up the registry entry
    Replication replica = (Replication) registry.lookup(primaryReplica);

    // Check if the primary replica is alive.
    if (pingReplica(replica)==false) {
        try {
            registry.unbind(primaryReplica);
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Choose a new primary replica
        Frontend.primaryReplica = choosePrimary();
        System.out.println("The new primary replica that was chose is: "+primaryReplica);

        // Get a new replica
        Replication newreplica = getReplica();
        return newreplica;
    } else {
        return replica;
    }
}




    
    
}