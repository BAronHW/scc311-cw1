import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server implements Auction {
    ArrayList<AuctionItem> itemlist = new ArrayList<AuctionItem>();
    public Server(){
        super();
        for(int i=0;i<100;i++){
            AuctionItem item = new AuctionItem();
            item.itemID = i;
            item.name = ("name" + i);
            item.description = ("description" + i);
            item.highestBid = i;
            itemlist.add(item);
            
        }
        

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
