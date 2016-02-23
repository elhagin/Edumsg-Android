package edumsg.edumsg_android_app;

/**
 * Created by Omar on 22/2/2016.
 */
public class TweetObject {
    private String imgUrl;
    private String tweet;

    public TweetObject() {}

    public TweetObject(String imgUrl, String tweet)
    {
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
}
