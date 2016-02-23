package edumsg.edumsg_android_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private static final String TAG = "Request";
    private List<TweetObject> tweetObjects;
    private RVAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tweets_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        tweetObjects = new ArrayList<>();
        rvAdapter = new RVAdapter(this, tweetObjects);
        recyclerView.setAdapter(rvAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        getTweets();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
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

    private void getTweets()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://10.0.2.2:80/data.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonParser jsonParser = null;
                try {
                    JsonFactory jsonFactory = new JsonFactory();
                    jsonParser = jsonFactory.createParser(response);
                    if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                        throw new IOException("Expected data to start with an Object");
                    }
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            TweetObject tweetObject = new TweetObject();
                            jsonParser.nextToken();
                            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
//                                jsonParser.nextToken();
                                String fieldName = jsonParser.getCurrentName();

                                if (fieldName.equals("imgUrl")) {
                                    tweetObject.setImgUrl(jsonParser.getValueAsString());
                                } else {
                                    if (fieldName.equals("tweet")) {
                                        tweetObject.setTweet(jsonParser.getValueAsString());
                                    } else {
                                        throw new IOException("Unrecognized field '" + fieldName + "'");
                                    }
                                }
                            }
                            tweetObjects.add(tweetObject);
                        }
                    }

                    rvAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    Log.e("JACKSON", e.getMessage());
                }
                finally {
                    if (jsonParser != null && !jsonParser.isClosed()) {
                        try {
                            jsonParser.close();
                        } catch (IOException e) {
                            Log.e("JACKSON", e.getMessage());
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.getMessage());
            }
        });

        getVolleyRequestQueue().add(stringRequest);
    }
}
