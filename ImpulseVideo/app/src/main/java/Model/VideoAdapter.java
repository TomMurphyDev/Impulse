package Model;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.x00075294.impulsevideo.MainActivity;
import com.example.x00075294.impulsevideo.R;
import com.example.x00075294.impulsevideo.ViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import Model.Video;

/**
 * Created by Thomas Murphy on 03/04/2017.
 */
public class VideoAdapter extends ArrayAdapter<Video> {

    private static final String TAG = "IMP: List Loader";
    private String[] imageURLArray;
    public List<Video> vids;
    private Context mContext;
    public VideoAdapter(Context context, int resource, List<Video> objects) {
        super(context, resource, objects);
    }

    public VideoAdapter(Context context, ArrayList<Video> arrayOfVideo) {
        super(context, 0, arrayOfVideo);
        this.mContext = context;
    }
    private void loadPreview(Video v) {
        Log.v(TAG, "Load preview ..... ");
        Intent intent = new Intent(mContext, ViewActivity.class);
        intent.putExtra("videoUri", v.getStreamUrl());
        intent.putExtra("title",v.getTitle());
        intent.putExtra("desc",v.getDescription());
        intent.putExtra("prof",v.getProfileID());
        intent.putExtra("vidId",v.getId());
        intent.putExtra("userId",v.getProfileID());
        mContext.startActivity(intent);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        View row = convertView;
        final Video vid = getItem(position);

        VideoHolder holder = null;
        // Check if an existing view is being reused, otherwise inflate the view
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.item_video, parent, false);
        }
        row.setTag(vid);
        row.setOnClickListener(new AdapterView.OnClickListener(){
            @Override
            public void onClick(View view) {
                loadPreview(vid);
            }
        });
        holder = new VideoHolder();
        holder.title = (TextView) row.findViewById(R.id.title_list);
        holder.desc = (TextView) row.findViewById(R.id.list_desc);
        holder.prof = (TextView) row.findViewById(R.id.prof);
        holder.thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
        holder.date = (TextView) row.findViewById(R.id.item_video_date);
        //load image directly
        assert vid != null;
        if(!vid.getThumbUrl().isEmpty())
        {
            Picasso.with(getContext()).load(vid.getThumbUrl()).into(holder.thumbnail);
            Log.v(TAG, "Download Completed :)");
            Log.v(TAG,vid.getStreamUrl());
        }
        Profile p = new Profile();
        MainActivity t = new MainActivity();
        t.new LoadProfile((TextView) row.findViewById(R.id.prof)).execute(vid.getProfileID());
        holder.title.setText(vid.getTitle());
        holder.desc.setText(vid.getDescription());
        holder.prof.setText(p.getUsername());
        holder.date.setText(vid.getCreate().substring(0,10));
        // Return the completed view to render on screen
        return row;
    }
    static class VideoHolder
    {
        TextView title;
        TextView desc;
        TextView prof;
        ImageView thumbnail;
        TextView date;
    }
}
