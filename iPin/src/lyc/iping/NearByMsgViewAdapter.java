package lyc.iping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lyc.iping.EditMsgViewAdapter.ViewHolder;
import lyc.iping.EditMsgViewAdapter.ViewHolderCom;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NearByMsgViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;	
	private List<NearByMsgEntity> nearby;
    private Context ctx;
    
	
	public View getView(int position, View convertView, ViewGroup parent) {
		NearByMsgEntity entity = nearby.get(position);
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	convertView = mInflater.inflate(R.layout.nearby_item_msg_text, null);
		    viewHolder = new ViewHolder();
		    viewHolder.nearby_head = (ImageView) convertView.findViewById(R.id.nearby_head);
		    viewHolder.nearby_username = (TextView) convertView.findViewById(R.id.nearby_username);
		    viewHolder.nearby_distance = (TextView) convertView.findViewById(R.id.nearby_distance);
		    viewHolder.nearby_telnum = (TextView) convertView.findViewById(R.id.nearby_telnum);
		    viewHolder.nearby_destination = (TextView) convertView.findViewById(R.id.nearby_destination);
			convertView.setTag(viewHolder);				  
			
			viewHolder.nearby_username.setText(entity.getUsername());
			viewHolder.nearby_distance.setText(entity.getDistance());
			viewHolder.nearby_telnum.setText(entity.getTelnum());
			viewHolder.nearby_destination.setText(entity.getDestination());
			
			String AppPath = ctx.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
	        String ImgPath = AppPath+"HeadImage/"+entity.getID()+"_"+entity.getHeadImageVersion()+".jpg";
	        File ImgFile = new File(ImgPath);
	        if(ImgFile.exists())
	        {
	        	Bitmap HeadImage = null;
	        	HeadImage = BitmapFactory.decodeFile(ImgPath,null);
	        	viewHolder.nearby_head.setImageBitmap(HeadImage);
	        }
	        else
	        	viewHolder.nearby_head.setImageResource(R.drawable.default_head);
	    }else{
	          viewHolder = (ViewHolder) convertView.getTag();
	          String AppPath = ctx.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
		      String ImgPath = AppPath+"HeadImage/"+entity.getID()+"_"+entity.getHeadImageVersion()+".jpg";
	          File ImgFile = new File(ImgPath);
	          if(ImgFile.exists())
	          {
	        	  Bitmap HeadImage = null;
	        	  HeadImage = BitmapFactory.decodeFile(ImgPath,null);
	        	  viewHolder.nearby_head.setImageBitmap(HeadImage);
	          }
	          else
	        	  viewHolder.nearby_head.setImageResource(R.drawable.default_head);
	          
	          viewHolder.nearby_username.setText(entity.getUsername());
			  viewHolder.nearby_distance.setText(entity.getDistance());
			  viewHolder.nearby_telnum.setText(entity.getTelnum());
			  viewHolder.nearby_telnum.setText(entity.getDestination());
	    }
		return convertView;
	}	
	
	public NearByMsgViewAdapter(Context context, List<NearByMsgEntity> nearby) {
        ctx = context;
        this.nearby = nearby;
        mInflater = LayoutInflater.from(context);
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nearby.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return nearby.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	static class ViewHolder {
    	public ImageView nearby_head;
        public TextView nearby_username;
        public TextView nearby_distance;
        public TextView nearby_telnum;
        public TextView nearby_destination;

    }

}
