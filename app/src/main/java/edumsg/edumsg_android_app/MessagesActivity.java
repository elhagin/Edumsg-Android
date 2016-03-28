package edumsg.edumsg_android_app;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import butterknife.ButterKnife;

public class MessagesActivity extends AppCompatActivity {
    private int userId;
    private List<DirectMessage> messages;
    private RequestQueue mRequestQueue;
    private MessagesAdapter messagesAdapter;
    @Bind(R.id.toolbar_msgs) Toolbar toolbar;
//    @Bind(R.id.refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.messages_recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);
        userId = getIntent().getIntExtra("userId", -1);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Messages");

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messages, userId);
        recyclerView.setAdapter(messagesAdapter);
        getMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.new_msg_btn:
                getRecipient();
                break;
        }

        return true;
    }

    private void getMessages()
    {
        final LoadToast loadToast = new LoadToast(this);
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "DM");
        jsonParams.put("method", "get_convs");
        jsonParams.put("user_id", userId + "");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
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
                        ArrayList messagesArray = (ArrayList) responseMap.get("convs");
                        Iterator iterator = messagesArray.iterator();
                        while (iterator.hasNext())
                        {
                            Map<String, Object> convJson = mapper
                                    .readValue(mapper.writeValueAsString(iterator.next()),
                                            new TypeReference<HashMap<String, Object>>() {
                                            });
                            int convId = (int) convJson.get("id");
                            LinkedHashMap lastDmMap = (LinkedHashMap) convJson.get("lastDM");
                            LinkedHashMap senderMap = (LinkedHashMap) lastDmMap.get("sender");
                            LinkedHashMap receiverMap = (LinkedHashMap) lastDmMap.get("reciever");
                            String dmText = (String) lastDmMap.get("dm_text");
                            User sender = new User();
                            sender.setId((int) senderMap.get("id"));
                            sender.setName((String) senderMap.get("name"));
                            User receiver = new User();
                            receiver.setId((int) receiverMap.get("id"));
                            receiver.setName((String) receiverMap.get("name"));
                            DirectMessage lastDm = new DirectMessage();
                            lastDm.setDmText(dmText);
                            lastDm.setSender(sender);
                            lastDm.setReciever(receiver);
                            messages.add(lastDm);
                        }
                        messagesAdapter.notifyDataSetChanged();
                        loadToast.success();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
//                    Toast.makeText(this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
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
        jsonObjectRequest.setTag("Request");
        getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void getRecipient()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//        input.setLines(4);
        input.setSingleLine(true);
        input.setBackgroundDrawable(null);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getMessage(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Enter recipient username");
        alertDialog.show();
    }

    private void getMessage(final String recipient)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(4);
        input.setSingleLine(false);
        input.setBackgroundDrawable(null);
        builder.setView(input);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMessage(recipient, input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Enter message");
        alertDialog.show();
    }

    private void sendMessage(String recipient, String message)
    {

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
