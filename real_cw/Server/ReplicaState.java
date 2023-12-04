import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ReplicaState implements Serializable {
    private ConcurrentHashMap<Integer, AuctionItem> itemMap;
    private ConcurrentHashMap<Integer, Integer> useridanditem;
    private ConcurrentHashMap<Integer, String> userHashMap;
    private ConcurrentHashMap<Integer, Integer> highestBidders;
    private HashMap<Integer, PublicKey> everyuserpubkey;
    private HashMap<Integer, String> randomstringhashmap;
    private ConcurrentHashMap<Integer, TokenInfo> usertokenmap;
    private KeyPair pair;
    private int userID;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private int id;

    public ReplicaState(
            ConcurrentHashMap<Integer, AuctionItem> itemMap,
            ConcurrentHashMap<Integer, Integer> useridanditem,
            ConcurrentHashMap<Integer, String> userHashMap,
            ConcurrentHashMap<Integer, Integer> highestBidders,
            HashMap<Integer, PublicKey> everyuserpubkey,
            HashMap<Integer, String> randomstringhashmap,
            ConcurrentHashMap<Integer, TokenInfo> usertokenmap,
            KeyPair pair,
            int userID,
            PublicKey publicKey,
            PrivateKey privateKey
    ) {
        this.itemMap = itemMap;
        this.useridanditem = useridanditem;
        this.userHashMap = userHashMap;
        this.highestBidders = highestBidders;
        this.everyuserpubkey = everyuserpubkey;
        this.randomstringhashmap = randomstringhashmap;
        this.usertokenmap = usertokenmap;
        this.pair = pair;
        this.userID = userID;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }



        public void setItemMap(ConcurrentHashMap<Integer, AuctionItem> itemMap) {
            this.itemMap = itemMap;
        }
        public ConcurrentHashMap<Integer, AuctionItem> getItemMap() {
            return itemMap;
        }
        public void setUseridanditem(ConcurrentHashMap<Integer, Integer> useridanditem) {
            this.useridanditem = useridanditem;
        }
        public ConcurrentHashMap<Integer, Integer> getUseridanditem() {
            return useridanditem;
        }
        public void setUserHashMap(ConcurrentHashMap<Integer, String> userHashMap) {
            this.userHashMap = userHashMap;
        }
        public ConcurrentHashMap<Integer, String> getUserHashMap() {
            return userHashMap;
        }
        public void setHighestBidders(ConcurrentHashMap<Integer, Integer> highestBidders) {
            this.highestBidders = highestBidders;
        }
        public ConcurrentHashMap<Integer, Integer> getHighestBidders() {
            return highestBidders;
        }
        public void setEveryuserpubkey(HashMap<Integer, PublicKey> everyuserpubkey) {
            this.everyuserpubkey = everyuserpubkey;
        }
        public HashMap<Integer, PublicKey> getEveryuserpubkey() {
            return everyuserpubkey;
        }
        public void setRandomstringhashmap(HashMap<Integer, String> randomstringhashmap) {
            this.randomstringhashmap = randomstringhashmap;
        }
        public HashMap<Integer, String> getRandomstringhashmap() {
            return randomstringhashmap;
        }
        public void setUsertokenmap(ConcurrentHashMap<Integer, TokenInfo> usertokenmap) {
            this.usertokenmap = usertokenmap;
        }
        public ConcurrentHashMap<Integer, TokenInfo> getUsertokenmap() {
            return usertokenmap;
        }
        public void setPair(KeyPair pair) {
            this.pair = pair;
        }
        public KeyPair getPair() {
            return pair;
        }
        public void setUserID(int userID) {
            this.userID = userID;
        }
        public int getUserID() {
            return userID;
        }
        public PrivateKey getPrivateKey() {
            return privateKey;
        }
        public PublicKey getPublicKey() {
            return publicKey;
        }
        public void setPrivateKey(PrivateKey privateKey) {
            this.privateKey = privateKey;
        }
        public void setPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
    }
