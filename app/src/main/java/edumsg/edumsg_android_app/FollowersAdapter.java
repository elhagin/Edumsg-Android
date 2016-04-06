package edumsg.edumsg_android_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;
import java.util.List;

import java.util.*;

/**
 * Created by omarelhagin on 29/3/16.
 */
public class FollowersAdapter extends ArrayAdapter<User>
{
    List<User> users;

    public FollowersAdapter(Context context, int resource, java.util.List<User> objects) {
        super(context, resource, objects);
        users = new ArrayList<>(objects.size());
        users.addAll(objects);
    }
    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((User) resultValue).getUsername();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<User> suggestions = new ArrayList<>();
                for (User user : users) {
                    if (user.getUsername().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(user);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @TargetApi(11)
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                addAll((ArrayList<User>) results.values);
            } else {
                addAll(users);
            }
            notifyDataSetChanged();
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(this.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        User user = getItem(position);
        TextView txt1 = (TextView) view.findViewById(android.R.id.text1);
        txt1.setText(user.getUsername());

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
