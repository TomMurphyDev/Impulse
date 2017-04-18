package Model;

/**
 * Created by thomas murphy X00075294 on 08/03/2017.
 */

public class Profile{
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("username")
    private String Username;
    @com.google.gson.annotations.SerializedName("location")
    private String Location ;
    @com.google.gson.annotations.SerializedName("bio")
    private String Bio;
    @com.google.gson.annotations.SerializedName("url")
    private String Url;

    public Profile(String id) {
        this.id = id;
        this.Username = "To be Set";
        this.Location = "To be Set";
        this.Bio = "To be Set";
    }

    public Profile(){
    }

    public Profile(String id,String userName, String location, String bi) {
        this.id = id;
        this.Username = userName;
        this.Location = location;
        this.Bio = bi;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public void setUrl(String url) {
        Url = url;
    }
    public String getUrl(){
        return Url;
    }
}
