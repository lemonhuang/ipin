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
	 * �����Ի���֪ͨ�û����³���  
	 *  
	 * �����Ի���Ĳ��裺 
	 *  1.����alertDialog��builder.   
	 *  2.Ҫ��builder��������, �Ի��������,��ʽ,��ť 
	 *  3.ͨ��builder ����һ���Ի��� 
	 *  4.�Ի���show()����   
	 */  
	protected void showUpdataDialog() {  
	    AlertDialog.Builder builer = new Builder(this) ;   
	    builer.setTitle("�汾����");  
	    builer.setMessage("�����⵽�°汾���뼰ʱ����");  
	    //����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ    
	    builer.setPositiveButton("ȷ��", new OnClickListener() {  
	    public void onClick(DialogInterface dialog, int which) {  
	             System.out.println("���ظ���");
	            downLoadApk();  
	        }     
	    });  
	    //����ȡ����ťʱ���е�¼   
	    builer.setNegativeButton("ȡ��", new OnClickListener() {  
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
	 * �ӷ�����������APK 
	 */
	
	protected void downLoadApk() {  
	    final ProgressDialog pd;    //�������Ի���   
	    pd = new  ProgressDialog(this);  
	    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	    pd.setMessage("�������ظ���");  
	    pd.show();  
	    new Thread(){  
	        @Override  
	        public void run() {  
	            try {  
	                File file = getFileFromServer("http://www.ipinstudio.tk/"+apk, pd);  
	                //sleep(3000);  
	                pd.dismiss(); //�������������Ի���   
	                installApk(file); 
	            } catch (Exception e) { 
	            	System.out.println("����apkʧ��");
	            	Toast.makeText(Appstart.this, "�����°汾ʧ��", Toast.LENGTH_SHORT).show();
	            	Intent intent = new Intent (Appstart.this,LoginActivity.class);			
					startActivity(intent);			
					Appstart.this.finish();
	            }  
	        }}.start();  
	}
	
	//��װapk    
	protected void installApk(File file) {  
	    Intent intent = new Intent();  
	    //ִ�ж���   
	    intent.setAction(Intent.ACTION_VIEW);  
	    //ִ�е���������   
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//���߰����˴�AndroidӦΪandroid��������ɰ�װ����    
	    startActivity(intent);  
	}  
	
	//�ӷ���������apk: 
	public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{   
	//�����ȵĻ���ʾ��ǰ��sdcard�������ֻ��ϲ����ǿ��õ�    
	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){   
	       URL url = new URL(path);   
	        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();   
	        conn.setConnectTimeout(5000);   
	        //��ȡ���ļ��Ĵ�С     
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
		            //��ȡ��ǰ������    
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
				//Toast.makeText(Appstart.this, "����汾��ʱ���뾡�����", Toast.LENGTH_SHORT).show();
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