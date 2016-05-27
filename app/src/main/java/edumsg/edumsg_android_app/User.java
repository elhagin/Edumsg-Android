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

/**
 * ORM class for mapping between JSON and Java.
 */
public class User implements Parcelable {
	private Integer id;
	private String username;
	private String email;
	private String encrypted_password;
	private String name;
	private String language;
	private String country;
	private String bio;
	private String website;
	private String created_at;
	private String avatar_url;
	private Boolean overlay;
	private String link_color;
	private String background_color;
	private Boolean protected_tweets;
	private String session_id;

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
        this.created_at = created_at;
        this.avatar_url = avatar_url;
        this.overlay = overlay;
        this.link_color = link_color;
        this.background_color = background_color;
        this.protected_tweets = protected_tweets;
        this.session_id = session_id;
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
        dest.writeString(encrypted_password);
        dest.writeString(name);
        dest.writeString(language);
        dest.writeString(country);
        dest.writeString(bio);
        dest.writeString(website);
        dest.writeString(created_at);
        dest.writeString(avatar_url);
        boolean[] arr = new boolean[1];
        arr[0] = overlay;
        dest.writeBooleanArray(arr);
        dest.writeString(link_color);
        dest.writeString(background_color);
        arr[0] = protected_tweets;
        dest.writeBooleanArray(arr);
        dest.writeString(session_id);
	}

	public void readFromParcel(Parcel in)
	{
		id = in.readInt();
		username = in.readString();
		email = in.readString();
		encrypted_password = in.readString();
		name = in.readString();
		language = in.readString();
		country = in.readString();
		bio = in.readString();
		website = in.readString();
		created_at = in.readString();
		avatar_url = in.readString();
		boolean[] arr = new boolean[1];
		in.readBooleanArray(arr);
        overlay = arr[0];
        arr = new boolean[1];
		link_color = in.readString();
		background_color = in.readString();
		in.readBooleanArray(arr);
        protected_tweets = arr[0];
		session_id = in.readString();
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

    public String getEncrypted_password() {
        return encrypted_password;
    }

    public void setEncrypted_password(String encrypted_password) {
        this.encrypted_password = encrypted_password;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Boolean getOverlay() {
        return overlay;
    }

    public void setOverlay(Boolean overlay) {
        this.overlay = overlay;
    }

    public String getLink_color() {
        return link_color;
    }

    public void setLink_color(String link_color) {
        this.link_color = link_color;
    }

    public String getBackground_color() {
        return background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public Boolean getProtected_tweets() {
        return protected_tweets;
    }

    public void setProtected_tweets(Boolean protected_tweets) {
        this.protected_tweets = protected_tweets;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }
}
