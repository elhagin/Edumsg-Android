package edumsg.edumsg_android_app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigationFragment extends Fragment{

    @Bind(R.id.profile_button) Button profileBtn;
    @Bind(R.id.msgs_button) Button msgsBtn;
    @Bind(R.id.lists_button) Button listsBtn;
    @Bind(R.id.settings_button) Button settingsBtn;

    public NavigationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.bind(this, view);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity parentActivity = (MyAppCompatActivity) getActivity();
                Intent intent = new Intent(parentActivity, ProfileActivity.class);
                intent.putExtra("username", parentActivity.getUsername());
                intent.putExtra("name", parentActivity.getName());
                intent.putExtra("avatar_url", parentActivity.getAvatarUrl());
                intent.putExtra("bio", parentActivity.getBio());
                intent.putExtra("userId", parentActivity.getUserId());
                startActivity(intent);
            }
        });
        msgsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity parentActivity = (MyAppCompatActivity) getActivity();
                Intent intent = new Intent(parentActivity, MessagesActivity.class);
                intent.putExtra("userId", parentActivity.getUserId());
                startActivity(intent);
            }
        });
        listsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity parentActivity = (MyAppCompatActivity) getActivity();
                Intent intent = new Intent(parentActivity, ProfileActivity.class);
                intent.putExtra("userId", parentActivity.getUserId());
                startActivity(intent);
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity parentActivity = (MyAppCompatActivity) getActivity();
                Intent intent = new Intent(parentActivity, ProfileActivity.class);
                intent.putExtra("userId", parentActivity.getUserId());
                startActivity(intent);
            }
        });

        return view;
    }
}
