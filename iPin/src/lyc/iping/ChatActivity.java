package lyc.iping;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import lyc.iping.MessageDB;
import lyc.iping.Group;
import lyc.iping.GroupInfoActivity.GroupInfoRefreshTask;

public class ChatActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private String LoginUser;
	private String ID;
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private MessageDB messageDB;
	private Group group;
	private Boolean isCreator = false;
	private GroupInfoRefreshTask mTask;
	private int memberCount;
	private Bitmap[] HeadImage = null;
	private String LastDate = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		group = (Group) getIntent().getSerializableExtra("group");
		memberCount = Integer.parseInt(group.getMemberCount());
		DatabaseHelper dbHelper1 = new DatabaseHelper(ChatActivity.this, "iPin");
		SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
		Cursor cursor = db1.query("LoginUser",
				new String[] { "ID", "username", "password", "sex",
						"telephone", "HeadImageVersion", "autoLogin" },
				"username<>?", new String[] { "null" }, null, null, null);
		while (cursor.moveToNext()) {
			LoginUser = cursor.getString(cursor.getColumnIndex("username"));
			ID = cursor.getString(cursor.getColumnIndex("ID"));
		}
		db1.close();
		dbHelper1.close();
		messageDB = new MessageDB(ID, this);
		if (LoginUser.equals(group.getCreatorUsername()))
			isCreator = true;

		HeadImage = new Bitmap[4];
		initView();
		initData();

		// add by hx
		// unregisterReceiver(rcv);
		Chatrcv rcv = new Chatrcv();

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.rcv");
		this.registerReceiver(rcv, filter);
		System.out.println("注册chat广播完成！");

		// end

	}

	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);

		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		mTask = new GroupInfoRefreshTask();
		mTask.execute((Void) null);
	}

	// 从数据库中读取之前的聊天记录
	public void initData() {
		List<ChatMsgEntity> list = messageDB.getMsg(Integer.parseInt(group
				.getGroupID()));
		if (list.size() > 0) {
			for (ChatMsgEntity entity : list) {
				if (entity.getName().equals("")) {
					entity.setName(group.getCreatorUsername());
				}
				DatabaseHelper dbHelper = new DatabaseHelper(ChatActivity.this,
						"iPin");
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				Cursor cursor = db.query("GroupMember", new String[] { "rowid",
						"ID", "username", "HeadImageVersion" }, "username=?",
						new String[] { entity.getName() }, null, null, null);
				String ID = null, HeadImageVersion = null;
				int rowid = memberCount;
				while (cursor.moveToNext()) {
					ID = cursor.getString(cursor.getColumnIndex("ID"));
					HeadImageVersion = cursor.getString(cursor
							.getColumnIndex("HeadImageVersion"));
					rowid = cursor.getInt(cursor.getColumnIndex("rowid"));
				}
				entity.setIndex(rowid);
				entity.setID(ID);
				entity.setHeadImageVersion(HeadImageVersion);

				if (entity.getDate().equals(LastDate))
					entity.setShowDate(false);
				LastDate = entity.getDate();

				mDataArrays.add(entity);
			}
			Collections.reverse(mDataArrays);
		}
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays, HeadImage);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(mAdapter.getCount() - 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		messageDB.close();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			finish();
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			// entity.setName(util.getName());
			entity.setName(LoginUser);
			entity.setDate(getDate());
			entity.setMessage(contString);
			// 需要当前用户的图片
			// entity.setImg(util.getImg());
			entity.setMsgType(false);

			messageDB.saveMsg(Integer.parseInt(group.getGroupID()), entity);
			DatabaseHelper dbHelper1 = new DatabaseHelper(ChatActivity.this,
					"iPin");
			SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
			Cursor cursor = db1.query("LoginUser", new String[] { "ID",
					"username", "password", "sex", "telephone",
					"HeadImageVersion", "autoLogin" }, "username=?",
					new String[] { LoginUser }, null, null, null);
			String ID = null, HeadImageVersion = null;
			while (cursor.moveToNext()) {
				ID = cursor.getString(cursor.getColumnIndex("ID"));
				HeadImageVersion = cursor.getString(cursor
						.getColumnIndex("HeadImageVersion"));
			}

			int rowid = memberCount;
			cursor = db1.query("GroupMember", new String[] { "rowid" }, "ID=?",
					new String[] { ID }, null, null, null);
			while (cursor.moveToNext()) {
				rowid = cursor.getInt(cursor.getColumnIndex("rowid"));
			}
			db1.close();
			dbHelper1.close();
			entity.setIndex(rowid);
			entity.setID(ID);
			entity.setHeadImageVersion(HeadImageVersion);

			if (entity.getDate().equals(LastDate))
				entity.setShowDate(false);
			LastDate = entity.getDate();

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
			mEditTextContent.setText("");// 清空编辑框数据
			mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项

			// add by hx
			// 将数据广播出去
			Intent intent = new Intent();
			intent.setAction("com.send");
			intent.putExtra("type", "message");
			intent.putExtra("LoginUser", LoginUser);
			intent.putExtra("groupID", group.getGroupID());
			intent.putExtra("date", getDate());
			intent.putExtra("Msg", contString);
			ChatActivity.this.sendBroadcast(intent);
			System.out.println("向服务端发送信息:" + contString);
			// end
		}
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH) + 1);
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}

	public void teaminfo(View v) {
		Intent intent = new Intent(ChatActivity.this, GroupInfoActivity.class);
		intent.putExtra("group", group);
		intent.putExtra("isCreator", isCreator);
		intent.putExtra("LoginUser", LoginUser);
		startActivity(intent);
	}

	public class Chatrcv extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// unregisterReceiver(this);
			String type = intent.getStringExtra("type");
			if(type.equals("message"))
			{
				String chat_name = intent.getStringExtra("name");
				String chat_groupid = intent.getStringExtra("groupID");
				String chat_msg = intent.getStringExtra("msg");
				String chat_date = intent.getStringExtra("date");

				if (chat_groupid.equals(group.getGroupID())) {
					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setName(chat_name);
					entity.setDate(chat_date);
					entity.setMessage(chat_msg);
					// entity.setImg(util.getImg());
					entity.setMsgType(true);
					DatabaseHelper dbHelper = new DatabaseHelper(ChatActivity.this,
							"iPin");
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					Cursor cursor = db.query("GroupMember", new String[] { "rowid",
							"ID", "username", "HeadImageVersion" }, "username=?",
							new String[] { chat_name }, null, null, null);
					String ID = null, HeadImageVersion = null;
					int rowid = memberCount;
					while (cursor.moveToNext()) {
						ID = cursor.getString(cursor.getColumnIndex("ID"));
						HeadImageVersion = cursor.getString(cursor
								.getColumnIndex("HeadImageVersion"));
						rowid = cursor.getInt(cursor.getColumnIndex("rowid"));
					}
					entity.setIndex(rowid);
					entity.setID(ID);
					entity.setHeadImageVersion(HeadImageVersion);

					if (entity.getDate().equals(LastDate))
						entity.setShowDate(false);
					LastDate = entity.getDate();

					mDataArrays.add(entity);
					mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
					mListView.setSelection(mListView.getCount() - 1);// ListView显示选择最后一项
				}
			}
			else if(type.equals("minusCount"))
			{
				String groupID = intent.getStringExtra("GroupID");
				if(groupID.equals(group.getGroupID()))
				{
					System.out.println(group.getMemberCount()+" "+group.getMemberList());
					memberCount--;
					group.setMemberCount(memberCount+"");					
					String name=intent.getStringExtra("name");
					String memberList = group.getMemberList();
					memberList.replace(name+"|", "");
					group.setMemberList(memberList);
					System.out.println(group.getMemberCount()+" "+group.getMemberList());
				}
			}
			else if(type.equals("plusCount"))
			{
				String groupID = intent.getStringExtra("GroupID");
				if(groupID.equals(group.getGroupID()))
				{
					System.out.println(group.getMemberCount()+" "+group.getMemberList());
					memberCount++;
					group.setMemberCount(memberCount+"");					
					String name=intent.getStringExtra("name");
					String memberList = group.getMemberList()+name+"|";					
					group.setMemberList(memberList);
					System.out.println(group.getMemberCount()+" "+group.getMemberList());
					mTask = new GroupInfoRefreshTask();
					mTask.execute((Void) null);
				}
			}
		}
	}

	public class GroupInfoRefreshTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				String memberList = group.getMemberList();
				String[] temp = memberList.split("\\|");
//				out.print("GroupInfoRefresh " + memberCount);
//				for (int i = 0; i < memberCount; i++)
//					out.print(" " + temp[i]);
				out.print("GroupInfoRefresh2 "+group.getGroupID());
				out.flush();
				System.out.println("GroupInfoRefresh2 "+group.getGroupID());
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[2048];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					String GroupInfoRefreshMsg = new String(buffer, 0, readSize);
					System.out.println(GroupInfoRefreshMsg);
					if (GroupInfoRefreshMsg.contains("GroupInfoRefreshNone"))
						return false;
					HeadImageDownloader downloader = new HeadImageDownloader();
					temp = GroupInfoRefreshMsg.split(" ");
					DatabaseHelper dbHelper = new DatabaseHelper(
							ChatActivity.this, "iPin");
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					db.delete("GroupMember", null, null);
					memberCount=Integer.parseInt(temp[0]);
					group.setMemberCount(temp[0]);
					for (int i = 0; i < memberCount; i++) {
						String ID, HeadImageVersion, username, sex, telephone;
						ID = temp[i * 5 + 1];
						HeadImageVersion = temp[i * 5 + 2];
						username = temp[i * 5 + 3];
						sex = temp[i * 5 + 4];
						telephone = temp[i * 5 + 5];
						ContentValues values = new ContentValues();
						values.put("GroupID", group.getGroupID());
						values.put("ID", ID);
						values.put("HeadImageVersion", HeadImageVersion);
						values.put("username", username);
						values.put("sex", sex);
						values.put("telephone", telephone);
						db.insert("GroupMember", null, values);
						downloader.download(ChatActivity.this, ID,
								HeadImageVersion);
					}
					db.close();
					dbHelper.close();
					socket.close();
					// mAdapter.notifyDataSetChanged();
					// initData();
					// return true;
				}
			} catch (Exception e) {
				return false;
			}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mTask = null;
			if (success) {
				System.out.println("ChatActivity PostExecute");
				mAdapter.notifyDataSetChanged();
			} else {

			}

		}

		@Override
		protected void onCancelled() {
			mTask = null;
		}
	}

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
}