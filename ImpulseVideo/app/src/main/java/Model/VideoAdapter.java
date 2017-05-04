package Model;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.x00075294.impulsevideo.MainActivity;
import com.example.x00075294.impulsevideo.ProfileActivity;
import com.example.x00075294.impulsevideo.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import Model.Video;

import static com.example.x00075294.impulsevideo.ProfileActivity.*;

/**
 * Created by Thomas Murphy on 03/04/2017.
 */
public class VideoAdapter extends ArrayAdapter<Video> {

    private String[] imageURLArray;
    public List<Video> vids;
    public VideoAdapter(Context context, int resource, List<Video> objects) {
        super(context, resource, objects);
    }
    public VideoAdapter(Context context, ArrayList<Video> arrayOfVideo) {
        super(context,0,arrayOfVideo);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Video vid = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
            TextView title = (TextView) convertView.findViewById(R.id.title_list);
            TextView desc = (TextView) convertView.findViewById(R.id.list_desc);
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            //load image directly
            Bitmap imageBitmap = null;
            try {
                URL imageURL = new URL(vid.getThumbUrl());
                imageBitmap = BitmapFactory.decodeStream(imageURL.openStream());
                thumbnail.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                // TODO: handle exception
                Log.e("error", "Downloading Image Failed");
                thumbnail.setImageResource(R.drawable.ic_videocam_black_24dp);
            }
            title.setText(vid.getTitle());
            desc.setText(vid.getDescription());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
