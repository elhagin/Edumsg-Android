package edumsg.edumsg_android_app;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Started from {@link MainActivity} to display search results when a user uses the {@link android.support.v7.widget.SearchView} in {@link MainActivity}'s action bar.
 */
public class SearchResultsActivity extends MyAppCompatActivity {

    private SearchResultsAdapter searchResultsAdapter;
    private ArrayList<User> userResults;
    @Bind(R.id.search_recycler_view) RecyclerView searchRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);
        searchRV.setHasFixedSize(true);
        Paint paint = new Paint();
        paint.setStrokeWidth(3.0f);
        paint.setColor(Color.rgb(220, 220, 220));
        paint.setAntiAlias(true);
        searchRV.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .paint(paint).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchRV.setLayoutManager(linearLayoutManager);
//        userResults = new ArrayList<>();
        try {
            handleIntent(getIntent());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleIntent(Intent intent) throws JSONException {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            final LoadToast loadToast = new LoadToast(this);
            loadToast.show();
            JSONObject requestParams = new JSONObject();
            requestParams.put("queue", "USER");
            requestParams.put("method", "get_users");
            requestParams.put("user_substring", query);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    MyAppCompatActivity.requestUrl, requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.get("code").equals("200")) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            userResults = objectMapper.readValue(response.get("users").toString(),
                                    new TypeReference<ArrayList<User>>() {});
                            searchResultsAdapter = new SearchResultsAdapter(SearchResultsActivity.this, userResults);
                            searchRV.setAdapter(searchResultsAdapter);
                            searchResultsAdapter.notifyItemRangeInserted(0, userResults.size());
                            loadToast.success();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
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
                }
            };
            jsonObjectRequest.setTag("Request");
            getVolleyRequestQueue().add(jsonObjectRequest);
        }
    }
}
