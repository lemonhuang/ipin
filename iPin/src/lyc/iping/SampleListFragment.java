package lyc.iping;

import android.content.Context;  
import android.content.Intent;
import android.os.Bundle;  
import android.support.v4.app.ListFragment;  
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.ArrayAdapter;  
import android.widget.ImageView;  
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
            adapter.add(new SampleItem("我的信息", android.R.drawable.ic_menu_search));  
            adapter.add(new SampleItem("我的拼车", android.R.drawable.ic_menu_search));  
            adapter.add(new SampleItem("认证申请", android.R.drawable.ic_menu_search));  
            adapter.add(new SampleItem("关于ipin", android.R.drawable.ic_menu_search));  
            adapter.add(new SampleItem("建议反馈", android.R.drawable.ic_menu_search));   
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
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);  
            icon.setImageResource(getItem(position).iconRes);  
            TextView title = (TextView) convertView.findViewById(R.id.row_title);  
            title.setText(getItem(position).tag);  
  
            return convertView;  
        }  
  
    }  
    
    @Override  
    public void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);  
        
        if(position == 0)
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
        if(position == 4)
        {
        	Intent intent = new Intent(getActivity(), SetAdviceActivity.class);
    		startActivity(intent);
        }
        
    }
    
}  

