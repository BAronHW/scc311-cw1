import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


class AuctionData {
    private int id = 0;
    private ConcurrentHashMap<Integer, AuctionItem> itemMap;
    private ConcurrentHashMap<Integer, Integer> useridanditem;
    private ConcurrentHashMap<Integer, String> userHashMap;
    private ConcurrentHashMap<Integer, Integer> highestBidders; //ItemID -> HighestBidderID
    private KeyPairGenerator generator;
    private KeyPair pair;
    private static ScheduledExecutorService executorService;

    public AuctionData(int replicaID) {
        super();
        this.itemMap = new ConcurrentHashMap<>();
        this.useridanditem = new ConcurrentHashMap<>();
        this.userHashMap = new ConcurrentHashMap<>();
        this.highestBidders = new ConcurrentHashMap<>();
        // this.usertokenmap = new ConcurrentHashMap<>();
        executorService = Executors.newScheduledThreadPool(1);
        try {
            Registry registry = LocateRegistry.getRegistry();
            // this.generator = KeyPairGenerator.getInstance("RSA");
            // this.generator.initialize(2048,new SecureRandom());
            // AuctionData.this.pair = generator.generateKeyPair();
            // PublicKey pubkey = pair.getPublic();
            // String[] reglist = registry.list();
            // ArrayList<String> arrayList = new ArrayList<String>();
            // for (String string : reglist) {
            //     if (string.startsWith("Replica ")) {
            //         arrayList.add(string);
            //     }
            // }
            // if (arrayList.size() < 1) {
            //     storePublicKey(pubkey, "../keys/serverKey.pub");
            // } else {
            //     System.out.println("More than one replica: don't store the public key again?");
            // }


        } catch (Exception e) {
            System.out.println(e);
        }
    }


    // Method to write a public key to a file.
// Example use: storePublicKey(aPublicKey, ‘../keys/serverKey.pub’)
    public void storePublicKey(PublicKey publicKey, String filePath) throws Exception {
    // Convert the public key to a byte array
        byte[] publicKeyBytes = publicKey.getEncoded();
    // Encode the public key bytes as Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes);
    // Write the Base64 encoded public key to a file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
        fos.write(publicKeyBase64.getBytes());
        }
    }

    public synchronized int registerUser(int userID, String email, PublicKey publicKey) {
        userHashMap.put(userID, email);
        // everyuserpubkey.put(userID, publicKey);
        return userID;
    }

    public synchronized int createNewAuction(int userID, AuctionSaleItem item, String token){
        boolean booleantoken = true;
        if(booleantoken == true){
        id++;
        AuctionItem auctionItem = new AuctionItem();
        auctionItem.itemID = id;
        auctionItem.name = item.name;
        auctionItem.description = item.description;
        auctionItem.highestBid = item.reservePrice;
        itemMap.put(id, auctionItem);
        useridanditem.put(userID, auctionItem.itemID);
        }
        return id;
    }

    public AuctionItem[] listItems(int userID, String token) {
        // boolean booleantoken = true;
        AuctionItem[] array = null;  // Initialize array outside the if block
        array = getItemMap().values().toArray(new AuctionItem[0]);
        
    
        return array;  // Move the return statement outside the if block
    }
    
    
    public synchronized AuctionResult closeAuction(int userID, int itemID, String token) {
        boolean booleantoken = true;
        if (booleantoken) {
            AuctionItem closeItem = itemMap.get(itemID);
            if (closeItem != null && useridanditem.containsKey(userID) && useridanditem.get(userID) == itemID) {
                Integer highestBidderID = highestBidders.get(itemID);
                if (highestBidderID != null) {
                    String winemail = userHashMap.get(highestBidderID);
                    AuctionResult result = new AuctionResult();
                    result.winningEmail = winemail;
                    result.winningPrice = closeItem.highestBid;
                    itemMap.remove(itemID);
                    highestBidders.remove(itemID);
                    return result;
                } else {
                    // Handle the case where highestBidderID is null
                    AuctionResult result = new AuctionResult();
                    result.winningEmail = "null";
                    result.winningPrice = closeItem.highestBid;
                    itemMap.remove(itemID);
                    return result;
                }
            } else {
                return null; // or throw an exception indicating permission issue
            }
        }
        return null;
    }
    

    public synchronized boolean placeBid(int userID, int itemID, int price, String token) {
        boolean booleantoken = true;
        if (booleantoken) {
            if (!userHashMap.containsKey(userID)) {
            System.out.println("User not registered.");
            return false;
        }

        AuctionItem item = itemMap.get(itemID);
        if (item == null) {
            System.out.println("Item not found.");
            return false;
        }

        if (price > item.highestBid) {
            item.highestBid = price;
            highestBidders.put(itemID, userID);
            System.out.println("Bid successful. New highest bid: " + price);
            return true;
        } else {
            System.out.println("Bid not successful. Bid price must be greater than the current highest bid.");
            return false;
        }
        }
        return booleantoken;
    }

    public AuctionItem getSpec(int userID,int itemID, String token) {
        boolean booleantoken = true;
        AuctionItem item = itemMap.get(itemID);
        if (booleantoken) {
            if (item != null) {
                return item;
            } else {
                return null; // or throw an exception indicating item not found
            }
        }
        return null;
    }

    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException {
        // try {
        //     PrivateKey privkey = pair.getPrivate();
        //     Signature sig = Signature.getInstance("SHA256withRSA");
        //     sig.initSign(privkey);
        //     sig.update(clientChallenge.getBytes(StandardCharsets.UTF_8));
        //     byte[] digitalSignature = sig.sign();
        //     ChallengeInfo challengeInfo = new ChallengeInfo();
        //     String randomString = UUID.randomUUID().toString();
        //     // randomstringhashmap.put(userID, randomString);
        //     challengeInfo.response = digitalSignature;
        //     challengeInfo.clientChallenge = randomString;
        //     return challengeInfo;
        // } catch (Exception e) {
        //     System.out.println(e.getMessage());
            return null;
        }
    
    
    // ...
    
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        try {
            // PublicKey cPublicKey = everyuserpubkey.get(userID);
            // Verify the signature using the client's public key
            // Signature sig = Signature.getInstance("SHA256withRSA");
            // sig.initVerify(cPublicKey);
            // String random = randomstringhashmap.get(userID);
            // sig.update(random.getBytes());
            // boolean isValidSignature = sig.verify(signature);
                // Generate a one-time use token
                String tokenString = generateToken();
                // Set the expiration time (e.g., 10 seconds from now)
                long expirationTimeMillis = System.currentTimeMillis() + 10 * 1000; // 10 seconds
                // Create TokenInfo object
                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.token = tokenString;
                tokenInfo.expiryTime = expirationTimeMillis;
                // usertokenmap.put(userID, tokenInfo);
                // Return the TokenInfo object
                return null;
        } catch (Exception e) {
            System.out.println("Exception during authentication: " + e.getMessage());
        }
        
        // Return null if authentication fails
        return null;
    }
    
    
    // Helper method to generate a random token (you can adjust it based on your requirements)
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    private void scheduleTokenExpiration(int userID) {
        // TokenInfo tok = usertokenmap.get(userID);
        // Long time = tok.expiryTime;

        // System.out.println(time.toString());
        // executorService.schedule(() -> {
        //     // Remove the token logic
        //     dumpToken(userID);
        // }, 10, TimeUnit.SECONDS); // Adjust the expiration time as needed
    }

    private void dumpToken(int userID){
        // usertokenmap.remove(userID);
        // System.out.println("Token has expired for user " + userID);
    }

    private boolean validateToken(int userID, String token) {
        // try {
        //     if (usertokenmap.containsKey(userID)) {
        //     TokenInfo tokeString = usertokenmap.get(userID);
        //     Long expiretime = tokeString.expiryTime;
        //     String thistoken = tokeString.token;
        //     if (thistoken.equals(token)) {
        //         if (System.currentTimeMillis()<expiretime) {
        //             scheduleTokenExpiration(userID);
        //             return true;
        //         }else{
        //         dumpToken(userID); 
                
        //     }
        //     }
        // }
        // } catch (Exception e) {
        //     // TODO: handle exception
        //     e.printStackTrace();
        // }
        
        return false;
        
    }

    // public HashMap<Integer, PublicKey> getEveryuserpubkey() {
    //     return everyuserpubkey;
    // }
    public ConcurrentHashMap<Integer, Integer> getHighestBidders() {
        return highestBidders;
    }
    public ConcurrentHashMap<Integer, AuctionItem> getItemMap() {
        return itemMap;
    }
    public KeyPair getPair() {
        return pair;
    }
    public ConcurrentHashMap<Integer, String> getUserHashMap() {
        return userHashMap;
    }
    public ConcurrentHashMap<Integer, Integer> getUseridanditem() {
        return useridanditem;
    }
    // public ConcurrentHashMap<Integer, TokenInfo> getUsertokenmap() {
    //     return usertokenmap;
    // }
    // public HashMap<Integer, String> getRandomstringhashmap() {
    //     return randomstringhashmap;
    // }
    public int getId() {
        return id;
    }
    public KeyPairGenerator getGenerator() {
        return generator;
    }
    public void setHighestBidders(ConcurrentHashMap<Integer, Integer> highestBidders) {
        this.highestBidders = highestBidders;
    }
    public void setUserHashMap(ConcurrentHashMap<Integer, String> userHashMap) {
        this.userHashMap = userHashMap;
    }
    public void setUseridanditem(ConcurrentHashMap<Integer, Integer> useridanditem) {
        this.useridanditem = useridanditem;
    }
    // public void setUsertokenmap(ConcurrentHashMap<Integer, TokenInfo> usertokenmap) {
    //     this.usertokenmap = usertokenmap;
    // }
    // public void setEveryuserpubkey(HashMap<Integer, PublicKey> everyuserpubkey) {
    //     this.everyuserpubkey = everyuserpubkey;
    // }
    public void setId(int id) {
        this.id = id;
    }
    public void setItemMap(ConcurrentHashMap<Integer, AuctionItem> itemMap) {
        this.itemMap = itemMap;
    }
    // public void setRandomstringhashmap(HashMap<Integer, String> randomstringhashmap) {
    //     this.randomstringhashmap = randomstringhashmap;
    // }
    
    
}