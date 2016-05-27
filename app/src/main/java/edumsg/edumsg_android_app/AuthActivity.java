package edumsg.edumsg_android_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Starts a {@link LoginFragment} which can use this Activity to start a {@link RegisterFragment}
 * if needed.
 */
public class AuthActivity extends MyAppCompatActivity
{
//    MyPagerAdapter pagerAdapter;
//    @Bind(R.id.pager) ViewPager mPager;
//    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @BindColor(R.color.colorPrimary) int cPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, loginFragment).commit();
//        tabLayout.addTab(tabLayout.newTab().setText("Sign in"));
//        tabLayout.addTab(tabLayout.newTab().setText("Register"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setBackgroundColor(cPrimary);
//        pagerAdapter = new_user MyPagerAdapter(getSupportFragmentManager(), this);
//        mPager.setAdapter(pagerAdapter);
//        mPager.addOnPageChangeListener(new_user TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new_user TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//                Intent intent = new_user Intent(AuthActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//                LoginFragment loginFragment = LoginFragment.newInstance();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                loginFragment.show(fragmentManager, "LoginFragment");
    }

//    public static class MyPagerAdapter extends FragmentPagerAdapter
//    {
//        java.util.List<String> fragments;
//        Context context;
//
//        public MyPagerAdapter(FragmentManager fragmentManager, Context context) {
//            super(fragmentManager);
//            this.context = context;
//            fragments = new ArrayList<>();
//            fragments.add(LoginFragment.class.getName());
//            fragments.add(RegisterFragment.class.getName());
//        }
//        @Override
//        public Fragment getItem(int position) {
//            return Fragment.instantiate(context, fragments.get(position));
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//    }
//

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mUsername;
//        private final String mEmail;
//        private final String mPassword;
//        private final String mMethod;
//        private User user;
//
//        UserLoginTask(String email, String password, String method, String username) {
//            mUsername = username;
//            mEmail = email;
//            mPassword = password;
//            mMethod = method;
//            user = null;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            Map<String, String> jsonParams = new_user HashMap<>();
//            jsonParams.put("queue", "USER");
//            if (mMethod.equals("register")) {
//                jsonParams.put("method", "register");
//                jsonParams.put("username", mUsername);
//                jsonParams.put("password", mPassword);
//                jsonParams.put("email", mEmail);
//                jsonParams.put("name", "Omar ElHagin");
//            }
//            else
//            {
//                jsonParams.put("method", "login");
//                jsonParams.put("username", mUsername);
//                jsonParams.put("password", mPassword);
//            }
//            JSONObject jsonRequest = new_user JSONObject(jsonParams);
//            JsonObjectRequest jsonObjectRequest = new_user JsonObjectRequest(Request.Method.POST,
//                    MainActivity.requestUrl, jsonRequest, new_user Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    ObjectMapper mapper = new_user ObjectMapper();
//                    try {
//                        Map<String, String> responseMap = mapper
//                                .readValue(new_user ByteArrayInputStream(response.toString()
//                                                .getBytes("UTF-8")),
//                                        new_user TypeReference<HashMap<String, String>>() {
//                                        });
//                        if (responseMap.get("code").equals("200"))
//                        {
//                            JSONObject userJson = new_user JSONObject(responseMap.get("user"));
//                            user = mapper.readValue(userJson.toString(), User.class);
//                        }
//                    }
//                    catch (Exception e)
//                    {
//                        Log.e("JSONMapper", e.getMessage());
//                    }
//                }
//            }, new_user Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("Volley", error.toString());
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new_user HashMap<String, String>();
//                    headers.put("Content-Type", "application/json; charset=utf-8");
//                    //headers.put("User-agent", System.getProperty("http.agent"));
//                    return headers;
//                };
//            };
//            getVolleyRequestQueue().add(jsonObjectRequest);
//            return true;
//        }
//
//        @Override
//        protected void onCancelled() {
//            //mAuthTask = null;
//        }
//
//
//        /**
//         * Returns a Volley request queue for creating network requests
//         *
//         * @return {@link com.android.volley.RequestQueue}
//         */
//        public RequestQueue getVolleyRequestQueue()
//        {
//            if (mRequestQueue == null)
//            {
//                mRequestQueue = Volley.newRequestQueue(AuthActivity.this);
//            }
//
//            return mRequestQueue;
//        }
//
//        /**
//         * Cancels all the request in the Volley queue for a given tag
//         *
//         * @param tag associated with the Volley requests to be cancelled
//         */
//        public void cancelAllRequests(String tag)
//        {
//            if (getVolleyRequestQueue() != null)
//            {
//                getVolleyRequestQueue().cancelAll(tag);
//            }
//        }
//    }
}
