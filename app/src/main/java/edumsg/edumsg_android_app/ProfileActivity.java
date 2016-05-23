package edumsg.edumsg_android_app;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import edumsg.edumsg_android_app.EditProfileFragment.OnInfoEditedListener;

/**
 * Contains a user's image, bio, and timeline. The timeline contains all tweets and retweets
 * created by the user.
 */
public class ProfileActivity extends MyAppCompatActivity implements OnInfoEditedListener {

    /**
     * tweetObjects: A {@link List} of type {@link Tweet} which represents the tweets in the timeline.
     * rvAdapter: An instance of the class {@link RVAdapter} which is a custom made RecyclerView
     * Adapter to display each tweet's view.
     * creatorId: The ID of the user that the profile belongs to. If a value of -2 is found then
     * the profile of the current logged in user is fetched, otherwise the profile of the user
     * requested is fetched, which can be accomplished by clicking on any user's profile picture
     * in the news feed.
     * favorites: An {@link ArrayList} that contains all the current logged in user's favorites' IDs.
     * It is used to set the correct button states for previously favorited tweets.
     * followings: An {@link ArrayList} that contains all the user's followings. This is used to check if
     * this user is already followed by the logged in user, and sets the follow/unfollow button's state appropriately.
     * owner: A boolean value indicating whether this profile belongs to the logged in user or not.
     * isFollowed: A boolean value indicating whether this user is followed by the logged in user or not.
     * profileUsername: The username that belongs to the user who is the owner of the current profile.
     */
    private List<Tweet> tweetObjects;
    private RVAdapter rvAdapter;
    private int creatorId;
    private ArrayList favorites;
    private ArrayList followings;
    private boolean owner;
    private boolean isFollowed;
    private LoadToast loading;
    private User user;
    private String profileUsername;
    @Bind(R.id.toolbar_profile) Toolbar toolbar;
    @Bind(R.id.timeline_recycler_view) RecyclerView recyclerView;
    @Bind(R.id.refresh_profile) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.bio) EditText bioEditText;
    @Bind(R.id.done_btn) Button doneBtn;
    @Bind(R.id.profile_layout) RelativeLayout profileLayout;
    @Bind(R.id.avatar) ImageView avatar;
    @Bind(R.id.username_text) TextView usernameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileUsername = getIntent().getStringExtra("username");
        creatorId = getIntent().getIntExtra("creatorId", -1);
        if (creatorId == -2)
            owner = true;
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        tweetObjects = new ArrayList<>();
        rvAdapter = new RVAdapter(this, tweetObjects, sessionId);
        recyclerView.setAdapter(rvAdapter);

        if (!owner) {
            bioEditText.setClickable(false);
            bioEditText.setFocusable(false);
            bioEditText.setFocusableInTouchMode(false);
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (70 * scale + 0.5f);
            ViewGroup.LayoutParams params = bioEditText.getLayoutParams();
            params.height = pixels;
            bioEditText.setLayoutParams(params);
        }
        else {
//            getTimeline();
//            usernameTxt.setText(name);
//            Picasso.with(this).load(avatarUrl).placeholder(R.mipmap.ic_launcher).fit()
//                    .into(avatar);
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String editedBio = bioEditText.getText().toString();
                    updateUser(editedBio);
                }
            });
        }
        getUser();

        bioEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (70 * scale + 0.5f);
                    ViewGroup.LayoutParams params = bioEditText.getLayoutParams();
                    params.height = pixels;
                    bioEditText.setLayoutParams(params);
                    doneBtn.setVisibility(View.VISIBLE);
                } else {
                    final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (100 * scale + 0.5f);
                    ViewGroup.LayoutParams params = bioEditText.getLayoutParams();
                    params.height = pixels;
                    bioEditText.setLayoutParams(params);
                    InputMethodManager imm = (InputMethodManager) profileLayout.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(profileLayout.getWindowToken(), 0);
                    doneBtn.setVisibility(View.GONE);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetObjects.clear();
                getTimeline();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (owner) {
            inflater.inflate(R.menu.menu_profile, menu);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!owner)
            return true;
        switch (item.getItemId()) {
            case R.id.edit_profile_btn:
                final FragmentManager fragmentManager = getSupportFragmentManager();
                final EditProfileFragment editProfileFragment = new EditProfileFragment();
                final Bundle bundle = new Bundle();
                Map<String, String> jsonParams = new HashMap<>();
                jsonParams.put("queue", "USER");
                jsonParams.put("method", "get_user2");
                jsonParams.put("username", username);
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
                            if (responseMap.get("code").equals("200")
                                    && responseMap.get("method").equals("get_user2"))
                            {
                                user = mapper.readValue(response.get("user").toString(),
                                        new TypeReference<User>() {

                                        });
                                bundle.putParcelable("user", user);
                                editProfileFragment.setArguments(bundle);
                                fragmentManager.beginTransaction()
                                        .add(android.R.id.content, editProfileFragment).addToBackStack("edit")
                                        .commit();
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
                    }
                };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                jsonObjectRequest.setTag(TAG);
                getVolleyRequestQueue().add(jsonObjectRequest);
                break;
            case R.id.home_btn:
                finish();
                break;
        }

        return true;
    }
//1

    private void getUser()
    {
        if (loading == null)
            loading = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loading.setTranslationY(pixels);
        loading.setText("Loading...");
        loading.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        if (owner) {
            jsonParams.put("method", "my_profile");
            jsonParams.put("session_id", sessionId);
        }
        else {
            jsonParams.put("method", "get_user");
            jsonParams.put("user_id", creatorId + "");
        }
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
                        user = mapper.readValue(response.get("user").toString(),
                                new TypeReference<User>() {

                                });
//                        Map<String, Object> userMap = mapper
//                                .readValue(mapper.writeValueAsString(responseMap.get("user")),
//                                        new_user TypeReference<HashMap<String, Object>>() {
//                                        });
                        bioEditText.setText(user.getBio());
                        usernameTxt.setText(user.getName());
                        Picasso.with(ProfileActivity.this).load(user.getAvatar_url())
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void getTimeline()
    {
        final ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "get_favorites");
        jsonParams.put("session_id", sessionId);
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
                        jsonParams.put("method", "following");
                        jsonParams.put("session_id", sessionId);
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
                                        followings = (ArrayList) responseMap.get("following");
                                        if (!owner)
                                            checkIfFollowed(followings);
                                        Map<String, String> jsonParams = new HashMap<>();
                                        jsonParams.put("queue", "USER");
                                        if (owner)
                                        {
                                            jsonParams.put("method", "user_tweets");
                                            jsonParams.put("session_id", sessionId);
                                        }
                                        else
                                        {
                                            jsonParams.put("method", "user_tweets2");
                                            jsonParams.put("username", profileUsername);
                                        }
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
                                                            String tweetText = (String) tweetJsonObj.get("tweet_text");
                                                            String avatarUrl = (String) creatorMap.get("avatar_url");
                                                            User creator = new User();
                                                            creator.setId(creatorId);
                                                            creator.setName((String) creatorMap.get("name"));
                                                            creator.setUsername((String) creatorMap.get("username"));
                                                            creator.setAvatar_url(avatarUrl);
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
                                                            rvAdapter.notifyDataSetChanged();
                                                            swipeRefreshLayout.setRefreshing(false);
                                                        }
                                                        else
                                                        {
                                                            rvAdapter.notifyItemRangeInserted(0, tweetObjects.size());
                                                        }
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    if (loading != null)
                                                        loading.error();
                                                    else
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                if (loading != null)
                                                    loading.error();
                                                else
                                                    swipeRefreshLayout.setRefreshing(false);
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
                                        jsonObjectRequest4.setRetryPolicy(new DefaultRetryPolicy(10000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        getVolleyRequestQueue().add(jsonObjectRequest4);
                                    }
                                }
                                catch (Exception e)
                                {
                                    if (loading != null)
                                        loading.error();
                                    else
                                        swipeRefreshLayout.setRefreshing(false);
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (loading != null)
                                    loading.error();
                                else
                                    swipeRefreshLayout.setRefreshing(false);
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
                        jsonObjectRequest2.setRetryPolicy(new DefaultRetryPolicy(10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        getVolleyRequestQueue().add(jsonObjectRequest2);
                    }
                }
                catch (Exception e)
                {
                    if (loading != null)
                        loading.error();
                    else
                        swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (loading != null)
                    loading.error();
                else
                    swipeRefreshLayout.setRefreshing(false);
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
        jsonObjectRequest3.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getVolleyRequestQueue().add(jsonObjectRequest3);
    }

//    private void createTweet(final String tweet)
//    {
//        final LoadToast loadToast = new LoadToast(this);
//        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
//        int pixels = (int) (56 * scale + 0.5f);
//        loadToast.setTranslationY(pixels);
//        loadToast.setText("Tweeting...");
//        loadToast.show();
//        Map<String, String> jsonParams = new HashMap<>();
//        jsonParams.put("queue", "TWEET");
//        jsonParams.put("method", "tweet");
//        jsonParams.put("tweet_text", tweet);
//        jsonParams.put("session_id", sessionId + "");
//        JSONObject jsonRequest = new JSONObject(jsonParams);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(final JSONObject response) {
//                final ObjectMapper mapper = new ObjectMapper();
//                try {
//                    final Map<String, Object> responseMap = mapper
//                            .readValue(response.toString(),
//                                    new TypeReference<HashMap<String, Object>>() {
//                                    });
//                    if (responseMap.get("code").equals("200"))
//                    {
//                        if (owner)
//                        {
//                            User creator = new User();
//                            creator.setSession_id(sessionId);
//                            creator.setUsername(username);
//                            creator.setAvatar_url(avatarUrl);
//                            creator.setName(name);
//                            creator.setBio(bio);
//                            Tweet t = new Tweet((int) responseMap.get("id"), creator, tweet);
//                            tweetObjects.add(0, t);
//                            rvAdapter.notifyItemInserted(0);
//                        }
//                        loadToast.success();
//                    }
//                }
//                catch (Exception e)
//                {
//                    Log.e("JSONMapper", e.getMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                loadToast.error();
//                if (volleyError.networkResponse != null
//                        && volleyError.networkResponse.data != null
//                        && volleyError.networkResponse.statusCode == 400)
//                {
//                    try {
//                        String errorJson = new String(volleyError.networkResponse.data);
//                        JSONObject errorObj = new JSONObject(errorJson);
//                        String error = errorObj.getString("message");
//                        Log.e("Error from server", error);
////                        if (error.toLowerCase().contains("username"))
////                        {
////                            mUsername.setError(error);
////                            mUsername.requestFocus();
////                        }
////                        if (error.toLowerCase().contains("password")) {
////                            mPassword.setError(error);
////                            mPassword.requestFocus();
////                        }
//                    }
//                    catch (JSONException e)
//                    {
//                        Log.e("Response Error Msg", e.getMessage());
//                    }
//                }
//                else {
//                    Log.e("Volley", volleyError.toString());
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                //headers.put("User-agent", System.getProperty("http.agent"));
//                return headers;
//            };
//        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        jsonObjectRequest.setTag(TAG);
//        getVolleyRequestQueue().add(jsonObjectRequest);
//    }

    private void updateUser(String bioStr)
    {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.setText("Updating bio...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "update_user");
        jsonParams.put("session_id", sessionId);
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
                        bioEditText.clearFocus();
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void follow()
    {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.setText("Following...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "follow");
        jsonParams.put("session_id", sessionId);
        jsonParams.put("followee_id", creatorId + "");
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void unfollow()
    {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.setText("Unfollowing...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "USER");
        jsonParams.put("method", "unfollow");
        jsonParams.put("session_id", sessionId);
        jsonParams.put("followee_id", creatorId + "");
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                if (profileUsername.equals(usernameInMap))
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

    @Override
    public void onInfoEdited(User user) {
        if (user.getName() != null)
            usernameTxt.setText(user.getName());
        if (user.getAvatar_url() != null)
            Picasso.with(this).load(user.getAvatar_url())
                    .fit().placeholder(R.mipmap.ic_launcher)
                    .into(avatar);
        int i = tweetObjects.size();
        tweetObjects.clear();
        rvAdapter.notifyItemRangeRemoved(0, i);
        getTimeline();
        getSupportFragmentManager().popBackStack();
    }
}
