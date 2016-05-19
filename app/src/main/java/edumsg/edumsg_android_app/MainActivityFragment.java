package edumsg.edumsg_android_app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Used to show all replies to a tweet.
 */
public class MainActivityFragment extends Fragment {
    @Bind(R.id.replies_recycler_view) RecyclerView recyclerView;
    private List<Tweet> replies;
    private RVAdapter rvAdapter;
    private int tweetId;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        replies = new ArrayList<>();
        rvAdapter = new RVAdapter(getActivity(), replies, MyAppCompatActivity.sessionId);
        recyclerView.setAdapter(rvAdapter);
        populateRV();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tweetId = getArguments().getInt("tweetId");
    }

    private void populateRV()
    {
        final MainActivity main = (MainActivity) getActivity();
        final LoadToast loadToast = new LoadToast(getContext());
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        final ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "get_replies");
        jsonParams.put("tweet_id", tweetId + "");
        jsonParams.put("session_id", MyAppCompatActivity.sessionId);
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
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
                        ArrayList retweets = main.getRetweets();
                        ArrayList favorites = main.getFavorites();
                        ArrayList tweetsArray = (ArrayList) responseMap.get("replies");
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
                            creator.setUsername((String) creatorMap.get("username"));
                            creator.setAvatar_url(avatarUrl);
                            final Tweet tweetObject = new Tweet(tweetId, creator, tweetText);
                            if (avatarUrl != null && !avatarUrl.equals(""))
                            {
                                tweetObject.setImgUrl(avatarUrl);
                            }
                            if (retweets != null && retweets.contains(Integer.valueOf(tweetId)))
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
                            replies.add(tweetObject);
                        }
                        rvAdapter.notifyDataSetChanged();
                    }
                }
                catch (Exception e)
                {
//                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadToast.error();
//                error.printStackTrace();
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
        jsonObjectRequest.setTag("Request");
        main.getVolleyRequestQueue().add(jsonObjectRequest);
    }
}
