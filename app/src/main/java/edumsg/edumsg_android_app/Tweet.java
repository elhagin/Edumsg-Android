/*
EduMsg is made available under the OSI-approved MIT license.
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
IN THE SOFTWARE.
*/

package edumsg.edumsg_android_app;

import java.sql.Timestamp;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class Tweet {
	private Integer id;
	private String tweet;
	private String imgUrl;
	private String createdAt;
	private User creator;
	private User retweeter;
	private User favoriter;
	private Integer retweets;
	private Integer favorites;
	private boolean isRetweeted;
	private boolean isFavorited;
	private ArrayList<Tweet> replies;

	public Tweet(int id, User creator, String tweet)
	{
		this.id = id;
		this.creator = creator;
		this.tweet = tweet;
	}

	public Tweet(int id, User creator, User retweeter, String tweet)
	{
		this.id = id;
        this.creator = creator;
		this.retweeter = retweeter;
		this.tweet = tweet;
	}

    public Tweet(int id, User creator, String imgUrl, String tweet)
    {
        this.id = id;
        this.creator = creator;
        this.imgUrl = imgUrl;
        this.tweet = tweet;
    }

    public Tweet(int id, User creator, User retweeter, String imgUrl, String tweet)
    {
        this.id = id;
        this.creator = creator;
        this.retweeter = retweeter;
        this.imgUrl = imgUrl;
        this.tweet = tweet;
    }

    @Override
    public String toString() {
        return "fav: " + isFavorited + ", retweet: " + isRetweeted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getRetweeter() {
        return retweeter;
    }

    public void setRetweeter(User retweeter) {
        this.retweeter = retweeter;
    }

    public User getFavoriter() {
        return favoriter;
    }

    public void setFavoriter(User favoriter) {
        this.favoriter = favoriter;
    }

    public Integer getRetweets() {
        return retweets;
    }

    public void setRetweets(Integer retweets) {
        this.retweets = retweets;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
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

    public ArrayList<Tweet> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Tweet> replies) {
        this.replies = replies;
    }
}
