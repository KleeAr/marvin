package ar.com.klee.social.services;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import ar.com.klee.social.services.exceptions.WhatsAppException;

/**
 *
 * Service class used to interact with WhatsApp
 *
 * @author msalerno
 */
public class WhatsAppService {

    private final Activity activity;

    public WhatsAppService(Activity activity) {
        this.activity = activity;
    }

    public void sendWhatsApp(String textToPublish) throws WhatsAppException {
        try {
            Intent sendIntent = new Intent();

            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, textToPublish);
            sendIntent.setType("text/plain");
            PackageInfo info = activity.getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            sendIntent.setPackage(info.packageName);
        }catch (PackageManager.NameNotFoundException e) {
            throw new WhatsAppException("WhatsApp message not send", e);
        }
    }
}
