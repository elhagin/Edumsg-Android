package edumsg.edumsg_android_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Unused class. Purpose was to persist logged in user but not implemented.
 */

public class ActivityLauncher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Intent intent;
        boolean userLoggedIn = false;
        if (userLoggedIn)
        {
            intent = new Intent(this, MainActivity.class);
        }
        else
        {
            intent = new Intent(this, AuthActivity.class);
        }
//        intent = new_user Intent(this, ListsActivity.class);
        startActivity(intent);
        finish();
    }
}
