package ddnet.mobile.attachments;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import ddnet.mobile.MainActivity;
import ddnet.mobile.R;

public class ActionableImageView extends ImageView implements View.OnClickListener {
    private String action;
    private final Bundle bundle = new Bundle();
    private ActionListener actionListener;

    public ActionableImageView(Context context) {
        super(context);

        initializeComponent();
    }

    public ActionableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeComponent();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public void onClick(View v) {
        ActionListener listener = actionListener;
        if (listener != null)
            listener.onAction(this, action);
    }

    private void initializeComponent() {
        setOnClickListener(this);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ActionListener {
        void onAction(ActionableImageView source, String action);
    }
}
