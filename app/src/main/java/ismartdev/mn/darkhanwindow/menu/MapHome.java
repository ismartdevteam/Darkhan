package ismartdev.mn.darkhanwindow.menu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ismartdev.mn.darkhanwindow.R;
import ismartdev.mn.darkhanwindow.model.Places;
import ismartdev.mn.darkhanwindow.util.MySingleton;
import ismartdev.mn.darkhanwindow.util.Utils;

public class MapHome extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private List<Places> places = new ArrayList<>();
    private SupportMapFragment fragment;
    // TODO: Rename parameter arguments, choose names that match


    public MapHome() {

    }


    public static MapHome newInstance() {
        MapHome fragment = new MapHome();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setHasOptionsMenu(true);

    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            fragment = ((SupportMapFragment)

                    getChildFragmentManager().findFragmentById(R.id.map));
            fragment.getMapAsync(this);

            // Check if we were successful in obtaining the map.

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home_map, container, false);

        return v;
    }


    private void getData() {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("url", getString(R.string.main_ip) + "getPlaces.php");
        JsonObjectRequest loginReq = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.main_ip) + "getPlaces.php", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("map res", response.toString());
                try {
                    switch (response.getInt("error_number")) {
                        case 1000:

                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                Places place = new Places();
                                JSONObject item = data.getJSONObject(i);

                                place.setId(item.getInt("ID"));
                                place.setPost_date(item.getString("post_date"));
                                place.setPost_content(item.getString("post_content"));
                                place.setPost_title(item.getString("post_title"));
                                place.setComment_status(item.getString("comment_status"));
                                place.setComment_count(item.getInt("comment_count"));
                                place.setLng(item.getDouble("lng"));
                                place.setLat(item.getDouble("lat"));
                                if(!item.isNull("rating_average"))
                                    place.setRating_average(item.getDouble("rating_average"));
                                if(!item.isNull("rating_count"))
                                    place.setRating_count(item.getInt("rating_count"));
                                place.setItem_phone(item.getString("item_phone"));
                                place.setItem_address(item.getString("item_address"));
                                if(!item.isNull("detail_images"))
                                    place.setDetail_images(item.getJSONArray("detail_images").toString());
                                place.setThumbnail_id(item.getString("thumbnail_id"));
                                if(!item.isNull("videoID"))
                                    place.setVideoID(item.getString("videoID"));
                                place.setWebsite(item.getString("website"));
                                place.setEmail(item.getString("email"));
                                place.setDistance(item.getDouble("distance"));
                                places.add(place);
                                LatLng placeLatLng = new LatLng(place.getLat(), place.getLng());

                                // create marker
                                MarkerOptions marker = new MarkerOptions().position(placeLatLng);
                                // ROSE color icon
                                marker.icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                marker.title(place.getPost_title());

                                // adding marker
                                mMap.addMarker(marker);

                            }

                            break;
                        case 1001:
                            Toast.makeText(getActivity(), R.string.no_data, Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(getActivity(), R.string.error_request, Toast.LENGTH_SHORT).show();
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("error", error.getMessage() + "");
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_list) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction().replace(R.id.main_frame, ItemFragment.newInstance()).commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        getData();

    }
}
