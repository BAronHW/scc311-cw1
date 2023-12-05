import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    static boolean loop = true;
    private static KeyPairGenerator generator;
    private static KeyPair pair;
    // private static HashMap<Integer,PrivateKey> everyuserprivkey = new HashMap<Integer,PrivateKey>();

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            while (loop) {
                System.out.println("\n"+
                                    "\nCommands:"+
                                    "\nexit"+
                                    "\nlist"+
                                    "\ngetspec"+
                                    "\nregister"+
                                    "\nnewauction"+
                                    "\ncloseauction"+
                                    "\nbid");
                String cmd = scanner.nextLine();
                String name = "Frontend";
                Registry registry = LocateRegistry.getRegistry("localhost");
                Auction server = (Auction) registry.lookup(name);


                switch (cmd) {
                    case "exit":
                        loop = false;
                        break;
                        case "list":
                        System.out.println("enter userid");
                        if (scanner.hasNextInt()) {
                            int userid = scanner.nextInt();
                            // TokenInfo tokinf = gTokenInfo(userid, server);
                            // System.out.println(tokinf.token);
                            AuctionItem[] array = server.listItems(userid,null);
                    
                                if (array != null) {
                                    if (array.length == 0) {
                                        System.out.println("You have no items!");
                                    } else {
                                        for (AuctionItem i : array) {
                                            System.out.println("Name: " + i.name +
                                                    "\nDescription: " + i.description +
                                                    "\nItemID: " + i.itemID +
                                                    "\nHighestBid: " + i.highestBid +
                                                    "\n");
                                        }
                                    }
                                } else {
                                    System.out.println("Server returned null array");
                        }
                    }
                        break;                    
                        case "getspec":
                        // Extract itemID from the command (assuming the format is "getSpec <itemID>")
                        System.out.println("enter an userID");
                        if (scanner.hasNextInt()) {
                            int id = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("enter an itemID");
                            int itemID = scanner.nextInt();
                            // TokenInfo tok = gTokenInfo(id, server);
                            AuctionItem auitem = server.getSpec(id,itemID,null);
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
                            generator = KeyPairGenerator.getInstance("RSA");
                            generator.initialize(2048,new SecureRandom());
                            pair = generator.generateKeyPair();

                            PrivateKey privkey = pair.getPrivate();
                            PublicKey pubkey = pair.getPublic();
                            int userid = server.register(email, null);
                            // everyuserprivkey.put(userid,privkey);
                            
                            System.out.println("This is your userID: " + userid);
                        }
                        break;

                        case "newauction":
                            System.out.println("Enter your user ID:");

                            try {
                                if (scanner.hasNextInt()) {
                                int userID = scanner.nextInt();
                                // TokenInfo newtoken = gTokenInfo(userID, server);
                                scanner.nextLine();  // Consume the newline character
                                AuctionSaleItem newItem = new AuctionSaleItem();
                                System.out.println("Enter your item name:");
                                newItem.name = scanner.nextLine();
                                System.out.println("Enter your item description:");
                                newItem.description = scanner.nextLine();
                                System.out.println("Enter a reserve Price: ");
                                newItem.reservePrice = scanner.nextInt();
                                server.newAuction(userID, newItem,null);
                            } else {
                                System.out.println("Invalid input. User ID must be an integer.");
                                scanner.nextLine();  // Consume the invalid input
                            }
                            } catch (Exception e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }
                            
                            
                            break;
                            
                            case "closeauction":
                            System.out.println("Enter your USER ID:");
                            if (scanner.hasNextInt()) {
                                int userID = scanner.nextInt();
                                // TokenInfo tok = gTokenInfo(userID, server);
                                scanner.nextLine();
                                System.out.println("Enter the Item id:");
                                int itemid = scanner.nextInt();
                                AuctionResult result = server.closeAuction(userID, itemid, null);
                                System.out.println("Winner Email: "+result.winningEmail + "\n" + "Winning price: " + result.winningPrice);
                            }
                            break;
                            case "bid":
                            System.out.println("enter your USER ID:");
                            if (scanner.hasNextInt()) {
                                int userid = scanner.nextInt();
                                // TokenInfo tok = gTokenInfo(userid, server);
                                System.out.println("Enter item ID: ");
                                int itemid = scanner.nextInt();
                                System.out.println("enter a price");
                                int price = scanner.nextInt();
                                Boolean result = server.bid(userid, itemid, price, null);
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

    public static TokenInfo gTokenInfo(int userid, Auction server) throws InvalidKeyException, RemoteException, NoSuchAlgorithmException, SignatureException {
            return null;  // or handle the exception as needed
        }


public static PublicKey readKey(String filePath) {
        try {
            // Read the public key from the specified file
            byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));
            String publicKeyBase64 = new String(keyBytes, StandardCharsets.UTF_8);

            // Decode the Base64-encoded public key
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

            // Generate the PublicKey object from the decoded bytes
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            return null;
        } catch (Exception e) {
            System.err.println("Exception in readKey: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

