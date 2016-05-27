package edumsg.edumsg_android_app;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Base class for all activities that contains all variables that need to be shared throughout the application,
 * and the main Volley request queue.
 *
 * Created by omarelhagin on 27/3/16.
 */
public class MyAppCompatActivity extends AppCompatActivity {

    int userId;
    protected static String username;
    String avatarUrl;
    String name;
    String bio;
    protected static String sessionId;
    public static final String requestUrl = "http://10.0.2.2:8080/";
    RequestQueue mRequestQueue;
    static final String TAG = "Request";

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

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getSessionId() {
        return sessionId;
    }
}
