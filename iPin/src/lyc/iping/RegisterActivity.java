package lyc.iping;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private EditText register_name = null;
	private EditText register_password = null;
	private EditText register_password_confirm = null;
	private EditText register_telephone = null;
	private RadioGroup radioSex = null;
	private RadioButton radio_male = null;
	private RadioButton radio_female = null;
	private String register_name_str = null;
	private String register_password_str = null;
	private String register_password_confirm_str = null;
	private String MD5_Password = null;
	private String register_telephone_str = null;
	private String register_sex = null;

	private String RegisterMsg = null;

	private View mRegisterFormView = null;
	private View mRegisterStatusView = null;

	private UserLoginTask mAuthTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register);

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		register_name = (EditText) findViewById(R.id.register_name);
		register_password = (EditText) findViewById(R.id.register_password);
		register_password_confirm = (EditText) findViewById(R.id.register_password_confirm);
		register_telephone = (EditText) findViewById(R.id.register_telephone);
		radioSex = (RadioGroup) findViewById(R.id.radio_sex);
		radio_male = (RadioButton) findViewById(R.id.radioButton_male);
		radio_female = (RadioButton) findViewById(R.id.radioButton_female);

		mRegisterFormView = findViewById(R.id.Register_ScrollView);
		mRegisterStatusView = findViewById(R.id.register_status);

		findViewById(R.id.register_confirm).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
		findViewById(R.id.register_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(RegisterActivity.this,
								LoginActivity.class);
						startActivity(intent);
						RegisterActivity.this.finish();
					}
				});
		register_name.requestFocus();
	}

	private void attemptRegister() {
		register_name_str = register_name.getText().toString();
		register_password_str = register_password.getText().toString();
		register_password_confirm_str = register_password_confirm.getText()
				.toString();
		register_telephone_str = register_telephone.getText().toString();
		if (TextUtils.isEmpty(register_name_str)) {
			register_name.setError("注册用户名不能为空");
			register_name.requestFocus();
		} else if (TextUtils.isEmpty(register_password_str)) {
			register_password.setError("注册密码不能为空");
			register_password.requestFocus();
		} else if (register_name_str.length() > 20) {
			register_name.setError("用户名长度超过20个字符");
			register_name.requestFocus();
		} else if (register_password_str.length() < 6) {
			register_password.setError("注册密码不能少于6位");
			register_password.requestFocus();
		} else if (TextUtils.isEmpty(register_password_confirm_str)) {
			register_password_confirm.setError("确认密码不能为空");
			register_password_confirm.requestFocus();
		} else if (!register_password_confirm_str.equals(register_password_str)) {
			register_password_confirm.setError("确认密码与注册密码必须相同");
			register_password_confirm.requestFocus();
		} else if (register_telephone_str.length() != 11) {
			register_telephone.setError("请输入正确的手机号码");
			register_telephone.requestFocus();
		} else {
			// 注册信息符合标准，发送到服务器端进行核对
			//showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				MD5_Password = MD5(register_password_str);
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				if (radioSex.getCheckedRadioButtonId() == radio_male.getId()) {
					register_sex = "male";
				} else if (radioSex.getCheckedRadioButtonId() == radio_female
						.getId()) {
					register_sex = "female";
				}
				out.print("userRegister " + register_name_str + " "
						+ MD5_Password + " " + register_sex + " "
						+ register_telephone_str);
				out.flush();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[1024];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					RegisterMsg = new String(buffer, 0, readSize);
					socket.close();
					return RegisterMsg.contains("userRegisterSuccess");
				}
			} catch (Exception e) {
				return false;
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				String[] registerMsg = RegisterMsg.split(":");
				DatabaseHelper dbHelper = new DatabaseHelper(
						RegisterActivity.this, "iPin");
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("ID", registerMsg[1]);
				values.put("username", register_name_str);
				values.put("password", MD5_Password);
				values.put("sex", register_sex);
				values.put("telephone", register_telephone_str);
				values.put("HeadImageVersion", 0);
				values.put("autoLogin", false);
				db.update("LoginUser", values, "autoLogin=?",
						new String[] { "0" });
				db.close();
				dbHelper.close();
				Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_LONG).show();  
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
			} else {
				showProgress(false);
				register_password_confirm.setError("注册失败");
				register_password_confirm.requestFocus();
			}
			try {
				socket.close();
			} catch (Exception e) {
			}

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		this.finish();
		// super.onBackPressed();

	}
}
