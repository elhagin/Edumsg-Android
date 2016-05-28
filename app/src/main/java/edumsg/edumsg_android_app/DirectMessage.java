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

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

/**
 * ORM class for mapping between JSON and Java.
 */
@SuppressWarnings("unused")
public class DirectMessage implements Parcelable, Comparable {
	private Integer id;
	private User sender;
	private User reciever;
	private String dm_text;
	private String image_url;
	private Boolean read;
	private String created_at;
	private String userImgUrl;

    public DirectMessage() {
    }

    public DirectMessage(Parcel in)
    {
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public DirectMessage createFromParcel(Parcel in) {
                    return new DirectMessage(in);
                }

                public DirectMessage[] newArray(int size) {
                    return new DirectMessage[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(sender, flags);
        dest.writeParcelable(reciever, flags);
        dest.writeString(dm_text);
        dest.writeString(image_url);
        boolean[] arr = new boolean[1];
        arr[0] = read;
        dest.writeBooleanArray(arr);
        dest.writeString(created_at);
        dest.writeString(userImgUrl);
    }

    public void readFromParcel(Parcel in)
    {
        id = in.readInt();
        sender = in.readParcelable(null);
        reciever = in.readParcelable(null);
        dm_text = in.readString();
        image_url = in.readString();
        boolean[] arr = new boolean[1];
        in.readBooleanArray(arr);
        read = arr[0];
        created_at = in.readString();
        userImgUrl = in.readString();
    }

    public void setId(int id) {
		this.id = id;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public void setReciever(User reciever) {
		this.reciever = reciever;
	}

	public void setDmText(String dm_text) {
		this.dm_text = dm_text;
	}

	public void setImageUrl(String image_url) {
		this.image_url = image_url;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void setCreatedAt(Timestamp created_at) {
		this.created_at = created_at.toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getSender() {
		return sender;
	}

	public User getReciever() {
		return reciever;
	}

	public String getDm_text() {
		return dm_text;
	}

	public void setDm_text(String dm_text) {
		this.dm_text = dm_text;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUserImgUrl() {
		return userImgUrl;
	}

	public void setUserImgUrl(String userImgUrl) {
		this.userImgUrl = userImgUrl;
	}


	@Override
	public int compareTo(Object o)
	{
		if (o instanceof DirectMessage)
		{
			DirectMessage c = (DirectMessage) o;
			Timestamp t = Timestamp.valueOf(getCreated_at());
			Timestamp t2 = Timestamp.valueOf(c.getCreated_at());
			return t.compareTo(t2);
		}
		else
		{
			return 0;
		}
	}

}
