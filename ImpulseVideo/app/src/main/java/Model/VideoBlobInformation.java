package Model;

import java.net.URI;

/**
 * Created by Thomas Murphy on 26/04/2017.
 */
public class VideoBlobInformation {
    private URI BlobUri;
    private String BlobName;
    private String BlobNameWithoutExtension;
    private String ProfileId;
    private String VideoId;

    public VideoBlobInformation(URI blobUri, String blobName, String profileId, String videoId) {
        BlobUri = blobUri;
        BlobName = blobName;
        ProfileId = profileId;
        VideoId = videoId;
    }

    public URI getBlobUri() {
        return BlobUri;
    }

    public void setBlobUri(URI blobUri) {
        BlobUri = blobUri;
    }

    public String getBlobName() {
        return BlobName;
    }

    public void setBlobName(String blobName) {
        BlobName = blobName;
    }

    public String getBlobNameWithoutExtension() {
        return BlobNameWithoutExtension;
    }

    public void setBlobNameWithoutExtension(String blobNameWithoutExtension) {
        BlobNameWithoutExtension = blobNameWithoutExtension;
    }

    public String getProfileId() {
        return ProfileId;
    }

    public void setProfileId(String profileId) {
        ProfileId = profileId;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }
}
