package com.naman14.fieldmapview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 18/04/15.
 */
public class DistrictsFragment extends Fragment {

    private GoogleMap mMap;
    private int resultCode;
    private RecyclerView mRecyclerView;
    DistrictAdapter myAdapter;
    Spinner spinner;
    Toolbar toolbar;


    ArrayList<DistrictData> listdistricts = new ArrayList<DistrictData>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View v = inflater.inflate(R.layout.fragment_maps, container, false);

        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        mRecyclerView=(RecyclerView) v.findViewById(R.id.recycler_view);
        spinner=(Spinner)getActivity().findViewById(R.id.spinner_nav);
        spinner.setVisibility(View.VISIBLE);
        final List<String> list = new ArrayList<String>();
        list.add("ARUNACHAL PRADESH");
        list.add("PUDUCHERRY");
        list.add("JHARKHAND");list.add("HARYANA");list.add("MANIPUR");list.add("GOA");list.add("MEGHALAYA");
        list.add("CHHATTISGARH");list.add("LAKSHADWEEP");list.add("KERALA");list.add("TAMIL NADU");
        list.add("RAJASTHAN");list.add("DELHI");list.add("UTTAR PRADESH");list.add("NAGALAND");list.add("MAHARASHTRA");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (listdistricts!=null){
                    listdistricts.clear();
                }
                fetchData(list.get(i));


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        toolbar=(Toolbar) getActivity().findViewById(R.id.toolbar);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setHasFixedSize(true);

        resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(resultCode != ConnectionResult.SUCCESS)
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 69);
            dialog.setCancelable(true);

            dialog.show();
        }



        return v;
    }


    public void fetchData(String StateName){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "District2013");
        query.whereMatches("statename",StateName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                doneFetching(parseObjects);
            }
        });
    }



    public void doneFetching(List<ParseObject> objects) {

            mMap.clear();
        for (ParseObject item : objects) {

            DistrictData itemData = new DistrictData();


            itemData.statename= item.getString("statename");
            itemData.imageUrl=    R.color.colorAccent;
            itemData.distname= item.getString("distname");


            try {
                itemData.latlong = "" + item.getParseGeoPoint("lat").getLatitude() + "," + item.getParseGeoPoint("lat").getLongitude();
                addToMap(itemData.getLatlong().toString(), itemData.getDistname());
            }
            catch (NullPointerException e){

            }

            listdistricts.add(itemData);
        }

        myAdapter=new DistrictAdapter(getActivity(),listdistricts);

        mRecyclerView.setAdapter(myAdapter);

    }

    public class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.ViewHolder> {
        ArrayList<DistrictData> itemsData;
        private Context context;

        private int lastPosition = -1;

        public DistrictAdapter(Context context, ArrayList<DistrictData> itemsData) {
            this.itemsData = itemsData;
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_district, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData
            setAnimation(viewHolder.itemView, position);

            viewHolder.disname.setText(itemsData.get(position).getDistname());
            viewHolder.imgViewIcon.setImageResource(itemsData.get(position).getImageUrl());
            viewHolder.state.setText(itemsData.get(position).getStatename());

            viewHolder.imgViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });



        }

        // inner class to hold a reference to each item of RecyclerView
        public  class ViewHolder extends RecyclerView.ViewHolder {

            public TextView disname, state;
            public ImageView imgViewIcon;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                disname = (TextView) itemLayoutView.findViewById(R.id.item_title);
                imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
                state = (TextView) itemLayoutView.findViewById(R.id.item_block);
            }
        }


        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemsData.size();
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

    }

    private void addToMap(String latlong,String title){



        MarkerOptions markerOptions;
        LatLng position;
        String lati=latlong.substring(0,latlong.indexOf(",")),longi=latlong.substring(latlong.indexOf(",")+1,latlong.length());

        markerOptions = new MarkerOptions();


        position = new LatLng(Double.parseDouble(lati), Double.parseDouble(longi));
        markerOptions.position(position);
        markerOptions.title(title);
        mMap.addMarker(markerOptions);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(position, 6.0f);


        mMap.animateCamera(cameraPosition);

    }


}

