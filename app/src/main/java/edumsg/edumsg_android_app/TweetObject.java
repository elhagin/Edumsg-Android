package edumsg.edumsg_android_app;

import java.util.ArrayList;

/**
 * Created by Omar on 22/2/2016.
 */
public class TweetObject {
    private int tweetId;
    private int creatorId;
    private int retweeterId;
    private String imgUrl;
    private String tweet;
    private boolean isRetweeted;
    private boolean isFavorited;
    private ArrayList<TweetObject> replies;

    public TweetObject(int tweetId, int creatorId, String tweet)
    {
        this.tweetId = tweetId;
        this.creatorId = creatorId;
        this.tweet = tweet;
    }

    public TweetObject(int tweetId, int creatorId, int retweeterId, String tweet)
    {
        this.tweetId = tweetId;
        this.creatorId = creatorId;
        this.retweeterId = retweeterId;
        this.tweet = tweet;
    }

    public TweetObject(int tweetId, int creatorId, String imgUrl, String tweet)
    {
        this.tweetId = tweetId;
        this.creatorId = creatorId;
        this.imgUrl = imgUrl;
        this.tweet = tweet;
    }

    public TweetObject(int tweetId, int creatorId, int retweeterId, String imgUrl, String tweet)
    {
        this.tweetId = tweetId;
        this.creatorId = creatorId;
        this.retweeterId = retweeterId;
        this.imgUrl = imgUrl;
        this.tweet = tweet;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getRetweeterId() {
        return retweeterId;
    }

    public void setRetweeterId(int retweeterId) {
        this.retweeterId = retweeterId;
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public void setIsRetweeted(boolean isRetweeted) {
        this.isRetweeted = isRetweeted;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public ArrayList<TweetObject> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<TweetObject> replies) {
        this.replies = replies;
    }
}
