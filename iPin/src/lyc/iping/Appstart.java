package lyc.iping;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import lyc.iping.InfoListActivity.InfoRefreshTask;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class Appstart extends Activity{

	private VersionCheckTask mVersionCheckTask = null;
	private String apk = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.appstart);
		String AppPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";		
		File file = new File(AppPath + "HeadImage");
		if(!file.exists()) file.mkdir();
		
		mVersionCheckTask = new VersionCheckTask();
		mVersionCheckTask.execute((Void) null);
   }
	
	/* 
	 *  
	 * 弹出对话框通知用户更新程序  
	 *  
	 * 弹出对话框的步骤： 
	 *  1.创建alertDialog的builder.   
	 *  2.要给builder设置属性, 对话框的内容,样式,按钮 
	 *  3.通过builder 创建一个对话框 
	 *  4.对话框show()出来   
	 */  
	protected void showUpdataDialog() {  
	    AlertDialog.Builder builer = new Builder(this) ;   
	    builer.setTitle("版本升级");  
	    builer.setMessage("软件检测到新版本，请及时更新");  
	    //当点确定按钮时从服务器上下载 新的apk 然后安装    
	    builer.setPositiveButton("确定", new OnClickListener() {  
	    public void onClick(DialogInterface dialog, int which) {  
	             System.out.println("下载更新");
	            downLoadApk();  
	        }     
	    });  
	    //当点取消按钮时进行登录   
	    builer.setNegativeButton("取消", new OnClickListener() {  
	        public void onClick(DialogInterface dialog, int which) {  
	            // TODO Auto-generated method stub   
	        	Intent intent = new Intent (Appstart.this,LoginActivity.class);			
				startActivity(intent);			
				Appstart.this.finish();  
	        }  
	    });  
	    AlertDialog dialog = builer.create();  
	    dialog.show();  
	}
	
	/* 
	 * 从服务器中下载APK 
	 */
	
	protected void downLoadApk() {  
	    final ProgressDialog pd;    //进度条对话框   
	    pd = new  ProgressDialog(this);  
	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	    pd.setMessage("正在下载更新");  
	    pd.show();  
	    new Thread(){  
	        @Override  
	        public void run() {  
	            try {  
	                File file = getFileFromServer("http://www.ipinstudio.tk/"+apk, pd);  
	                //sleep(3000);  
	                pd.dismiss(); //结束掉进度条对话框   
	                installApk(file); 
	            } catch (Exception e) { 
	            	System.out.println("下载apk失败");
	            	Toast.makeText(Appstart.this, "下载新版本失败", Toast.LENGTH_SHORT).show();
	            	Intent intent = new Intent (Appstart.this,LoginActivity.class);			
					startActivity(intent);			
					Appstart.this.finish();
	            }  
	        }}.start();  
	}
	
	//安装apk    
	protected void installApk(File file) {  
	    Intent intent = new Intent();  
	    //执行动作   
	    intent.setAction(Intent.ACTION_VIEW);  
	    //执行的数据类型   
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了    
	    startActivity(intent);  
	}  
	
	//从服务器下载apk: 
	public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{   
	//如果相等的话表示当前的sdcard挂载在手机上并且是可用的    
	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){   
	       URL url = new URL(path);   
	        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();   
	        conn.setConnectTimeout(5000);   
	        //获取到文件的大小     
		        pd.setMax(conn.getContentLength());   
		        InputStream is = conn.getInputStream();   
		        File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");   
		        FileOutputStream fos = new FileOutputStream(file);   
		        BufferedInputStream bis = new BufferedInputStream(is);   
		        byte[] buffer = new byte[1024];   
		        int len ;   
		        int total=0;   
		        while((len =bis.read(buffer))!=-1){   
		            fos.write(buffer, 0, len);   
		            total+= len;   
		            //获取当前下载量    
		            pd.setProgress(total);   
		        }   
		        fos.close();   
		        bis.close();   
		        is.close();   
		        return file;   
		    }   
		    else{   
		        return null;   
		    }   
		}  
	
	public class VersionCheckTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;		
        PrintWriter out =  null;
        
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try{
				socket = new Socket(getString(R.string.Server_IP),Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(),true);
				out.print("VersionCheck");
				out.flush();
				InputStream br = socket.getInputStream();
				byte[] buffer =new byte[2048];
				int readSize=br.read(buffer);
				if(readSize>0)
				{
					String VersionCheckMsg=new String(buffer,0,readSize);
					String[] temp = VersionCheckMsg.split(" ");
					String Version=temp[1];
					apk=temp[2];
					System.out.println(VersionCheckMsg);
					socket.close();					
					if(!Version.equals(getString(R.string.Version))) return true;
					else return false;
				}
			}catch(Exception e)
			{
				return false;
			}
			return false;
			// TODO: register the new account here.
			//return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mVersionCheckTask = null;			
			if (success) {
				//Toast.makeText(Appstart.this, "软件版本过时，请尽快更新", Toast.LENGTH_SHORT).show();
				showUpdataDialog();
			} else {
				Handler handler = new Handler();		
				handler.postDelayed(new Runnable(){
				@Override
				public void run(){
					Intent intent = new Intent (Appstart.this,LoginActivity.class);			
					startActivity(intent);			
					Appstart.this.finish();
				}
			}, 1000);
			}			
			
		}

		@Override
		protected void onCancelled() {
			mVersionCheckTask = null;
		}		
	}
}