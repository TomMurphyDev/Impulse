package Model;

import android.net.Uri;

import java.net.URI;

/**
 * Created by thomas murphy X00075294 on 20/04/2017.
 */

public class BlobInformation {
    public URI BlobUri;
    public String BlobName;
    public String BlobNameWithoutExtension;
    public String ProfileId;

    public BlobInformation(URI blobUri, String blobName, String profileId) {
        BlobUri = blobUri;
        BlobName = blobName;
        ProfileId = profileId;
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
}
