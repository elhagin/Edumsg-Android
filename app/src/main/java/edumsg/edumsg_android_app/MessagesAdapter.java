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
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creates a View for each item in {@link MessagesActivity#recyclerView}
 *
 * Created by Omar on 22/2/2016.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.user_image_msg) ImageView userImage;
        @Bind(R.id.creator_info) TextView creatorInfo;
        @Bind(R.id.msg) TextView message;

        MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private List<Conversation> conversations;
    private Context context;
    private String sessionId;

    MessagesAdapter(Context context, List<Conversation> conversations, String sessionId)
    {
        this.context = context;
        this.conversations = conversations;
        this.sessionId = sessionId;
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {
        final Conversation conv = conversations.get(position);
        String info = "";
        String imageUrl = "";
        final DirectMessage lastDm = conv.getLastDM();
        if (lastDm.getReciever().getUsername().equals(MyAppCompatActivity.username)) {
            info = context.getString(R.string.user_info, lastDm.getSender().getName(),
                    lastDm.getSender().getUsername());
            imageUrl = lastDm.getSender().getAvatar_url();
        }
        else
        {
            info = context.getString(R.string.user_info, lastDm.getReciever().getName(),
                    lastDm.getReciever().getUsername());
            imageUrl = lastDm.getReciever().getAvatar_url();
        }
        Picasso.with(context).load(imageUrl)
                .placeholder(R.mipmap.ic_launcher).fit()
                .into(holder.userImage);
        holder.creatorInfo.setText(info);
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity main = (MyAppCompatActivity) context;
                Intent intent = new Intent(main, ProfileActivity.class);
                intent.putExtra("username", main.getUsername());
                intent.putExtra("name", main.getName());
                intent.putExtra("avatar_url", main.getAvatarUrl());
                intent.putExtra("bio", main.getBio());
                intent.putExtra("creatorId",
                        lastDm.getReciever().getUsername().equals(MyAppCompatActivity.username)
                                ? lastDm.getSender().getId()
                                : lastDm.getReciever().getId());
                intent.putExtra("userId", main.getUserId());
                main.startActivity(intent);
            }
        });
        holder.message.setText(lastDm.getDm_text());
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity main = (MyAppCompatActivity) context;
                FragmentManager fragmentManager = main.getSupportFragmentManager();
                ConversationFragment conversationFragment = new ConversationFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("convId", conv.getId());
                conversationFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(android.R.id.content, conversationFragment).addToBackStack("conv")
                        .commit();
            }
        });
        holder.creatorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppCompatActivity main = (MyAppCompatActivity) context;
                FragmentManager fragmentManager = main.getSupportFragmentManager();
                ConversationFragment conversationFragment = new ConversationFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("convId", conv.getId());
                conversationFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(android.R.id.content, conversationFragment).addToBackStack("conv")
                        .commit();
            }
        });
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_view, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder(view);
        return messageViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
