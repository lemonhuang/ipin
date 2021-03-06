package lyc.iping;

import java.io.IOException;
//add sliding menu support by hx
import android.support.v4.app.FragmentActivity; 
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
//end
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lyc.iping.DiscussActivity.grouprcv;
import lyc.iping.LoginActivity.UserLoginTask;

import org.w3c.dom.Text;

import com.readystatesoftware.viewbadger.BadgeView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


public class InfoListActivity extends FragmentActivity implements OnScrollListener,SwipeRefreshLayout.OnRefreshListener{

	/** Called when the activity is first created. */

	// ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,
	// String>>();

	String info_username = null;
	String info_from = null;
	String info_to = null;
	String info_date = null;
	String info_detail = null;

	private ImageView img_setting = null;
	private ImageView img_discuss = null;
	private ImageView img_nearby = null;

	private ImageView img_refresh = null;

	private ListView listview = null;
	private View mLoadMoreView = null;
	private Button mLoadMoreBtn = null;
	
	private EditText searchText_from;
	private EditText searchText_to;
	private EditText searchText_date = null;
	private SwipeRefreshLayout swipeLayout = null;
	
	View target = null;
	BadgeView badge = null;

	private List<InfoListMsgEntity> mDataArrays = null;
	private Map<String,Bitmap> headImage = null;
	private InfoListMsgViewAdapter mAdapter = null;
	private ReRefreshTask mReRefreshTask = null;
	private InfoRefreshTask mInfoRefreshTask = null;
	private AuthRefreshTask mAuthRefreshTask = null;
	private int really_out = 0;
	
	private int visibleLastIndex = 0; //最后的可视项索引
	private int datasize = 38; //模拟数据集的条数
	private int count = 0;
	
	//add sliding menu support by hx
	private SlidingMenu menu;
	//end add

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//add sliding menu by hx
		setTitle("Attach");  
       
		//end add
		
		setContentView(R.layout.main_tab_info);
		
		initSlidingMenu(); 
		
		headImage = new HashMap<String,Bitmap>();
		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);
		
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		target = findViewById(R.id.img_address);
		badge = new BadgeView(InfoListActivity.this, target);
		

		swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		
		mLoadMoreView = getLayoutInflater().inflate(R.layout.loadmore, null);
		mLoadMoreBtn = (Button)mLoadMoreView.findViewById(R.id.loadMoreButton);
		mLoadMoreBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoadMoreBtn.setText("正在加载中..."); //设置按钮文字
				loadMoreData();
//				swipeLayout.setRefreshing(true);
			}
		});
		listview = (ListView) findViewById(R.id.info_list);
		listview.addFooterView(mLoadMoreView);
		initView();
		listview.setOnScrollListener(this);
		initView();
		img_refresh = (ImageView) findViewById(R.id.infolist_refresh);
		img_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mInfoRefreshTask = new InfoRefreshTask();
				mInfoRefreshTask.execute((Void) null);
			}
		});
		// add by hx
		Listrcv rcv = new Listrcv();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.rcv");
		this.registerReceiver(rcv, filter);
		System.out.println("注册List广播完成！");
		// end

		searchText_date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){		 
	         @Override
	         public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	                 long arg3) {
	             // TODO Auto-generated method stub
	        	 Intent intent = new Intent(InfoListActivity.this,
	     				ReleasedActivity.class);
	     		intent.putExtra("position", position);
	     		startActivity(intent);
	     		InfoListActivity.this.finish();
	            
	         }
	          
	     });
		
	}

	private void initSlidingMenu() {  
        // 设置主界面视图  
       // setContentView(R.layout.content_frame);  
       // getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SampleListFragment()).commit();  
  
        // 设置滑动菜单的属性值  
        menu = new SlidingMenu(this); 
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
        menu.setShadowWidthRes(R.dimen.shadow_width);  
        menu.setShadowDrawable(R.drawable.shadow);  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
        menu.setFadeDegree(0.35f);  
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);  
        // 设置滑动菜单的视图界面  
        menu.setMenu(R.layout.menu_frame);    
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new SampleListFragment()).commit();  
    }  
	
	
	private void showDialog() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int monthOfYear = c.get(Calendar.MONTH);
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog datedialog = new DatePickerDialog(
				InfoListActivity.this, new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						searchText_date.setText(year + "-" + (monthOfYear + 1)
								+ "-" + dayOfMonth);
						searchText_date.setSelection(searchText_date.length());

					}
				}, year, monthOfYear, dayOfMonth);
		datedialog.show();
	}

	public void initView() {
		/*
		 * DatabaseHelper dbHelper = new
		 * DatabaseHelper(InfoListActivity.this,"info"); SQLiteDatabase db =
		 * dbHelper.getWritableDatabase(); Cursor cursor = db.query("info", new
		 * String
		 * []{"info_username","info_from","info_to","info_date","info_detail"},
		 * null, null, null, null, null); while(cursor.moveToNext()) { info_from
		 * = cursor.getString(cursor.getColumnIndex("info_from")); info_to =
		 * cursor.getString(cursor.getColumnIndex("info_to")); info_date =
		 * cursor.getString(cursor.getColumnIndex("info_date")); info_username =
		 * cursor.getString(cursor.getColumnIndex("info_username"));
		 * 
		 * HashMap<String, String> map = new HashMap<String, String>();
		 * map.put("info_username", info_username); map.put("info_date",
		 * info_date); map.put("info_from", info_from); map.put("info_to",
		 * info_to); list.add(map); } db.close(); dbHelper.close();
		 * InfoListMsgViewAdapter listAdapter = new InfoListMsgViewAdapter(this,
		 * list, R.layout.info_item_msg_text, new String[] { "info_username",
		 * "info_date", "info_from", "info_to"}, new int[] {
		 * R.id.info_username,R.id.info_date, R.id.info_from,R.id.info_to});
		 * setListAdapter(listAdapter);
		 */
		img_discuss = (ImageView) findViewById(R.id.img_address);
		img_nearby = (ImageView) findViewById(R.id.img_nearby);
		img_setting = (ImageView) findViewById(R.id.img_settings);

		mDataArrays = new ArrayList<InfoListMsgEntity>();
		List<InfoListMsgEntity> mOriginalValues;
		/*
		 * InfoListMsgEntity entity = new InfoListMsgEntity();
		 * entity.setID("000000001"); entity.setHeadImageVersion("1");
		 * entity.setUsername("user"); entity.setFrom("aaa");
		 * entity.setTo("bbb"); entity.setDate("7/7"); mDataArrays.add(entity);
		 */
		mAdapter = new InfoListMsgViewAdapter(this, mDataArrays, headImage);
		listview.setAdapter(mAdapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(InfoListActivity.this,
						ReleasedActivity.class);
				intent.putExtra("position", position);
				startActivity(intent);
				InfoListActivity.this.finish();
			}
		});

		EditText filterEditText1 = (EditText) findViewById(R.id.editText1);
		EditText filterEditText2 = (EditText) findViewById(R.id.searchText2);
		EditText filterEditText3 = (EditText) findViewById(R.id.searchText3);
		searchText_date = filterEditText3;
		mAuthRefreshTask= new AuthRefreshTask();
		mAuthRefreshTask.execute((Void) null);
		mInfoRefreshTask = new InfoRefreshTask();
		mInfoRefreshTask.execute((Void) null);
		// Add Text Change Listener to EditText
		filterEditText1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Call back the Adapter with current character to Filter
				EditText filterString1 = (EditText) findViewById(R.id.editText1);
				EditText filterString2 = (EditText) findViewById(R.id.searchText2);
				EditText filterString3 = (EditText) findViewById(R.id.searchText3);
				CharSequence s1, s2, s3;
				if ("".equals(filterString1.getText().toString().trim())) {
					s1 = null;
				} else {
					s1 = (CharSequence) (filterString1.getText().toString());
				}

				if ("".equals(filterString2.getText().toString().trim())) {
					s2 = null;
				} else {
					s2 = (CharSequence) (filterString2.getText().toString());
				}
				if ("".equals(filterString3.getText().toString().trim())) {
					s3 = null;
				} else {
					s3 = (CharSequence) (filterString3.getText().toString());
				}
				s = s1 + " " + s2 + " " + s3;
				mAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		filterEditText2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Call back the Adapter with current character to Filter
				EditText filterString1 = (EditText) findViewById(R.id.editText1);
				EditText filterString2 = (EditText) findViewById(R.id.searchText2);
				EditText filterString3 = (EditText) findViewById(R.id.searchText3);
				CharSequence s1, s2, s3;
				if ("".equals(filterString1.getText().toString().trim())) {
					s1 = null;
				} else {
					s1 = (CharSequence) (filterString1.getText().toString());
				}

				if ("".equals(filterString2.getText().toString().trim())) {
					s2 = null;
				} else {
					s2 = (CharSequence) (filterString2.getText().toString());
				}
				if ("".equals(filterString3.getText().toString().trim())) {
					s3 = null;
				} else {
					s3 = (CharSequence) (filterString3.getText().toString());
				}
				s = s1 + " " + s2 + " " + s3;
				mAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		filterEditText3.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Call back the Adapter with current character to Filter
				EditText filterString1 = (EditText) findViewById(R.id.editText1);
				EditText filterString2 = (EditText) findViewById(R.id.searchText2);
				EditText filterString3 = (EditText) findViewById(R.id.searchText3);
				CharSequence s1, s2, s3;
				if ("".equals(filterString1.getText().toString().trim())) {
					s1 = null;
				} else {
					s1 = (CharSequence) (filterString1.getText().toString());
				}

				if ("".equals(filterString2.getText().toString().trim())) {
					s2 = null;
				} else {
					s2 = (CharSequence) (filterString2.getText().toString());
				}
				if ("".equals(filterString3.getText().toString().trim())) {
					s3 = null;
				} else {
					s3 = (CharSequence) (filterString3.getText().toString());
				}
				s = s1 + " " + s2 + " " + s3;
				mAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 显示消息数目
		if (service.group_msg_num != 0) {
			badge.setText(service.group_msg_num + "");
			// badge.setTextColor(Color.RED);
			badge.setTextSize(10);
			badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			badge.setBadgeMargin(0, 0);
			badge.show();
		}
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 可以根据多个请求代码来作相应的操作
		if (20 == resultCode) { /*
								 * String
								 * cnfrom=data.getExtras().getString("cnfrom");
								 * String
								 * cnto=data.getExtras().getString("cnto");
								 * String
								 * cndate=data.getExtras().getString("cndate");
								 * String
								 * cnname=data.getExtras().getString("cnname");
								 * 
								 * HashMap<String, String> map = new
								 * HashMap<String, String>();
								 * map.put("info_username", cnname);
								 * map.put("info_date", cndate);
								 * map.put("info_from", cnfrom);
								 * map.put("info_to", cnto); list.add(map);
								 * InfoListMsgViewAdapter listAdapter = new
								 * InfoListMsgViewAdapter(this, list,
								 * R.layout.info_item_msg_text, new String[] {
								 * "info_username", "info_date", "info_from",
								 * "info_to"}, new int[] {
								 * R.id.info_username,R.id.info_date,
								 * R.id.info_from,R.id.info_to});
								 * setListAdapter(listAdapter);
								 */
		} else if (50 == resultCode) {
			mInfoRefreshTask = new InfoRefreshTask();
			mInfoRefreshTask.execute((Void) null);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 设置标题栏右侧按钮的作用
	public void btnmainright(View v) {
		Intent intent = new Intent(InfoListActivity.this, EditActivity.class);
		//startActivityForResult(intent, 100);
		startActivity(intent);
		InfoListActivity.this.finish();
		// finish();
	}

	public void btn_discuss(View v) {
		Intent intent = new Intent(InfoListActivity.this, DiscussActivity.class);
		startActivity(intent);
		InfoListActivity.this.finish();
	}

	public void btn_nearby(View v) {
		Intent intent = new Intent(InfoListActivity.this, NearByActivity.class);
		intent.putExtra("fromActivity", 1);
		startActivity(intent);
		InfoListActivity.this.finish();
	}

	public void btn_setting(View v) {
		Intent intent = new Intent(InfoListActivity.this, SettingActivity.class);
		startActivity(intent);
		InfoListActivity.this.finish();
	}


//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		// TODO Auto-generated method stub
//		super.onListItemClick(l, v, position, id);
//		Intent intent = new Intent(InfoListActivity.this,
//				ReleasedActivity.class);
//		intent.putExtra("position", position);
//		startActivity(intent);
//		InfoListActivity.this.finish();
//	}


	public class InfoRefreshTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("InfoListRefresh "+count+" "+"10");
				out.flush();
//				mDataArrays.clear();
				InputStream br = socket.getInputStream();
				
				byte[] buffer = new byte[10240];
				int readSize = br.read(buffer);
/*				
				//modify
				int readSize = 0;
				String InfoRefreshMsg = "";
				socket.setSoTimeout(500);
				try 
				{
				    int now_size;
					while((now_size = br.read(buffer)) != -1)
				    {
						System.out.println("now_size is "+now_size);
						InfoRefreshMsg += new String(buffer, 0, now_size);
				        readSize += now_size;
				        System.out.println("now_size is "+now_size+" string: "+InfoRefreshMsg );
				    }	
				}
				catch (SocketTimeoutException e)
				{
				    System.out.println("socket is out of time");
				    br.close();
				    socket.close();
				    
				}
				
				//end
				System.out.println("readSize is "+readSize);
*/
				if (readSize > 0) {
					String InfoRefreshMsg = new String(buffer, 0, readSize);					
					System.out.println(InfoRefreshMsg);
					if (InfoRefreshMsg.contains("InfoRefreshNone"))
						return false;
					String[] temp = InfoRefreshMsg.split("`");

					HeadImageDownloader downloader = new HeadImageDownloader();

					DatabaseHelper dbHelper = new DatabaseHelper(
							InfoListActivity.this, "iPin");
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					db.delete("info", null, null);
					System.out.println("change the list0");
					count = count + Integer.parseInt(temp[0]);
					for (int i = 0; i < Integer.parseInt(temp[0]); i++) {
						InfoListMsgEntity entity = new InfoListMsgEntity();
						String Index, ID, HeadImageVersion, username, from, to, date, detail, time, memberCount, memberList;
						Index = temp[i * 11 + 1];
						ID = temp[i * 11 + 2];
						HeadImageVersion = temp[i * 11 + 3];
						username = temp[i * 11 + 4];
						from = temp[i * 11 + 5];
						to = temp[i * 11 + 6];
						date = temp[i * 11 + 7];
						detail = temp[i * 11 + 8];
						time = temp[i * 11 + 9];
						memberCount = temp[i * 11 + 10];
						memberList = temp[i * 11 + 11];
						entity.setID(ID);
						entity.setHeadImageVersion(HeadImageVersion);
						entity.setUsername(username);
						entity.setFrom(from);
						entity.setTo(to);
						entity.setDate(date);
						entity.setMemberCount(memberCount);
						mDataArrays.add(entity);
						ContentValues values = new ContentValues();
						values.put("GroupID", Index);
						values.put("info_ID", ID);
						values.put("info_HeadImageVersion", HeadImageVersion);
						values.put("info_username", username);
						values.put("info_from", from);
						values.put("info_to", to);
						values.put("info_date", date);
						values.put("info_detail", detail);
						values.put("info_time", time);
						values.put("memberCount", memberCount);
						values.put("memberList", memberList);
						db.insert("info", null, values);
						downloader.download(InfoListActivity.this, ID,
								HeadImageVersion);
						System.out.println("change the list1 "+i+" "+temp[0]);
					}
					db.close();
					dbHelper.close();
					socket.close();
					
					System.out.println("change the list1");
									
					return true;
				}
			} catch (Exception e) {
				return false;
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mInfoRefreshTask = null;
			if (success) {
				mAdapter.notifyDataSetChanged();
				mLoadMoreBtn.setText("查看更多..."); //恢复按钮文字
				System.out.println("change the list");
			} else {
				listview.removeFooterView(mLoadMoreView);
				Toast.makeText(InfoListActivity.this, "没有更多数据了！", Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected void onCancelled() {
			mInfoRefreshTask = null;
		}
	}

	public class AuthRefreshTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;
		String LoginMsg;
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				String ID="";
				DatabaseHelper dbHelper = new DatabaseHelper(InfoListActivity.this,
						"iPin");
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				Cursor cursor = db.query("LoginUser",
						new String[] { "ID", "username", "password", "sex",
								"telephone", "HeadImageVersion", "autoLogin","auth","StudentID","PersonID" },
						"username<>?", new String[] { "null" }, null, null, null);
				while (cursor.moveToNext()) {
					ID = cursor.getString(cursor.getColumnIndex("ID"));					
				}
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("AuthRefresh " + ID);
				out.flush();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[1024];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					LoginMsg = new String(buffer, 0, readSize);
					socket.close();
					// add by hx
					// end
					return LoginMsg.contains("AuthRefreshSuccess");
				}
			} catch (Exception e) {
				return false;
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthRefreshTask = null;
			if (success) {
				String[] loginMsg = LoginMsg.split(":");
				DatabaseHelper dbHelper = new DatabaseHelper(
						InfoListActivity.this, "iPin");
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();				
				values.put("auth", loginMsg[1]);
				values.put("StudentID", loginMsg[2]);
				values.put("PersonID", loginMsg[3]);
				db.update("LoginUser", values, "username<>?",
						new String[] { "null" });
				db.close();
				dbHelper.close();
				
				DatabaseHelper dbHelper2 = new DatabaseHelper(InfoListActivity.this, "iPin");
				SQLiteDatabase db2 = dbHelper2.getWritableDatabase();
				Cursor cursor = db2.query("LoginUser",
						new String[] { "ID", "username", "password", "sex",
								"telephone", "HeadImageVersion", "autoLogin","auth" },
						"username<>?", new String[] { "null" }, null, null, null);
				while (cursor.moveToNext()) {
					int auth = cursor.getInt(cursor
							.getColumnIndex("auth"));
					switch(auth)
					{
					case 0:
						Toast.makeText(InfoListActivity.this, "请在个人信息中进行认证",
								Toast.LENGTH_SHORT).show();
						break;
					case 1:
						Toast.makeText(InfoListActivity.this, "您的认证信息正在处理中",
								Toast.LENGTH_SHORT).show();
						break;
					case 2:
						Toast.makeText(InfoListActivity.this, "您输入的一卡通号不存在",
								Toast.LENGTH_SHORT).show();
						break;
					case 3:
						Toast.makeText(InfoListActivity.this, "您输入的一卡通号已绑定其他帐号",
								Toast.LENGTH_SHORT).show();
						break;
					case 4:
						Toast.makeText(InfoListActivity.this, "您的信息验证不通过，请重新申请验证",
								Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
					}
				}
				db2.close();
				dbHelper2.close();
			} else {
				
			}
			try {
				socket.close();
			} catch (Exception e) {
			}

		}

		@Override
		protected void onCancelled() {
			mAuthRefreshTask = null;			
		}		
	}
	/*
	 * //add by hx
	 * 
	 * @Override public void onBackPressed() {
	 * 
	 * try { service.clientsocket.close(); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * service.handler.removeCallbacks(service.runnable); service.tdrun = 0;
	 * Intent intent2=new Intent(this,service.class); stopService(intent2);
	 * 
	 * super.onBackPressed();
	 * 
	 * } //end
	 */

	public class Listrcv extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 显示消息数目
			badge.setText(service.group_msg_num + "");
			// badge.setTextColor(Color.RED);
			badge.setTextSize(10);
			badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			badge.setBadgeMargin(0, 0);
			badge.show();

		}
	}

	// add by hx
	@Override
	public void onBackPressed() {
		if (really_out == 0) {
			Toast.makeText(InfoListActivity.this, "再按一次退出键，你可就真的就退出了哦~",
					Toast.LENGTH_SHORT).show();
			really_out = 1;
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					really_out = 0;
				}
			}, 2500);
		}

		else {
			service.tdrun = 0;
			try {
				service.clientBuff.close();
				service.clientoutput.close();
				service.clientsocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			service.handler.removeCallbacks(service.runnable);
			Intent intent2 = new Intent(this, service.class);
			stopService(intent2);

			super.onBackPressed();
		}
	}

	// end

	// 创建菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "退出程序");
		return super.onCreateOptionsMenu(menu);
	}

	// 菜单响应
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			ActivityManager.getInstance().exit();
		}
		return true;
	}
	
	/**
	* 加载更多数据
	*/
	private void loadMoreData(){
		int count = mAdapter.getCount();
		count++;
		//这里添加发送请求
		mInfoRefreshTask = new InfoRefreshTask();
		mInfoRefreshTask.execute((Void) null);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
		Log.e("========================= ","========================");
		Log.e("firstVisibleItem = ",firstVisibleItem+"");
		Log.e("visibleItemCount = ",visibleItemCount+"");
		Log.e("totalItemCount = ",totalItemCount+"");
		Log.e("========================= ","========================");
		//如果所有的记录选项等于数据集的条数，则移除列表底部视图
		if(totalItemCount == datasize+1){
//			listview.removeFooterView(mLoadMoreView);
//			Toast.makeText(this, "数据全部加载完!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		int itemsLastIndex = mAdapter.getCount()-1; //数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1;
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex - 1) {			
//				loadMoreData();
//				Toast.makeText(this, "到底了！", Toast.LENGTH_LONG).show();
			}			
	}
	
	

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mReRefreshTask = new ReRefreshTask();
		mReRefreshTask.execute((Void) null);
		
	}

	public class ReRefreshTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("InfoListRefresh "+0+" "+"10");
				out.flush();
				mDataArrays.clear();
				InputStream br = socket.getInputStream();
				
				byte[] buffer = new byte[10240];
//				int readSize = br.read(buffer);
				
				//modify
				int readSize = 0;
				String InfoRefreshMsg = "";
				socket.setSoTimeout(500);
				try 
				{
				    int now_size;
					while((now_size = br.read(buffer)) != -1)
				    {
						System.out.println("now_size is "+now_size);
						InfoRefreshMsg += new String(buffer, 0, now_size);
				        readSize += now_size;
				        System.out.println("now_size is "+now_size+" string: "+InfoRefreshMsg );
				    }	
				}
				catch (SocketTimeoutException e)
				{
				    System.out.println("socket is out of time");
				    br.close();
				    socket.close();
				    
				}
				
				//end
				System.out.println("readSize is "+readSize);
				
				if (readSize > 0) {
//					String InfoRefreshMsg = new String(buffer, 0, readSize);					
					System.out.println(InfoRefreshMsg);
					if (InfoRefreshMsg.contains("InfoRefreshNone"))
						return false;
					String[] temp = InfoRefreshMsg.split("`");

					HeadImageDownloader downloader = new HeadImageDownloader();

					DatabaseHelper dbHelper = new DatabaseHelper(
							InfoListActivity.this, "iPin");
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					db.delete("info", null, null);
					System.out.println("change the list0");
					count = count + Integer.parseInt(temp[0]);
					for (int i = 0; i < Integer.parseInt(temp[0]); i++) {
						InfoListMsgEntity entity = new InfoListMsgEntity();
						String Index, ID, HeadImageVersion, username, from, to, date, detail, time, memberCount, memberList;
						Index = temp[i * 11 + 1];
						ID = temp[i * 11 + 2];
						HeadImageVersion = temp[i * 11 + 3];
						username = temp[i * 11 + 4];
						from = temp[i * 11 + 5];
						to = temp[i * 11 + 6];
						date = temp[i * 11 + 7];
						detail = temp[i * 11 + 8];
						time = temp[i * 11 + 9];
						memberCount = temp[i * 11 + 10];
						memberList = temp[i * 11 + 11];
						entity.setID(ID);
						entity.setHeadImageVersion(HeadImageVersion);
						entity.setUsername(username);
						entity.setFrom(from);
						entity.setTo(to);
						entity.setDate(date);
						entity.setMemberCount(memberCount);
						mDataArrays.add(entity);
						ContentValues values = new ContentValues();
						values.put("GroupID", Index);
						values.put("info_ID", ID);
						values.put("info_HeadImageVersion", HeadImageVersion);
						values.put("info_username", username);
						values.put("info_from", from);
						values.put("info_to", to);
						values.put("info_date", date);
						values.put("info_detail", detail);
						values.put("info_time", time);
						values.put("memberCount", memberCount);
						values.put("memberList", memberList);
						db.insert("info", null, values);
						downloader.download(InfoListActivity.this, ID,
								HeadImageVersion);
						System.out.println("change the list1 "+i+" "+temp[0]);
					}
					db.close();
					dbHelper.close();
	//				socket.close();
					
					System.out.println("change the list1");
									
					return true;
				}
			} catch (Exception e) {
				return false;
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mReRefreshTask = null;
			if (success) {
				swipeLayout.setRefreshing(false);
				mAdapter.notifyDataSetChanged();
				mLoadMoreBtn.setText("查看更多..."); //恢复按钮文字
				System.out.println("change the list");
			} else {
				listview.removeFooterView(mLoadMoreView);
				Toast.makeText(InfoListActivity.this, "没有更多数据了！", Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected void onCancelled() {
			mReRefreshTask = null;
		}
	}
	

}