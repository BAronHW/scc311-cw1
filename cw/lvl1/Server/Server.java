import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server implements Auction {
    ArrayList<AuctionItem> itemlist = new ArrayList<AuctionItem>();
    public Server(){
        super();
    }

    public AuctionItem getSpec(int itemID){
        return itemlist.get(itemID);
    }

    public static void main(String[] args) {
        try{
            Server server = new Server();
            String name = "myserver";
            Auction stub = (Auction) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("server ready");
        } catch (RemoteException e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }

    
    
}
