package ddnet.mobile.integration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ddnet.mobile.Constants;

public class DDNETServicesProxyFactory {
    private static final DDNETServicesProxyFactory INSTANCE = new DDNETServicesProxyFactory();

    private DDNETServicesProxyFactory() { }

    public static DDNETServicesProxyFactory getInstance() {
        return INSTANCE;
    }

    public DDNETServicesProxy create(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final String username = sharedPreferences.getString(Constants.DDNET_SERVER_USERNAME, null);
        final String password = sharedPreferences.getString(Constants.DDNET_SERVER_PASSWORD, null);

        return new DDNETServicesProxy(Constants.DDNET_SERVER, username, password);
    }
}
