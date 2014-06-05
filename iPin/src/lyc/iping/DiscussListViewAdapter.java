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

public class DiscussListViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<DiscussListMsgEntity> coll;
	private Context ctx;
	private int[] flag;
	private Map<String,Bitmap> headImg = null;
	
	public View getView(int position, View convertView, ViewGroup parent) {
		DiscussListMsgEntity entity = coll.get(position);
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	convertView = mInflater.inflate(R.layout.info_item_msg_text, null);
		    viewHolder = new ViewHolder();
		    viewHolder.head = (ImageView) convertView.findViewById(R.id.head);
		    //viewHolder.info_username = (TextView) convertView.findViewById(R.id.info_username);
			viewHolder.info_from = (TextView) convertView.findViewById(R.id.info_from);
			viewHolder.info_to = (TextView) convertView.findViewById(R.id.info_to);
			viewHolder.info_date = (TextView) convertView.findViewById(R.id.info_date);	
			viewHolder.memberCount = (TextView) convertView.findViewById(R.id.current_num);
			convertView.setTag(viewHolder);				  
			
			//viewHolder.info_username.setText(entity.getUsername());
			viewHolder.info_from.setText(entity.getFrom());
			viewHolder.info_to.setText(entity.getTo());
			viewHolder.info_date.setText(entity.getDate());
			viewHolder.memberCount.setText(entity.getMemberCount());
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
	        
	        if(flag!=null)
			{
				Arrays.sort(flag); //首先对数组排序
				int result = Arrays.binarySearch(flag, position); //在数组中搜索是否含有position
				if(result>=0)
				{
					convertView.setBackgroundColor(Color.CYAN);
				}
			}
	        
	    }else{
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
			  viewHolder.info_date.setText(entity.getDate());
	    }
		return convertView;
	}	
	
	public DiscussListViewAdapter(Context context, List<DiscussListMsgEntity> coll, int []which, Map<String,Bitmap> img) {
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
        public TextView memberCount;
    }
}

