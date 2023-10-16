import java.rmi.Remote;
import java.rmi.RemoteException;
public interface Auction extends Remote {
    public AuctionItem getSpec(int itemID) throws RemoteException;
    }