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

/**
 * ORM class for mapping between JSON and Java.
 */
@SuppressWarnings("unused")
public class Conversation implements Comparable {
	private ArrayList<DirectMessage> dms;
	private Integer id;
	private DirectMessage lastDM;

    public ArrayList<DirectMessage> getDms() {
        return dms;
    }

    public void setDms(ArrayList<DirectMessage> dms) {
        this.dms = dms;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DirectMessage getLastDM() {
        return lastDM;
    }

    public void setLastDM(DirectMessage lastDM) {
        this.lastDM = lastDM;
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof Conversation)
        {
            Conversation c = (Conversation) o;
            Timestamp t = Timestamp.valueOf(lastDM.getCreated_at());
            Timestamp t2 = Timestamp.valueOf(c.getLastDM().getCreated_at());
            return t2.compareTo(t);
        }
        else
        {
            return 0;
        }
    }
}
