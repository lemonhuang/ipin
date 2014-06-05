package lyc.iping;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;

import lyc.iping.SetTelephoneActivity.SetTelephoneTask;

import android.app.Activity;
import android.content.ContentValues;
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

public class SetPasswordActivity extends Activity {
	private String LoginUser;
	private String ID;
	private String oldpassword_MD5;
	private String HeadImageVersion;

	private String MD5Password;
	private String oldPassword_str;
	private String newPassword_str;
	private String confirmPassword_str;

	private ImageView icon;
	private TextView username;
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword;
	private SetPasswordTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpassword);

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		icon = (ImageView) findViewById(R.id.setpassword_head);
		username = (TextView) findViewById(R.id.setpassword_username);
		oldPassword = (EditText) findViewById(R.id.setpassword_oldpassword);
		newPassword = (EditText) findViewById(R.id.setpassword_newpassword);
		confirmPassword = (EditText) findViewById(R.id.setpassword_confirmpassword);

		DatabaseHelper dbHelper = new DatabaseHelper(SetPasswordActivity.this,
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
			oldpassword_MD5 = cursor.getString(cursor
					.getColumnIndex("password"));
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
	}

	public void btn_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void btn_setpassword(View v) { // 标题栏 返回按钮
		oldPassword_str = oldPassword.getText().toString();
		newPassword_str = newPassword.getText().toString();
		confirmPassword_str = confirmPassword.getText().toString();
		MD5Password = MD5(oldPassword_str);
		if (TextUtils.isEmpty(oldPassword_str)) {
			oldPassword.setError("旧密码不能为空");
			oldPassword.requestFocus();
			return;
		} else if (TextUtils.isEmpty(newPassword_str)) {
			newPassword.setError("新设定密码不能为空");
			newPassword.requestFocus();
			return;
		} else if (newPassword_str.length() < 6) {
			newPassword.setError("密码不能少于6位");
			newPassword.requestFocus();
			return;
		} else if (!newPassword_str.equals(confirmPassword_str)) {
			confirmPassword.setError("确认密码必须与新设定密码相同");
			confirmPassword.requestFocus();
			return;
		} else if (!MD5Password.equals(oldpassword_MD5)) {
			oldPassword.setError("旧密码错误");
			oldPassword.requestFocus();
			return;
		}
		MD5Password = MD5(newPassword_str);
		DatabaseHelper dbHelper = new DatabaseHelper(SetPasswordActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("password", MD5Password);
		db.update("LoginUser", values, "username=?", new String[] { LoginUser });
		db.close();
		dbHelper.close();
		mTask = new SetPasswordTask();
		mTask.execute((Void) null);
		this.finish();
	}

	public String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public class SetPasswordTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("SetPassword " + ID + " " + MD5Password);
				out.flush();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[1024];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					String SetPasswordMsg = new String(buffer, 0, readSize);
					socket.close();
					return SetPasswordMsg.contains("SetPasswordSuccess");
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
				Toast.makeText(SetPasswordActivity.this, "更改密码成功",
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
