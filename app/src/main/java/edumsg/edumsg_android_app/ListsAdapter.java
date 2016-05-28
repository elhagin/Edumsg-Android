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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creates a View for each item in {@link ListsActivity#listsRecyclerView}.
 *
 * Created by omarelhagin on 30/3/16.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListItemViewHolder>
{
    /**
     * Custom ViewHolder implementation of {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * to represent the view for each list item.
     */
    public static class ListItemViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.list_item_layout) RelativeLayout listItemLayout;
        @Bind(R.id.list_name) TextView listName;
        @Bind(R.id.list_description) TextView listDescription;
        @Bind(R.id.list_creator) TextView listCreator;

        ListItemViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private ArrayList<List> lists;
    private Context context;
    private int userId;

    public ListsAdapter(ArrayList<List> lists, Context context, int userId) {
        this.lists = lists;
        this.context = context;
        this.userId = userId;
    }

    @Override
    public int getItemCount()
    {
        return lists.size();
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder holder, final int position)
    {
        final List list = lists.get(position);
        holder.listName.setText(list.getName());
        holder.listDescription.setText(list.getDescription());
        String creator = context.getString(R.string.list_creator, list.getCreator().getName(),
                list.getCreator().getUsername());
        holder.listCreator.setText(creator);
        holder.listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity parentActivity = (MyAppCompatActivity) context;
                Intent intent = new Intent(parentActivity, ListActivity.class);
                intent.putExtra("userId", parentActivity.getUserId());
                intent.putExtra("listId", list.getId());
                intent.putExtra("listName", list.getName());
                parentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view, parent, false);
        ListItemViewHolder listItemViewHolder = new ListItemViewHolder(view);
        return listItemViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
