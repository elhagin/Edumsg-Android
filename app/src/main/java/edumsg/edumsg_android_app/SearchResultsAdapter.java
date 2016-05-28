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
 * Creates a View for each item in {@link SearchResultsActivity#searchRV}.
 *
 * Created by omarelhagin on 22/5/16.
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder> {

    /**
     * Custom ViewHolder implementation of {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * to represent the view for each user in the search results.
     */
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
