import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Replication extends Remote{
    void replicateData(AuctionData data) throws RemoteException;
}
