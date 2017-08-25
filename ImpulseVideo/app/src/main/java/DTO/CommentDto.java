package DTO;

/**
 * Created by tomo on 25/08/2017.
 */

public class CommentDto {
    private  String profileId;
    private String profileName;
    private String thumbUrl;
    private String content;

    public CommentDto(String profileId,String profileName,String thumbUrl, String content)
    {
        this.profileId = profileId;
        this.profileName = profileName;
        this.thumbUrl = thumbUrl;
        this.content = content;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getContent() {
        return content;
    }
}
