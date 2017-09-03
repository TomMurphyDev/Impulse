package Model;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.impulsevid.x00075294.impulsevideo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import DTO.CommentDto;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thomas Murphy X00075294 on 1/05/2017.
 */

public class CommentAdapter extends ArrayAdapter<CommentDto> {
    private static final String TAG = "IMP:Comment";
    private final Context mContext;
    public List<Comment> vids;
    private String[] imageURLArray;

    public CommentAdapter(Context context, ArrayList<CommentDto> arrayOfComment) {
        super(context, 0, arrayOfComment);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final CommentDto com = getItem(position);
        assert com != null;
        Log.v(TAG, com.getProfileName() + "" + com.getProfileId());
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
        }
        assert com != null;
        TextView user = (TextView) convertView.findViewById(R.id.commentUser);
        TextView content = (TextView) convertView.findViewById(R.id.commentContent);
        content.setTextColor(ColorStateList.valueOf(Color.BLACK));
        CircleImageView pp = (CircleImageView) convertView.findViewById(R.id.profilepic_comment);
        user.setText(com.getProfileName());
        content.setText(com.getContent());
        if (com.getThumbUrl() != null || !com.getThumbUrl().isEmpty()) {
            Picasso.with(getContext()).load(com.getThumbUrl()).into(pp);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
