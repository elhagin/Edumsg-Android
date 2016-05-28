/*
 * EduMsg is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package edumsg.edumsg_android_app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment that is started for each conversation in {@link MessagesActivity#conversations}.
 */
public class ConversationFragment extends Fragment {
    /**
     * convId: ID of current conversation
     * messages: A {@link java.util.List} of type {@link DirectMessage}, which contains all the
     * messages in the conversation.
     * conversationAdapter: An instance of the class {@link ConversationAdapter} which is a custom made RecyclerView
     * Adapter to display each message's view.
     * conversation: An instance of the class {@link Conversation} which contains all the conversation's attributes.
     * mCallback: An interface that allows communication between this fragment and its parent activity.
     */
    private int convId;
    private java.util.List<DirectMessage> messages;
    private ConversationAdapter conversationAdapter;
    private Conversation conversation;
    private OnMessageSentListener mCallback;
    @Bind(R.id.conv_recycler_view)
    RecyclerView conversationRV;
    @Bind(R.id.chat_edit_text1)
    EditText sendMessage;
    @Bind(R.id.enter_chat1)
    ImageView sendBtn;
//    private int userId;

    /**
     * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be communicated to the activity and potentially other fragments contained in that activity.
     */
    public interface OnMessageSentListener
    {
        void onMessageSent();
    }

    public ConversationFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnMessageSentListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        convId = getArguments().getInt("convId");
//        userId = getArguments().getInt("userId");
        getConversation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this, view);

//        conversationRV.addItemDecoration(new_user HorizontalDividerItemDecoration.Builder(getContext()).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        conversationRV.setLayoutManager(linearLayoutManager);
        sendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    sendBtn.setImageResource(R.drawable.ic_chat_send);
                else
                    sendBtn.setImageResource(R.drawable.ic_chat_send_active);
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationRV.scrollToPosition(messages.size() - 1);
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conversation != null) {
                    DirectMessage lastDm = conversation.getDms().get(0);
                    sendMessage(lastDm.getSender().getUsername().equals(MyAppCompatActivity.username) ?
                            lastDm.getReciever().getId() : lastDm.getSender().getId(),
                            sendMessage.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getWindow()
                            .getDecorView().getRootView().getWindowToken(), 0);
                    sendMessage.setText("");
                }
            }
        });
        messages = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(messages, getContext(), MyAppCompatActivity.sessionId);
        conversationRV.setAdapter(conversationAdapter);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }

    private void getConversation()
    {
        MyAppCompatActivity main = (MyAppCompatActivity) getContext();
        final LoadToast loadToast = new LoadToast(main);
        final float scale = main.getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.show();
        JSONObject requestParams = new JSONObject();
        try
        {
            requestParams.put("queue", "DM");
            requestParams.put("method", "get_conv");
            requestParams.put("conv_id", convId);
            requestParams.put("session_id", MyAppCompatActivity.sessionId);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    MainActivity.requestUrl, requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    try {
                        if (response.get("code").equals("200"))
                        {
                            ObjectMapper mapper = new ObjectMapper();
                            conversation = mapper.readValue(
                                    response.get("conv").toString(),
                                    new TypeReference<Conversation>() {
                                    });
                            messages = conversation.getDms();
                            Collections.sort(messages);
                            conversationAdapter.setMessages(messages);
                            conversationAdapter.notifyDataSetChanged();
                            conversationRV.scrollToPosition(messages.size() - 1);
                            loadToast.success();
                        }
                    } catch (Exception e) {
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
            jsonObjectRequest.setTag(MyAppCompatActivity.TAG);
            main.getVolleyRequestQueue().add(jsonObjectRequest);
        }
        catch (JSONException e)
        {
            loadToast.error();
            e.printStackTrace();
        }

    }

    private void sendMessage(int recipientId, final String message) {
        Map<String, String> jsonParams2 = new HashMap<>();
        jsonParams2.put("queue", "DM");
        jsonParams2.put("method", "create_dm");
        jsonParams2.put("session_id", MyAppCompatActivity.sessionId);
        jsonParams2.put("receiver_id", recipientId + "");
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
                        getConversation();
                        mCallback.onMessageSent();
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
            }
        };
        jsonObjectRequest2.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest2.setTag("Request");
        ((MyAppCompatActivity) getActivity()).getVolleyRequestQueue().add(jsonObjectRequest2);
    }
}
