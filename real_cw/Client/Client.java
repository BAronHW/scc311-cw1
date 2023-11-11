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
                    default:
                        if (cmd.startsWith("getSpec")) {
                            // Extract itemID from the command (assuming the format is "getSpec <itemID>")
                            String[] parts = cmd.split(" ");
                            if (parts.length == 2) {
                                int itemID = Integer.parseInt(parts[1]);
                                AuctionItem auitem = server.getSpec(itemID);
                                System.out.println("Name: " + auitem.name +
                                        "\nDescription: " + auitem.description +
                                        "\nItemID: " + auitem.itemID +
                                        "\nHighestBid: " + auitem.highestBid +
                                        "\n");
                            } else {
                                System.out.println("Invalid command. Use 'getSpec <itemID>' format.");
                            }
                        } else {
                            System.out.println("Invalid command. Try 'exit', 'list', or 'getSpec <itemID>'.");
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
