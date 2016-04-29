package ismartdev.mn.darkhanwindow.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ismartdev.mn.darkhanwindow.MainActivity;
import ismartdev.mn.darkhanwindow.R;
import ismartdev.mn.darkhanwindow.model.Category;
import ismartdev.mn.darkhanwindow.model.Places;
import ismartdev.mn.darkhanwindow.util.MySingleton;
import ismartdev.mn.darkhanwindow.util.Utils;


public class HomeFragment extends Fragment {


    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    List<Category> categoryList = new ArrayList<>();

    public HomeFragment() {

    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        mPager = (ViewPager) view.findViewById(R.id.home_pager);
        getCategory();
        return view;
    }

    private void getCategory() {
        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                "", getString(R.string.loading));
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
                            makePager();
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
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("error", error.getMessage() + "");
                progressDialog.dismiss();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(loginReq);
    }

    private void makePager() {
        mPagerAdapter = new ScreenSlidePagerAdapter(((AppCompatActivity) getActivity()).getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(10);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

//            return ItemFragment.newInstance(categoryList.get(position).getTerm_taxonomy_id());
            return null;
        }

        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoryList.get(position).getName() + " ";
        }


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


}
