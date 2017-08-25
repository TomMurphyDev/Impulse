package Model;

/**
 * Created by thomas murphy X00075294 on 20/04/2017.
 */

public class Video {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("title")
    private String Title;
    @com.google.gson.annotations.SerializedName("category")
    private String Category;
    @com.google.gson.annotations.SerializedName("description")
    private String Description;
    @com.google.gson.annotations.SerializedName("available")
    private boolean Available;
    @com.google.gson.annotations.SerializedName("profileID")
    private String ProfileID;
    @com.google.gson.annotations.SerializedName("blobUrl")
    private String BlobUrl ;
    @com.google.gson.annotations.SerializedName("streamUrl")
    private String StreamUrl;
    @com.google.gson.annotations.SerializedName("thumbUrl")
    private String ThumbUrl ;
    @com.google.gson.annotations.SerializedName("createdAt")
    private String Create;
    public Video(String id, String title, String category, String description, boolean available, String profileID, String blobUrl, String streamUrl, String thumbUrl,String create) {
        this.id = id;
        Title = title;
        Category = category;
        Description = description;
        Available = available;
        ProfileID = profileID;
        BlobUrl = blobUrl;
        StreamUrl = streamUrl;
        ThumbUrl = thumbUrl;
        Create = create;
    }
    public Video(String id, String title, String category, String description, String profileID, String blobUrl) {
        this.id = id;
        Title = title;
        Category = category;
        Description = description;
        Available = false;
        ProfileID = profileID;
        BlobUrl = blobUrl;
    }

    public Video(String ti, String desc) {
        Title = ti;
        Description = desc;
    }

    public Video(String ti, String desc, String category) {
        Title = ti;
        Category = category;
        Description = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isAvailable() {
        return Available;
    }

    public void setAvailable(boolean available) {
        Available = available;
    }

    public String getProfileID() {
        return ProfileID;
    }

    public void setProfileID(String profileID) {
        ProfileID = profileID;
    }

    public String getBlobUrl() {
        return BlobUrl;
    }

    public void setBlobUrl(String blobUrl) {
        BlobUrl = blobUrl;
    }

    public String getStreamUrl() {
        return StreamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        StreamUrl = streamUrl;
    }

    public String getThumbUrl() {
        return ThumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        ThumbUrl = thumbUrl;
    }
    public String getCreate() {
        return Create;
    }
    public void setCreate(String create) {
        this.Create = create;
    }
}
