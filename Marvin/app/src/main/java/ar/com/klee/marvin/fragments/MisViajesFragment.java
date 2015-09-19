package ar.com.klee.marvin.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.gps.Trip;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;


public class MisViajesFragment extends Fragment {

    private CommandHandlerManager commandHandlerManager;
    private Trip chosenTrip;
    private MyBaseAdapter adapter;
    private static MisViajesFragment instance;

    public MisViajesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_mis_viajes, container, false);
        init((RecyclerView) v.findViewById(R.id.recycler_view));

        instance = this;

        commandHandlerManager = CommandHandlerManager.getInstance();
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY,commandHandlerManager.getMainActivity());

        return v;
    }


    private void init(RecyclerView recyclerView) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MyBaseAdapter();
        recyclerView.setAdapter(adapter);
        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(recyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });

        recyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        recyclerView.setOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        recyclerView.addOnItemTouchListener(new SwipeableItemClickListener(getActivity(),new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        } else {
                            chosenTrip = adapter.getTrip(position);
                            Intent intent = new Intent(commandHandlerManager.getContext(), TripActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            commandHandlerManager.getContext().startActivity(intent);
                        }
                    }
                }));
    }

    public Trip getChosenTrip(){
        return chosenTrip;
    }

    public static MisViajesFragment getInstance(){
        return instance;
    }

    static class MyBaseAdapter extends RecyclerView.Adapter<MyBaseAdapter.MyViewHolder> {

        private List<Trip> tripList = new ArrayList<Trip>();
        private List<String> destinationList = new ArrayList<String>();
        private List<Integer> colourList = Arrays.asList(Color.BLUE,Color.GREEN,Color.RED,Color.GRAY,Color.MAGENTA,Color.CYAN,
                Color.YELLOW,Color.DKGRAY,Color.LTGRAY);

        private MainMenuActivity mainMenuActivity;

        MyBaseAdapter() {

            mainMenuActivity = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

            SharedPreferences mPrefs = mainMenuActivity.getPreferences(mainMenuActivity.MODE_PRIVATE);

            int numberOfTrips = mPrefs.getInt("NumberOfTrips",0);

            Integer i;
            for(i=numberOfTrips;i>=1;i--) {
                Gson gson = new Gson();
                String json = mPrefs.getString("Trip"+i.toString(), "");
                tripList.add(gson.fromJson(json, Trip.class));
            }

            if(tripList.size()==0)
                Toast.makeText(mainMenuActivity, "Historial de Viajes Vacío", Toast.LENGTH_SHORT).show();

            CommandHandlerManager.getInstance().defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY, mainMenuActivity);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mis_viajes, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.distance.setText(tripList.get(position).getDistance() + " km");
            holder.beginningAddress.setText("Desde: " + tripList.get(position).getBeginningAddress());
            holder.endingAddress.setText("Hasta: " + tripList.get(position).getEndingAddress());

            String formattedDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(tripList.get(position).getStartTime());
            holder.beginningTime.setText(formattedDate);

            formattedDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(tripList.get(position).getFinishTime());
            holder.endingTime.setText(formattedDate);

            int i = 0;

            while(i < destinationList.size()){
                if(holder.endingAddress.getText().toString().equals(destinationList.get(i))){
                    break;
                }
                i++;
            }

            if(i == destinationList.size()) {
                destinationList.add(holder.endingAddress.getText().toString());
            }

            holder.layoutCar.setBackgroundColor(colourList.get(i%9));
        }

        @Override
        public int getItemCount() {
            return tripList.size();
        }

        public void remove(int position) {
            if(tripList.size()!=0) {
                tripList.remove(position);
                notifyItemRemoved(position);

                MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

                SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);

                Integer numberOfTrips = tripList.size();

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                prefsEditor.putInt("NumberOfTrips", numberOfTrips);
                Integer i;
                for (i = numberOfTrips; i >= 1; i--) {
                    String json = gson.toJson(tripList.get(numberOfTrips - i));
                    prefsEditor.putString("Trip" + i.toString(), json);
                }
                prefsEditor.commit();
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView distance, beginningAddress, endingAddress, beginningTime, endingTime;
            LinearLayout layoutCar;
            MyViewHolder(View view) {
                super(view);
                distance = ((TextView) view.findViewById(R.id.distance));
                beginningAddress = ((TextView) view.findViewById(R.id.beginningAddress));
                endingAddress = ((TextView) view.findViewById(R.id.endingAddress));
                beginningTime = ((TextView) view.findViewById(R.id.beginningTime));
                endingTime = ((TextView) view.findViewById(R.id.endingTime));
                layoutCar= (LinearLayout) view.findViewById(R.id.lyt_2);

            }
        }

        public Trip getTrip(int position){
            return tripList.get(position);
        }

        public boolean checkTripExistence(String trip){

            if(trip.equals("last")){
                return tripList.size()!=0;
            }

            if(trip.startsWith("number - ")){
                trip = trip.replace("number - ","");
                return Integer.parseInt(trip) < tripList.size();
            }

            boolean isBeginnig = true;

            if(trip.startsWith("begin - ")) {
                trip = trip.replace("begin - ", "");
                isBeginnig = true;
            }else if(trip.startsWith("finish - ")) {
                trip = trip.replace("finish - ", "");
                isBeginnig = false;
            }

            int i = 0;

            while(i < tripList.size()){

                if(isBeginnig){

                    if(trip.startsWith(tripList.get(i).getBeginningAddress()))
                        break;

                }else{

                    if(trip.startsWith(tripList.get(i).getEndingAddress()))
                        break;

                }

                i++;
            }

            if(i == tripList.size())
                return false;

            return true;
        }

        public Trip openTrip(String trip){

            if(trip.equals("last")){
                return tripList.get(0);
            }

            if(trip.startsWith("number - ")){
                trip = trip.replace("number - ","");
                return tripList.get(Integer.parseInt(trip));
            }

            boolean isBeginnig = true;

            if(trip.startsWith("begin - ")) {
                trip = trip.replace("begin - ", "");
                isBeginnig = true;
            }else if(trip.startsWith("finish - ")) {
                trip = trip.replace("finish - ", "");
                isBeginnig = false;
            }

            int i = 0;

            while(i < tripList.size()){

                if(isBeginnig){

                    if(trip.startsWith(tripList.get(i).getBeginningAddress()))
                        break;

                }else{

                    if(trip.startsWith(tripList.get(i).getEndingAddress()))
                        break;

                }

                i++;
            }

            if(i == tripList.size())
                return null;

            return tripList.get(i);
        }

    }

    public boolean checkTripExistence(String trip){

        return adapter.checkTripExistence(trip);

    }

    public void openTrip(String trip){

        chosenTrip = adapter.openTrip(trip);

        if(chosenTrip != null) {
            Intent intent = new Intent(commandHandlerManager.getContext(), TripActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            commandHandlerManager.getContext().startActivity(intent);
        }

    }

}
