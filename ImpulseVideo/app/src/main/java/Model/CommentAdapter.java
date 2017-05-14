package Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.x00075294.impulsevideo.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by thomas murphy on 1/05/2017.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    private static final String TAG = "IMP: Comment List Loader";
    private String[] imageURLArray;
    public List<Comment> vids;
    private final Context mContext;

    public CommentAdapter(Context context, ArrayList<Comment> arrayOfComment) {
        super(context, 0, arrayOfComment);
        this.mContext = context;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Comment com = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
        }
        TextView user = (TextView) convertView.findViewById(R.id.commentUser);
        TextView content = (TextView) convertView.findViewById(R.id.commentContent);
        assert com != null;
        user.setText(com.getProfileID());
        content.setText(com.getCommentContent());
        // Return the completed view to render on screen
        return convertView;
    }
}
