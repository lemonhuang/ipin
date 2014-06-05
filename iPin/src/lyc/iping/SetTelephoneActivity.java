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

public class SetTelephoneActivity extends Activity {

	private ImageView icon;
	private TextView username;
	private TextView oldNumber;
	private EditText newNumber;
	private String LoginUser;
	private String ID;
	private String HeadImageVersion;
	private String telephone;
	private SetTelephoneTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settelephone);

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		icon = (ImageView) findViewById(R.id.settelephone_head);
		username = (TextView) findViewById(R.id.settelephone_username);
		oldNumber = (TextView) findViewById(R.id.settelephone_oldnumber);
		newNumber = (EditText) findViewById(R.id.settelephone_newnumber);
		DatabaseHelper dbHelper = new DatabaseHelper(SetTelephoneActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("LoginUser",
				new String[] { "ID", "username", "password", "sex",
						"telephone", "HeadImageVersion", "autoLogin" },
				"username<>?", new String[] { "null" }, null, null, null);
		while (cursor.moveToNext()) {
			ID = cursor.getString(cursor.getColumnIndex("ID"));
			HeadImageVersion = cursor.getString(cursor
					.getColumnIndex("HeadImageVersion"));
			LoginUser = cursor.getString(cursor.getColumnIndex("username"));
			telephone = cursor.getString(cursor.getColumnIndex("telephone"));
		}
		db.close();
		dbHelper.close();
		username.setText(LoginUser);
		oldNumber.setText(telephone);
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

	}

	public void btn_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void btn_submit(View v) { // 标题栏 返回按钮
		telephone = newNumber.getText().toString();
		if (TextUtils.isEmpty(telephone)) {
			newNumber.setError("请输入新的联系方式");
			newNumber.requestFocus();
			return;
		} else if (telephone.length() != 11) {
			newNumber.setError("请输入正确的联系方式");
			newNumber.requestFocus();
			return;
		}
		DatabaseHelper dbHelper = new DatabaseHelper(SetTelephoneActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("telephone", telephone);
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
				out.print("SetTelephone " + ID + " " + telephone);
				out.flush();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[1024];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					String SetTelephoneMsg = new String(buffer, 0, readSize);
					socket.close();
					return SetTelephoneMsg.contains("SetTelephoneSuccess");
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
				Toast.makeText(SetTelephoneActivity.this, "更改联系方式成功",
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
