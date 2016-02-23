package edumsg.edumsg_android_app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Omar on 22/2/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TweetViewHolder> {

    public static class TweetViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView tweet;
        BootstrapButton retweetBtn;
        BootstrapButton replyBtn;

        TweetViewHolder(View itemView) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.user_image);
            tweet = (TextView) itemView.findViewById(R.id.tweet);
            retweetBtn = (BootstrapButton) itemView.findViewById(R.id.retweet_button);
            replyBtn = (BootstrapButton) itemView.findViewById(R.id.reply_button);
        }
    }

    List<TweetObject> tweetObjects;
    Context context;

    RVAdapter(Context context, List<TweetObject> tweetObjects)
    {
        this.context = context;
        this.tweetObjects = tweetObjects;
    }

    @Override
    public int getItemCount() {
        //TODO: use JSON
        return tweetObjects.size();
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        Picasso.with(context).load(tweetObjects.get(position).getImgUrl())
                .placeholder(R.mipmap.ic_launcher).fit()
                .into(holder.userImage);
        holder.tweet.setText(tweetObjects.get(position).getTweet());
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweets_rv, parent, false);
        TweetViewHolder tweetViewHolder = new TweetViewHolder(view);
        return tweetViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
