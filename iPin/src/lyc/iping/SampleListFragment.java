package lyc.iping;

import android.app.ActionBar.LayoutParams;
import android.content.Context;  
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;  
import android.support.v4.app.ListFragment;  
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.AbsListView;
import android.widget.ArrayAdapter;  
import android.widget.ImageView;  
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;  
  
/** 
 * @author hx
 *  功能描述：列表Fragment，用来显示列表视图 
 */  
public class SampleListFragment extends ListFragment {  
  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        return inflater.inflate(R.layout.list, null);  
    }  
  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        SampleAdapter adapter = new SampleAdapter(getActivity());    
        	//读取数据库
        	String username="";
        	DatabaseHelper dbHelper = new DatabaseHelper(getActivity(),
				"iPin");
        	SQLiteDatabase db = dbHelper.getWritableDatabase();
        	Cursor cursor = db.query("LoginUser",
				new String[] { "ID", "username", "password", "sex",
						"telephone", "HeadImageVersion", "autoLogin","auth","StudentID","PersonID" },
				"username<>?", new String[] { "null" }, null, null, null);
        	while (cursor.moveToNext()) {
        		username = cursor.getString(cursor.getColumnIndex("username"));					
        	}
        	db.close();
        	dbHelper.close();
        	//end
        	adapter.add(new SampleItem(username, R.drawable.portrait));  
            adapter.add(new SampleItem("拼车管理", R.drawable.car));  
            adapter.add(new SampleItem("认证申请", R.drawable.certification));  
            adapter.add(new SampleItem("告诉朋友", R.drawable.share));  
            adapter.add(new SampleItem("个人设置", R.drawable.settings));  
        //    adapter.add(new SampleItem("建议反馈", android.R.drawable.ic_menu_search));   
        setListAdapter(adapter);  
    }  
  
    private class SampleItem {  
        public String tag;  
        public int iconRes;  
        public SampleItem(String tag, int iconRes) {  
            this.tag = tag;   
            this.iconRes = iconRes;  
        }  
    }  
  
    public class SampleAdapter extends ArrayAdapter<SampleItem> {  
  
        public SampleAdapter(Context context) {  
            super(context, 0);  
        }  
  
        public View getView(int position, View convertView, ViewGroup parent) {  
            if (convertView == null) {  
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);  
            }  
            if(position == 0)
            {     
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 100);
                convertView.setLayoutParams(lp);
                TextView title = (TextView) convertView.findViewById(R.id.row_title);  
            	title.setText(getItem(position).tag); 
            	ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            	android.view.ViewGroup.LayoutParams para = icon.getLayoutParams();               
                para.height = LayoutParams.MATCH_PARENT;  
                para.width = 100;  
                icon.setLayoutParams(para);              	  
            	icon.setImageResource(getItem(position).iconRes);  
            }
            else
            {
            	ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);  
            	icon.setImageResource(getItem(position).iconRes);  
            	TextView title = (TextView) convertView.findViewById(R.id.row_title);  
            	title.setText(getItem(position).tag);  
            }
            return convertView;  
        }  
  
    }  
    
    @Override  
    public void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);  
        
        if(position == 4)
        {
        	Intent intent = new Intent(getActivity(), SettingActivity.class);
    		startActivity(intent);
        }
        
        if(position == 1)
        {
        	Intent intent = new Intent(getActivity(), PostMgrActivity.class);
    		startActivity(intent);
        }
        if(position == 2)
        {
        	Intent intent = new Intent(getActivity(), SetAuthActivity.class);
    		startActivity(intent);
        }
        if(position == 3)
        {
        	Intent intent = new Intent(getActivity(), AboutActivity.class);
    		startActivity(intent);
        }
        /*
        if(position == 5)
        {
        	Intent intent = new Intent(getActivity(), SetAdviceActivity.class);
    		startActivity(intent);
        }
        */
        
    }
    
}  

