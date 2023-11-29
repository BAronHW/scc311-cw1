    import java.io.Serializable;
import java.security.KeyPair;
    import java.security.KeyPairGenerator;
    import java.security.PublicKey;
    import java.util.HashMap;
    import java.util.concurrent.ConcurrentHashMap;

    public class ReplicaState implements Serializable{

        private ConcurrentHashMap<Integer, AuctionItem> itemMap;
        private ConcurrentHashMap<Integer, Integer> useridanditem;
        private ConcurrentHashMap<Integer, String> userHashMap;
        private ConcurrentHashMap<Integer, Integer> highestBidders; //ItemID -> HighestBidderID
        private HashMap<Integer,PublicKey> everyuserpubkey;
        private HashMap<Integer,String> randomstringhashmap;
        private HashMap<Integer,TokenInfo> usertokenmap;
        private KeyPair pair;
        private int userID;

        public ReplicaState(ConcurrentHashMap<Integer, AuctionItem> itemMap, ConcurrentHashMap<Integer, Integer> useridanditem,ConcurrentHashMap<Integer, String> userHashMap,ConcurrentHashMap<Integer, Integer> highestBidders, HashMap<Integer,PublicKey> everyuserpubkey, HashMap<Integer,String> randomstringhashmap, HashMap<Integer,TokenInfo> usertokenmap,KeyPair pair, int userID){
            this.itemMap = new ConcurrentHashMap<Integer, AuctionItem>();
            this.useridanditem = new ConcurrentHashMap<Integer, Integer>();
            this.userHashMap = new ConcurrentHashMap<Integer,String>();
            this.highestBidders = new ConcurrentHashMap<Integer,Integer>();
            this.usertokenmap = new HashMap<Integer,TokenInfo>();
            this.everyuserpubkey = new HashMap<Integer,PublicKey>();
            this.randomstringhashmap = new HashMap<Integer, String>();
            this.pair = pair;
            this.userID = userID;
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
        public void setUsertokenmap(HashMap<Integer, TokenInfo> usertokenmap) {
            this.usertokenmap = usertokenmap;
        }
        public HashMap<Integer, TokenInfo> getUsertokenmap() {
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
    }
