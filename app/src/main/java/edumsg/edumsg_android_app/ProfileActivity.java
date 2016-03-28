package edumsg.edumsg_android_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    public static final String requestUrl = "http://10.0.3.2:8080/";
    private RequestQueue mRequestQueue;
    private static final String TAG = "Request";
    private List<Tweet> tweetObjects;
    private RVAdapter rvAdapter;
    private int userId;
    private int creatorId;
    private ArrayList favorites;
    private ArrayList followers;
    private String username;
    private String avatarUrl;
    private String name;
    private String bioStr;
    private boolean owner;
    private boolean isFollowed;
    private LoadToast loading;
    @Bind(R.id.toolbar_profile) Toolbar toolbar;
    @Bind(R.id.timeline_recycler_view) RecyclerView recyclerView;
    @Bind(R.id.refresh_profile) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.bio) EditText bio;
    @Bind(R.id.done_btn) Button doneBtn;
    @Bind(R.id.profile_layout) RelativeLayout profileLayout;
    @Bind(R.id.avatar) ImageView avatar;
    @Bind(R.id.username_text) TextView usernameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getIntent().getStringExtra("username");
        avatarUrl = getIntent().getStringExtra("avatar_url");
        name = getIntent().getStringExtra("name");
        bioStr = getIntent().getStringExtra("bio");
        userId = getIntent().getIntExtra("userId", -1);
        creatorId = getIntent().getIntExtra("creatorId", -1);
        if (creatorId == userId)
            owner = true;
//        userId = 1;
//        avatarUrl = "http://i.imgur.com/hYg5OfG.jpg";
//        name = "Omar ElHagin";
//        bioStr = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent mollis congue lorem ac dictum. In aliquam ultricies neque in lacinia. Phasellus gravida metus.";
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.menu_main, null
        );
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarLayout);

        ImageButton homeButton = ButterKnife.findById(actionBarLayout, R.id.btn_home);
        ImageButton searchButton = ButterKnife.findById(actionBarLayout, R.id.btn_search);
        ImageButton createButton = ButterKnife.findById(actionBarLayout, R.id.btn_create);
        ImageButton navButton = ButterKnife.findById(actionBarLayout, R.id.btn_nav);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("avatar_url", avatarUrl);
                intent.putExtra("bio", bioStr);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                final EditText input = new EditText(ProfileActivity.this);
                input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                input.setLines(4);
                input.setSingleLine(false);
                input.setBackgroundDrawable(null);
                builder.setView(input);
                builder.setPositiveButton("Tweet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createTweet(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        tweetObjects = new ArrayList<>();
        rvAdapter = new RVAdapter(this, tweetObjects, userId);
        recyclerView.setAdapter(rvAdapter);

        if (!owner) {
            bio.setClickable(false);
            bio.setFocusable(false);
            bio.setFocusableInTouchMode(false);
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (70 * scale + 0.5f);
            ViewGroup.LayoutParams params = bio.getLayoutParams();
            params.height = pixels;
            bio.setLayoutParams(params);
        }
        else {
//            getTimeline();
//            usernameTxt.setText(name);
//            Picasso.with(this).load(avatarUrl).placeholder(R.mipmap.ic_launcher).fit()
//                    .into(avatar);
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String editedBio = bio.getText().toString();
                    updateUser(editedBio);
                }
            });
        }
        getUser();

        bio.setText(bioStr);
        bio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (70 * scale + 0.5f);
                    ViewGroup.LayoutParams params = bio.getLayoutParams();
                    params.height = pixels;
                    bio.setLayoutParams(params);
                    doneBtn.setVisibility(View.VISIBLE);
                } else {
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (100 * scale + 0.5f);
                    ViewGroup.LayoutParams params = bio.getLayoutParams();
                    params.height = pixels;
                    bio.setLayoutParams(params);
                    InputMethodManager imm = (InputMethodManager) profileLayout.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(profileLayout.getWindowToken(), 0);
                    doneBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getUser()
    {
        if (loading == null)
            loading = new LoadToast(this);
        loading.setText("Loading...");
        loading.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "get_user");
        jsonParams.put("user_id", creatorId + "");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
                        Map<String, Object> userMap = mapper
                                .readValue(mapper.writeValueAsString(responseMap.get("user")),
                                        new TypeReference<HashMap<String, Object>>() {
                                        });
                        bio.setText((String) userMap.get("bio"));
                        usernameTxt.setText((String) userMap.get("name"));
                        Picasso.with(ProfileActivity.this).load((String) userMap.get("avatar_url"))
                                .placeholder(R.mipmap.ic_launcher).fit()
                                .into(avatar);
                        getTimeline();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loading.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
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
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
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
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void getTimeline()
    {
        final ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "get_favorites");
        jsonParams.put("user_id", creatorId+"");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest(Request.Method.POST,
                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
                        favorites = (ArrayList) responseMap.get("favorites");
                        Map<String, String> jsonParams = new HashMap<>();
                        jsonParams.put("queue", "USER");
                        jsonParams.put("method", "followers");
                        jsonParams.put("user_id", creatorId+"");
                        JSONObject jsonRequest = new JSONObject(jsonParams);
                        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST,
                                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try
                                {
                                    Map<String, Object> responseMap = mapper
                                            .readValue(response.toString(),
                                                    new TypeReference<HashMap<String, Object>>() {
                                                    });
                                    if (responseMap.get("code").equals("200"))
                                    {
                                        followers = (ArrayList) responseMap.get("followers");
                                        if (!owner)
                                            checkIfFollowed(followers);
                                        Map<String, String> jsonParams = new HashMap<>();
                                        jsonParams.put("queue", "USER");
                                        jsonParams.put("method", "user_tweets");
                                        jsonParams.put("user_id", creatorId+"");
                                        JSONObject jsonRequest = new JSONObject(jsonParams);
                                        JsonObjectRequest jsonObjectRequest4 = new JsonObjectRequest(Request.Method.POST,
                                                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    Map<String, Object> responseMap = mapper
                                                            .readValue(response.toString(),
                                                                    new TypeReference<HashMap<String, Object>>() {
                                                                    });
                                                    if (responseMap.get("code").equals("200"))
                                                    {
                                                        if (!swipeRefreshLayout.isRefreshing())
                                                            loading.success();
                                                        ArrayList tweetsArray = (ArrayList) responseMap.get("tweets");
                                                        final Iterator iterator = tweetsArray.iterator();
                                                        while (iterator.hasNext())
                                                        {
                                                            final Map<String, Object> tweetJsonObj = mapper
                                                                    .readValue(mapper.writeValueAsString(iterator.next()),
                                                                            new TypeReference<HashMap<String, Object>>() {
                                                                            });
                                                            final int tweetId = (int) tweetJsonObj.get("id");
                                                            final LinkedHashMap creatorMap = (LinkedHashMap) tweetJsonObj.get("creator");
                                                            final int creatorId = (int) creatorMap.get("id");
                                                            if (creatorId == userId)
                                                                continue;
                                                            String tweetText = (String) tweetJsonObj.get("tweet_text");
                                                            String avatarUrl = (String) creatorMap.get("avatar_url");
                                                            User creator = new User();
                                                            creator.setId(creatorId);
                                                            creator.setAvatarUrl(avatarUrl);
                                                            final Tweet tweetObject = new Tweet(tweetId, creator, tweetText);
                                                            if (avatarUrl != null && !avatarUrl.equals(""))
                                                            {
                                                                tweetObject.setImgUrl(avatarUrl);
                                                            }
                                                            Iterator favIter = favorites.iterator();
                                                            while (favIter.hasNext())
                                                            {
                                                                Map<String, Object> tweetJsonObj2 = mapper
                                                                        .readValue(mapper.writeValueAsString(favIter.next()),
                                                                                new TypeReference<HashMap<String, Object>>() {
                                                                                });
                                                                if (tweetId == (int) tweetJsonObj2.get("id"))
                                                                {
                                                                    tweetObject.setIsFavorited(true);
                                                                    break;
                                                                }
                                                            }
                                                            tweetObjects.add(tweetObject);
                                                        }
                                                        if (swipeRefreshLayout.isRefreshing())
                                                        {
                                                            swipeRefreshLayout.setRefreshing(false);
                                                        }
                                                        rvAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                loading.error();
                                                error.printStackTrace();
                                            }
                                        }) {
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                HashMap<String, String> headers = new HashMap<String, String>();
                                                headers.put("Content-Type", "application/json; charset=utf-8");
                                                //headers.put("User-agent", System.getProperty("http.agent"));
                                                return headers;
                                            };
                                        };
                                        jsonObjectRequest4.setTag(TAG);
                                        getVolleyRequestQueue().add(jsonObjectRequest4);
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                //headers.put("User-agent", System.getProperty("http.agent"));
                                return headers;
                            };
                        };
                        jsonObjectRequest2.setTag(TAG);
                        getVolleyRequestQueue().add(jsonObjectRequest2);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.error();
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            };
        };
        jsonObjectRequest3.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest3);
    }

    private void createTweet(final String tweet)
    {
        final LoadToast loadToast = new LoadToast(this);
        loadToast.setText("Tweeting...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "message");
        jsonParams.put("tweet_text", tweet);
        jsonParams.put("creator_id", userId + "");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
                        loadToast.success();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
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
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
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
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void updateUser(String bioStr)
    {
        final LoadToast loadToast = new LoadToast(this);
        loadToast.setText("Updating bio...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "update_user");
        jsonParams.put("user_id", userId + "");
        jsonParams.put("bio", bioStr);
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
                        loadToast.success();
                        InputMethodManager imm = (InputMethodManager) profileLayout.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(profileLayout.getWindowToken(), 0);
                        doneBtn.setVisibility(View.GONE);
                        bio.clearFocus();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
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
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
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
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void follow()
    {
        final LoadToast loadToast = new LoadToast(this);
        loadToast.setText("Following...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "follow");
        jsonParams.put("user_id", creatorId + "");
        jsonParams.put("follower_id", userId + "");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
                        loadToast.success();
                        doneBtn.setText("Unfollow");
                        doneBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unfollow();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
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
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
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
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void unfollow()
    {
        final LoadToast loadToast = new LoadToast(this);
        loadToast.setText("Unfollowing...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "unfollow");
        jsonParams.put("user_id", creatorId + "");
        jsonParams.put("follower_id", userId + "");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
                        loadToast.success();
                        doneBtn.setText("Follow");
                        doneBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                follow();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
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
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
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
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void checkIfFollowed(ArrayList followersList)
    {
        Iterator iterator = followersList.iterator();
        while (iterator.hasNext())
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                final Map<String, Object> userObj = mapper
                        .readValue(mapper.writeValueAsString(iterator.next()),
                                new TypeReference<HashMap<String, Object>>() {
                                });
                String usernameInMap = (String) userObj.get("username");
                if (username.equals(usernameInMap))
                {
                    isFollowed = true;
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (!isFollowed)
        {
            doneBtn.setText("Follow");
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow();
                }
            });
        }
        else
        {
            doneBtn.setText("Unfollow");
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unfollow();
                }
            });
        }
        doneBtn.setVisibility(View.VISIBLE);
    }

    /**
     * Returns a Volley request queue for creating network requests
     *
     * @return {@link com.android.volley.RequestQueue}
     */
    public RequestQueue getVolleyRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(this);
        }

        return mRequestQueue;
    }

    /**
     * Cancels all the request in the Volley queue for a given tag
     *
     * @param tag associated with the Volley requests to be cancelled
     */
    public void cancelAllRequests(String tag)
    {
        if (getVolleyRequestQueue() != null)
        {
            getVolleyRequestQueue().cancelAll(tag);
        }
    }
}
