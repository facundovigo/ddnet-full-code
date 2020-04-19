package ddnet.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ddnet.mobile.integration.DDNETServicesProxy;
import ddnet.mobile.integration.DDNETServicesProxyFactory;
import ddnet.mobile.session.SessionManager;
import ddnet.mobile.util.Holder;

public class LoginActivity extends Activity {
    private static final String LOG_TAG = "DDMOBILE.Login";

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
        usernameInput = (EditText) findViewById(R.id.usernameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        loginButton = (Button) findViewById(R.id.loginButton);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    String username = usernameInput.getText().toString();
                    String password = passwordInput.getText().toString();

                    if (username.trim().length() > 0 && password.trim().length() > 0) {
                        saveCredentials(username, password);
                        if (validateCredentials()) {
                            // Registramos el login exitoso del usuario
                            sessionManager.onLogin(username);
                            // Lo llevamos a la actividad principal.
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Usuario/password invalido
                            Toast.makeText(getApplicationContext(), "Usuario o clave inválidos.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Usuario/password no ingresados
                        Toast.makeText(getApplicationContext(), "Debe ingresar usuario y clave para ingresar a la aplicación.", Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "Error en login", t);
                    Toast.makeText(getApplicationContext(), "Error intentando login de usuario. " +
                            "Compruebe que se encuentra online y reintente la operación.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveCredentials(String username, String password) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.DDNET_SERVER_USERNAME, username);
        editor.putString(Constants.DDNET_SERVER_PASSWORD, password);
        editor.commit();
    }

    private boolean validateCredentials() {
        final DDNETServicesProxy ddnetServicesProxy =
                DDNETServicesProxyFactory.getInstance().create(getApplicationContext());

        final Holder<Boolean> resultHolder = new Holder<>(false);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                 resultHolder.setValue(ddnetServicesProxy.authenticateQuietly());
            }
        });

        try {
            thread.start();
            thread.join(5000);
        } catch (Throwable ignore) { }

        return resultHolder.getValue();
    }

    @Override
    public void onBackPressed() {
        // Evitamos volver a la actividad anterior si estoy haciendo login.
        moveTaskToBack(true);
    }
}
