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
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class User implements Parcelable {
	private Integer id;
	private String username;
	private String email;
	private String encryptedPassword;
	private String name;
	private String language;
	private String country;
	private String bio;
	private String website;
	private String createdAt;
	private String avatarUrl;
	private Boolean overlay;
	private String linkColor;
	private String backgroundColor;
	private Boolean protectedTweets;
	private String sessionId;

	public User() {	}

    public User(Integer id, String username, String email, String name, String language,
                String country, String bio, String website, String created_at, String avatar_url,
                Boolean overlay, String link_color, String background_color,
                Boolean protected_tweets, String session_id) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.language = language;
        this.country = country;
        this.bio = bio;
        this.website = website;
        this.createdAt = created_at;
        this.avatarUrl = avatar_url;
        this.overlay = overlay;
        this.linkColor = link_color;
        this.backgroundColor = background_color;
        this.protectedTweets = protected_tweets;
        this.sessionId = session_id;
    }

    public User(Parcel in)
	{
		readFromParcel(in);
	}

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public User createFromParcel(Parcel in) {
                    return new User(in);
                }

                public User[] newArray(int size) {
                    return new User[size];
                }
            };

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(encryptedPassword);
        dest.writeString(name);
        dest.writeString(language);
        dest.writeString(country);
        dest.writeString(bio);
        dest.writeString(website);
        dest.writeString(createdAt);
        dest.writeString(avatarUrl);
        boolean[] arr = new boolean[1];
        arr[0] = overlay;
        dest.writeBooleanArray(arr);
        dest.writeString(linkColor);
        dest.writeString(backgroundColor);
        arr[0] = protectedTweets;
        dest.writeBooleanArray(arr);
        dest.writeString(sessionId);
	}

	public void readFromParcel(Parcel in)
	{
		id = in.readInt();
		username = in.readString();
		email = in.readString();
		encryptedPassword = in.readString();
		name = in.readString();
		language = in.readString();
		country = in.readString();
		bio = in.readString();
		website = in.readString();
		createdAt = in.readString();
		avatarUrl = in.readString();
		boolean[] arr = new boolean[1];
		in.readBooleanArray(arr);
        overlay = arr[0];
        arr = new boolean[1];
		linkColor = in.readString();
		backgroundColor = in.readString();
		in.readBooleanArray(arr);
        protectedTweets = arr[0];
		sessionId = in.readString();
	}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getOverlay() {
        return overlay;
    }

    public void setOverlay(Boolean overlay) {
        this.overlay = overlay;
    }

    public String getLinkColor() {
        return linkColor;
    }

    public void setLinkColor(String linkColor) {
        this.linkColor = linkColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Boolean getProtectedTweets() {
        return protectedTweets;
    }

    public void setProtectedTweets(Boolean protectedTweets) {
        this.protectedTweets = protectedTweets;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }
}
