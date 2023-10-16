import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
public class Client {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Client n");
            return;
        }
        int id = Integer.parseInt(args[0]);
        
        try{
            String name = "myserver";
            Registry registry = LocateRegistry.getRegistry("localhost");
            Auction server = (Auction) registry.lookup(name);

            AuctionItem a1 = server.getSpec(1);
            System.out.println("empty auctionitem obj:"+ a1.itemID);
            System.out.println("empty auctionitem obj:"+ a1.name);
            System.out.println("empty auctionitem obj:"+ a1.description);
            System.out.println("empty auctionitem obj:"+ a1.highestBid);


        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
    
}
