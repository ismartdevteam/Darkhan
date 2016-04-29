package ismartdev.mn.darkhanwindow.menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.cache.DiskLruBasedCache;
import com.android.volley.cache.plus.SimpleImageLoader;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.ui.NetworkImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ismartdev.mn.darkhanwindow.R;
import ismartdev.mn.darkhanwindow.model.Category;
import ismartdev.mn.darkhanwindow.model.DatabaseHelper;
import ismartdev.mn.darkhanwindow.model.Location;
import ismartdev.mn.darkhanwindow.model.Places;
import ismartdev.mn.darkhanwindow.text.Bold;
import ismartdev.mn.darkhanwindow.text.Light;
import ismartdev.mn.darkhanwindow.text.Regular;
import ismartdev.mn.darkhanwindow.util.MySingleton;
import ismartdev.mn.darkhanwindow.util.OnLoadMoreListener;
import ismartdev.mn.darkhanwindow.util.Utils;

public class ItemFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private List<Places> places = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyItemRecyclerViewAdapter adapter;
    private int index = 0;
    List<Category> categoryList = new ArrayList<>();
    List<Location> locationList = new ArrayList<>();
    private String catR = "";
    private String locR = "";
    private String searchR = "";
    private String distance = "";
    private GoogleApiClient mGoogleApiClient;
    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    private LocationRequest mLocationRequest;
    private android.location.Location mBestReading;
    private DatabaseHelper helper;

    public ItemFragment() {
    }

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();

        return fragment;
    }

    private void getCategories() {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest loginReq = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.main_ip) + "getTerms.php?name=item_category", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("login res", response.toString());
                try {
                    switch (response.getInt("error_number")) {
                        case 1000:
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                Category category = new Category();
                                JSONObject item = data.getJSONObject(i);

                                category.setTerm_id(item.getInt("term_id"));
                                category.setName(item.getString("name"));
                                category.setTerm_taxonomy_id(item.getInt("term_taxonomy_id"));
                                categoryList.add(category);

                            }
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

            }

        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
    }

    private void getLocations() {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest loginReq = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.main_ip) + "getTerms.php?name=item_location", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("login res", response.toString());
                try {
                    switch (response.getInt("error_number")) {
                        case 1000:
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                Location location = new Location();
                                JSONObject item = data.getJSONObject(i);

                                location.setTerm_id(item.getInt("term_id"));
                                location.setName(item.getString("name"));
                                location.setTerm_taxonomy_id(item.getInt("term_taxonomy_id"));
                                locationList.add(location);

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

            }

        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private android.location.Location bestLastKnownLocation(float minAccuracy, long minTime) {
        android.location.Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        android.location.Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        } else {
            return bestResult;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        helper = new DatabaseHelper(getActivity());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction().replace(R.id.main_frame, MapHome.newInstance()).commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));


        }
        return view;
    }

    private void getData(final int status, final String cat_id, final String searchQ, final String location_id) {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        if (cat_id != null) {
            catR = "&cat_id=" + cat_id;
        }

        if (location_id != null) {
            locR = "&loc_id=" + location_id;
        }

        if (searchQ != null) {
            searchR = "&search=" + searchQ;
        }

        if (mBestReading != null) {
            distance = "&lat=" + mBestReading.getLatitude() + "&lng=" + mBestReading.getLongitude();
        }

        String url = getString(R.string.main_ip) + "getPlaces.php?index=" + index + catR + locR + searchR + distance;
        Log.e("url", url);
        JsonObjectRequest loginReq = new JsonObjectRequest(Request.Method.GET,
                url.trim(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    Log.e("login res", response.toString());
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
                                if (!item.isNull("rating_average"))
                                    place.setRating_average(item.getDouble("rating_average"));
                                if (!item.isNull("rating_count"))
                                    place.setRating_count(item.getInt("rating_count"));
                                place.setItem_phone(item.getString("item_phone"));
                                place.setItem_address(item.getString("item_address"));
                                if (!item.isNull("detail_images"))
                                    place.setDetail_images(item.getJSONArray("detail_images").toString());
                                place.setThumbnail_id(item.getString("thumbnail_id"));
                                if (!item.isNull("videoID"))
                                    place.setVideoID(item.getString("videoID"));
                                place.setWebsite(item.getString("website"));
                                place.setEmail(item.getString("email"));
                                place.setDistance(item.getDouble("distance"));
                                places.add(place);

                            }
                            index = index + 10;
                            if (status == 0)
                                setAdapter(response.getInt("number_row"));
                            else
                                refreshAdapter(response.getInt("number_row"));


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
                Log.i("error", error + "");
                Toast.makeText(getActivity(), error.getMessage() + "", Toast.LENGTH_SHORT).show();
            }

        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
    }

    private void refreshAdapter(int number_row) {
        places.remove(places.size() - 1);
        adapter.notifyItemRemoved(places.size());
        adapter.notifyDataSetChanged();
        adapter.setLoaded();
        if (number_row < 10) {
            adapter.setOnLoadMoreListener(null);
        }
    }

    private void setAdapter(int row) {
        adapter = new MyItemRecyclerViewAdapter(places);
        recyclerView.setAdapter(adapter);
        if (row > 10)
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    places.add(null);
                    adapter.notifyItemInserted(places.size() - 1);
                    getData(1, null, null, null);
                }
            });
    }

    @Override
    public void onStart() {
        Log.e("onstart", "yes");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

        if (null == mBestReading
                || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
                || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN) {

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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        }

        getData(0, null, null, null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("locaiton", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        Log.e("location changed", location.toString());
        if (null == mBestReading || location.getAccuracy() < mBestReading.getAccuracy()) {
            mBestReading = location;

            if (mBestReading.getAccuracy() < MIN_ACCURACY) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }

    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public NetworkImageView image;
        public Bold title;
        public RatingBar bar;
        public Light review;
        public Regular address;
        public Light distance;
        public ImageView location;
        public ImageView save;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (Bold) itemView.findViewById(R.id.post_title);

            image = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
            location = (ImageView) itemView.findViewById(R.id.item_location);
            save = (ImageView) itemView.findViewById(R.id.item_save);
            bar = (RatingBar) itemView.findViewById(R.id.item_rating);
            review = (Light) itemView.findViewById(R.id.post_review);
            address = (Regular) itemView.findViewById(R.id.item_address);
            distance = (Light) itemView.findViewById(R.id.item_distance);

        }

        @Override
        public void onClick(View v) {

            Log.d("dasdas", "onClick " + getAdapterPosition());
        }
    }

    class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<Places> mValues;
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private final ImageLoader mImageLoader;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public MyItemRecyclerViewAdapter(List<Places> items) {
            mImageLoader = MySingleton.getInstance(getActivity()).getImageLoader();
            mValues = items;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof PlaceViewHolder) {
                final Places place = mValues.get(position);
                final PlaceViewHolder hol = (PlaceViewHolder) holder;
                hol.title.setText(place.getPost_title());
                hol.review.setText(place.getRating_count() + " Reviews");
                hol.bar.setRating((float) place.getRating_average());
                hol.address.setText(place.getItem_address());
                hol.distance.setText("Зай " + place.getDistance() + " км");

                hol.image.setImageUrl("http://urbancity.mn/window/wp-content/uploads/" + place.getThumbnail_id().replace(".", "-400x219."), mImageLoader);
                hol.image.setDefaultImageResId(R.drawable.no_place);

                hol.location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("location butnn", "yes");
                    }
                });
                boolean saved = (boolean) hol.save.getTag(1);
                if (saved) {
                    hol.save.setImageResource(R.drawable.fav_sel);
                }
                hol.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            helper.getPlaces().create(place);
                            Toast.makeText(getActivity(), place.getPost_title() + " " + getString(R.string.success_saved), Toast.LENGTH_LONG).show();
                            hol.save.setImageResource(R.drawable.fav_sel);
                            hol.save.setTag(1, true);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });


            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return mValues == null ? 0 : mValues.size();
        }

        public void setLoaded() {
            isLoading = false;
        }

        @Override
        public int getItemViewType(int position) {
            return mValues.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_place_item, parent, false);
                return new PlaceViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }
    }

}
