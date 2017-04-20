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
}
