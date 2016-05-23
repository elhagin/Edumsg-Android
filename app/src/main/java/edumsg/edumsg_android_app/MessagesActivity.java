package edumsg.edumsg_android_app;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import edumsg.edumsg_android_app.ConversationFragment.OnMessageSentListener;

/**
 * Displays all conversations for the logged in user, showing the last message sent or received in each one.
 * Also allows the user to send a new message.
 */
public class MessagesActivity extends MyAppCompatActivity implements OnMessageSentListener {
    private int userId;
    private List<Conversation> conversations;
    private RequestQueue mRequestQueue;
    private MessagesAdapter messagesAdapter;
    private ArrayList<User> followers;
    @BindColor(R.color.colorPrimary) int cPrimary;
    @Bind(R.id.toolbar_msgs)
    Toolbar toolbar;
    //    @Bind(R.id.refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.messages_recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);
//        sessionId = getIntent().getStringExtra("sessionId");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Messages");

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        conversations = new ArrayList<>();
        followers = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, conversations, sessionId);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_msg_btn:
                sendMessage();
                break;
        }

        return true;
    }

    private void getMessages() {
        final LoadToast loadToast = new LoadToast(this);
        if (conversations.isEmpty())
        {
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (56 * scale + 0.5f);
            loadToast.setTranslationY(pixels);
            loadToast.show();
        }
        Map<String, String> jsonParams2 = new HashMap<>();
        jsonParams2.put("queue", "USER");
        jsonParams2.put("method", "followers");
        jsonParams2.put("session_id", sessionId + "");
        JSONObject jsonRequest2 = new JSONObject(jsonParams2);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200")) {
                        getFollowers((ArrayList) responseMap.get("followers"));
                        Map<String, String> jsonParams = new HashMap<>();
                        jsonParams.put("queue", "DM");
                        jsonParams.put("method", "get_convs");
                        jsonParams.put("session_id", sessionId);
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
                                    if (responseMap.get("code").equals("200")) {
                                        ArrayList messagesArray = (ArrayList) responseMap.get("convs");
                                        Iterator iterator = messagesArray.iterator();
                                        while (iterator.hasNext()) {
//                                            Map<String, Object> convJson = mapper
//                                                    .readValue(mapper.writeValueAsString(iterator.next()),
//                                                            new_user TypeReference<HashMap<String, Object>>() {
//                                                            });
//                                            int convId = (int) convJson.get("id");
//                                            LinkedHashMap lastDmMap = (LinkedHashMap) convJson.get("lastDM");
//                                            LinkedHashMap senderMap = (LinkedHashMap) lastDmMap.get("sender");
//                                            LinkedHashMap receiverMap = (LinkedHashMap) lastDmMap.get("reciever");
//                                            String dmText = (String) lastDmMap.get("dm_text");
//                                            User sender = new_user User();
//                                            sender.setId((int) senderMap.get("id"));
//                                            sender.setName((String) senderMap.get("name"));
//                                            sender.setUsername((String) senderMap.get("username"));
//                                            sender.setAvatar_url((String) senderMap.get("avatar_url"));
//                                            User receiver = new_user User();
//                                            receiver.setId((int) receiverMap.get("id"));
//                                            receiver.setName((String) receiverMap.get("name"));
//                                            receiver.setUsername((String) receiverMap.get("username"));
//                                            receiver.setAvatar_url((String) receiverMap.get("avatar_url"));
                                            String userImg = "";
                                            Conversation conversation = mapper
                                                    .readValue(mapper.writeValueAsString(iterator.next()),
                                                            new TypeReference<Conversation>() {
                                                            });
                                            DirectMessage lastDm = conversation.getLastDM();
                                            User sender = lastDm.getSender();
                                            User receiver = lastDm.getReciever();
                                            if (sender.getUsername().equals(MyAppCompatActivity.username))
                                                userImg = receiver.getAvatar_url();
                                            else
                                                userImg = sender.getAvatar_url();
                                            lastDm.setUserImgUrl(userImg);
                                            conversation.setLastDM(lastDm);
                                            conversations.add(conversation);
                                        }
                                        Collections.sort(conversations);
                                        messagesAdapter.notifyItemRangeInserted(0, conversations.size());
                                        loadToast.success();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                if (volleyError.networkResponse != null
                                        && volleyError.networkResponse.data != null
                                        && volleyError.networkResponse.statusCode == 400) {
                                    try {
                                        String errorJson = new String(volleyError.networkResponse.data);
                                        JSONObject errorObj = new JSONObject(errorJson);
                                        String error = errorObj.getString("message");
                                    } catch (JSONException e) {
                                        Log.e("Response Error Msg", e.getMessage());
                                    }
                                } else {
//                    Toast.makeText(this, volleyError.sendMessage(), Toast.LENGTH_SHORT).show();
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
                        jsonObjectRequest.setTag("Request");
                        getVolleyRequestQueue().add(jsonObjectRequest);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400) {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
                    } catch (JSONException e) {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                } else {
//                    Toast.makeText(this, volleyError.sendMessage(), Toast.LENGTH_SHORT).show();
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
        jsonObjectRequest2.setTag("Request");
        getVolleyRequestQueue().add(jsonObjectRequest2);
    }

    private void getFollowers(ArrayList followersJson)
    {
        Iterator iterator = followersJson.iterator();
        while (iterator.hasNext())
        {
            try
            {
                ObjectMapper mapper = new ObjectMapper();
                final Map<String, Object> userObj = mapper
                        .readValue(mapper.writeValueAsString(iterator.next()),
                                new TypeReference<HashMap<String, Object>>() {
                                });
                User user = new User();
                user.setId((int) userObj.get("id"));
                user.setUsername((String) userObj.get("username"));
                user.setName((String) userObj.get("name"));
                user.setAvatar_url((String) userObj.get("avatar_url"));
                followers.add(user);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() {
//        previousDialog.dismiss();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final AutoCompleteTextView getRecipient = new AutoCompleteTextView(this);
        getRecipient.setHint("Recipient");
        final FollowersAdapter followersAdapter = new FollowersAdapter(this, android.R.layout.simple_list_item_1,
                followers);
        getRecipient.setAdapter(followersAdapter);
        getRecipient.setThreshold(1);
        getRecipient.setSingleLine(true);
        final Button sendBtn = new Button(MessagesActivity.this);
        final EditText input = new EditText(MessagesActivity.this);
        input.setHint("Enter your message here");
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(4);
        input.setSingleLine(false);
        input.setBackgroundDrawable(null);

        linearLayout.addView(getRecipient, 0);
        linearLayout.addView(input, 1);

        builder.setView(linearLayout);
//        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                sendMessage(recipient, input.getText().toString());
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setTitle("Send message");
        alertDialog.show();
        Button cancelBtn = new Button(MessagesActivity.this);
        sendBtn.setBackgroundColor(cPrimary);
        sendBtn.setTextColor(Color.WHITE);
        final float scale = getApplicationContext()
                .getResources().getDisplayMetrics().density;
        int pixels = (int) (10 * scale + 0.5f);
        LinearLayout.LayoutParams layoutParams2
                = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins(0, 0, pixels, 0);
        sendBtn.setLayoutParams(layoutParams2);
        cancelBtn.setBackgroundColor(cPrimary);
        cancelBtn.setTextColor(Color.WHITE);
        sendBtn.setText(getString(R.string.action_send));
        pixels = (int) (80 * scale + 0.5f);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(pixels,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        pixels = (int) (10 * scale + 0.5f);
        layoutParams.rightMargin = pixels;
        sendBtn.setLayoutParams(layoutParams);
        layoutParams.rightMargin = pixels;
        cancelBtn.setLayoutParams(layoutParams);
        cancelBtn.setText(getString(R.string.action_cancel));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        getRecipient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User recipient = followers.get(position);
                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (input.getText().length() > 0) {
                            alertDialog.dismiss();
                            sendMessage(recipient, input.getText().toString());
                        }
                    }
                });
            }
        });
        LinearLayout buttons = new LinearLayout(MessagesActivity.this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.addView(cancelBtn, 0);
        buttons.addView(sendBtn, 1);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.RIGHT;
        buttons.setLayoutParams(layoutParams1);
        linearLayout.addView(buttons, 2);
    }

    private void sendMessage(final User recipient, final String message) {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.setText("Sending message...");
        loadToast.show();
        Map<String, String> jsonParams2 = new HashMap<>();
        jsonParams2.put("queue", "DM");
        jsonParams2.put("method", "create_dm");
        jsonParams2.put("session_id", sessionId);
        jsonParams2.put("receiver_id", recipient.getId() + "");
        jsonParams2.put("dm_text", message);
        JSONObject jsonRequest2 = new JSONObject(jsonParams2);
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200")) {
                        DirectMessage directMessage = new DirectMessage();
                        directMessage.setUserImgUrl(recipient.getAvatar_url());
                        User sender = new User();
                        sender.setId(userId);
                        sender.setAvatar_url(avatarUrl);
                        sender.setUsername(username);
                        sender.setName(name);
                        directMessage.setSender(sender);
                        directMessage.setReciever(recipient);
                        directMessage.setDm_text(message);
                        loadToast.success();
                        int size = conversations.size();
                        conversations.clear();
                        messagesAdapter.notifyItemRangeRemoved(0, size);
                        getMessages();
                    }
                } catch (Exception e) {
                    loadToast.error();
                    e.printStackTrace();
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
                    } catch (JSONException e) {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                } else {
//                    Toast.makeText(this, volleyError.sendMessage(), Toast.LENGTH_SHORT).show();
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
        jsonObjectRequest2.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest2.setTag("Request");
        getVolleyRequestQueue().add(jsonObjectRequest2);
    }

    @Override
    public void onMessageSent() {
        int i = conversations.size();
        conversations.clear();
        messagesAdapter.notifyItemRangeRemoved(0, i);
        getMessages();
    }
}
