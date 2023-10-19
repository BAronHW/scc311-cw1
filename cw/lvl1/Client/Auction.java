import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.crypto.SealedObject;
public interface Auction extends Remote {
    public AuctionItem getSpec(int itemID) throws RemoteException;
    }