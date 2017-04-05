package Model;

/**
 * Created by thomas murphy X00075294 on 08/03/2017.
 */

public class Profile{
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    public String Username;
    public String Location ;
    public String Bio;

    public Profile(String id) {
        this.id = id;
        this.Username = "To be Set";
        this.Location = "To be Set";
        this.Bio = "To be Set";
    }

    public Profile(){
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
}
