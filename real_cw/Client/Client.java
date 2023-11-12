import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    static boolean loop = true;

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            while (loop) {
                System.out.println("Enter a command or exit by typing 'exit'");
                String cmd = scanner.nextLine();
                String name = "myserver";
                Registry registry = LocateRegistry.getRegistry("localhost");
                Auction server = (Auction) registry.lookup(name);

                switch (cmd) {
                    case "exit":
                        loop = false;
                        break;
                    case "list":
                        AuctionItem[] array = server.listItems();
                        for (AuctionItem i : array) {
                            System.out.println("Name: " + i.name +
                                    "\nDescription: " + i.description +
                                    "\nItemID: " + i.itemID +
                                    "\nHighestBid: " + i.highestBid +
                                    "\n");
                        }
                        break;
                        case "getspec":
                        // Extract itemID from the command (assuming the format is "getSpec <itemID>")
                        System.out.println("enter an itemID");
                        if (scanner.hasNextInt()) {
                            int id = scanner.nextInt();
                            AuctionItem auitem = server.getSpec(id);
                                System.out.println("Name: " + auitem.name +
                                        "\nDescription: " + auitem.description +
                                        "\nItemID: " + auitem.itemID +
                                        "\nHighestBid: " + auitem.highestBid +
                                        "\n");
                        } else{
                            System.out.println("invalid itemID");
                        }
                        break;
                    
                }
            }
            
        } catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
        }
}
}
