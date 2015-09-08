package ar.com.klee.marvin.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ar.com.klee.marvin.CardAdapter;
import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.gps.Site;
import ar.com.klee.marvin.gps.Trip;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;


public class MisSitiosFragment extends Fragment {

    public static RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<Site> lSites = new ArrayList<Site>();

    private CommandHandlerManager commandHandlerManager;

    public MisSitiosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.site_recycler_view,container, false);

        commandHandlerManager = CommandHandlerManager.getInstance();
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_PLACES,commandHandlerManager.getMainActivity());

        MainMenuActivity mainMenuActivity;

        mainMenuActivity = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

        SharedPreferences mPrefs = mainMenuActivity.getPreferences(mainMenuActivity.MODE_PRIVATE);

        int numberOfSites = mPrefs.getInt("NumberOfSites",0);

        Integer i;

        for(i=1; i<=numberOfSites; i++) {
            Gson gson = new Gson();
            String json = mPrefs.getString("Site"+i.toString(), "");
            lSites.add(gson.fromJson(json, Site.class));
        }

        if(lSites.size()==0)
            Toast.makeText(mainMenuActivity, "No hay sitios guardados", Toast.LENGTH_SHORT).show();

        java.util.Collections.sort(lSites,new SiteComparator());

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardAdapter(lSites);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder =new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                builder.setCancelable(false);
                builder.setTitle("Nuevo Sitio Favorito");

                Context context = getActivity();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText siteName = new EditText(context);
                siteName.setHint("Sitio");
                siteName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
                layout.addView(siteName);

                final EditText siteAddress = new EditText(context);
                siteAddress.setHint("Dirección");
                layout.addView(siteAddress);

                builder.setView(layout);
                builder.setPositiveButton("Insertar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

                        if(siteName.getText().toString().equals("")) {
                            Toast.makeText(mma, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(siteAddress.getText().toString().equals("")) {
                            Toast.makeText(mma, "Debe ingresar una dirección", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int j=0;

                        while(j<lSites.size()){
                            Site s = lSites.get(j);
                            if(s.getSiteName().equals(siteName.getText().toString())){
                                Toast.makeText(mma, "El sitio ya exite", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        LatLng coordinates = getCoordinates(siteAddress.getText().toString());

                        if(coordinates==null){
                            Toast.makeText(mma, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final Site newSite = new Site(siteName.getText().toString(),siteAddress.getText().toString(),coordinates,0);

                        lSites.add(newSite);

                        SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(newSite);

                        Integer numberOfSites = mPrefs.getInt("NumberOfSites",0);

                        numberOfSites++;

                        prefsEditor.putInt("NumberOfSites",numberOfSites);
                        prefsEditor.putString("Site" + numberOfSites.toString(), json);
                        prefsEditor.commit();

                        final ImageGetterTask task = (ImageGetterTask) new ImageGetterTask().execute();

                        dialog.dismiss();

                        if(task.getImageExists()){

                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                            builder.setCancelable(true);
                            builder.setTitle("Imagen a Mostrar");

                            final Context context = getActivity();
                            final LinearLayout layout = new LinearLayout(context);
                            layout.setOrientation(LinearLayout.HORIZONTAL);

                            final ImageView image1 = new ImageView(context);
                            final ImageView image2 = new ImageView(context);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    image1.setImageBitmap(task.getImg1());
                                    layout.addView(image1);

                                    image2.setImageBitmap(task.getImg2());
                                    layout.addView(image2);

                                    int px = (int) (250 * context.getResources().getDisplayMetrics().density);

                                    image1.getLayoutParams().height = px;
                                    image1.getLayoutParams().width = px;

                                    image2.getLayoutParams().height = px;
                                    image2.getLayoutParams().width = px;

                                    builder.setView(layout);
                                }
                            }, 3000);


                            builder.setNegativeButton("Imagen 1", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    File mediaStorageDir = new File("/sdcard/", "MARVIN");

                                    if (!mediaStorageDir.exists()) {
                                        if (!mediaStorageDir.mkdirs()) {
                                            return;
                                        }
                                    }

                                    mediaStorageDir = new File("/sdcard/MARVIN", "Sitios");

                                    if (!mediaStorageDir.exists()) {
                                        if (!mediaStorageDir.mkdirs()) {
                                            return;
                                        }
                                    }

                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream("/sdcard/MARVIN/Sitios/" + newSite.getSiteName() + ".png");
                                        task.getImg1().compress(Bitmap.CompressFormat.PNG, 90, out);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    newSite.setSiteThumbnail(1);
                                    newSite.setSiteImage("/sdcard/MARVIN/Sitios/" + newSite.getSiteName() + ".png");
                                    lSites.get(lSites.size()-1).setSiteThumbnail(1);
                                    lSites.get(lSites.size()-1).setSiteImage("/sdcard/MARVIN/Sitios/" + newSite.getSiteName() + ".png");

                                    MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();
                                    SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(newSite);
                                    Integer numberOfSites = mPrefs.getInt("NumberOfSites",0);
                                    prefsEditor.putString("Site" + numberOfSites.toString(), json);
                                    prefsEditor.commit();

                                    dialog.dismiss();

                                    mAdapter = new CardAdapter(lSites);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });

                            builder.setPositiveButton("Imagen 2", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    File mediaStorageDir = new File("/sdcard/", "MARVIN");

                                    if (!mediaStorageDir.exists()) {
                                        if (!mediaStorageDir.mkdirs()) {
                                            return;
                                        }
                                    }

                                    mediaStorageDir = new File("/sdcard/MARVIN", "Sitios");

                                    if (!mediaStorageDir.exists()) {
                                        if (!mediaStorageDir.mkdirs()) {
                                            return;
                                        }
                                    }

                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream("/sdcard/MARVIN/Sitios/" + newSite.getSiteName() + ".png");
                                        task.getImg2().compress(Bitmap.CompressFormat.PNG, 90, out);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    newSite.setSiteThumbnail(1);
                                    newSite.setSiteImage("/sdcard/MARVIN/Sitios/" + newSite.getSiteName() + ".png");
                                    lSites.get(lSites.size()-1).setSiteThumbnail(1);
                                    lSites.get(lSites.size()-1).setSiteImage("/sdcard/MARVIN/Sitios/" + newSite.getSiteName() + ".png");

                                    MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();
                                    SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(newSite);
                                    Integer numberOfSites = mPrefs.getInt("NumberOfSites", 0);
                                    prefsEditor.putString("Site" + numberOfSites.toString(), json);
                                    prefsEditor.commit();

                                    dialog.dismiss();

                                    mAdapter = new CardAdapter(lSites);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });
                            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {

                                        newSite.setSiteThumbnail(0);
                                        newSite.setSiteImage(null);
                                        lSites.get(lSites.size()-1).setSiteThumbnail(0);
                                        lSites.get(lSites.size()-1).setSiteImage(null);

                                        MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();
                                        SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                        Gson gson = new Gson();
                                        String json = gson.toJson(newSite);
                                        Integer numberOfSites = mPrefs.getInt("NumberOfSites", 0);
                                        prefsEditor.putString("Site" + numberOfSites.toString(), json);
                                        prefsEditor.commit();

                                        dialog.dismiss();

                                        mAdapter = new CardAdapter(lSites);
                                        mRecyclerView.setAdapter(mAdapter);

                                        dialog.dismiss();
                                        return true;
                                    }
                                    return false;
                                }

                            });

                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                public void run() {
                                    builder.show();
                                }
                            }, 3000);

                        }
                        else {
                            mAdapter = new CardAdapter(lSites);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                builder.show();

            }
        });

        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(mRecyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {

                                File file = new File("/sdcard/MARVIN/Sitios/"+lSites.get(position).getSiteName()+".png");
                                file.delete();

                                lSites.remove(position);
                                MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

                                SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);

                                Integer numberOfSites = lSites.size();

                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                Gson gson = new Gson();
                                prefsEditor.putInt("NumberOfSites",numberOfSites);
                                Integer i;
                                for(i=1;i<=numberOfSites;i++) {
                                    String json = gson.toJson(lSites.get(i-1));
                                    prefsEditor.putString("Site" + i.toString(), json);
                                }
                                prefsEditor.commit();

                                mAdapter = new CardAdapter(lSites);
                                mRecyclerView.setAdapter(mAdapter);

                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        mRecyclerView.addOnItemTouchListener(new SwipeableItemClickListener(getActivity(),new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.txt_delete) {
                    touchListener.processPendingDismisses();
                } else if (view.getId() == R.id.txt_undo) {
                    touchListener.undoPendingDismiss();
                }
            }
        }));


        return v;
    }

    public LatLng getCoordinates(String address){

        Geocoder geocoder = new Geocoder(CommandHandlerManager.getInstance().getActivity());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address, 1, -55.0, -73.0, -21.0, -56.0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Double latitude = 0.0;
        Double longitude = 0.0;

        LatLng coordinates = null;

        if(addresses!=null && addresses.size() > 0) {
            latitude= addresses.get(0).getLatitude();
            longitude= addresses.get(0).getLongitude();
            coordinates = new LatLng(latitude,longitude);
        }

        return coordinates;

    }

    class ImageGetterTask extends AsyncTask<Void, Void, Void> {

        private Bitmap img1;
        private Bitmap img2;
        private boolean imageExists;

        protected Void doInBackground(Void... params) {

            MainMenuActivity mainMenuActivity;

            mainMenuActivity = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

            SharedPreferences mPrefs = mainMenuActivity.getPreferences(mainMenuActivity.MODE_PRIVATE);

            Integer numberOfSites = mPrefs.getInt("NumberOfSites",0);

            Gson gson = new Gson();
            String json = mPrefs.getString("Site"+numberOfSites.toString(), "");
            final Site newSite = gson.fromJson(json, Site.class);

            String xml = "http://cbk0.google.com/cbk?output=xml&hl=x-local&ll=";
            xml += ((Double)(newSite.getCoordinates().latitude)).toString() + ",";
            xml += ((Double)(newSite.getCoordinates().longitude)).toString() + "&it=all";

            String panoId = "";
            imageExists = true;

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                db = dbf.newDocumentBuilder();
                Document doc = db.parse(new URL(xml).openStream());

                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(doc), new StreamResult(writer));
                String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

                if(output.equals("<panorama/>"))
                    imageExists = false;
                else{
                    int index = output.indexOf("pano_id");
                    panoId = output.substring(index+9,index+31);
                }

                if(imageExists) {

                    String url1, url2;

                    url1 = "http://cbk0.google.com/cbk?output=tile&panoid=" + panoId + "&zoom=3&x=4&y=1";
                    url2 = "http://cbk0.google.com/cbk?output=tile&panoid=" + panoId + "&zoom=3&x=2&y=1";

                    img1 = null;
                    img2 = null;

                    try {
                        URL url = new URL(url1);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        img1 = BitmapFactory.decodeStream(input,null,options);

                        if(input!=null)
                            input.close();

                        URL urlDos = new URL(url2);
                        HttpURLConnection connection2 = (HttpURLConnection) urlDos.openConnection();
                        connection2.setDoInput(true);
                        connection2.connect();
                        InputStream input2 = connection2.getInputStream();
                        BitmapFactory.Options options2 = new BitmapFactory.Options();
                        img2 = BitmapFactory.decodeStream(input2,null,options2);

                        if(input2!=null)
                            input.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }

            return null;

        }

        public Bitmap getImg1() {
            return img1;
        }

        public Bitmap getImg2() {
            return img2;
        }

        public boolean getImageExists() {
            return imageExists;
        }

    }

    class SiteComparator implements Comparator<Site> {
        @Override
        public int compare(Site a, Site b) {
            return a.getSiteName().compareToIgnoreCase(b.getSiteName());
        }
    }

}
