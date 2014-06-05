package lyc.iping;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class ChatMsgViewAdapter extends BaseAdapter {

	private int[] imgs = { R.drawable.icon };

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private List<ChatMsgEntity> coll;

	private Context ctx;

	private LayoutInflater mInflater;
	
	private Bitmap[] HeadImage;

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll,Bitmap[] Head) {
		ctx = context;
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
		HeadImage = Head;
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
		ChatMsgEntity entity = coll.get(position);

		if (entity.getMsgType()) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();

		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tv_username);
					
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(entity.isShowDate())
		{
			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
			viewHolder.tvSendTime.setText(entity.getDate());
		}
		else{
			viewHolder.tvSendTime.setVisibility(View.INVISIBLE);
		}
		
		if (isComMsg) {
			viewHolder.tvUserName.setVisibility(View.VISIBLE);
			viewHolder.tvUserName.setText(entity.getName());
		}else{
			viewHolder.tvUserName.setVisibility(View.INVISIBLE);
			viewHolder.tvUserName.setText(entity.getName());
		}
		viewHolder.tvContent.setText(entity.getMessage());
		String AppPath = ctx.getApplicationContext().getFilesDir()
				.getAbsolutePath()
				+ "/";
		String ImgPath = AppPath + "HeadImage/" + entity.getID() + "_"
				+ entity.getHeadImageVersion() + ".jpg";
		File ImgFile = new File(ImgPath);
		if (ImgFile.exists()) {
			//Bitmap HeadImage = null;
			HeadImage[entity.getIndex()] = BitmapFactory.decodeFile(ImgPath, null);
			viewHolder.icon.setImageBitmap(HeadImage[entity.getIndex()]);
		} else
			viewHolder.icon.setImageResource(R.drawable.default_head);


		return convertView;
	}

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public ImageView icon;
		public boolean isComMsg = true;
	}

}
