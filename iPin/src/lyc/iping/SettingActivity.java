package lyc.iping;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import lyc.iping.NearByActivity.Nearrcv;

import com.readystatesoftware.viewbadger.BadgeView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity{

	private ImageView icon;
	private TextView myusername;
	private String LoginUser;
	private String HeadImageVersion;
	private ImageView img_info = null;
	private ImageView img_discuss = null;
	private ImageView img_nearby = null;
	private RelativeLayout SetAuth = null;
	private RelativeLayout SetHeadImage = null;
	private RelativeLayout AboutiPin = null;
	private RelativeLayout Advice = null;
	private RelativeLayout SetPassword = null;
	private RelativeLayout SetTelephone = null;
	//add by hx
	private RelativeLayout PostMgr = null;
	//end
	private RelativeLayout ClearMessage = null;
	private Button logout = null;
	private String username = null;
	private String ID = null;
	private UploadTask uploadTask = null;
	private String ImgPath = null;
	View target = null;
	BadgeView badge = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_settings);
		
		//退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);
		
		//add by hx
    	Setrcv rcv = new Setrcv();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.rcv");
		this.registerReceiver(rcv, filter);
		System.out.println("注册Set广播完成！");
		//end
		
		icon = (ImageView) findViewById(R.id.imageView1);
		myusername = (TextView) findViewById(R.id.textView1);
		
		DatabaseHelper dbHelper1 = new DatabaseHelper(SettingActivity.this,
				"iPin");
		SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
		Cursor cursor1 = db1.query("LoginUser",
				new String[] { "ID", "username", "password", "sex",
						"telephone", "HeadImageVersion", "autoLogin" },
				"username<>?", new String[] { "null" }, null, null, null);
		while (cursor1.moveToNext()) {
			ID = cursor1.getString(cursor1.getColumnIndex("ID"));
			HeadImageVersion = cursor1.getString(cursor1
					.getColumnIndex("HeadImageVersion"));
			LoginUser = cursor1.getString(cursor1.getColumnIndex("username"));
		}
		db1.close();
		dbHelper1.close();
		myusername.setText(LoginUser);
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
		
		 target = findViewById(R.id.img_address_3);
	     badge = new BadgeView(SettingActivity.this, target);
	     
	   //显示消息数目
   		if(service.group_msg_num!=0)
   		{
   	     	badge.setText(service.group_msg_num+"");
   	     //	badge.setTextColor(Color.RED);
   	     	badge.setTextSize(10);
   	     	badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
   	     	badge.setBadgeMargin(0,0);
   	     	badge.show();
   		}
		
		
		DatabaseHelper dbHelper = new DatabaseHelper(SettingActivity.this,"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		Cursor cursor = db.query("LoginUser", new String[]{"ID","username","password","sex","autoLogin"}, null, null, null, null, null);
		while(cursor.moveToNext())
		{
			username = cursor.getString(cursor.getColumnIndex("username"));
			ID = cursor.getString(cursor.getColumnIndex("ID"));
		}
		db.close();
		dbHelper.close();
		
		img_info = (ImageView)findViewById(R.id.img_weixin_3);
		img_discuss = (ImageView)findViewById(R.id.img_address_3);
		img_nearby = (ImageView)findViewById(R.id.img_nearby_3);
		
		logout=(Button)findViewById(R.id.logout_btn);
		logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				DatabaseHelper dbHelper = new DatabaseHelper(SettingActivity.this,"iPin");
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues value = new ContentValues();
				value.put("autoLogin", false);
				db.update("LoginUser",value,"username=?",new String[]{username});
				db.close();
				dbHelper.close();
				
				//add by hx
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
				
				Intent intent2=new Intent(SettingActivity.this,service.class);
				stopService(intent2);
				//end
				
				Intent intent = new Intent (SettingActivity.this,LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);			
				SettingActivity.this.finish();
				
			}
		});
		/*
		SetHeadImage=(RelativeLayout)findViewById(R.id.SetHeadImage);
		SetHeadImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				//Intent intent = new Intent();
		        //intent.setAction(Intent.ACTION_GET_CONTENT);
		        //intent.setType("image/*");
				Intent intent = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);						 
						
		        startActivityForResult(intent,12);
		        //startActivityForResult(Intent.createChooser(intent, "Select Picture"),12);
			}
		});
		*/
		AboutiPin=(RelativeLayout)findViewById(R.id.AboutiPin);
		AboutiPin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(SettingActivity.this, "Click", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingActivity.this,AboutActivity.class);
		        startActivity(intent);
			}
		});
		
		Advice=(RelativeLayout)findViewById(R.id.Advice);
		Advice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(SettingActivity.this, "Click", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingActivity.this,SetAdviceActivity.class);
		        startActivity(intent);
			}
		});
		
		SetAuth=(RelativeLayout)findViewById(R.id.SetAuth);
		SetAuth.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(SettingActivity.this, "Click", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingActivity.this,SetAuthActivity.class);
		        startActivity(intent);
			}
		});
		
		SetPassword=(RelativeLayout)findViewById(R.id.SetPassword);
		SetPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(SettingActivity.this, "Click", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingActivity.this,SetPasswordActivity.class);
		        startActivity(intent);
			}
		});
		
		SetTelephone=(RelativeLayout)findViewById(R.id.SetTelephone);
		SetTelephone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(SettingActivity.this, "Click", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SettingActivity.this,SetTelephoneActivity.class);
		        startActivity(intent);
			}
		});
		
		//add by hx
		PostMgr = (RelativeLayout)findViewById(R.id.PostMgr);
		PostMgr.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this,PostMgrActivity.class);
		        startActivity(intent);
			}
		});
		//end
		
		ClearMessage=(RelativeLayout)findViewById(R.id.clearMessage);
		ClearMessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(SettingActivity.this, "Click", Toast.LENGTH_SHORT).show();
				Dialog dialog = new AlertDialog.Builder(SettingActivity.this).setTitle("I拼提示").setMessage("确定清空聊天记录吗？")
						.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						deleteDatabase("chat"+ID);
						Toast.makeText(SettingActivity.this, "清空聊天记录成功", Toast.LENGTH_LONG).show();
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).setCancelable(false).show();
				Window window = dialog.getWindow();
				window.setGravity(Gravity.CENTER);
			}				 
			
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != RESULT_OK) { 
            return;
        }
		if (requestCode == 12) {

             try {

                 Uri originalUri = data.getData();
                 String[] proj = {MediaStore.Images.Media.DATA};
                 //好像是android多媒体数据库的封装接口，具体的看Android文档

                 Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null); 
               //将光标移至开头 ，这个很重要，不小心很容易引起越界

                 cursor.moveToFirst();

                 //按我个人理解 这个是获得用户选择的图片的索引值

                 int column_index = cursor.getColumnIndex(proj[0]);                 

                 //最后根据索引值获取图片路径

                 ImgPath = cursor.getString(column_index);
                 System.out.println("图片位置："+ImgPath);
                 
                 uploadTask=new UploadTask();
                 uploadTask.execute((Void) null);
             }catch (Exception e) {

             }
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
		public class UploadTask extends AsyncTask<Void, Void, Boolean> {		
	    
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			int HeadImageVersion = 0;
			String ID = null;
			DatabaseHelper dbHelper = new DatabaseHelper(SettingActivity.this,"iPin");
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.query("LoginUser", new String[]{"ID","username","password","sex","telephone","HeadImageVersion","autoLogin"}, null, null, null, null, null);
			while(cursor.moveToNext())
			{
				HeadImageVersion = cursor.getInt(cursor.getColumnIndex("HeadImageVersion"));
				ID = cursor.getString(cursor.getColumnIndex("ID"));
				ContentValues values =new ContentValues();
				values.put("HeadImageVersion", HeadImageVersion+1);
				db.update("LoginUser", values, "ID=?", new String[]{new String(ID)});				
				db.close();
				dbHelper.close();
				String AppPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
				String filePath = new String(AppPath + "HeadImage/" + ID + "_" + HeadImageVersion + ".jpg");
				File file = new File(filePath);
				if(file.exists()) file.delete();
				HeadImageVersion = HeadImageVersion + 1;
			}
			try {					        
		        
		        BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inJustDecodeBounds = true;
		        Bitmap bmp = BitmapFactory.decodeFile(ImgPath, options);
		        System.out.println("length is :"+options.outHeight+"  width is :"+options.outWidth);
		        if(options.outHeight>200||options.outWidth>200){
		        	return false;
		        }
				File fromFile = new File(ImgPath);
				File toFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/HeadImage/" + ID + "_" + HeadImageVersion + ".jpg");
				FileInputStream fosfrom = new FileInputStream(fromFile);
				FileOutputStream fosto = new FileOutputStream(toFile);				
				
				byte bt[] = new byte[1024];
				int len;
				while((len = fosfrom.read(bt)) > 0) {
					fosto.write(bt, 0, len); //将内容写到新文件当中						
				}
				fosfrom.close();
				fosto.close();
				
				
				uploadFile("http://"+getString(R.string.Server_IP)+":"+getString(R.string.HeadImageServer_Port)+"/"+getString(R.string.HeadImageUploadFile), ID + "_" + HeadImageVersion + ".jpg", fromFile);
				Socket socket = new Socket(getString(R.string.Server_IP),Integer.parseInt(getString(R.string.Server_Port)));
		        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);		        		        
		        out.print("UpdateHeadImageVersion " + ID + " " + HeadImageVersion);
		        out.flush();
		        InputStream br = socket.getInputStream();
				byte[] buffer =new byte[1024];
				int readSize=br.read(buffer);				
				if(readSize>0)
				{						
					String uploadMsg=new String(buffer,0,readSize);
					socket.close();
					return uploadMsg.contains("uploadSuccess");
				}
				else
					socket.close();
			} catch (Exception e) {
				System.out.println("上传头像错误："+e.toString());
				return false;
			}
			
			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			uploadTask = null;			
			if (success) {
				Toast.makeText(SettingActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
				
			} else {
				Toast.makeText(SettingActivity.this, "上传失败，请检查网络链接或者图片尺寸", Toast.LENGTH_SHORT).show();
			}			
			
		}

		@Override
		protected void onCancelled() {
			uploadTask = null;
		}
		
		private void uploadFile(String uploadUrl,String filename,File file)  
		  {  
		    String end = "\r\n";  
		    String twoHyphens = "--";  
		    String boundary = "******";  
		    try  
		    {  
		      URL url = new URL(uploadUrl);  
		      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();  
		      // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃  
		      // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。  
		      httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  
		      // 允许输入输出流  
		      httpURLConnection.setDoInput(true);  
		      httpURLConnection.setDoOutput(true);  
		      httpURLConnection.setUseCaches(false);  
		      // 使用POST方法  
		      httpURLConnection.setRequestMethod("POST");  
		      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
		      httpURLConnection.setRequestProperty("Charset", "UTF-8");  
		      httpURLConnection.setRequestProperty("Content-Type",  
		          "multipart/form-data;boundary=" + boundary);  
		  
		      DataOutputStream dos = new DataOutputStream(  
		          httpURLConnection.getOutputStream());  
		      dos.writeBytes(twoHyphens + boundary + end);  
		      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + filename + "\"" + end);  
		      dos.writeBytes(end);		  
		      FileInputStream fis = new FileInputStream(file);
		      byte[] buffer = new byte[8192]; // 8k  
		      int count = 0;  
		      // 读取文件  
		      while ((count = fis.read(buffer)) != -1)  
		      {  
		        dos.write(buffer, 0, count);  
		      }  
		      fis.close();  
		  
		      dos.writeBytes(end);  
		      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
		      dos.flush();  
		  
		      InputStream is = httpURLConnection.getInputStream();  
		      InputStreamReader isr = new InputStreamReader(is, "utf-8");  
		      BufferedReader br = new BufferedReader(isr);  
		      String result = br.readLine();  
		  
		      Toast.makeText(SettingActivity.this, result, Toast.LENGTH_LONG).show();  
		      dos.close();  
		      is.close();  
		  
		    } catch (Exception e)  
		    {  
		      e.printStackTrace();  
		      setTitle(e.getMessage());  
		    }  
		  }
	}
		
		public class Setrcv extends BroadcastReceiver
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				//显示消息数目
	 	     	badge.setText(service.group_msg_num+"");
	 	     //	badge.setTextColor(Color.RED);
	 	     	badge.setTextSize(10);
	 	     	badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
	 	     	badge.setBadgeMargin(0,0);
	 	     	badge.show();

			}
		}

		
		
		@Override
		public void onBackPressed()
		{
			Intent intent=new Intent(this,InfoListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			this.finish();
			//super.onBackPressed(); 
			
		}
		
		public void btn_back(View v) { // 标题栏 返回按钮
			this.finish();
		}

		
		
		public void btn_infolist(View v) {  
			Intent intent = new Intent (SettingActivity.this,InfoListActivity.class);				
			startActivity(intent);
			SettingActivity.this.finish();
		}
		
		public void btn_discuss(View v) {  
			Intent intent = new Intent (SettingActivity.this,DiscussActivity.class);				
			startActivity(intent);
			SettingActivity.this.finish();
		}
		
		public void btn_nearby(View v) {  
			Intent intent = new Intent (SettingActivity.this,NearByActivity.class);				
			intent.putExtra("fromActivity", 4);
			startActivity(intent);
			SettingActivity.this.finish();
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
