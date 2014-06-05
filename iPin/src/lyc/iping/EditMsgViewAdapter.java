
package lyc.iping;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.List;

public class EditMsgViewAdapter extends BaseAdapter {
	
	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private static final String TAG = EditMsgViewAdapter.class.getSimpleName();

    private List<EditMsgEntity> coll;

    private Context ctx;
    
    private LayoutInflater mInflater;

    public EditMsgViewAdapter(Context context, List<EditMsgEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    


	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
	 	EditMsgEntity entity = coll.get(position);
	 	
	 	if (entity.getMsgType())
	 	{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}
	 	
	}


	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	EditMsgEntity entity = coll.get(position);
    	boolean isComMsg = entity.getMsgType();
    		
    	ViewHolder viewHolder = null;	
    	ViewHolderCom viewHolderCom = null;	
	    if (convertView == null)
	    {
	    	  if (isComMsg)
			  {
				  convertView = mInflater.inflate(R.layout.edit_item_msg_text_left, null);
		    	  viewHolderCom = new ViewHolderCom();
				  viewHolderCom.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
				  viewHolderCom.tvUserName = (TextView) convertView.findViewById(R.id.tv_name);
//				  viewHolderCom.tvFrom = (TextView) convertView.findViewById(R.id.tv_From);
//				  viewHolderCom.tvTo = (TextView) convertView.findViewById(R.id.tv_To);
//				  viewHolderCom.tvDate = (TextView) convertView.findViewById(R.id.tv_Date);
				  viewHolderCom.tvDetail = (TextView) convertView.findViewById(R.id.tv_Detail);
				  viewHolderCom.isComMsg = isComMsg;
				  
				  convertView.setTag(viewHolderCom);
				  
				  viewHolderCom.tvSendTime.setText(entity.getTime());
				  viewHolderCom.tvUserName.setText(entity.getName());
//				  viewHolderCom.tvFrom.setText(entity.getFrom());
//				  viewHolderCom.tvTo.setText(entity.getTo());
//				  viewHolderCom.tvDate.setText(entity.getDate());
				  viewHolderCom.tvDetail.setText(entity.getDetail());
			  }else{
				  convertView = mInflater.inflate(R.layout.edit_item_msg_text_right, null);
		    	  viewHolder = new ViewHolder();
		    	  viewHolder.tvUserHead = (ImageView) convertView.findViewById(R.id.iv_userhead);
				  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
				  viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
//				  viewHolder.tvFrom = (TextView) convertView.findViewById(R.id.tv_From);
//				  viewHolder.tvTo = (TextView) convertView.findViewById(R.id.tv_To);
//				  viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_Date);
				  viewHolder.tvDetail = (TextView) convertView.findViewById(R.id.tv_Detail);
				  viewHolder.isComMsg = isComMsg;
				  
				  convertView.setTag(viewHolder);
				  
				  
				  viewHolder.tvSendTime.setText(entity.getTime());
				  viewHolder.tvUserName.setText(entity.getName());
//				  viewHolder.tvFrom.setText(entity.getFrom());
//				  viewHolder.tvTo.setText(entity.getTo());
//				  viewHolder.tvDate.setText(entity.getDate());
				  viewHolder.tvDetail.setText(entity.getDetail());
			  }


			  
			  
	    }else{
	          viewHolder = (ViewHolder) convertView.getTag();
	          String AppPath = ctx.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
	          String ImgPath = AppPath+"HeadImage/"+entity.getUserHead();
	          File ImgFile = new File(ImgPath);
	          if(ImgFile.exists())
	          {
	        	  Bitmap HeadImage = null;
	        	  HeadImage = BitmapFactory.decodeFile(ImgPath,null);
	        	  viewHolder.tvUserHead.setImageBitmap(HeadImage);
	          }
	          else
	        	  viewHolder.tvUserHead.setImageResource(R.drawable.default_head);
	        	  //System.out.print("HeadImage:" + entity.getUserHead());
	          
			  viewHolder.tvSendTime.setText(entity.getTime());
			  viewHolder.tvUserName.setText(entity.getName());
//			  viewHolder.tvFrom.setText(entity.getFrom());
//			  viewHolder.tvTo.setText(entity.getTo());
//			  viewHolder.tvDate.setText(entity.getDate());
			  viewHolder.tvDetail.setText(entity.getDetail());
	    }
	
	    
	    

	    
	    return convertView;
    }
    

    static class ViewHolder {
    	public ImageView tvUserHead;
        public TextView tvSendTime;
        public TextView tvUserName;
//        public TextView tvFrom;
//        public TextView tvTo;
//        public TextView tvDate;
        public TextView tvDetail;
        public boolean isComMsg = true;
    }
    
    static class ViewHolderCom { 
        public TextView tvSendTime;
        public TextView tvUserName;
//        public TextView tvFrom;
//        public TextView tvTo;
//        public TextView tvDate;
        public TextView tvDetail;
        public boolean isComMsg = true;
    }


}
