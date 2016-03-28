package edumsg.edumsg_android_app;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by omarelhagin on 27/3/16.
 */
public class MyAppCompatActivity extends AppCompatActivity {

    int userId;
    String username;
    String avatarUrl;
    String name;
    String bio;

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
}
