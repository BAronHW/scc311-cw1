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
                        if (array.length == 0) {
                            System.out.println("You have no items!");
                        }
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
                        case "register":
                        System.out.println("please enter your email address");
                        if (scanner.hasNext()) {
                            String email = scanner.next();
                            int userid = server.register(email);
                            System.out.println("This is your userID: " + userid);
                        }
                        break;

                        case "newauction":
                            System.out.println("Enter your user ID:");
                            if (scanner.hasNextInt()) {
                                int userID = scanner.nextInt();
                                scanner.nextLine();  // Consume the newline character
                                AuctionSaleItem newItem = new AuctionSaleItem();
                                System.out.println("Enter your item name:");
                                newItem.name = scanner.nextLine();
                                System.out.println("Enter your item description:");
                                newItem.description = scanner.nextLine();
                                System.out.println("Enter a reserve Price: ");
                                newItem.reservePrice = scanner.nextInt();
                                server.newAuction(userID, newItem);
                            } else {
                                System.out.println("Invalid input. User ID must be an integer.");
                                scanner.nextLine();  // Consume the invalid input
                            }
                            break;
                            
                            case "closeauction":
                            System.out.println("Enter your USER ID:");
                            if (scanner.hasNextInt()) {
                                int userID = scanner.nextInt();
                                scanner.nextLine();
                                System.out.println("Enter the Item id:");
                                int itemid = scanner.nextInt();
                                AuctionResult result = server.closeAuction(userID, itemid);
                                System.out.println("Winner Email: "+result.winningEmail + "\n" + "Winning price: " + result.winningPrice);
                            }
                            break;
                            case "bid":
                            System.out.println("enter your USER ID:");
                            if (scanner.hasNextInt()) {
                                int userid = scanner.nextInt();
                                System.out.println("Enter item ID: ");
                                int itemid = scanner.nextInt();
                                System.out.println("enter a price");
                                int price = scanner.nextInt();
                                Boolean result = server.bid(userid, itemid, price);
                                if (result == true) {
                                    System.out.println("success! you have successfully bid!");
                                }else{
                                    System.out.println("something went wrong either your bid wasnt high enough or you are not registered or your item does not exist");
                                }
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
