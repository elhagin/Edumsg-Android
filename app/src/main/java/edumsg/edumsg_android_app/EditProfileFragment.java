package edumsg.edumsg_android_app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment that provides the user with many views that allow the user to edit their info.
 */
public class EditProfileFragment extends Fragment {
    private User user;
    private JSONObject requestParams;
    OnInfoEditedListener mCallback;
    @Bind(R.id.toolbar_edit_profile) Toolbar toolbar;
    @Bind(R.id.edit_name) TextInputEditText editName;
    @Bind(R.id.edit_language) TextInputEditText editLanguage;
    @Bind(R.id.edit_country) TextInputEditText editCountry;
    @Bind(R.id.edit_website) TextInputEditText editWebsite;
    @Bind(R.id.edit_avatar_url) TextInputEditText editAvatarUrl;
    @Bind(R.id.overlay_switch) Switch editOverlay;
    @Bind(R.id.edit_link_color) TextInputEditText editLinkColor;
    @Bind(R.id.edit_background_color) TextInputEditText editBackgroundColor;
    @Bind(R.id.protected_switch) Switch editProtected;

    public interface OnInfoEditedListener {
        void onInfoEdited(User user);
    }

    public EditProfileFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnInfoEditedListener) context;
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
        user = getArguments().getParcelable("user");
        requestParams = new JSONObject();
        try {
            requestParams.put("queue", "USER");
            requestParams.put("method", "update_user");
            requestParams.put("session_id", MyAppCompatActivity.sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);

        toolbar.setTitle("Edit Profile");
        toolbar.inflateMenu(R.menu.menu_edit_profile);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cancel_btn:
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    case R.id.save_btn:
//                        if (!editName.getText().equals(user.getName())
//                                || !editLanguage.getText().equals(user.getLanguage())
//                                || !editCountry.getText().equals(user.getCountry())
//                                || !editWebsite.getText().equals(user.getWebsite())
//                                || !editAvatarUrl.getText().equals(user.getAvatar_url())
//                                || editOverlay.isChecked() && !user.getOverlay()
//                                || !editLinkColor.getText().equals(user.getLink_color())
//                                || !editBackgroundColor.getText().equals(user.getBackground_color())
//                                || editProtected.isChecked() && !user.getProtected_tweets())
                        if (requestParams.length() > 3)
                            commitChanges();
                        else
                            getActivity().getSupportFragmentManager().popBackStack();
                        break;
                }

                return true;
            }
        });

        editName.setText(user.getName());
        editLanguage.setText(user.getLanguage());
        editCountry.setText(user.getCountry());
        editWebsite.setText(user.getWebsite());
        editAvatarUrl.setText(user.getAvatar_url());
        editOverlay.setChecked(user.getOverlay());
        editLinkColor.setText(user.getLink_color());
        editBackgroundColor.setText(user.getBackground_color());
        editProtected.setChecked(user.getProtected_tweets());

        setListeners();
        return view;
    }

    private void setListeners() {
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    requestParams.put("name", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editLanguage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    requestParams.put("language", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    requestParams.put("country", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    requestParams.put("website", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editAvatarUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    requestParams.put("avatar_url", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editLinkColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    requestParams.put("link_color", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editBackgroundColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    requestParams.put("background_color", s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editOverlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    requestParams.put("overlay", isChecked + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        editProtected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    requestParams.put("protected_tweets", isChecked + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void commitChanges() {
        MyAppCompatActivity main = (MyAppCompatActivity) getContext();
        final LoadToast loadToast = new LoadToast(main);
        final float scale = main.getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (56 * scale + 0.5f);
        loadToast.setTranslationY(pixels);
        loadToast.show();
        try
        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    MainActivity.requestUrl, requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    try {
                        if (response.get("code").equals("200"))
                        {
                            loadToast.success();
                            InputMethodManager imm = (InputMethodManager) getContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getWindow()
                                    .getDecorView().getRootView().getWindowToken(), 0);
                            User user = new User();
                            if (requestParams.has("name"))
                                user.setName(requestParams.getString("name"));
                            if (requestParams.has("avatar_url"))
                                user.setAvatar_url(requestParams.getString("avatar_url"));
                            mCallback.onInfoEdited(user);
//                            if (requestParams.has("name"))
//                                ((ProfileActivity) getActivity())
//                                        .usernameTxt.setText(requestParams.getString("name"));
//                            if (requestParams.has("avatar_url"))
//                                Picasso.with(getContext()).load(requestParams.getString("avatar_url"))
//                                        .fit().placeholder(R.mipmap.ic_launcher)
//                                        .into(((ProfileActivity) getActivity()).avatar);
//                            getActivity().getSupportFragmentManager().popBackStack();
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
        catch (Exception e)
        {
            loadToast.error();
            e.printStackTrace();
        }

    }
}
