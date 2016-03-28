package edumsg.edumsg_android_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by Omar on 22/2/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TweetViewHolder> {

    public class TweetViewHolder extends RecyclerView.ViewHolder
    {

        @Bind(R.id.user_image) ImageView userImage;
        @Bind(R.id.tweet) TextView tweet;
        @Bind(R.id.retweet_button) ImageButton retweetBtn;
        @Bind(R.id.reply_button) ImageButton replyBtn;
        @Bind(R.id.favorite_button) ImageButton favoriteBtn;
        @Bind(R.id.replies_progress) ProgressBar repliesProgress;
        @Bind(R.id.replies) RelativeLayout repliesLayout;
        @Bind(R.id.reply1) TextView reply1;
        @Bind(R.id.user_image_1) ImageView replyUserImg1;
        @Bind(R.id.reply_button_1) ImageButton replyBtn1;
        @Bind(R.id.retweet_button_1) ImageButton retweetBtn1;
        @Bind(R.id.favorite_button_1) ImageButton favoriteBtn1;
        @Bind(R.id.replyLayout2) RelativeLayout replyLayout2;
        @Bind(R.id.reply2) TextView reply2;
        @Bind(R.id.user_image_2) ImageView replyUserImg2;
        @Bind(R.id.reply_button_2) ImageButton replyBtn2;
        @Bind(R.id.retweet_button_2) ImageButton retweetBtn2;
        @Bind(R.id.favorite_button_2) ImageButton favoriteBtn2;
        @Bind(R.id.replyLayout3) RelativeLayout replyLayout3;
        @Bind(R.id.reply3) TextView reply3;
        @Bind(R.id.user_image_3) ImageView replyUserImg3;
        @Bind(R.id.reply_button_3) ImageButton replyBtn3;
        @Bind(R.id.retweet_button_3) ImageButton retweetBtn3;
        @Bind(R.id.favorite_button_3) ImageButton favoriteBtn3;
        @Bind(R.id.show_more_replies) Button showMoreBtn;

        TweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private List<Tweet> tweetObjects;
    private Context context;
    private int userId;
    @BindColor(R.color.colorPrimary) int cPrimary;

    RVAdapter(Context context, List<Tweet> tweetObjects, int userId)
    {
        this.context = context;
        this.tweetObjects = tweetObjects;
        this.userId = userId;
    }

    @Override
    public int getItemCount() {
        return tweetObjects.size();
    }

    @Override
    public void onBindViewHolder(final TweetViewHolder holder, final int position) {
        final Tweet tweetObject = tweetObjects.get(position);
        holder.repliesProgress.setIndeterminate(true);
        Picasso.with(context).load(tweetObject.getImgUrl())
                .placeholder(R.mipmap.ic_launcher).fit()
                .into(holder.userImage);
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    MainActivity main = (MainActivity) context;
                    Intent intent = new Intent(main, ProfileActivity.class);
                    intent.putExtra("username", main.getUsername());
                    intent.putExtra("name", main.getName());
                    intent.putExtra("avatar_url", main.getAvatarUrl());
                    intent.putExtra("bio", main.getBio());
                    intent.putExtra("creatorId", tweetObject.getCreator().getId());
                    intent.putExtra("userId", main.getUserId());
                    main.startActivity(intent);
                }
            }
        });
        holder.tweet.setText(tweetObject.getTweet());
        holder.replyBtn.setColorFilter(Color.rgb(128, 128, 128));
        setButtonColors(tweetObject, holder.retweetBtn, holder.favoriteBtn);
        holder.retweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnTag = (String) holder.retweetBtn.getTag();
                if (btnTag.equals("notR")) {
                    retweet(tweetObject.getId());
                    tweetObject.setIsRetweeted(true);
                    int colorFilter2 = Color.rgb(0, 0, 0);
                    holder.retweetBtn.setColorFilter(colorFilter2);
                    holder.retweetBtn.setTag("R");
                } else if (btnTag.equals("R")) {
                    unretweet(tweetObject.getId());
                    tweetObject.setIsRetweeted(false);
                    int colorFilter1 = Color.rgb(128, 128, 128);
                    holder.retweetBtn.setColorFilter(colorFilter1);
                    holder.retweetBtn.setTag("notR");
                }
            }
        });
        if (tweetObject.getCreator().getId() == userId)
        {
            holder.retweetBtn.setColorFilter(Color.rgb(210, 210, 210));
            holder.retweetBtn.setClickable(false);
        }
        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnTag = (String) holder.favoriteBtn.getTag();
                if (btnTag.equals("notF")) {
                    favorite(tweetObject.getId());
                    tweetObject.setIsFavorited(true);
                    int colorFilter2 = Color.rgb(0, 0, 0);
                    holder.favoriteBtn.setColorFilter(colorFilter2);
                    holder.favoriteBtn.setTag("F");
                } else if (btnTag.equals("F")) {
                    unfavorite(tweetObject.getId());
                    tweetObject.setIsFavorited(false);
                    int colorFilter1 = Color.rgb(128, 128, 128);
                    holder.favoriteBtn.setColorFilter(colorFilter1);
                    holder.favoriteBtn.setTag("notF");
                }
            }
        });

        holder.replyBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.replyBtn.setColorFilter(Color.rgb(55, 58, 60));
                        return true;
                    case MotionEvent.ACTION_UP:
                        holder.replyBtn.setColorFilter(Color.rgb(128, 128, 128));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final EditText input = new EditText(context);
                        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input.setLines(4);
                        input.setSingleLine(false);
                        input.setBackgroundDrawable(null);
                        builder.setView(input);
                        builder.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reply(tweetObject.getId(), input.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(cPrimary);
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(cPrimary);
                            }
                        });
                        dialog.show();
                        return true;
                }
                return false;
            }
        });
        holder.tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Tweet> replies = tweetObject.getReplies();
                if (holder.repliesLayout.getVisibility() == View.VISIBLE) {
                    holder.repliesLayout.setVisibility(View.GONE);
                } else {
                    AppCompatActivity main;
                    if (context instanceof MainActivity)
                        main = (MainActivity) context;
                    else
                        main = (ProfileActivity) context;
                    Map<String, String> jsonParams = new HashMap<>();
                    jsonParams.put("queue", "TWEET");
                    jsonParams.put("method", "get_earliest_replies");
                    jsonParams.put("tweet_id", tweetObject.getId() + "");
                    jsonParams.put("user_id", userId + "");
                    JSONObject jsonRequest = new JSONObject(jsonParams);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            final ObjectMapper mapper = new ObjectMapper();
                            try {
                                final Map<String, Object> responseMap = mapper
                                        .readValue(response.toString(),
                                                new TypeReference<HashMap<String, Object>>() {
                                                });
                                if (responseMap.get("code").equals("200")) {
                                    ArrayList repliesMap = (ArrayList) responseMap.get("earliest_replies");
                                    ArrayList<Tweet> replies = getEarliestReplies(repliesMap);
                                    tweetObject.setReplies(replies);
                                    if (!replies.isEmpty()) {
                                        Tweet firstReply = replies.get(0);
                                        holder.reply1.setText(firstReply.getTweet());
                                        Picasso.with(context).load(firstReply.getCreator().getAvatarUrl())
                                                .placeholder(R.mipmap.ic_launcher).fit()
                                                .into(holder.replyUserImg1);
                                        holder.replyBtn1.setColorFilter(Color.rgb(128, 128, 128));
                                        setButtonColors(firstReply, holder.retweetBtn1, holder.favoriteBtn1);

                                        if (replies.size() > 1) {
                                            Tweet secondReply = replies.get(1);
                                            holder.reply2.setText(secondReply.getTweet());
                                            Picasso.with(context).load(secondReply.getCreator().getAvatarUrl())
                                                    .placeholder(R.mipmap.ic_launcher).fit()
                                                    .into(holder.replyUserImg2);
                                            holder.replyBtn2.setColorFilter(Color.rgb(128, 128, 128));
                                            setButtonColors(secondReply, holder.retweetBtn2, holder.favoriteBtn2);
                                            holder.replyLayout2.setVisibility(View.VISIBLE);
                                        }
                                        if (replies.size() > 2) {
                                            Tweet thirdReply = replies.get(2);
                                            holder.reply3.setText(thirdReply.getTweet());
                                            Picasso.with(context).load(thirdReply.getCreator().getAvatarUrl())
                                                    .placeholder(R.mipmap.ic_launcher).fit()
                                                    .into(holder.replyUserImg3);
                                            holder.replyBtn3.setColorFilter(Color.rgb(128, 128, 128));
                                            setButtonColors(thirdReply, holder.retweetBtn3, holder.favoriteBtn3);
                                            holder.replyLayout3.setVisibility(View.VISIBLE);
                                        }
                                        if (replies.size() > 3) {
                                            holder.showMoreBtn.setVisibility(View.VISIBLE);
                                        }
                                        holder.repliesProgress.setVisibility(View.GONE);
                                        holder.repliesLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.repliesProgress.setVisibility(View.GONE);
                                        holder.repliesLayout.setVisibility(View.GONE);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("JSONMapper", e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (volleyError.networkResponse != null
                                    && volleyError.networkResponse.data != null
                                    && volleyError.networkResponse.statusCode == 400) {
                                try {
                                    String errorJson = new String(volleyError.networkResponse.data);
                                    JSONObject errorObj = new JSONObject(errorJson);
                                    String error = errorObj.getString("message");
                                } catch (JSONException e) {
                                    Log.e("Response Error Msg", e.getMessage());
                                }
                            } else {
                                Log.e("Volley", volleyError.toString());
                            }
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            return headers;
                        }

                        ;
                    };
                    jsonObjectRequest.setTag("Request");
                    if (main instanceof MainActivity)
                        ((MainActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
                    else
                        ((ProfileActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
                    holder.repliesProgress.setVisibility(View.VISIBLE);
                }
            }
        });

        final Handler handler = new Handler();
        holder.replyBtn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.replyBtn1.setColorFilter(Color.rgb(55, 58, 60));
                        return true;
                    case MotionEvent.ACTION_UP:
                        holder.replyBtn1.setColorFilter(Color.rgb(128, 128, 128));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final EditText input = new EditText(context);
                        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input.setLines(4);
                        input.setSingleLine(false);
                        input.setBackgroundDrawable(null);
                        builder.setView(input);
                        builder.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (holder.replyBtn1.getVisibility() == View.VISIBLE) {
                                            while (tweetObject.getReplies() == null) ;
                                            reply(tweetObject.getReplies().get(0).getId(),
                                                    input.getText().toString());
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(cPrimary);
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(cPrimary);
                            }
                        });
                        dialog.show();
                        return true;
                }
                return false;
            }
        });

        holder.replyBtn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.replyBtn2.setColorFilter(Color.rgb(55, 58, 60));
                        return true;
                    case MotionEvent.ACTION_UP:
                        holder.replyBtn2.setColorFilter(Color.rgb(128, 128, 128));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final EditText input = new EditText(context);
                        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input.setLines(4);
                        input.setSingleLine(false);
                        input.setBackgroundDrawable(null);
                        builder.setView(input);
                        builder.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (holder.replyBtn2.getVisibility() == View.VISIBLE) {
                                            while (tweetObject.getReplies() == null) ;
                                            reply(tweetObject.getReplies().get(1).getId(),
                                                    input.getText().toString());
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(cPrimary);
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(cPrimary);
                            }
                        });
                        dialog.show();
                        return true;
                }
                return false;
            }
        });

        holder.replyBtn3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.replyBtn3.setColorFilter(Color.rgb(55, 58, 60));
                        return true;
                    case MotionEvent.ACTION_UP:
                        holder.replyBtn3.setColorFilter(Color.rgb(128, 128, 128));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final EditText input = new EditText(context);
                        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input.setLines(4);
                        input.setSingleLine(false);
                        input.setBackgroundDrawable(null);
                        builder.setView(input);
                        builder.setPositiveButton("Reply", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (holder.replyBtn3.getVisibility() == View.VISIBLE) {
                                            while (tweetObject.getReplies() == null) ;
                                            reply(tweetObject.getReplies().get(2).getId(),
                                                    input.getText().toString());
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(cPrimary);
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(cPrimary);
                            }
                        });
                        dialog.show();
                        return true;
                }
                return false;
            }
        });

        holder.retweetBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String btnTag = (String) holder.retweetBtn1.getTag();
                if (holder.retweetBtn1.getVisibility() == View.VISIBLE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            while (tweetObject.getReplies() == null) ;
                            Tweet reply = tweetObject.getReplies().get(0);
                            if (btnTag.equals("notR")) {
                                retweet(reply.getId());
                                reply.setIsRetweeted(true);
                                int colorFilter2 = Color.rgb(0, 0, 0);
                                holder.retweetBtn1.setColorFilter(colorFilter2);
                                holder.retweetBtn1.setTag("R");
                            } else if (btnTag.equals("R")) {
                                unretweet(reply.getId());
                                reply.setIsRetweeted(false);
                                int colorFilter1 = Color.rgb(128, 128, 128);
                                holder.retweetBtn1.setColorFilter(colorFilter1);
                                holder.retweetBtn1.setTag("notR");
                            }
                        }
                    });
                }
            }
        });
        holder.favoriteBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String btnTag = (String) holder.favoriteBtn1.getTag();
                if (holder.favoriteBtn1.getVisibility() == View.VISIBLE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            while(tweetObject.getReplies() == null);
                            Tweet reply = tweetObject.getReplies().get(0);
                            if (btnTag.equals("notF")) {
                                favorite(reply.getId());
                                reply.setIsFavorited(true);
                                int colorFilter2 = Color.rgb(0, 0, 0);
                                holder.favoriteBtn1.setColorFilter(colorFilter2);
                                holder.favoriteBtn1.setTag("F");
                            } else if (btnTag.equals("F")) {
                                unfavorite(reply.getId());
                                reply.setIsFavorited(false);
                                int colorFilter1 = Color.rgb(128, 128, 128);
                                holder.favoriteBtn1.setColorFilter(colorFilter1);
                                holder.favoriteBtn1.setTag("notF");
                            }
                        }
                    });
                }
            }
        });

        holder.retweetBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String btnTag = (String) holder.retweetBtn2.getTag();
                if (holder.retweetBtn2.getVisibility() == View.VISIBLE)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            while(tweetObject.getReplies() == null);
                            Tweet reply = tweetObject.getReplies().get(1);
                            if (btnTag.equals("notR")) {
                                retweet(reply.getId());
                                reply.setIsRetweeted(true);
                                int colorFilter2 = Color.rgb(0, 0, 0);
                                holder.retweetBtn2.setColorFilter(colorFilter2);
                                holder.retweetBtn2.setTag("R");
                            } else if (btnTag.equals("R")) {
                                unretweet(reply.getId());
                                reply.setIsRetweeted(false);
                                int colorFilter1 = Color.rgb(128, 128, 128);
                                holder.retweetBtn2.setColorFilter(colorFilter1);
                                holder.retweetBtn2.setTag("notR");
                            }
                        }
                    });
                }
            }
        });
        holder.favoriteBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String btnTag = (String) holder.favoriteBtn2.getTag();
                if (holder.favoriteBtn2.getVisibility() == View.VISIBLE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            while(tweetObject.getReplies() == null);
                            Tweet reply = tweetObject.getReplies().get(1);
                            if (btnTag.equals("notF")) {
                                favorite(reply.getId());
                                reply.setIsFavorited(true);
                                int colorFilter2 = Color.rgb(0, 0, 0);
                                holder.favoriteBtn2.setColorFilter(colorFilter2);
                                holder.favoriteBtn2.setTag("F");
                            } else if (btnTag.equals("F")) {
                                unfavorite(reply.getId());
                                reply.setIsFavorited(false);
                                int colorFilter1 = Color.rgb(128, 128, 128);
                                holder.favoriteBtn2.setColorFilter(colorFilter1);
                                holder.favoriteBtn2.setTag("notF");
                            }
                        }
                    });
                }
            }
        });

        holder.retweetBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String btnTag = (String) holder.retweetBtn3.getTag();
                if (holder.retweetBtn3.getVisibility() == View.VISIBLE)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            while(tweetObject.getReplies() == null);
                            Tweet reply = tweetObject.getReplies().get(2);
                            if (btnTag.equals("notR")) {
                                retweet(reply.getId());
                                reply.setIsRetweeted(true);
                                int colorFilter2 = Color.rgb(0, 0, 0);
                                holder.retweetBtn3.setColorFilter(colorFilter2);
                                holder.retweetBtn3.setTag("R");
                            } else if (btnTag.equals("R")) {
                                unretweet(reply.getId());
                                reply.setIsRetweeted(false);
                                int colorFilter1 = Color.rgb(128, 128, 128);
                                holder.retweetBtn3.setColorFilter(colorFilter1);
                                holder.retweetBtn3.setTag("notR");
                            }
                        }
                    });
                }
            }
        });
        holder.favoriteBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String btnTag = (String) holder.favoriteBtn3.getTag();
                if (holder.favoriteBtn3.getVisibility() == View.VISIBLE) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            while(tweetObject.getReplies() == null);
                            Tweet reply = tweetObject.getReplies().get(2);
                            if (btnTag.equals("notF")) {
                                favorite(reply.getId());
                                reply.setIsFavorited(true);
                                int colorFilter2 = Color.rgb(0, 0, 0);
                                holder.favoriteBtn3.setColorFilter(colorFilter2);
                                holder.favoriteBtn3.setTag("F");
                            } else if (btnTag.equals("F")) {
                                unfavorite(reply.getId());
                                reply.setIsFavorited(false);
                                int colorFilter1 = Color.rgb(128, 128, 128);
                                holder.favoriteBtn3.setColorFilter(colorFilter1);
                                holder.favoriteBtn3.setTag("notF");
                            }
                        }
                    });
                }
            }
        });

        holder.showMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity main;
                if (context instanceof MainActivity)
                    main = (MainActivity) context;
                else
                    main = (ProfileActivity) context;
                FragmentManager fragmentManager = main.getSupportFragmentManager();
                MainActivityFragment mainActivityFragment = new MainActivityFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("tweetId", tweetObject.getId());
                bundle.putInt("userId", userId);
                mainActivityFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(android.R.id.content, mainActivityFragment).addToBackStack("replies")
                        .commit();
            }
        });
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

    private void retweet(int tweetId)
    {
        AppCompatActivity main;
        if (context instanceof MainActivity)
            main = (MainActivity) context;
        else
            main = (ProfileActivity) context;
//        final LoadToast loadToast = new LoadToast(main);
//        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "retweet");
        jsonParams.put("tweet_id", tweetId+"");
        jsonParams.put("user_id", userId+"");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
//                        loadToast.success();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
//                        Toast.makeText(main, error, Toast.LENGTH_LONG).show();
//                        if (error.toLowerCase().contains("username"))
//                        {
//                            mUsername.setError(error);
//                            mUsername.requestFocus();
//                        }
//                        if (error.toLowerCase().contains("password")) {
//                            mPassword.setError(error);
//                            mPassword.requestFocus();
//                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
//                    Toast.makeText(main, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Volley", volleyError.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag("Request");
        if (main instanceof MainActivity)
            ((MainActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
        else
            ((ProfileActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void unretweet(int tweetId)
    {
        AppCompatActivity main;
        if (context instanceof MainActivity)
            main = (MainActivity) context;
        else
            main = (ProfileActivity) context;
//        final LoadToast loadToast = new LoadToast(main);
//        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "unretweet");
        jsonParams.put("tweet_id", tweetId+"");
        jsonParams.put("user_id", userId+"");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
//                        loadToast.success();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
//                        Toast.makeText(main, error, Toast.LENGTH_LONG).show();
//                        if (error.toLowerCase().contains("username"))
//                        {
//                            mUsername.setError(error);
//                            mUsername.requestFocus();
//                        }
//                        if (error.toLowerCase().contains("password")) {
//                            mPassword.setError(error);
//                            mPassword.requestFocus();
//                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
//                    Toast.makeText(main, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Volley", volleyError.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag("Request");
        if (main instanceof MainActivity)
            ((MainActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
        else
            ((ProfileActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void reply(int tweetId, String tweet)
    {
        AppCompatActivity main;
        if (context instanceof MainActivity)
            main = (MainActivity) context;
        else
            main = (ProfileActivity) context;
        final LoadToast loadToast = new LoadToast(main);
        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "reply");
        jsonParams.put("tweet_id", tweetId+"");
        jsonParams.put("tweet_text", tweet);
        jsonParams.put("creator_id", userId+"");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
                        loadToast.success();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
//                        Toast.makeText(main, error, Toast.LENGTH_LONG).show();
//                        if (error.toLowerCase().contains("username"))
//                        {
//                            mUsername.setError(error);
//                            mUsername.requestFocus();
//                        }
//                        if (error.toLowerCase().contains("password")) {
//                            mPassword.setError(error);
//                            mPassword.requestFocus();
//                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
//                    Toast.makeText(main, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Volley", volleyError.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag("Request");
        if (main instanceof MainActivity)
            ((MainActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
        else
            ((ProfileActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void favorite(int tweetId)
    {
        AppCompatActivity main;
        if (context instanceof MainActivity)
            main = (MainActivity) context;
        else
            main = (ProfileActivity) context;

//        final LoadToast loadToast = new LoadToast(main);
//        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "favorite");
        jsonParams.put("tweet_id", tweetId+"");
        jsonParams.put("user_id", userId+"");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
//                        loadToast.success();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
//                        Toast.makeText(main, error, Toast.LENGTH_LONG).show();
//                        if (error.toLowerCase().contains("username"))
//                        {
//                            mUsername.setError(error);
//                            mUsername.requestFocus();
//                        }
//                        if (error.toLowerCase().contains("password")) {
//                            mPassword.setError(error);
//                            mPassword.requestFocus();
//                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
//                    Toast.makeText(main, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Volley", volleyError.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag("Request");
        if (main instanceof MainActivity)
            ((MainActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
        else
            ((ProfileActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private void unfavorite(int tweetId)
    {
        AppCompatActivity main;
        if (context instanceof MainActivity)
            main = (MainActivity) context;
        else
            main = (ProfileActivity) context;
//        final LoadToast loadToast = new LoadToast(main);
//        loadToast.show();
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("queue", "TWEET");
        jsonParams.put("method", "unfavorite");
        jsonParams.put("tweet_id", tweetId+"");
        jsonParams.put("user_id", userId+"");
        JSONObject jsonRequest = new JSONObject(jsonParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                MainActivity.requestUrl, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ObjectMapper mapper = new ObjectMapper();
                try {
                    final Map<String, Object> responseMap = mapper
                            .readValue(response.toString(),
                                    new TypeReference<HashMap<String, Object>>() {
                                    });
                    if (responseMap.get("code").equals("200"))
                    {
//                        loadToast.success();
                    }
                }
                catch (Exception e)
                {
                    Log.e("JSONMapper", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                loadToast.error();
                if (volleyError.networkResponse != null
                        && volleyError.networkResponse.data != null
                        && volleyError.networkResponse.statusCode == 400)
                {
                    try {
                        String errorJson = new String(volleyError.networkResponse.data);
                        JSONObject errorObj = new JSONObject(errorJson);
                        String error = errorObj.getString("message");
//                        Toast.makeText(main, error, Toast.LENGTH_LONG).show();
//                        if (error.toLowerCase().contains("username"))
//                        {
//                            mUsername.setError(error);
//                            mUsername.requestFocus();
//                        }
//                        if (error.toLowerCase().contains("password")) {
//                            mPassword.setError(error);
//                            mPassword.requestFocus();
//                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("Response Error Msg", e.getMessage());
                    }
                }
                else {
//                    Toast.makeText(main, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Volley", volleyError.toString());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            };
        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag("Request");
        if (main instanceof MainActivity)
            ((MainActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
        else
            ((ProfileActivity) main).getVolleyRequestQueue().add(jsonObjectRequest);
    }

    private ArrayList<Tweet> getEarliestReplies(ArrayList repliesMap)
    {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Tweet> replies = new ArrayList<>();
        if (repliesMap.isEmpty())
            return replies;
        try {
            Map<String, Object> tweetJsonObj = mapper
                    .readValue(mapper.writeValueAsString(repliesMap.get(0)),
                            new TypeReference<HashMap<String, Object>>() {
                            });
            final int replyId = (int) tweetJsonObj.get("id");
            String tweetText = (String) tweetJsonObj.get("tweet_text");
            LinkedHashMap creatorMap = (LinkedHashMap) tweetJsonObj.get("creator");
            int creatorId = (int) creatorMap.get("id");
            String creatorUsername = (String) creatorMap.get("username");
            String creatorName = (String) creatorMap.get("name");
            String avatarUrl = (String) creatorMap.get("avatar_url");
            User creator = new User();
            creator.setId(creatorId);
            creator.setUsername(creatorUsername);
            creator.setName(creatorName);
            creator.setAvatarUrl(avatarUrl);
            Tweet reply1 = new Tweet(replyId, creator, tweetText);
            reply1.setIsFavorited((boolean) tweetJsonObj.get("is_favorited"));
            reply1.setIsRetweeted((boolean) tweetJsonObj.get("is_retweeted"));
            replies.add(reply1);
            if (repliesMap.size() > 1)
            {
                Map<String, Object> tweetJsonObj2 = mapper
                        .readValue(mapper.writeValueAsString(repliesMap.get(1)),
                                new TypeReference<HashMap<String, Object>>() {
                                });
                final int replyId2 = (int) tweetJsonObj2.get("id");
                String tweetText2 = (String) tweetJsonObj2.get("tweet_text");
                LinkedHashMap creatorMap2 = (LinkedHashMap) tweetJsonObj2.get("creator");
                int creatorId2 = (int) creatorMap2.get("id");
                String creatorUsername2 = (String) creatorMap2.get("username");
                String creatorName2 = (String) creatorMap2.get("name");
                String avatarUrl2 = (String) creatorMap2.get("avatar_url");
                User creator2 = new User();
                creator2.setId(creatorId2);
                creator2.setUsername(creatorUsername2);
                creator2.setName(creatorName2);
                creator2.setAvatarUrl(avatarUrl2);
                Tweet reply2 = new Tweet(replyId2, creator2, tweetText2);
                reply2.setIsFavorited((boolean) tweetJsonObj2.get("is_favorited"));
                reply2.setIsRetweeted((boolean) tweetJsonObj2.get("is_retweeted"));
                replies.add(reply2);
            }
            if (repliesMap.size() > 2)
            {
                Map<String, Object> tweetJsonObj3 = mapper
                        .readValue(mapper.writeValueAsString(repliesMap.get(2)),
                                new TypeReference<HashMap<String, Object>>() {
                                });
                final int replyId3 = (int) tweetJsonObj3.get("id");
                String tweetText3 = (String) tweetJsonObj3.get("tweet_text");
                LinkedHashMap creatorMap3 = (LinkedHashMap) tweetJsonObj3.get("creator");
                int creatorId3 = (int) creatorMap3.get("id");
                String creatorUsername3 = (String) creatorMap3.get("username");
                String creatorName3 = (String) creatorMap3.get("name");
                String avatarUrl3 = (String) creatorMap3.get("avatar_url");
                User creator3 = new User();
                creator3.setId(creatorId3);
                creator3.setUsername(creatorUsername3);
                creator3.setName(creatorName3);
                creator3.setAvatarUrl(avatarUrl3);
                Tweet reply3 = new Tweet(replyId3, creator3, tweetText3);
                reply3.setIsFavorited((boolean) tweetJsonObj3.get("is_favorited"));
                reply3.setIsRetweeted((boolean) tweetJsonObj3.get("is_retweeted"));
                replies.add(reply3);
            }
            if (repliesMap.size() > 3)
            {
                replies.add(new Tweet(0, null, null));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return replies;
    }

    private void setButtonColors(Tweet tweetObject, ImageButton retweetBtn,
                                 ImageButton favoriteBtn)
    {
        if (tweetObject.getCreator().getId() == userId)
        {
            retweetBtn.setColorFilter(Color.rgb(210, 210, 210));
            retweetBtn.setClickable(false);
        }
        else
        {
            if (tweetObject.isRetweeted())
            {
                retweetBtn.setColorFilter(Color.rgb(0, 0, 0));
                retweetBtn.setTag("R");
            }
            else
            {
                retweetBtn.setColorFilter(Color.rgb(128, 128, 128));
                retweetBtn.setTag("notR");
            }
        }

        if (tweetObject.isFavorited())
        {
            favoriteBtn.setColorFilter(Color.rgb(0, 0, 0));
            favoriteBtn.setTag("F");
        }
        else
        {
            favoriteBtn.setColorFilter(Color.rgb(128, 128, 128));
            favoriteBtn.setTag("notF");
        }
    }
}
