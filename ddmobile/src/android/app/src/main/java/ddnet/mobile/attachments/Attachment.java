package ddnet.mobile.attachments;

import android.net.Uri;

public class Attachment {
    private final Uri uri;
    private final String mimeType;
    private final String extension;

    public Attachment(Uri uri, String mimeType, String extension) {
        if (uri == null)
            throw new IllegalArgumentException("uri");
        if (mimeType == null)
            throw new IllegalArgumentException("mimeType");
        if (extension == null)
            throw new IllegalArgumentException("extension");

        this.uri = uri;
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public Uri getUri() {
        return uri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;
        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
}
