import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Map;

public interface Replication extends Remote{
    public Integer register(String email, PublicKey pubKey) throws RemoteException;
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException, InvalidKeyException, NoSuchAlgorithmException, SignatureException;
    public TokenInfo authenticate(int userID, byte signature[]) throws RemoteException;
    public AuctionItem getSpec(int userID, int itemID, String token) throws RemoteException;
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException, NotBoundException;
    public AuctionItem[] listItems(int userID, String token) throws RemoteException;
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException;
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException;
    public void replicateData(AuctionData data) throws RemoteException;
    public int getReplicaID() throws RemoteException;
    public void addToReplicationMap(int replicaID, Replication replica) throws RemoteException;
    public void setCurrentstate(ReplicaState currentstate) throws RemoteException;
    public ReplicaState getCurrentstate() throws RemoteException;
    public void getState(int replica) throws RemoteException, NotBoundException;
    public ReplicaState returncurrState() throws RemoteException;
    public Map<Integer, Replication> getReplicationMap() throws RemoteException;
    public void setIsprimary(boolean isprimary) throws RemoteException;
    public boolean getisprimary() throws RemoteException;
    public boolean ping() throws RemoteException;
    public Long getLastupdate() throws RemoteException;
    public void setLastupdate(Long lastupdate) throws RemoteException;
}
