public class AuctionItem implements java.io.Serializable {
    int itemID;
    String name;
    String description;
    int highestBid;
    

    public void setID(int id){
        this.itemID = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setdesc(String desc){
        this.description = desc;
    }
    public void sethighestbid(int highestbid){
        this.highestBid = highestbid;
    }
}