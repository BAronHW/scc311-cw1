import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;

public interface Replication extends Remote{
    public Integer register(String email, PublicKey pubKey) throws RemoteException;
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException;
    public TokenInfo authenticate(int userID, byte signature[]) throws RemoteException;
    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException;
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException;
    public AuctionItem[] listItems(int userID, String token) throws RemoteException;
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException;
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException;
    public int getPrimaryReplicaID() throws RemoteException;
    public void replicateData(AuctionData data) throws RemoteException;
    public int getReplicaID() throws RemoteException;
    public void addToReplicationMap(int replicaID, Replication replica) throws RemoteException;
    public void setCurrentstate(ReplicaState currentstate) throws RemoteException;
}
