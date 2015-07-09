package ar.com.klee.marvin.social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

import ar.com.klee.marvin.social.exceptions.InstagramException;

/**
 * @author msalerno
 */
public class InstagramService {

    private Context context;

    public InstagramService(Context context) {
        this.context = context;
    }

    public void postImageOnInstagram(String type, String textToPublish, String imagePath) {
        try {

            PackageInfo info = context.getPackageManager().getPackageInfo("com.instagram.android", PackageManager.GET_META_DATA);
            // Create the new Intent using the 'Send' action.
            Intent intent = new Intent(Intent.ACTION_SEND);

            // Set the MIME type
            intent.setType(type);
            // Create the URI from the media
            File media = new File(imagePath);
            Uri uri = Uri.fromFile(media);
            // Add the URI and the caption to the Intent.
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, textToPublish);
            context.startActivity(intent);

        }catch (PackageManager.NameNotFoundException e) {
            throw new InstagramException("Instagram not installed in the device", e);
        }
    }
}
