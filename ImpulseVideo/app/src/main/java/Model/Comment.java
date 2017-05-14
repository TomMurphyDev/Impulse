package Model;

/**
 * Created by thomas murphy on 08/05/2017.
 */
public class Comment {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("profileID")
    private String ProfileID;
    @com.google.gson.annotations.SerializedName("videoID")
    private String VideoID;
    @com.google.gson.annotations.SerializedName("commentContent")
    private String CommentContent;

    public Comment(String profileID, String videoID, String commentContent) {
        ProfileID = profileID;
        VideoID = videoID;
        CommentContent = commentContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileID() {
        return ProfileID;
    }

    public void setProfileID(String profileID) {
        ProfileID = profileID;
    }

    public String getVideoID() {
        return VideoID;
    }

    public void setVideoID(String videoID) {
        VideoID = videoID;
    }

    public String getCommentContent() {
        return CommentContent;
    }

    public void setCommentContent(String commentContent) {
        CommentContent = commentContent;
    }
}
