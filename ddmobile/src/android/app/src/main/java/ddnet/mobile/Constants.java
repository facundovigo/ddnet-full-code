package ddnet.mobile;

import android.net.Uri;

import java.util.regex.Pattern;

public class Constants {
    private Constants() {}

    //public static final Uri DDNET_SERVER = Uri.parse("http://10.0.2.2:8080/ddnet-web/");
    public static final Uri DDNET_SERVER = Uri.parse("http://eresungallina.no-ip.org:8080/ddnet-web/");
    public static final String DDNET_SERVER_USERNAME = "DDNET_SERVER_USERNAME";
    public static final String DDNET_SERVER_PASSWORD = "DDNET_SERVER_PASSWORD";
    public static final Pattern STUDY_QR_DATA_REGEX = Pattern.compile("^((?:\\d+\\.)+\\d+)\\|(.*)$");
}
