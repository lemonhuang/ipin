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

public class InfoListMsgViewAdapter extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater = null;	
	private List<InfoListMsgEntity> coll;
	List<InfoListMsgEntity> mOriginalValues; // Original Values
    private Context ctx;
    private Map<String,Bitmap> headImg = null;
	
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoListMsgEntity entity = coll.get(position);
    		
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
			viewHolder.dis1 = (TextView) convertView.findViewById(R.id.dis1);
			viewHolder.dis2 = (TextView) convertView.findViewById(R.id.total_num); 
			convertView.setTag(viewHolder);				  
			
			//viewHolder.info_username.setText(entity.getUsername());
			viewHolder.info_from.setText(entity.getFrom());
			viewHolder.info_to.setText(entity.getTo());
			viewHolder.info_date.setText(entity.getDate());
			if(entity.getMemberCount().equals("0"))
			{
				viewHolder.memberCount.setText("完结");
				viewHolder.dis1.setText("");
				viewHolder.dis2.setText("");
			}
			else
			{
				viewHolder.memberCount.setText(entity.getMemberCount());
				viewHolder.dis1.setText("/");
				viewHolder.dis2.setText("4");
			}
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
			  if(entity.getMemberCount().equals("0"))
				{
					viewHolder.memberCount.setText("完结");
					viewHolder.dis1.setText("");
					viewHolder.dis2.setText("");
				}
				else
				{
					viewHolder.memberCount.setText(entity.getMemberCount());
					viewHolder.dis1.setText("/");
					viewHolder.dis2.setText("4");
				}
	    }
		return convertView;
	}	
	
	public InfoListMsgViewAdapter(Context context, List<InfoListMsgEntity> coll, Map<String,Bitmap> img) {
        ctx = context;
        this.coll = coll;
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
        public TextView dis1;
        public TextView dis2;
    }
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				coll = (List<InfoListMsgEntity>) results.values; // has the
																	// filtered
																	// values
				notifyDataSetChanged(); // notifies the data with new filtered
										// values
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults(); // Holds the
																// results of a
																// filtering
																// operation in
																// values
				List<InfoListMsgEntity> FilteredArrList = new ArrayList<InfoListMsgEntity>();

				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<InfoListMsgEntity>(coll); // saves
																				// the
																				// original
																				// data
																				// in
																				// mOriginalValues
				}

				/********
				 * 
				 * If constraint(CharSequence that is received) is null returns
				 * the mOriginalValues(Original) values else does the Filtering
				 * and returns FilteredArrList(Filtered)
				 * 
				 ********/
//				if (constraint == null || constraint.length() == 0) {
//
//					// set the Original result to return
//					results.count = mOriginalValues.size();
//					results.values = mOriginalValues;
//				} else {
					String[] temp = ((String) constraint).split(" ");
//					constraint = constraint.toString().toLowerCase();
					CharSequence constraint1 = null;
					constraint1 = ((CharSequence)temp[0]).toString().trim().toLowerCase();
					CharSequence constraint2 = null;
					constraint2 = ((CharSequence)temp[1]).toString().trim().toLowerCase();
					CharSequence constraint3 = null;
					constraint3 = ((CharSequence)temp[2]).toString().trim().toLowerCase();
					
					
					if ((constraint1.toString().equals("null"))&&(!(constraint2.toString().equals("null")))&&(!(constraint3.toString().equals("null"))))
					{
						constraint2 = constraint2.toString().toLowerCase();
						constraint3 = constraint3.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							InfoListMsgEntity data = mOriginalValues.get(i);
							if ((data.getTo().toLowerCase().contains(constraint2.toString()))&&(data.getDate().toLowerCase().contains(constraint3.toString()))) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					else if ((!(constraint1.toString().equals("null")))&&(constraint2.toString().equals("null"))&&(!(constraint3.toString().equals("null"))))
					{
						constraint1 = constraint1.toString().toLowerCase();
						constraint3 = constraint3.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							InfoListMsgEntity data = mOriginalValues.get(i);
							if ((data.getFrom().toLowerCase().contains(constraint1.toString()))&&(data.getDate().toLowerCase().contains(constraint3.toString()))) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					else if ((!(constraint1.toString().equals("null")))&&(!(constraint2.toString().equals("null")))&&(constraint3.toString().equals("null")))
					{
						constraint1 = constraint1.toString().toLowerCase();
						constraint2 = constraint2.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							InfoListMsgEntity data = mOriginalValues.get(i);
							if ((data.getFrom().toLowerCase().contains(constraint1.toString()))&&(data.getTo().toLowerCase().contains(constraint2.toString()))) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					else if ((constraint1.toString().equals("null"))&&(constraint2.toString().equals("null"))&&(!(constraint3.toString().equals("null"))))
					{
						constraint3 = constraint3.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							InfoListMsgEntity data = mOriginalValues.get(i);
							if (data.getDate().toLowerCase().contains(constraint3.toString())) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					else if ((constraint1.toString().equals("null"))&&(!(constraint2.toString().equals("null")))&&(constraint3.toString().equals("null")))
					{
						constraint2 = constraint2.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							InfoListMsgEntity data = mOriginalValues.get(i);
							if (data.getTo().toLowerCase().contains(constraint2.toString())) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					else if ((!(constraint1.toString().equals("null")))&&(constraint2.toString().equals("null"))&&(constraint3.toString().equals("null")))
					{
						constraint1 = constraint1.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							InfoListMsgEntity data = mOriginalValues.get(i);
							if (data.getFrom().toLowerCase().contains(constraint1.toString())) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					else if ((!(constraint1.toString().equals("null")))&&(!(constraint2.toString().equals("null")))&&(!(constraint3.toString().equals("null"))))
					{
						constraint1 = constraint1.toString().toLowerCase();
						constraint2 = constraint2.toString().toLowerCase();
						constraint3 = constraint3.toString().toLowerCase();
						for (int i = 0; i < mOriginalValues.size(); i++) {
							InfoListMsgEntity data = mOriginalValues.get(i);
							if ((data.getFrom().toLowerCase().contains(constraint1.toString()))&&(data.getTo().toLowerCase().contains(constraint2.toString()))&&(data.getDate().toLowerCase().contains(constraint3.toString()))) {
								FilteredArrList.add(data);
							}
						}
						// set the Filtered result to return
						results.count = FilteredArrList.size();
						results.values = FilteredArrList;
					}
					else{
						// set the Original result to return
						results.count = mOriginalValues.size();
						results.values = mOriginalValues;
					}
					
//				}
				return results;
			}
		};
		return filter;
	}
}
