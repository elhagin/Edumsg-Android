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
    private int userId;

    MessagesAdapter(Context context, List<Conversation> conversations, int userId)
    {
        this.context = context;
        this.conversations = conversations;
        this.userId = userId;
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
        if (lastDm.getReciever().getId() == userId) {
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
                        lastDm.getReciever().getId() == userId ? lastDm.getSender().getId()
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
                bundle.putInt("userId", userId);
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
