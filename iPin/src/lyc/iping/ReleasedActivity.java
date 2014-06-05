package lyc.iping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lyc.iping.LoginActivity.UserLoginTask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReleasedActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private Button mBtnSend;
	private Button mBtnBack;
	private String GroupID = null;
	private String info_detail = null;
	private String info_from = null;
	private String info_to = null;
	private String info_date = null;
	private String info_username = null;
	private String memberCount = null;
	private String memberList = null;
	private String LoginUser = null;
	private JoinGroupTask mTask = null;

	private TextView current_detail = null;
	private TextView current_from = null;
	private TextView current_to = null;
	private TextView current_date = null;
	private TextView current_username = null;
	private ImageView current_HeadImage = null;
	private int position;
	private int state;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_released);

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}

	public void initView() {
		mBtnSend = (Button) findViewById(R.id.btn_add);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);

		current_detail = (TextView) findViewById(R.id.tv_Detail);
		current_from = (TextView) findViewById(R.id.tv_From);
		current_to = (TextView) findViewById(R.id.tv_To);
		current_date = (TextView) findViewById(R.id.tv_Date);
		current_username = (TextView) findViewById(R.id.released_username);
		current_HeadImage = (ImageView) findViewById(R.id.released_HeadImage);

		position = getIntent().getExtras().getInt("position") + 1;
		DatabaseHelper dbHelper = new DatabaseHelper(ReleasedActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("info", new String[] { "GroupID", "info_ID",
				"info_HeadImageVersion", "info_username", "info_from",
				"info_to", "info_date", "info_detail", "info_time",
				"memberCount", "memberList" }, null, null, null, null, null);
		if (cursor.move(position)) {
			info_detail = cursor
					.getString(cursor.getColumnIndex("info_detail"));
			current_detail.setText(info_detail);
			info_from = cursor.getString(cursor.getColumnIndex("info_from"));
			current_from.setText(info_from);
			info_to = cursor.getString(cursor.getColumnIndex("info_to"));
			current_to.setText(info_to);
			info_date = cursor.getString(cursor.getColumnIndex("info_date"));
			current_date.setText(info_date);
			info_username = cursor.getString(cursor
					.getColumnIndex("info_username"));
			current_username.setText(info_username);
			GroupID = cursor.getString(cursor.getColumnIndex("GroupID"));
			memberCount = cursor
					.getString(cursor.getColumnIndex("memberCount"));
			memberList = cursor.getString(cursor.getColumnIndex("memberList"));
			String ID = cursor.getString(cursor.getColumnIndex("info_ID"));
			int HeadImageVersion = cursor.getInt(cursor
					.getColumnIndex("info_HeadImageVersion"));

			String AppPath = getApplicationContext().getFilesDir()
					.getAbsolutePath() + "/";
			String ImgPath = AppPath + "HeadImage/" + ID + "_"
					+ HeadImageVersion + ".jpg";
			File ImgFile = new File(ImgPath);
			if (ImgFile.exists()) {
				Bitmap HeadImage = null;
				HeadImage = BitmapFactory.decodeFile(ImgPath, null);
				current_HeadImage.setImageBitmap(HeadImage);
			} else
				current_HeadImage.setImageResource(R.drawable.default_head);
		}
		db.close();
		dbHelper.close();

		dbHelper = new DatabaseHelper(ReleasedActivity.this, "iPin");
		db = dbHelper.getWritableDatabase();
		cursor = db.query("LoginUser",
				new String[] { "ID", "username", "password", "sex",
						"telephone", "HeadImageVersion", "autoLogin" },
				"username<>?", new String[] { "null" }, null, null, null);
		while (cursor.moveToNext()) {
			LoginUser = cursor.getString(cursor.getColumnIndex("username"));
		}

		if (Integer.parseInt(memberCount) == 0) {
			mBtnSend.setText("该主题已经完结");
			state = 0;
		} else if (memberList.contains(LoginUser)) {
			mBtnSend.setText("已加入本组，转到讨论组");
			state = 1;
		} else if (Integer.parseInt(memberCount) == 4) {
			state = 2;
			mBtnSend.setText("本讨论组已满员");
		} else {
			state = 3;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add:
			if (state == 1) {
				Intent intent = new Intent(ReleasedActivity.this,
						DiscussActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				this.finish();
			} else if (state == 3) {
				//mTask = new JoinGroupTask();
				//mTask.execute((Void) null);
				Intent intent = new Intent();
				intent.setAction("com.send");
				intent.putExtra("type", "joinGroup");
				intent.putExtra("command", "JoinGroup " + GroupID + " " + LoginUser);
				ReleasedActivity.this.sendBroadcast(intent);
				Intent intent2 = new Intent(ReleasedActivity.this,
						DiscussActivity.class);
				startActivity(intent2);
				Toast.makeText(ReleasedActivity.this, "成功加入讨论组",
						Toast.LENGTH_SHORT).show();
				ReleasedActivity.this.finish();
			}
			/*
			 * ContentValues values=new ContentValues();
			 * values.put("info_from",info_from); values.put("info_to",info_to);
			 * values.put("info_username",info_username);
			 * values.put("info_date",info_date);
			 * values.put("info_detail",info_detail); //放入讨论组数据库的table里面
			 * DatabaseHelper dbHelper_discuss = new
			 * DatabaseHelper(ReleasedActivity.this,"data"); SQLiteDatabase
			 * db_discuss = dbHelper_discuss.getWritableDatabase(); group_number
			 * = (int)db_discuss.insert("discuss", null, values);
			 * db_discuss.close(); dbHelper_discuss.close();
			 * 
			 * // 下面是切换到聊天界面处理 Group g = new Group();
			 * g.setUsername(info_username); g.setId(group_number-1);
			 * g.setFrom(info_from); g.setTo(info_to); g.setDate(info_date);
			 * 
			 * 
			 * Intent intent = new Intent(this,ChatActivity.class);
			 * intent.putExtra("group", g); startActivity(intent);
			 */
			break;
		case R.id.btn_back:
			Intent intent3 = new Intent(ReleasedActivity.this,
					InfoListActivity.class);
			startActivity(intent3);
			ReleasedActivity.this.finish();
			break;
		}
	}

	public class JoinGroupTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("JoinGroup " + GroupID + " " + LoginUser);
				out.flush();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[1024];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					String LoginMsg = new String(buffer, 0, readSize);
					socket.close();
					return LoginMsg.contains("JoinGroupSuccess");
				}
			} catch (Exception e) {
				return false;
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mTask = null;
			if (success) {
				Intent intent = new Intent(ReleasedActivity.this,
						DiscussActivity.class);
				startActivity(intent);
				Toast.makeText(ReleasedActivity.this, "成功加入讨论组",
						Toast.LENGTH_SHORT).show();
				ReleasedActivity.this.finish();
			} else {
				Toast.makeText(ReleasedActivity.this, "加入讨论组失败",
						Toast.LENGTH_SHORT).show();
			}
			try {
				socket.close();
			} catch (Exception e) {
			}

		}

	}

	// add by hx
	@Override
	public void onBackPressed() {
		Intent intent3 = new Intent(ReleasedActivity.this,
				InfoListActivity.class);
		startActivity(intent3);
		ReleasedActivity.this.finish();
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
}