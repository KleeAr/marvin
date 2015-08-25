package ar.com.klee.marvin.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;

import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.CardAdapter;
import ar.com.klee.marvin.R;
import ar.com.klee.marvin.Site;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;


public class MisSitiosFragment extends Fragment {

    public static RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<Site> lSites;

    private CommandHandlerManager commandHandlerManager;


    public MisSitiosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      //  View v = inflater.inflate(R.layout.fragment_mis_sitios, container, false);

        View v = inflater.inflate(R.layout.site_recycler_view,container, false);

        commandHandlerManager = CommandHandlerManager.getInstance();
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY,commandHandlerManager.getMainActivity());

        lSites = new ArrayList<Site>();

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


                        mAdapter = new CardAdapter(lSites,siteName.getText().toString(),siteAddress.getText().toString());
                        mRecyclerView.setAdapter(mAdapter);

                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
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
                } else if (view.getId() == R.id.txt_undo) {
                    touchListener.undoPendingDismiss();
                }
            }
        }));


        return v;
    }



}
