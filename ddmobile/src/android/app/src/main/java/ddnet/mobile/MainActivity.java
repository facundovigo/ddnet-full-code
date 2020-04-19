package ddnet.mobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import ddnet.mobile.attachments.ActionableImageView;
import ddnet.mobile.attachments.Attachment;
import ddnet.mobile.barcode.BarcodeCaptureActivity;
import ddnet.mobile.integration.DDNETAuthenticationException;
import ddnet.mobile.integration.DDNETServicesProxy;
import ddnet.mobile.integration.DDNETServicesProxyFactory;
import ddnet.mobile.session.SessionManager;

public class MainActivity extends Activity implements View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, ActionableImageView.ActionListener {
    private static final String LOG_TAG = "DDMOBILE.Main";
    private static final int CAPTURE_BARCODE_REQUEST_CODE = 10000;
    private static final int ADD_IMAGE_ATTACHMENT_REQUEST_CODE = 10001;
    private static final int ADD_AUDIO_ATTACHMENT_REQUEST_CODE = 10002;
    private static final int ADD_VIDEO_ATTACHMENT_REQUEST_CODE = 10003;
    private static final String ATTACH_IMAGE = "ATTACH_IMAGE";
    private static final String ATTACH_AUDIO = "ATTACH_AUDIO";
    private static final String ATTACH_VIDEO = "ATTACH_VIDEO";
    private static final String REMOVE_ALL_ATTACHMENTS = "REMOVE_ALL_ATTACHMENTS";
    private static final String ATTACHMENT_URI = "URI";

    private SessionManager sessionManager;
    private boolean useFlash;
    private boolean autoFocus;
    private boolean returnFirstDetectedBarcode;

    private SharedPreferences sharedPreferences;
    private TextView titleTextView;
    private TextView selectedStudyText;
    private ViewGroup attachmentsActions;
    private ViewGroup attachmentsContainer;
    private ActionableImageView addImageAttachmentWidget;
    private ActionableImageView addAudioAttachmentWidget;
    private ActionableImageView addVideoAttachmentWidget;
    private ActionableImageView removeAttachmentsWidget;
    private Button sendAttachmentsButton;
    private final Set<Attachment> attachments = new HashSet<>();
    private boolean studySelected;
    private String studyID;
    private String studyText;

    private final ActionableImageView.ActionListener previewAttachedContentListener = new ActionableImageView.ActionListener() {
        @Override
        public void onAction(ActionableImageView source, String action) {
            Uri uri = source.getBundle().getParcelable(ATTACHMENT_URI);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.checkLogin()) {
            finish();
            return;
        }
        Log.d(LOG_TAG, "Usuario activo: " + sessionManager.getUserDetails().getUsername());

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        selectedStudyText = (TextView) findViewById(R.id.selectedStudyText);
        attachmentsActions = (ViewGroup)findViewById(R.id.attachmentsActions);
        attachmentsContainer = (ViewGroup)findViewById(R.id.attachmentsContainer);
        sendAttachmentsButton = (Button)findViewById(R.id.sendAttachments);

        findViewById(R.id.scanStudyQRButton).setOnClickListener(this);
        findViewById(R.id.deselectStudyButton).setOnClickListener(this);
        sendAttachmentsButton.setOnClickListener(this);
        titleTextView.setText(String.format(getString(R.string.top_text_activity_main),
                sessionManager.getUserDetails().getUsername()));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        this.useFlash = sharedPreferences.getBoolean(getString(R.string.useFlashPreferenceKey), false);
        this.autoFocus = sharedPreferences.getBoolean(getString(R.string.autoFocusPreferenceKey), false);
        this.returnFirstDetectedBarcode = sharedPreferences.getBoolean(getString(R.string.returnFirstDetectedBarcodePreferenceKey), false);

        checkAndAdjustCameraSettings();
        addAttachmentsWidgets();
        checkSendAttachmentsAvailability();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sharedPreferences != null)
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "Preferencias actualizadas. Key=" + key);

        if (getString(R.string.useFlashPreferenceKey).equals(key))
            useFlash = sharedPreferences.getBoolean(key, useFlash);

        if (getString(R.string.autoFocusPreferenceKey).equals(key))
            autoFocus = sharedPreferences.getBoolean(key, autoFocus);

        if (getString(R.string.returnFirstDetectedBarcodePreferenceKey).equals(key))
            returnFirstDetectedBarcode = sharedPreferences.getBoolean(key, returnFirstDetectedBarcode);

        checkAndAdjustCameraSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            sessionManager.onLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scanStudyQRButton) {
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AUTO_FOCUS,
                    autoFocus);
            intent.putExtra(BarcodeCaptureActivity.USE_FLASH,
                    useFlash);
            intent.putExtra(BarcodeCaptureActivity.RETURN_FIRST_DETECTED_BARCODE,
                    returnFirstDetectedBarcode);
            startActivityForResult(intent, CAPTURE_BARCODE_REQUEST_CODE);
        } else if (v.getId() == R.id.deselectStudyButton) {
            deselectStudy();
        } else if (v.getId() == R.id.sendAttachments) {
            sendAttachments();
        }
    }

    @Override
    public void onAction(ActionableImageView source, String action) {
        if (ATTACH_IMAGE.equals(action))
            attachContent("image/*", ADD_IMAGE_ATTACHMENT_REQUEST_CODE);
        if (ATTACH_AUDIO.equals(action))
            attachContent("audio/*", ADD_AUDIO_ATTACHMENT_REQUEST_CODE);
        if (ATTACH_VIDEO.equals(action))
            attachContent("video/*", ADD_VIDEO_ATTACHMENT_REQUEST_CODE);
        if (REMOVE_ALL_ATTACHMENTS.equals(action))
            removeAllAttachments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_BARCODE_REQUEST_CODE) {
            try {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BARCODE);
                        if (barcode != null) {
                            Log.d(LOG_TAG, String.format("QR leído! RawValue=[%s]", barcode.rawValue));
                            processReadQR(barcode);
                        } else {
                            Log.d(LOG_TAG, "No se pudo leer código QR (null)");
                            Toast.makeText(this, R.string.qr_no_se_pudo_leer, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(LOG_TAG, "No se pudo leer código QR (sin datos)");
                        Toast.makeText(this, R.string.qr_no_se_pudo_leer, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(LOG_TAG, "No se pudo leer código QR: " +
                            CommonStatusCodes.getStatusCodeString(resultCode));
                    Toast.makeText(this, R.string.qr_no_se_pudo_leer, Toast.LENGTH_LONG).show();
                }
            } catch (Throwable t) {
                Log.e(LOG_TAG, "No se pudo leer código QR", t);
                Toast.makeText(this, R.string.qr_no_se_pudo_leer, Toast.LENGTH_LONG).show();
            } finally {
                checkSendAttachmentsAvailability();
            }
        } else if ((requestCode == ADD_IMAGE_ATTACHMENT_REQUEST_CODE) ||
                    (requestCode == ADD_AUDIO_ATTACHMENT_REQUEST_CODE) ||
                    (requestCode == ADD_VIDEO_ATTACHMENT_REQUEST_CODE)) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedItemUri = data.getData();
                    String mimeType = getContentResolver().getType(selectedItemUri);
                    String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

                    final Attachment attachment = new Attachment(selectedItemUri, mimeType, extension);
                    if (!attachments.contains(attachment)) {
                        attachments.add(attachment);
                        String attachmentType = null;
                        if (requestCode == ADD_IMAGE_ATTACHMENT_REQUEST_CODE)
                            attachmentType = ATTACH_IMAGE;
                        if (requestCode == ADD_AUDIO_ATTACHMENT_REQUEST_CODE)
                            attachmentType = ATTACH_AUDIO;
                        if (requestCode == ADD_VIDEO_ATTACHMENT_REQUEST_CODE)
                            attachmentType = ATTACH_VIDEO;
                        attachmentsContainer.addView(createAttachmentWidget(attachmentType, selectedItemUri));
                        checkSendAttachmentsAvailability();
                        Log.d(LOG_TAG, "Nuevo attachment: " + selectedItemUri);
                    } else {
                        Toast.makeText(this, "¡Adjunto duplicado!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(LOG_TAG, "Cancelada la acción de adjuntar archivos (null)");
                    Toast.makeText(this, "No se agregó el adjunto", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(LOG_TAG, "Cancelada la acción de adjuntar archivos (!RESULT_OK)");
                Toast.makeText(this, "No se agregó el adjunto", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendAttachments() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Set<Attachment> attachmentsCopy = new HashSet<>(attachments);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        removeAllAttachments();
                    }
                });

                DDNETServicesProxy ddnetServicesProxy =
                        DDNETServicesProxyFactory.getInstance().create(getApplicationContext());

                try {
                    ddnetServicesProxy.authenticate();
                    for(Attachment attachment : attachmentsCopy) {
                        InputStream fileInputStream = null;
                        try {
                            fileInputStream = getContentResolver().openInputStream(attachment.getUri());
                            ddnetServicesProxy.uploadStudyFile(studyID, fileInputStream, attachment.getExtension());
                        } finally {
                            if (fileInputStream != null) try { fileInputStream.close(); } catch (Throwable ignore) { }
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    String.format("Se enviaron %d adjuntos para el estudio '%s'", attachmentsCopy.size(), studyText),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (DDNETAuthenticationException e) {
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void checkAndAdjustCameraSettings() {
        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
            this.useFlash = false;

        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS))
            this.autoFocus = false;
    }

    private void addAttachmentsWidgets() {
        addImageAttachmentWidget = createAttachmenActiontWidget(ATTACH_IMAGE);
        attachmentsActions.addView(addImageAttachmentWidget);

        addAudioAttachmentWidget = createAttachmenActiontWidget(ATTACH_AUDIO);
        attachmentsActions.addView(addAudioAttachmentWidget);

        addVideoAttachmentWidget = createAttachmenActiontWidget(ATTACH_VIDEO);
        attachmentsActions.addView(addVideoAttachmentWidget);

        removeAttachmentsWidget = createAttachmenActiontWidget(REMOVE_ALL_ATTACHMENTS);
        attachmentsActions.addView(removeAttachmentsWidget);
    }

    private ActionableImageView createAttachmenActiontWidget(String attachmentType) {
        ActionableImageView widget = new ActionableImageView(getApplicationContext());

        int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                25, getResources().getDisplayMetrics());

        widget.setImageResource(getImageResouceIDByAttachmentType(attachmentType));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.leftMargin = size;
        widget.setLayoutParams(layoutParams);
        widget.setAction(attachmentType);
        widget.setActionListener(this);

        return widget;
    }

    private ActionableImageView createAttachmentWidget(String attachmentType, Uri contentUri) {
        ActionableImageView widget = new ActionableImageView(getApplicationContext());

        widget.setImageResource(getImageResouceIDByAttachmentType(attachmentType));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(24, 24);
        layoutParams.leftMargin = 15;
        widget.setLayoutParams(layoutParams);
        widget.getBundle().putParcelable(ATTACHMENT_URI, contentUri);
        widget.setActionListener(previewAttachedContentListener);

        return widget;
    }

    private int getImageResouceIDByAttachmentType(String attachmentType) {
        if (ATTACH_IMAGE.equals(attachmentType))
            return R.drawable.image;
        if (ATTACH_AUDIO.equals(attachmentType))
            return R.drawable.audio;
        if (ATTACH_VIDEO.equals(attachmentType))
            return R.drawable.video;
        if (REMOVE_ALL_ATTACHMENTS.equals(attachmentType))
            return R.drawable.remove;

        throw new RuntimeException("Tipo de attachment invalido: " + attachmentType);
    }

    private void processReadQR(Barcode barcode) {
        String barcodeData = barcode.rawValue != null ? barcode.rawValue : "";
        Matcher matcher = Constants.STUDY_QR_DATA_REGEX.matcher(barcodeData);

        String matchedStudyID = null;
        String matchedStudyText = null;
        if (matcher.matches()) {
            matchedStudyID = matcher.group(1);
            matchedStudyText = matcher.group(2);
        }

        if (matchedStudyID == null || matchedStudyID.trim().length() == 0 ||
                matchedStudyText == null || matchedStudyText.trim().length() == 0)
            throw new IllegalArgumentException("Datos QR de estudio inválidos: " + barcodeData);

        this.studySelected = true;
        this.studyID = matchedStudyID;
        this.studyText = matchedStudyText;
        this.selectedStudyText.setText(studyText);
    }

    private void deselectStudy() {
        this.studySelected = false;
        this.studyID = null;
        this.studyText = null;
        this.selectedStudyText.setText(getString(R.string.noStudySelected));
        checkSendAttachmentsAvailability();
    }

    private void attachContent(String type, int requestCode) {
        Intent intent = new Intent();
        intent.setType(type);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.CATEGORY_OPENABLE, true);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Seleccione el elemento a adjuntar"), requestCode);
    }

    private void removeAllAttachments() {
        attachmentsContainer.removeAllViews();
        attachments.clear();
        checkSendAttachmentsAvailability();
    }

    private void checkSendAttachmentsAvailability() {
        sendAttachmentsButton.setEnabled(studySelected && !attachments.isEmpty());
    }
}
