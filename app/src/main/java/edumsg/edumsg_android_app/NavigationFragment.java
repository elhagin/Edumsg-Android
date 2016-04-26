package edumsg.edumsg_android_app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigationFragment extends Fragment{

    @Bind(R.id.profile_button) Button profileBtn;
    @Bind(R.id.msgs_button) Button msgsBtn;
    @Bind(R.id.lists_button) Button listsBtn;
    @Bind(R.id.logout_button) Button logoutBtn;
    private MyAppCompatActivity parentActivity;

    public NavigationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.bind(this, view);
        parentActivity = (MyAppCompatActivity) getActivity();

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentActivity, ProfileActivity.class);
                intent.putExtra("username", parentActivity.getUsername());
                intent.putExtra("name", parentActivity.getName());
                intent.putExtra("avatar_url", parentActivity.getAvatarUrl());
                intent.putExtra("bio", parentActivity.getBio());
                intent.putExtra("sessionId", parentActivity.getSessionId());
                intent.putExtra("creatorId", -2);
                startActivity(intent);
                parentActivity.getSupportFragmentManager().popBackStack();
            }
        });
        msgsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentActivity, MessagesActivity.class);
                intent.putExtra("username", parentActivity.getUsername());
                intent.putExtra("name", parentActivity.getName());
                intent.putExtra("avatar_url", parentActivity.getAvatarUrl());
                intent.putExtra("bio", parentActivity.getBio());
                intent.putExtra("sessionId", parentActivity.getSessionId());
                startActivity(intent);
                parentActivity.getSupportFragmentManager().popBackStack();
            }
        });
        listsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parentActivity, ListsActivity.class);
                intent.putExtra("username", parentActivity.getUsername());
                intent.putExtra("name", parentActivity.getName());
                intent.putExtra("avatar_url", parentActivity.getAvatarUrl());
                intent.putExtra("bio", parentActivity.getBio());
                intent.putExtra("userId", parentActivity.getUserId());
                startActivity(intent);
                parentActivity.getSupportFragmentManager().popBackStack();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    private void logout() {
        final LoadToast loadToast = new LoadToast(getContext());
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.setText("Signing out...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "logout");
        jsonParams.put("user_id", parentActivity.getUserId() + "");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MyAppCompatActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200")) {
                        loadToast.success();
                        Intent intent = new Intent(parentActivity, AuthActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        parentActivity.finish();
                    }
                } catch (Exception e) {
                    loadToast.error();
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400) {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
                        Log.e("Error from server", error);
//                        if (error.toLowerCase().contains("username"))
//                        {
//                            mUsername.setError(error);
//                            mUsername.requestFocus();
//                        }
//                        if (error.toLowerCase().contains("password")) {
//                            mPassword.setError(error);
//                            mPassword.requestFocus();
//                        }
                    } catch (JSONException e) {
                        loadToast.error();
                        Log.e("Response Error Msg", e.getMessage());
                    }
                } else {
                    Log.e("Volley", volleyError.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }

            ;
        };
//        jsonObjectRequest.setRetryPolicy(new_user DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(parentActivity.TAG);
        parentActivity.getVolleyRequestQueue().add(jsonObjectRequest);
    }
}
