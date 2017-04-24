package Model;

/**
 * Created by thomas murphy X00075294 on 20/04/2017.
 */

public class Video {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("title")
    public String Title;
    @com.google.gson.annotations.SerializedName("category")
    public String Category;
    @com.google.gson.annotations.SerializedName("description")
    public String Description;
    @com.google.gson.annotations.SerializedName("available")
    public boolean Available;
    @com.google.gson.annotations.SerializedName("profileID")
    public String ProfileID;
    @com.google.gson.annotations.SerializedName("blobUrl")
    public String BlobUrl ;
    @com.google.gson.annotations.SerializedName("streamUrl")
    public String StreamUrl;
    @com.google.gson.annotations.SerializedName("thumbUrl")
    public String ThumbUrl ;

    public Video(String id, String title, String category, String description, boolean available, String profileID, String blobUrl, String streamUrl, String thumbUrl) {
        this.id = id;
        Title = title;
        Category = category;
        Description = description;
        Available = available;
        ProfileID = profileID;
        BlobUrl = blobUrl;
        StreamUrl = streamUrl;
        ThumbUrl = thumbUrl;
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
}
