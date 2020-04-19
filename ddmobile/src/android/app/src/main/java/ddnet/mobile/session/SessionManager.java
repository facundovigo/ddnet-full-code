package ddnet.mobile.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import ddnet.mobile.LoginActivity;

public class SessionManager {
    private static final String PREFERENCES_NAME = "DDMOBILE.PREF1";
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String KEY_USERNAME = "name";

    private Context context;
    private SharedPreferences sharedPreferences;
    private Editor editor;

    public SessionManager(Context context) {
        this.context = context;
        this.sharedPreferences = this.context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void onLogin(String name) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, name);
        editor.commit();
    }

    public boolean checkLogin() {
        if(!this.isLoggedIn()) {
            doLogin();
            return false;
        }
        return true;
    }

    public UserDetails getUserDetails() {
        UserDetails userDetails = new UserDetails();
        userDetails.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        return userDetails;
    }

    public void onLogout() {
        editor.clear();
        editor.commit();
        doLogin();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    private void doLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}