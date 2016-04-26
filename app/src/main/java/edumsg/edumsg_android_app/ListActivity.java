package edumsg.edumsg_android_app;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

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

import org.json.JSONObject;

import java.util.*;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListActivity extends MyAppCompatActivity {
    private int listId;
    private String listName;
    private java.util.List<Tweet> tweetObjects;
    private RVAdapter rvAdapter;
    private ArrayList retweets;
    private ArrayList favorites;
    @Bind(R.id.list_rv) RecyclerView listRecyclerView;
    @Bind(R.id.toolbar_list) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        userId = getIntent().getIntExtra("userId", -1);
        listId = getIntent().getIntExtra("listId", -1);
        listName = getIntent().getStringExtra("listName");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(listName);
        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listRecyclerView.setLayoutManager(linearLayoutManager);

        tweetObjects = new ArrayList<>();
        rvAdapter = new RVAdapter(this, tweetObjects, sessionId);
        listRecyclerView.setAdapter(rvAdapter);
        getFeed();
    }

    private void getFeed()
    {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.setText("Loading...");
        loadToast.show();
        Map<String, String> jsonParams2 = new HashMap<>();
        jsonParams2.put("queue", "USER");
        jsonParams2.put("method", "get_retweets");
        jsonParams2.put("user_id", userId + "");
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
                    jsonParams.put("user_id", userId+"");
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
                                jsonParams.put("queue", "LIST");
                                jsonParams.put("method", "get_list_feeds");
                                jsonParams.put("list_id", listId+"");
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
                                                loadToast.success();
                                                ArrayList tweetsArray = (ArrayList) responseMap.get("list_feeds");
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
                                                rvAdapter.notifyItemRangeInserted(0, tweetObjects.size());
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
}
