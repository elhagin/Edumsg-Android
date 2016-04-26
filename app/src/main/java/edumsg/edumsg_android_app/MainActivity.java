package edumsg.edumsg_android_app;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

public class MainActivity extends MyAppCompatActivity {

    private List<Tweet> tweetObjects;
    private RVAdapter rvAdapter;
    private ArrayList retweets;
    private ArrayList favorites;
    @BindColor(R.color.colorPrimary) int cPrimary;
    @Bind(R.id.my_toolbar) Toolbar toolbar;
    @Bind(R.id.refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.tweets_recycler_view) RecyclerView recyclerView;
//    @Bind(R.id.btn_home) ImageButton homeButton;
//    @Bind(R.id.btn_search) ImageButton searchButton;
//    @Bind(R.id.btn_create) ImageButton createButton;
//    @Bind(R.id.btn_nav) ImageButton navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = getIntent().getExtras().getString("sessionId");
        username = getIntent().getExtras().getString("username");
        setContentView(R.layout.activity_main);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                final EditText input = new EditText(MainActivity.this);
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
                final AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button posBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        posBtn.setBackgroundColor(cPrimary);
                        posBtn.setTextColor(Color.WHITE);
                        final float scale = getApplicationContext()
                                .getResources().getDisplayMetrics().density;
                        int pixels = (int) (10 * scale + 0.5f);
                        LinearLayout.LayoutParams layoutParams
                                = new LinearLayout.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, pixels, 0);
                        posBtn.setLayoutParams(layoutParams);
                        Button negBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        negBtn.setBackgroundColor(cPrimary);
                        negBtn.setTextColor(Color.WHITE);
                    }
                });
                dialog.show();
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> fragments = fragmentManager.getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment instanceof NavigationFragment)
                            return;
                    }
                }
                NavigationFragment navigationFragment = new NavigationFragment();
//                Bundle bundle = new_user Bundle();
//                bundle.putInt("userId", userId);
//                mainActivityFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(android.R.id.content, navigationFragment).addToBackStack("nav")
                        .commit();
//                logout();
//                launchMessages();
//                Intent intent = new_user Intent(MainActivity.this, ProfileActivity.class);
//                intent.putExtra("username", getUsername());
//                intent.putExtra("name", getName());
//                intent.putExtra("avatar_url", getAvatar_url());
//                intent.putExtra("bio", getBio());
//                intent.putExtra("creatorId", getUserId());
//                intent.putExtra("userId", getUserId());
//                startActivity(intent);
            }
        });

        recyclerView.setHasFixedSize(true);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (160 * scale + 0.5f);
        Paint paint = new Paint();
        paint.setStrokeWidth(3.0f);
        paint.setColor(Color.rgb(220, 220, 220));
        paint.setAntiAlias(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .paint(paint).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        tweetObjects = new ArrayList<>();
        rvAdapter = new RVAdapter(this, tweetObjects, sessionId);
        recyclerView.setAdapter(rvAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetObjects.clear();
                getFeed();
            }
        });

        getFeed();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    private void getFeed()
    {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        if (!swipeRefreshLayout.isRefreshing())
        {
            loadToast.setText("Loading...");
            loadToast.show();
        }
        Map<String, String> jsonParams2 = new HashMap<>();
        jsonParams2.put("queue", "USER");
        jsonParams2.put("method", "get_retweets");
        jsonParams2.put("session_id", sessionId + "");
        JSONObject jsonRequest2 = new JSONObject(jsonParams2);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST,
                requestUrl, jsonRequest2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    final ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    retweets = (ArrayList) responseMap.get("tweet_ids");
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
                                favorites = (ArrayList) responseMap.get("favorites");
                                Map<String, String> jsonParams = new HashMap<>();
                                jsonParams.put("queue", "USER");
                                jsonParams.put("method", "timeline");
                                jsonParams.put("session_id", sessionId);
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
                                                    loadToast.success();
                                                ArrayList tweetsArray = (ArrayList) responseMap.get("feeds");
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
                                                    final String creatorUsername = (String) creatorMap.get("username");
                                                    if (creatorUsername == username)
                                                        continue;
                                                    String tweetText = (String) tweetJsonObj.get("tweet_text");
                                                    String avatarUrl = (String) creatorMap.get("avatar_url");
                                                    User creator = new User();
                                                    creator.setId(creatorId);
                                                    creator.setName((String) creatorMap.get("name"));
                                                    creator.setUsername((String) creatorMap.get("username"));
                                                    creator.setAvatar_url(avatarUrl);
                                                    final LinkedHashMap retweeterMap = (LinkedHashMap) tweetJsonObj.get("retweeter");
                                                    final Tweet tweetObject = new Tweet(tweetId, creator, tweetText);
                                                    if (retweeterMap != null)
                                                    {
                                                        User retweeter = new User();
                                                        retweeter.setId((int) retweeterMap.get("id"));
                                                        retweeter.setName((String) retweeterMap.get("name"));
                                                        retweeter.setUsername((String) retweeterMap.get("username"));
                                                        tweetObject.setRetweeter(retweeter);
                                                    }
                                                    if (avatarUrl != null && !avatarUrl.equals(""))
                                                    {
                                                        tweetObject.setImgUrl(avatarUrl);
                                                    }
                                                    if (retweets.contains(Integer.valueOf(tweetId)))
                                                    {
                                                        tweetObject.setIsRetweeted(true);
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
                                            loadToast.error();
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        loadToast.error();
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
                            catch (Exception e)
                            {
                                loadToast.error();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loadToast.error();
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
                catch (Exception e)
                {
                    loadToast.error();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadToast.error();
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
        jsonObjectRequest2.setTag(TAG);
        jsonObjectRequest2.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getVolleyRequestQueue().add(jsonObjectRequest2);
    }

//    private void getTweets()
//    {
//        final LoadToast loadToast = new_user LoadToast(this);
//        loadToast.setText("Loading...");
//        loadToast.show();
//        Map<String, String> jsonParams = new_user HashMap<>();
//        jsonParams.put("queue", "USER");
//        jsonParams.put("method", "timeline");
//        jsonParams.put("user_id", userId+"");
//        JSONObject jsonRequest = new_user JSONObject(jsonParams);
//        JsonObjectRequest jsonObjectRequest = new_user JsonObjectRequest(Request.Method.POST,
//                requestUrl, jsonRequest, new_user Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(final JSONObject response) {
//                final ObjectMapper mapper = new_user ObjectMapper();
//                try {
//                    final Map<String, Object> responseMap = mapper
//                            .readValue(response.toString(),
//                                    new_user TypeReference<HashMap<String, Object>>() {
//                                    });
//                    if (responseMap.get("code").equals("200"))
//                    {
//                        loadToast.success();
//                        try {
//                            ArrayList tweetsArray = (ArrayList) responseMap.get("tweets");
//                            Iterator iterator = tweetsArray.iterator();
//                            while (iterator.hasNext()) {
//                                Map<String, Object> tweetJsonObj = mapper
//                                        .readValue(mapper.writeValueAsString(iterator.next()),
//                                                new_user TypeReference<HashMap<String, Object>>() {
//                                                });
//                                String tweetText = (String) tweetJsonObj.get("tweet_text");
//                                LinkedHashMap creator = (LinkedHashMap) tweetJsonObj.get("creator");
//                                String avatarUrl = (String) creator.get("avatar_url");
//                                Tweet tweetObject = new_user Tweet(userId, avatarUrl,
//                                        tweetText);
//                                tweetObjects.add(tweetObject);
//                            }
//                            rvAdapter.notifyDataSetChanged();
//                            if (swipeRefreshLayout.isRefreshing())
//                            {
//                                swipeRefreshLayout.setRefreshing(false);
//                            }
//                        }
//                        catch (Exception e)
//                        {
//                            Log.e("Getting tweets", e.getMessage());
//                        }
//                    }
//                }
//                catch (Exception e)
//                {
//                    Log.e("JSONMapper", e.getMessage());
//                }
//            }
//        }, new_user Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                loadToast.error();
//                if (volleyError.networkResponse != null
//                        && volleyError.networkResponse.data != null
//                        && volleyError.networkResponse.statusCode == 400)
//                {
//                    try {
//                        String errorJson = new_user String(volleyError.networkResponse.data);
//                        JSONObject errorObj = new_user JSONObject(errorJson);
//                        String error = errorObj.getString("message");
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
//                HashMap<String, String> headers = new_user HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                //headers.put("User-agent", System.getProperty("http.agent"));
//                return headers;
//            };
//        };
////        jsonObjectRequest.setRetryPolicy(new_user DefaultRetryPolicy(10000,
////                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        getVolleyRequestQueue().add(jsonObjectRequest);
//    }

    private void createTweet(final String tweet)
    {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.setText("Tweeting...");
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "tweet");
        jsonParams.put("tweet_text", tweet);
        jsonParams.put("session_id", sessionId);
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
                        loadToast.error();
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

    public ArrayList getRetweets() {
        return retweets;
    }

    public ArrayList getFavorites() {
        return favorites;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }
}
