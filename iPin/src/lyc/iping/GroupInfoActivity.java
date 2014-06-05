package lyc.iping;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import lyc.iping.MemberDB;
import lyc.iping.Group;
import lyc.iping.MemberEntity;
import lyc.iping.DiscussActivity.DiscussRefreshTask;

public class GroupInfoActivity extends Activity {

	private TextView From;
	private TextView To;
	private TextView Time;
	private TextView detail;
	private ImageView member0;
	private ImageView member1;
	private ImageView member2;
	private ImageView member3;
	private Button ExitGroup;
	private Group group;
	private int memberCount;
	private GroupInfoRefreshTask mTask;
	private ExitGroupTask mTask1;
	private Boolean isCreator = false;
	private String LoginUser = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groupinfo);

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		group = (Group) getIntent().getSerializableExtra("group");
		mTask = new GroupInfoRefreshTask();
		mTask.execute((Void) null);
		initView();
	}

	public void initView() {
		From = (TextView) findViewById(R.id.TextFrom);
		To = (TextView) findViewById(R.id.TextTo);
		Time = (TextView) findViewById(R.id.TextTime);
		detail = (TextView) findViewById(R.id.groupinfo_detail);
		ExitGroup = (Button) findViewById(R.id.exitgroup);
		From.setText(group.getFrom());
		To.setText(group.getTo());
		Time.setText(group.getDate());
		detail.setText(group.getDetail());

		member0 = (ImageView) findViewById(R.id.creator);
		member1 = (ImageView) findViewById(R.id.member1);
		member2 = (ImageView) findViewById(R.id.member2);
		member3 = (ImageView) findViewById(R.id.member3);
		memberCount = Integer.parseInt(group.getMemberCount());
		isCreator = getIntent().getBooleanExtra("isCreator", false);
		if (isCreator)
			ExitGroup.setText("解散讨论组");
		LoginUser = getIntent().getStringExtra("LoginUser");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void btn_send(View v) {
		mTask1 = new ExitGroupTask();
		mTask1.execute((Void) null);
	}

	public void OnMember0(View v) {
		DatabaseHelper dbHelper = new DatabaseHelper(GroupInfoActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("GroupMember", new String[] { "ID",
				"username", "sex", "telephone", "HeadImageVersion" }, null,
				null, null, null, null);
		cursor.moveToPosition(0);
		Intent intent = new Intent(GroupInfoActivity.this,
				MemberInfoActivity.class);
		intent.putExtra("ID", cursor.getString(cursor.getColumnIndex("ID")));
		intent.putExtra("username",
				cursor.getString(cursor.getColumnIndex("username")));
		intent.putExtra("sex", cursor.getString(cursor.getColumnIndex("sex")));
		intent.putExtra("telephone",
				cursor.getString(cursor.getColumnIndex("telephone")));
		intent.putExtra("HeadImageVersion",
				cursor.getString(cursor.getColumnIndex("HeadImageVersion")));
		intent.putExtra("isCreator", isCreator);
		intent.putExtra("memberIndex", 0);
		intent.putExtra("GroupID", group.getGroupID());
		startActivity(intent);
		db.close();
		dbHelper.close();

	}

	public void OnMember1(View v) {
		if (memberCount >= 2) {
			DatabaseHelper dbHelper = new DatabaseHelper(
					GroupInfoActivity.this, "iPin");
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.query("GroupMember", new String[] { "ID",
					"username", "sex", "telephone", "HeadImageVersion" }, null,
					null, null, null, null);
			cursor.moveToPosition(1);
			Intent intent = new Intent(GroupInfoActivity.this,
					MemberInfoActivity.class);
			intent.putExtra("ID", cursor.getString(cursor.getColumnIndex("ID")));
			intent.putExtra("username",
					cursor.getString(cursor.getColumnIndex("username")));
			intent.putExtra("sex",
					cursor.getString(cursor.getColumnIndex("sex")));
			intent.putExtra("telephone",
					cursor.getString(cursor.getColumnIndex("telephone")));
			intent.putExtra("HeadImageVersion",
					cursor.getString(cursor.getColumnIndex("HeadImageVersion")));
			intent.putExtra("isCreator", isCreator);
			intent.putExtra("memberIndex", 1);
			intent.putExtra("GroupID", group.getGroupID());
			startActivity(intent);
			db.close();
			dbHelper.close();
		}

	}

	public void OnMember2(View v) {
		if (memberCount >= 3) {
			DatabaseHelper dbHelper = new DatabaseHelper(
					GroupInfoActivity.this, "iPin");
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.query("GroupMember", new String[] { "ID",
					"username", "sex", "telephone", "HeadImageVersion" }, null,
					null, null, null, null);
			cursor.moveToPosition(2);
			Intent intent = new Intent(GroupInfoActivity.this,
					MemberInfoActivity.class);
			intent.putExtra("ID", cursor.getString(cursor.getColumnIndex("ID")));
			intent.putExtra("username",
					cursor.getString(cursor.getColumnIndex("username")));
			intent.putExtra("sex",
					cursor.getString(cursor.getColumnIndex("sex")));
			intent.putExtra("telephone",
					cursor.getString(cursor.getColumnIndex("telephone")));
			intent.putExtra("HeadImageVersion",
					cursor.getString(cursor.getColumnIndex("HeadImageVersion")));
			intent.putExtra("isCreator", isCreator);
			intent.putExtra("memberIndex", 2);
			intent.putExtra("GroupID", group.getGroupID());
			startActivity(intent);
			db.close();
			dbHelper.close();
		}

	}

	public void OnMember3(View v) {
		if (memberCount >= 4) {
			DatabaseHelper dbHelper = new DatabaseHelper(
					GroupInfoActivity.this, "iPin");
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.query("GroupMember", new String[] { "ID",
					"username", "sex", "telephone", "HeadImageVersion" }, null,
					null, null, null, null);
			cursor.moveToPosition(3);
			Intent intent = new Intent(GroupInfoActivity.this,
					MemberInfoActivity.class);
			intent.putExtra("ID", cursor.getString(cursor.getColumnIndex("ID")));
			intent.putExtra("username",
					cursor.getString(cursor.getColumnIndex("username")));
			intent.putExtra("sex",
					cursor.getString(cursor.getColumnIndex("sex")));
			intent.putExtra("telephone",
					cursor.getString(cursor.getColumnIndex("telephone")));
			intent.putExtra("HeadImageVersion",
					cursor.getString(cursor.getColumnIndex("HeadImageVersion")));
			intent.putExtra("isCreator", isCreator);
			intent.putExtra("memberIndex", 3);
			intent.putExtra("GroupID", group.getGroupID());
			startActivity(intent);
			db.close();
			dbHelper.close();
		}

	}

	public void btn_back(View v) {
		this.finish();
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
							GroupInfoActivity.this, "iPin");
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
						downloader.download(GroupInfoActivity.this, ID,
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
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mTask = null;
			if (success) {
				DatabaseHelper dbHelper = new DatabaseHelper(
						GroupInfoActivity.this, "iPin");
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				Cursor cursor = db.query("GroupMember", new String[] { "ID",
						"HeadImageVersion" }, null, null, null, null, null);
				int i = 0;
				while (cursor.moveToNext()) {
					String ID, HeadImageVersion;
					ID = cursor.getString(cursor.getColumnIndex("ID"));
					HeadImageVersion = cursor.getString(cursor
							.getColumnIndex("HeadImageVersion"));
					if (Integer.parseInt(HeadImageVersion) > 0) {
						String AppPath = getApplicationContext().getFilesDir()
								.getAbsolutePath() + "/";
						String ImgPath = AppPath + "HeadImage/" + ID + "_"
								+ HeadImageVersion + ".jpg";
						File ImgFile = new File(ImgPath);
						if (ImgFile.exists()) {
							Bitmap HeadImage = null;
							HeadImage = BitmapFactory.decodeFile(ImgPath, null);
							switch (i) {
							case 0:
								member0.setImageBitmap(HeadImage);
								break;
							case 1:
								member1.setImageBitmap(HeadImage);
								break;
							case 2:
								member2.setImageBitmap(HeadImage);
								break;
							case 3:
								member3.setImageBitmap(HeadImage);
								break;
							}
						}
					} else {
						switch (i) {
						case 0:
							member0.setImageResource(R.drawable.default_head);
							break;
						case 1:
							member1.setImageResource(R.drawable.default_head);
							break;
						case 2:
							member2.setImageResource(R.drawable.default_head);
							break;
						case 3:
							member3.setImageResource(R.drawable.default_head);
							break;
						}
					}
					i++;
				}
			} else {

			}

		}

		@Override
		protected void onCancelled() {
			mTask = null;
		}
	}

	public class ExitGroupTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				if (isCreator)
				{	
					//out.print("DischargeGroup " + group.getGroupID());
					Intent intent = new Intent();
					intent.setAction("com.send");
					intent.putExtra("type", "dischargeGroup");
					intent.putExtra("command", "DischargeGroup " + group.getGroupID());
					GroupInfoActivity.this.sendBroadcast(intent);					
				}					
				else
				{
					//out.print("ExitGroup " + group.getGroupID() + " "+ LoginUser);
					Intent intent = new Intent();
					intent.setAction("com.send");
					intent.putExtra("type", "exitGroup");
					intent.putExtra("command", "ExitGroup " + group.getGroupID() + " "+ LoginUser);
					GroupInfoActivity.this.sendBroadcast(intent);
				}
				//out.flush();
				socket.close();
				return true;
			} catch (Exception e) {
				return false;
			}
			// TODO: register the new account here.

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mTask1 = null;
			if (success) {
				if (isCreator) {
					Toast.makeText(GroupInfoActivity.this, "解散成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(GroupInfoActivity.this, "退出成功",
							Toast.LENGTH_SHORT).show();
				}
				Intent intent = new Intent(GroupInfoActivity.this,
						DiscussActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				GroupInfoActivity.this.finish();
			} else {
				Toast.makeText(GroupInfoActivity.this, "操作失败",
						Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mTask1 = null;
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