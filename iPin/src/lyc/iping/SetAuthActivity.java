package lyc.iping;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;

import lyc.iping.ChatActivity.GroupInfoRefreshTask;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SetAuthActivity extends Activity {

	private ImageView icon;
	private TextView username;
	private TextView setAuthState;
	private EditText setAuth_StudentID;
	private EditText setAuth_PersonID;
	private String LoginUser;
	private String ID;
	private String HeadImageVersion;
	private String telephone;
	private int auth;
	private String StudentID;
	private String PersonID;
	private SetTelephoneTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setauth);

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		icon = (ImageView) findViewById(R.id.settelephone_head);
		username = (TextView) findViewById(R.id.settelephone_username);
		setAuthState = (TextView) findViewById(R.id.setAuthState);
		setAuth_StudentID = (EditText) findViewById(R.id.setAuth_StudentID);
		setAuth_PersonID = (EditText) findViewById(R.id.setAuth_PersonID);
		DatabaseHelper dbHelper = new DatabaseHelper(SetAuthActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("LoginUser",
				new String[] { "ID", "username", "password", "sex",
						"telephone", "HeadImageVersion", "autoLogin","auth","StudentID","PersonID" },
				"username<>?", new String[] { "null" }, null, null, null);
		while (cursor.moveToNext()) {
			ID = cursor.getString(cursor.getColumnIndex("ID"));
			HeadImageVersion = cursor.getString(cursor
					.getColumnIndex("HeadImageVersion"));
			LoginUser = cursor.getString(cursor.getColumnIndex("username"));
			telephone = cursor.getString(cursor.getColumnIndex("telephone"));
			auth=cursor.getInt(cursor.getColumnIndex("auth"));
			StudentID=cursor.getString(cursor.getColumnIndex("StudentID"));
			PersonID=cursor.getString(cursor.getColumnIndex("PersonID"));
		}
		db.close();
		dbHelper.close();
		username.setText(LoginUser);		
		String AppPath = getApplicationContext().getFilesDir()
				.getAbsolutePath() + "/";
		String ImgPath = AppPath + "HeadImage/" + ID + "_" + HeadImageVersion
				+ ".jpg";
		File ImgFile = new File(ImgPath);
		if (ImgFile.exists()) {
			Bitmap HeadImage = null;
			HeadImage = BitmapFactory.decodeFile(ImgPath, null);
			icon.setImageBitmap(HeadImage);
		}
		switch(auth)
		{
		case 0:
			setAuthState.setText("未认证");
			break;
		case 1:
			setAuthState.setText("认证处理中");
			setAuth_StudentID.setText(StudentID);
			setAuth_PersonID.setText(PersonID);
			break;
		case 2:
			setAuthState.setText("一卡通号不存在");
			setAuth_StudentID.setText(StudentID);
			setAuth_PersonID.setText(PersonID);
			break;
		case 3:
			setAuthState.setText("一卡通号重复绑定");
			setAuth_StudentID.setText(StudentID);
			setAuth_PersonID.setText(PersonID);
			break;
		case 4:
			setAuthState.setText("认证不通过");
			setAuth_StudentID.setText(StudentID);
			setAuth_PersonID.setText(PersonID);
			break;
		case 5:
			setAuthState.setText("认证成功");
			setAuth_StudentID.setText(StudentID);
			setAuth_PersonID.setText(PersonID);
			break;
		default:
			break;
		}
	}

	public void btn_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void btn_submit(View v) { // 标题栏 返回按钮
		StudentID = setAuth_StudentID.getText().toString();
		PersonID = setAuth_PersonID.getText().toString();
		if (StudentID.length() != 9) {
			setAuth_StudentID.setError("请输入9位一卡通号");
			setAuth_StudentID.requestFocus();
			return;
		} else if (PersonID.length() != 6) {
			setAuth_PersonID.setError("请输入身份证后6位");
			setAuth_PersonID.requestFocus();
			return;
		}
		DatabaseHelper dbHelper = new DatabaseHelper(SetAuthActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("auth", 1);
		values.put("StudentID", StudentID);
		values.put("PersonID", PersonID);
		db.update("LoginUser", values, "username=?", new String[] { LoginUser });
		db.close();
		dbHelper.close();
		mTask = new SetTelephoneTask();
		mTask.execute((Void) null);
		this.finish();
	}

	public class SetTelephoneTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("Auth " + ID + " " + StudentID + " " + PersonID);
				out.flush();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[1024];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					String SetTelephoneMsg = new String(buffer, 0, readSize);
					socket.close();
					return SetTelephoneMsg.contains("AuthSuccess");
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
				Toast.makeText(SetAuthActivity.this, "认证申请已发送",
						Toast.LENGTH_SHORT).show();
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
