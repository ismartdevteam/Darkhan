package ismartdev.mn.darkhanwindow.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ismartdev.mn.darkhanwindow.MainActivity;
import ismartdev.mn.darkhanwindow.R;


public class Services {
    public static final String PROPERTY_REG_ID = "registration_id";

    public static void register(final Activity ac, final String username, final String name, final String email, final String password, final String profile_pic_url) {
        final SharedPreferences sp = ac.getSharedPreferences(ac.getPackageName(),
                Context.MODE_PRIVATE);

        StringRequest loginReq = new StringRequest(Request.Method.POST,
                ac.getString(R.string.main_ip) + "register.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String res) {
                Log.e("register res", res.toString());
                try {

                    JSONObject response = new JSONObject(res);
                    if (response.getInt("error_number") == 1000) {
                        SharedPreferences user = ac.getSharedPreferences("user", 1);
                        SharedPreferences.Editor edit = user.edit();
                        edit.putBoolean("isLogin", true);
                        JSONObject userObj = response.getJSONObject("data");
                        edit.putInt("user_id", userObj.getInt("ID"));
                        edit.putString("display_name", userObj.getString("display_name"));
                        edit.putString("user_email", userObj.getString("user_email"));
                        edit.putString("profile_pic_url", userObj.getString("user_url"));

                        edit.commit();
                        ac.finish();
                        Intent intent = new Intent(ac, MainActivity.class);
                        ac.startActivity(intent);
                    } else {
                        Toast.makeText(ac.getApplicationContext(), R.string.error_login_social, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ac.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username + "");
                params.put("password", password + "");
                params.put("display_name", name + "");
                params.put("email", email + "");
                params.put("gcm", sp.getString(PROPERTY_REG_ID, ""));
                params.put("user_url", profile_pic_url + "");

                return params;
            }
        };

        MySingleton.getInstance(ac.getApplicationContext()).addToRequestQueue(loginReq);


    }

    public static void registerGCM(final Activity ac, final String id) {
        if (!Utils.isNetworkAvailable(ac.getApplicationContext())) {
            return;
        }
        StringRequest registerGCMReq = new StringRequest(Request.Method.POST,
                ac.getString(R.string.main_ip) + "registerGCM.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("register res", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("error_number") == 1000) {
                        SharedPreferences sp = ac.getSharedPreferences("user", 1);
                        sp.edit().putBoolean("isregisteredGCM", true).commit();
                    }

                    Log.e("registerd GCM", new JSONObject(response).getString("error_description"));
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

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("gcm", id + "");
                return params;
            }
        };

        MySingleton.getInstance(ac.getApplicationContext()).addToRequestQueue(registerGCMReq);


    }

    public static void login(final Activity ac, final String username, final String password) {
        if (!Utils.isNetworkAvailable(ac.getApplicationContext())) {
            Toast.makeText(ac.getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = ProgressDialog.show(ac,
                "", ac.getString(R.string.loading));
        StringRequest loginReq = new StringRequest(Request.Method.POST,
                ac.getString(R.string.main_ip) + "login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                Log.e("login res", res.toString());

                try {
                    JSONObject response = new JSONObject(res);
                    switch (response.getInt("error_number")) {
                        case 1000:
                            SharedPreferences user = ac.getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = user.edit();
                            edit.putBoolean("isLogin", true);
                            JSONObject userObj = response.getJSONObject("data");
                            edit.putInt("user_id", userObj.getInt("ID"));
                            edit.putString("display_name", userObj.getString("display_name"));
                            edit.putString("user_email", userObj.getString("user_email"));
                            edit.putString("user_url", userObj.getString("user_url"));
                            edit.commit();
                            ac.finish();
                            Intent intent = new Intent(ac, MainActivity.class);
                            ac.startActivity(intent);
                            break;
                        case 1001:
                            Toast.makeText(ac.getApplicationContext(), R.string.user_not_found, Toast.LENGTH_SHORT).show();
                            break;
                        case 1004:
                            Toast.makeText(ac.getApplicationContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ac.getApplicationContext(), R.string.error_login_social, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ac.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username + "");
                params.put("password", password + "");
                return params;
            }
        };

        MySingleton.getInstance(ac.getApplicationContext()).addToRequestQueue(loginReq);


    }


}
