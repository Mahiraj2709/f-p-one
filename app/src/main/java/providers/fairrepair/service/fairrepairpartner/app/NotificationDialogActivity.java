package providers.fairrepair.service.fairrepairpartner.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.model.Customer;
import providers.fairrepair.service.fairrepairpartner.utils.ApplicationMetadata;
import providers.fairrepair.service.fairrepairpartner.utils.LocationUtils;


/**
 * Created by admin on 12/29/2016.
 */

public class NotificationDialogActivity extends BaseActivity {
    private Customer customer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationUtils locationUtils = new LocationUtils(this);
        locationUtils.showSettingDialog();
        String notificationData = getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA);
        final int notificationType = getIntent().getIntExtra(ApplicationMetadata.NOTIFICATION_TYPE, -1);


        if (notificationData != null && notificationType == 1) { // NEW OFFER FROM CUSTOMER
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, notificationData);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
            startActivity(intent);
            finish();

        } else if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_OFFER_ACCEPTED) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, notificationData);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
            startActivity(intent);
            finish();

        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setCanceledOnTouchOutside(false);
            View view = getLayoutInflater().inflate(R.layout.notification_dialog, null);
            Button button = (Button) view.findViewById(R.id.btn_dialog_ok);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(NotificationDialogActivity.this, MainActivity.class);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    NotificationDialogActivity.this.finish();
                }
            });

            alertDialog.setView(view);
            alertDialog.show();
        }

    }

}
