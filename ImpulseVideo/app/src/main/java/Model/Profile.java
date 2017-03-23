package Model;

/**
 * Created by thomas murphy X00075294 on 08/03/2017.
 */

public class Profile{
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    public String Uid ;
    public String Username;
    public String Location ;
    public String Bio;

    public Profile(String uid) {
        Uid = uid;
    }

    public Profile(){
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
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
