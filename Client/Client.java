import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.crypto.SealedObject;
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

            SealedObject auitem = server.getSpec(id);
            System.out.println(auitem);

            // System.out.println("ItemID:"+auitem.itemID);
            // System.out.println("Name:"+auitem.name);
            // System.out.println("description"+auitem.description);
            // System.out.println("highestBid"+auitem.highestBid);

        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
    }
    
}
