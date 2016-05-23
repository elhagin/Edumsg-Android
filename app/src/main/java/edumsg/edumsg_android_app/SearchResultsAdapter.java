package edumsg.edumsg_android_app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.*;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by omarelhagin on 22/5/16.
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder> {

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.result_layout) LinearLayout resultLayout;
        @Bind(R.id.user_image) ImageView userImg;
        @Bind(R.id.user_info) TextView userInfo;
        ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private java.util.List<User> users;
    private Context context;

    SearchResultsAdapter(Context context, List<User> users)
    {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_view, parent, false);
        ResultViewHolder resultViewHolder = new ResultViewHolder(view);
        return resultViewHolder;
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        final User user = users.get(position);
        Picasso.with(context).load(user.getAvatar_url())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .into(holder.userImg);
        String info = context.getString(R.string.user_info, user.getName(),
                user.getUsername());
        holder.userInfo.setText(info);

        holder.resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("username", user.getUsername());
                intent.putExtra("creatorId", user.getId());
                context.startActivity(intent);
                ((SearchResultsActivity) context).finish();
            }
        });
    }
}
