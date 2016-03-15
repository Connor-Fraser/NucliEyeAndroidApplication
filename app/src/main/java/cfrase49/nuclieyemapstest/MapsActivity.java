package cfrase49.nuclieyemapstest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import cfrase49.nuclieyemapstest.APICalls.Get.APIGetCall;
import cfrase49.nuclieyemapstest.APICalls.Get.APIResult;
import cfrase49.nuclieyemapstest.APICalls.Get.MachineCallResults;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<Marker, Integer> machineMarkerMap;
    private Marker lastClickedMarker;
    public static String SELECTED_MACHINE = "com.nuclieye.selectedmachine.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MachineCallResults getMachines = new MachineCallResults();
        APIGetCall getMachinesCall = new MapsAPIGetCall(getMachines, getApplicationContext());
        getMachinesCall.execute(getMachines.url);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0.equals(lastClickedMarker)) {
                    lastClickedMarker = null;
                    int machineID = machineMarkerMap.get(arg0);
                    Intent intent = new Intent(getApplicationContext(), MachineDataActivity.class);
                    intent.putExtra(SELECTED_MACHINE, machineID);
                    startActivity(intent);
                }
                else {
                    lastClickedMarker = arg0;
                    arg0.showInfoWindow();
                }
                return true;
            }
        });
    }


    private class MapsAPIGetCall extends APIGetCall {

       public MapsAPIGetCall(APIResult result, Context context){super(result, context);}
        @Override
        protected void onPostExecute(APIResult result) {
            //populate map with accessible machines
            MachineCallResults machines = (MachineCallResults)result;
            machineMarkerMap = new HashMap<Marker, Integer>();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for(MachineCallResults.Machine machine: machines.machines){
                LatLng tempLtLng = new LatLng(machine.lat, machine.lon);
                Marker temp = mMap.addMarker(new MarkerOptions()
                        .position(tempLtLng)
                        .title("Machine " + machine.machineID));

                builder.include(temp.getPosition());
                machineMarkerMap.put(temp, machine.machineID);
            }

            LatLngBounds bounds = builder.build();
            int padding = 250; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }
}
