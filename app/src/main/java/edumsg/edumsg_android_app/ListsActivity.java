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

import android.content.DialogInterface;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Shows all lists that the logged in user is subscribed to.
 */
public class ListsActivity extends MyAppCompatActivity {

    private ArrayList<List> lists;
    private ListsAdapter listsAdapter;
    @BindColor(R.color.colorPrimary) int cPrimary;
    @Bind(R.id.lists_rv) RecyclerView listsRecyclerView;
    @Bind(R.id.toolbar_lists) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        ButterKnife.bind(this);

        userId = getIntent().getIntExtra("userId", -1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Lists");

        lists = new ArrayList<>();
        listsRecyclerView.setHasFixedSize(true);
        listsRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listsRecyclerView.setLayoutManager(linearLayoutManager);
        listsAdapter = new ListsAdapter(lists, this, userId);
        listsRecyclerView.setAdapter(listsAdapter);
        getLists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_list_btn:
                createListDialog();
                break;
        }

        return true;
    }

    private void getLists()
    {
        if (!lists.isEmpty()) {
            lists.clear();
        }
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.show();
        Map<String, String> jsonParams2 = new HashMap<>();
        jsonParams2.put("queue", "USER");
        jsonParams2.put("method", "get_subscribed_lists");
        jsonParams2.put("session_id", MyAppCompatActivity.sessionId);
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
                        ArrayList listsArr = (ArrayList) responseMap.get("subscribed_lists");
                        Iterator iterator = listsArr.iterator();
                        while (iterator.hasNext())
                        {
                            final List listObj = mapper
                                    .readValue(mapper.writeValueAsString(iterator.next()),
                                            new TypeReference<List>() {
                                            });
                            lists.add(listObj);
                        }
                        listsAdapter.notifyDataSetChanged();
                        loadToast.success();
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
        jsonObjectRequest2.setTag("Request");
        jsonObjectRequest2.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getVolleyRequestQueue().add(jsonObjectRequest2);
    }

    private void createListDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final EditText input1 = new EditText(this);
        input1.setSingleLine(true);
        input1.setHint("Name");
        final EditText input2 = new EditText(this);
        input2.setHint("Description");
        input2.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input2.setLines(4);
        input2.setSingleLine(false);
//        input.setBackgroundDrawable(null);
        LinearLayout isPrivate = new LinearLayout(this);
        isPrivate.setOrientation(LinearLayout.HORIZONTAL);
        final TextView privateTextView = new TextView(this);
        privateTextView.setText("Private");
        final CheckBox checkBox = new CheckBox(this);
        isPrivate.addView(privateTextView);
        isPrivate.addView(checkBox);

        linearLayout.addView(input1);
        linearLayout.addView(input2);
        linearLayout.addView(isPrivate);

        builder.setView(linearLayout);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createList(input1.getText().toString(), input2.getText().toString(),
                        checkBox.isChecked());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Create list");
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button posBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
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
                Button negBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                negBtn.setBackgroundColor(cPrimary);
                negBtn.setTextColor(Color.WHITE);
            }
        });
        alertDialog.show();
    }

    private void createList(final String name, final String description, final boolean isPrivate)
    {
        final LoadToast loadToast = new LoadToast(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.show();
        Map<String, String> jsonParams2 = new HashMap<>();
        jsonParams2.put("queue", "LIST");
        jsonParams2.put("method", "create_list");
        jsonParams2.put("name", name);
        jsonParams2.put("description", description);
        jsonParams2.put("session_id", sessionId);
        jsonParams2.put("private", isPrivate+"");
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
                        getLists();
                        loadToast.success();
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
        jsonObjectRequest2.setTag("Request");
        jsonObjectRequest2.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getVolleyRequestQueue().add(jsonObjectRequest2);
    }
}
