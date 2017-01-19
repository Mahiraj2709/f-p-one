package providers.fairrepair.service.fairrepairpartner.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import providers.fairrepair.service.fairrepairpartner.FairRepairApplication;
import providers.fairrepair.service.fairrepairpartner.R;
import providers.fairrepair.service.fairrepairpartner.app.LoginActivity;
import providers.fairrepair.service.fairrepairpartner.app.MainActivity;
import providers.fairrepair.service.fairrepairpartner.data.DataManager;
import providers.fairrepair.service.fairrepairpartner.data.local.PrefsHelper;
import providers.fairrepair.service.fairrepairpartner.data.model.Service;
import providers.fairrepair.service.fairrepairpartner.fragment.HomeFragment;


public class DialogFactory {

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createSimpleOkSuccessDialog(Context context, @StringRes int title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.error_alert_dialog, null);
        TextView tv_errorMsg = (TextView) dialogView.findViewById(R.id.tv_errorMsg);
        tv_errorMsg.setText(message);
        alertDialog.setView(dialogView);
        final Dialog dialog = alertDialog.create();
        dialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context, context.getString(messageResource));
    }

    public static Dialog createLogoutDialog(final Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_no, null)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager dataManager = new DataManager(context);
                        PrefsHelper prefsHelper = new PrefsHelper(context);
                        Map<String, String> requestParams = new HashMap<>();
                        requestParams.put(ApplicationMetadata.SESSION_TOKEN, (String) prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN));
                        requestParams.put("language", (String) prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE));
                        dataManager.logout(requestParams);
                    }
                });
        return alertDialog.create();
    }

    public static Dialog createLogoutDialog(Context context,
                                            @StringRes int titleResource,
                                            @StringRes int messageResource) {

        return createLogoutDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createMultipleChoiceDialog(final Context context, final List<Service> serviceList, String previousSelectedServices) {

        // String array for alert dialog multi choice items
        final ArrayList<String> services = new ArrayList<>();
        // Boolean array for initial selected items
        final boolean[] checkedServices = new boolean[serviceList.size()];

        int pos = 0;
        for (Service service : serviceList) {
            Log.i("sfdf", previousSelectedServices);
            services.add(service.getName());
            if (previousSelectedServices.contains(service.getId() + "")) {
                checkedServices[pos] = true;
            }
            pos++;
        }

        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Convert the color array to list
        builder.setMultiChoiceItems(services.toArray(new String[services.size()]), checkedServices, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Update the current focused item's checked status
                checkedServices[which] = isChecked;
            }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle("Service Available");

        // Set the positive/yes button click listener
        builder.setPositiveButton(context.getString(R.string.dialog_action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedServices.length; i++) {
                    if (!checkedServices[i]) {
                        try {
                            serviceList.get(i).setName("no_service");
                        } catch (IndexOutOfBoundsException ex) {

                        }
                    }
                }

                FairRepairApplication.getBus().post(new ArrayList<>(serviceList));
            }
        });

        // Set the negative/no button click listener
        builder.setNegativeButton(context.getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the negative button
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        return dialog;
    }

    public static void createExitDialog(final Context context) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("Do you want to exit?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) context).finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void createComingSoonDialog(final Context context) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("Coming Soon.");

        alertDialog.setPositiveButton("OK", null
        );


        alertDialog.show();
    }

    public static Dialog createDialogForNotificationData(final Context context, int title, String payload) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(R.string.message_new_request)
                .setPositiveButton(R.string.show_customer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment newFragment = HomeFragment.newInstance(0);
                        ((MainActivity)context).addFragmentToStack(newFragment, "home_fragment");
                    }
                }).create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return alertDialog;
    }

    public static Dialog createRegisterSuccessDialog(final Context mContext, int title_success, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(title_success))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                    }
                });
        return alertDialog.create();
    }
}
