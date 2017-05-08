package Model;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.x00075294.impulsevideo.MainActivity;
import com.example.x00075294.impulsevideo.ProfileActivity;
import com.example.x00075294.impulsevideo.R;
import com.example.x00075294.impulsevideo.VideoPreviewActivity;
import com.example.x00075294.impulsevideo.ViewActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import Model.Video;
import static com.example.x00075294.impulsevideo.LaunchActivity.SHAREDPREFFILE;
import static com.example.x00075294.impulsevideo.LaunchActivity.USERIDPREF;
import static com.example.x00075294.impulsevideo.ProfileActivity.*;
/**
 * Created by Thomas Murphy on 03/04/2017.
 */
public class VideoAdapter extends ArrayAdapter<Video> {

    private static final String TAG = "IMP: List Loader";
    private String[] imageURLArray;
    public List<Video> vids;
    public Context mContext;
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
        mContext.startActivity(intent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Video vid = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
            convertView.setOnClickListener(new AdapterView.OnClickListener(){
                @Override
                public void onClick(View view) {
                    loadPreview(vid);
                }
            });
            TextView title = (TextView) convertView.findViewById(R.id.title_list);
            TextView desc = (TextView) convertView.findViewById(R.id.list_desc);
            TextView prof = (TextView) convertView.findViewById(R.id.prof);
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            //load image directly
            if(vid.getThumbUrl() != null)
            {
                Picasso.with(getContext()).load(vid.getThumbUrl()).into(thumbnail);
                Log.v(TAG, "Download Completed :)");
            }
            Profile p = new Profile();
            MainActivity t = new MainActivity();
            t.new LoadProfile((TextView) convertView.findViewById(R.id.prof)).execute(vid.getProfileID());
            title.setText(vid.getTitle());
            desc.setText(vid.getDescription());
            prof.setText(p.getUsername());
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
