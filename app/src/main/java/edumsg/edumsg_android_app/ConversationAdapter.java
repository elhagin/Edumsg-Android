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
import android.provider.Telephony;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.*;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creates a View for each item in {@link ConversationFragment#conversationRV}
 *
 * Created by omarelhagin on 2/4/16.
 */
public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    /**
     * Custom ViewHolder implementation of {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * to represent the view for received messages.
     */
    public static class ReceivedViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.received_username)
        TextView receivedUsername;
        @Bind(R.id.message_text)
        TextView messageView;
        @Bind(R.id.time_text)
        TextView timeText;

        ReceivedViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Custom ViewHolder implementation of {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * to represent the view for sent messages.
     */
    public static class SentViewHolder extends RecyclerView.ViewHolder
    {
        //        @Bind(R.id.user_img)
//        ImageView userImgView;
        @Bind(R.id.message_text)
        TextView messageView;
        @Bind(R.id.time_text)
        TextView timeText;

        SentViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private java.util.List<DirectMessage> messages;
    private Context context;
    private String sessionId;

    ConversationAdapter(List<DirectMessage> messages, Context context, String sessionId)
    {
        this.messages = messages;
        this.context = context;
        this.sessionId = sessionId;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().getUsername().equals(MyAppCompatActivity.username))
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        ReceivedViewHolder conversationViewHolder = new_user ReceivedViewHolder(view);
//        return conversationViewHolder;
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sent_msg, parent, false);
            return new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_received_msg, parent, false);
            return new ReceivedViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        DirectMessage message = messages.get(position);
        if (holder instanceof SentViewHolder)
        {
            ((SentViewHolder) holder).messageView.setText(message.getDm_text());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Timestamp.valueOf(message.getCreated_at()));
            String hour = calendar.get(Calendar.HOUR_OF_DAY)+"";
            if (hour.length() == 1)
                hour = "0" + hour;
            String minute = calendar.get(Calendar.MINUTE)+"";
            if (minute.length() == 1)
                minute = "0" + minute;
            String time = context.getString(R.string.time_format, hour, minute);
            ((SentViewHolder) holder).timeText.setText(time);
        }
        else
        {
            ((ReceivedViewHolder) holder).receivedUsername.setText(message.getSender().getName());
            ((ReceivedViewHolder) holder).messageView.setText(message.getDm_text());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Timestamp.valueOf(message.getCreated_at()));
            String hour = calendar.get(Calendar.HOUR_OF_DAY)+"";
            if (hour.length() == 1)
                hour = "0" + hour;
            String minute = calendar.get(Calendar.MINUTE)+"";
            if (minute.length() == 1)
                minute = "0" + minute;
            String time = context.getString(R.string.time_format, hour, minute);
            ((ReceivedViewHolder) holder).timeText.setText(time);
        }
//        DirectMessage message = messages.get(position);
//        Picasso.with(context).load(message.getSender().getAvatar_url())
//                .placeholder(R.mipmap.ic_launcher)
//                .fit()
//                .into(holder.userImgView);
//        holder.messageView.setText(message.getDm_text());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

//    @Override
//    public int getCount() {
//        return messages.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return messages.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return messages.get(position).getId();
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View v = null;
//        DirectMessage message = messages.get(position);
//        ReceivedViewHolder receivedViewHolder;
//        SentViewHolder sentViewHolder;
//
//        if (message.getSender().getId() == userId)
//        {
//            if (convertView == null)
//            {
//                v = LayoutInflater.from(context).inflate(R.layout.chat_sent_msg, null, false);
//                sentViewHolder = new_user SentViewHolder();
//
//                sentViewHolder.messageView.setText(message.getDm_text());
//            }
//        }
//    }

    public void setMessages(List<DirectMessage> messages) {
        this.messages = messages;
    }
}
