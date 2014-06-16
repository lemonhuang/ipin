package lyc.iping;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lyc.iping.InfoListMsgViewAdapter.ViewHolder;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PostListViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<DiscussListMsgEntity> coll;
	private Context ctx;
	private int[] flag;
	private Map<String,Bitmap> headImg = null;
	
	public View getView(int position, View convertView, ViewGroup parent) {
		DiscussListMsgEntity entity = coll.get(position);
		
		System.out.println("Now i'm in getView");
		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	System.out.println("Now here convertview == null");
	    	convertView = mInflater.inflate(R.layout.info_item_post_text, null);
		    viewHolder = new ViewHolder();
		    viewHolder.head = (ImageView) convertView.findViewById(R.id.head);
		    //viewHolder.info_username = (TextView) convertView.findViewById(R.id.info_username);
			viewHolder.info_from = (TextView) convertView.findViewById(R.id.info_from);
			viewHolder.info_to = (TextView) convertView.findViewById(R.id.info_to);
			viewHolder.info_date = (TextView) convertView.findViewById(R.id.info_date);	
			viewHolder.member_lack = (TextView) convertView.findViewById(R.id.current_lack);
			convertView.setTag(viewHolder);				  
			
			//viewHolder.info_username.setText(entity.getUsername());
			viewHolder.info_from.setText(entity.getFrom());
			viewHolder.info_to.setText(entity.getTo());
			viewHolder.info_date.setText(entity.getDate());
			System.out.println("view: the memberCount is "+ entity.getMemberCount());
			int temp = 4-Integer.parseInt(entity.getMemberCount());
			viewHolder.member_lack.setText(""+temp);
			if(headImg.containsKey(entity.getID()+"_"+entity.getHeadImageVersion()))
			{
				viewHolder.head.setImageBitmap(headImg.get(entity.getID()+"_"+entity.getHeadImageVersion()));
			}
			else
			{
				String AppPath = ctx.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
		        String ImgPath = AppPath+"HeadImage/"+entity.getID()+"_"+entity.getHeadImageVersion()+".jpg";
		        File ImgFile = new File(ImgPath);
		        if(ImgFile.exists())
		        {
		        	Bitmap HeadImage = null;
		        	HeadImage = BitmapFactory.decodeFile(ImgPath,null);
		        	headImg.put(entity.getID()+"_"+entity.getHeadImageVersion(), HeadImage);
		        	viewHolder.head.setImageBitmap(headImg.get(entity.getID()+"_"+entity.getHeadImageVersion()));
		        }
		        else
		        	viewHolder.head.setImageResource(R.drawable.default_head);
			}
	        
	    }else{
	    	  System.out.println("Now here convertview != null");
	          viewHolder = (ViewHolder) convertView.getTag();
	          if(headImg.containsKey(entity.getID()+"_"+entity.getHeadImageVersion()))
				{
					viewHolder.head.setImageBitmap(headImg.get(entity.getID()+"_"+entity.getHeadImageVersion()));
				}
				else
				{
					String AppPath = ctx.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
			        String ImgPath = AppPath+"HeadImage/"+entity.getID()+"_"+entity.getHeadImageVersion()+".jpg";
			        File ImgFile = new File(ImgPath);
			        if(ImgFile.exists())
			        {
			        	Bitmap HeadImage = null;
			        	HeadImage = BitmapFactory.decodeFile(ImgPath,null);
			        	headImg.put(entity.getID()+"_"+entity.getHeadImageVersion(), HeadImage);
			        	viewHolder.head.setImageBitmap(headImg.get(entity.getID()+"_"+entity.getHeadImageVersion()));
			        }
			        else
			        	viewHolder.head.setImageResource(R.drawable.default_head);
				}
	          
	          //viewHolder.info_username.setText(entity.getUsername());
			  viewHolder.info_from.setText(entity.getFrom());
			  viewHolder.info_to.setText(entity.getTo());
			  int temp = 4-Integer.parseInt(entity.getMemberCount());
			  viewHolder.member_lack.setText(""+temp);
			  viewHolder.info_date.setText(entity.getDate());
	    }
		return convertView;
	}	
	
	public PostListViewAdapter(Context context, List<DiscussListMsgEntity> coll, int []which, Map<String,Bitmap> img) {
        ctx = context;
        this.coll = coll;
        flag = which;
        headImg = img;
        mInflater = LayoutInflater.from(context);
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return coll.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return coll.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	static class ViewHolder {
    	public ImageView head;
        //public TextView info_username;
        public TextView info_from;
        public TextView info_to;
        public TextView info_date;
        public TextView member_lack;
    }
}

