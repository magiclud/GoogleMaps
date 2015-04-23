package com.example.aga.mymaps;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

  private GoogleMap mMap ;

    private Spinner spinner;
    private ArrayList<String> miasta;
    private ArrayList<LatLng> wspolrzedne;
    private ArrayList<Polyline> linieNaMapie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        wczytajDaneZPliku();
    }

    private void wczytajDaneZPliku() {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        miasta = new ArrayList<String>();
        wspolrzedne = new ArrayList<LatLng>();
        try {

            inputStream = getResources().openRawResource(R.raw.miasta);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String linia;

            while ((linia = bufferedReader.readLine()) != null) {
                String[] wczytaneDane = linia.split(",");
                miasta.add(wczytaneDane[0]);
                wspolrzedne.add(new LatLng(Double.parseDouble( wczytaneDane[1]), Double.parseDouble( wczytaneDane[2])));
            }
        } catch(IOException e) {
            Log.e("IOException : ", e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    bufferedReader.close();
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("IOException : ", e.getMessage());
                }
            }
        }
    }


    @Override
    protected void onResume() {
        Log.d("Cykl zycia:", "onResume");
        super.onResume();

    }

    /**
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapLoadedCallback(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("Cykl zycia ", "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        spinner = (Spinner) menu.findItem(R.id.action_settings).getActionView();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
                miasta);
        // wyswietlenie okreslonego widoku - nazwy miast
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Wybrano pozycje:  " , String.valueOf(position));
                aktualizujPolozenieKamery(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    private void aktualizujPolozenieKamery(int position) {
        int zoom = 10;
        LatLng latLang = wspolrzedne.get(position);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, zoom));
    }

    @Override
    public void onMapLoaded() {
        for(int i=0; i<miasta.size(); i++){
            mMap.addMarker(new MarkerOptions().position(wspolrzedne.get(i)).title(miasta.get(i)));//ustawiam marker na kazdym miescie
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wspolrzedne.get(0),7));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            Marker wcisnietyMarker;

            public boolean onMarkerClick(Marker marker2) {
                if (wcisnietyMarker != null) {
                    if (linieNaMapie == null) {
                        linieNaMapie = new ArrayList<Polyline>();
                    }
                    if (wcisnietyMarker != marker2) {
                        Polyline nowaLinia = mMap.addPolyline(new PolylineOptions().add(wcisnietyMarker.getPosition()).add(marker2.getPosition()).color(Color.RED));
                        linieNaMapie.add(nowaLinia);
                    }
                }
                wcisnietyMarker = marker2;

                return true;
            }
        });
    }

    public void usunLinie(View view){
        if(linieNaMapie != null){
            for (int i=0; i<linieNaMapie.size(); i++){
                linieNaMapie.get(i).remove();
            }
            linieNaMapie = null;
        }
    }
//    @Override
//    public void onMapClick(LatLng latLng) {
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("Marker"));
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d("Cykl zycia ", "onOptionItemSelected");
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}
