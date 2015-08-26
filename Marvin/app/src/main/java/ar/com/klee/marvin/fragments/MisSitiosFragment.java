package ar.com.klee.marvin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

        for(Integer i=1; i<=numberOfSites; i++) {
            Gson gson = new Gson();
            String json = mPrefs.getString("Site"+i.toString(), "");
            lSites.add(gson.fromJson(json, Site.class));
        }

        if(lSites.size()==0)
            Toast.makeText(mainMenuActivity, "No hay sitios guardados", Toast.LENGTH_SHORT).show();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder =new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
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

                        if(siteName.getText().equals("")) {
                            Toast.makeText(mma, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(siteAddress.getText().equals("")) {
                            Toast.makeText(mma, "Debe ingresar una dirección", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LatLng coordinates = getCoordinates(siteAddress.getText().toString());

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

                        String xml = "http://cbk0.google.com/cbk?output=xml&hl=x-local&ll=";
                        xml += ((Double)coordinates.latitude).toString() + ",";
                        xml += ((Double)coordinates.longitude).toString() + "&it=all";

                        String panoId = "";
                        boolean imageExists = true;

                        try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                            DocumentBuilder db = null;
                            db = dbf.newDocumentBuilder();
                            Document doc = db.parse(new URL(xml).openStream());
                            Element root = doc.getDocumentElement();
                            NodeList nodel = root.getChildNodes();

                            int a;

                            for (a = 0; a < nodel.getLength(); a++) {
                                Node node = nodel.item(a);
                                if(node instanceof Element) {
                                    panoId = ((Element)node).getAttribute("pano_id");
                                    break;
                                }
                            }

                            if(a == nodel.getLength())
                                imageExists = false;

                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(imageExists) {

                            String url1, url2;

                            url1 = "http://cbk0.google.com/cbk?output=tile&panoid=" + panoId + "&zoom=3&x=4&y=1";
                            url2 = "http://cbk0.google.com/cbk?output=tile&panoid=" + panoId + "&zoom=3&x=2&y=1";

                            Bitmap img1 = null;
                            Bitmap img2 = null;

                            try {
                                img1 = BitmapFactory.decodeStream((InputStream) new URL(url1).getContent());
                                img2 = BitmapFactory.decodeStream((InputStream) new URL(url2).getContent());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            dialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                            builder.setCancelable(true);
                            builder.setTitle("Imagen a Mostrar");

                            Context context = getActivity();
                            LinearLayout layout = new LinearLayout(context);
                            layout.setOrientation(LinearLayout.HORIZONTAL);

                            LinearLayout layoutLeft = new LinearLayout(context);
                            layoutLeft.setOrientation(LinearLayout.VERTICAL);

                            LinearLayout layoutRight = new LinearLayout(context);
                            layoutRight.setOrientation(LinearLayout.VERTICAL);

                            layout.addView(layoutLeft);
                            layout.addView(layoutRight);

                            final ImageView image1 = new ImageView(context);
                            image1.setImageBitmap(img1);
                            layoutLeft.addView(image1);

                            final ImageView image2 = new ImageView(context);
                            image2.setImageBitmap(img2);
                            layoutRight.addView(image2);

                            builder.setView(layout);
                            builder.setPositiveButton("Imagen 1", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    newSite.setSiteThumbnail(image1.getImageAlpha());

                                    MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();
                                    SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(newSite);
                                    Integer numberOfSites = mPrefs.getInt("NumberOfSites",0);
                                    prefsEditor.putString("Site" + numberOfSites.toString(), json);

                                    dialog.dismiss();

                                    mAdapter = new CardAdapter(lSites, siteName.getText().toString(), siteAddress.getText().toString(),newSite.getCoordinates(),newSite.getSiteThumbnail());
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });

                            builder.setPositiveButton("Imagen 2", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    newSite.setSiteThumbnail(image2.getImageAlpha());

                                    MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();
                                    SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(newSite);
                                    Integer numberOfSites = mPrefs.getInt("NumberOfSites",0);
                                    prefsEditor.putString("Site" + numberOfSites.toString(), json);

                                    dialog.dismiss();

                                    mAdapter = new CardAdapter(lSites, siteName.getText().toString(), siteAddress.getText().toString(),newSite.getCoordinates(),newSite.getSiteThumbnail());
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            });
                            builder.show();
                        }
                        else {
                            mAdapter = new CardAdapter(lSites, siteName.getText().toString(), siteAddress.getText().toString(),coordinates,0);
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
                                lSites.remove(position);
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

                    lSites.remove(position);

                    MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

                    SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);

                    Integer numberOfSites = lSites.size()-1;

                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    Gson gson = new Gson();
                    prefsEditor.putInt("numberOfSites",numberOfSites);
                    Integer i;
                    for(i=0;i<=numberOfSites;i++) {
                        String json = gson.toJson(lSites.get(i));
                        prefsEditor.putString("Site" + numberOfSites.toString(), json);
                    }
                    prefsEditor.commit();

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

        if(addresses!=null && addresses.size() > 0) {
            latitude= addresses.get(0).getLatitude();
            longitude= addresses.get(0).getLongitude();
        }

        return new LatLng(latitude,longitude);

    }

}
