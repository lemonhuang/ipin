package lyc.iping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

public class HeadImageDownloader {
	private String ID;
	private String HeadImageVersion;
	
	void download(Context ctx,String ID_Str,String HeadImage_Str){
		ID=ID_Str;
		HeadImageVersion=HeadImage_Str;
		String AppPath = ctx.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        String ImgPath = AppPath+"HeadImage/"+ID+"_"+HeadImageVersion+".jpg";
        File ImgFile = new File(ImgPath);
		if(!ImgFile.exists() && !HeadImageVersion.equals("0"))
		{
			try{
				URL url = new URL("http://"+ctx.getString(R.string.Server_IP)+":"+ctx.getString(R.string.HeadImageServer_Port)+"/HeadImage/"+ID+"_"+HeadImageVersion+".jpg");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream in = connection.getInputStream();
				FileOutputStream out = new FileOutputStream(ImgFile);
				byte buffer[] = new byte[1024];
				int length;
				while((length=in.read(buffer))>0)
				{
					out.write(buffer, 0, length);
				}
				out.flush();
				out.close();
			}catch(Exception e)
			{
				System.out.print("ImageDownloader Failed!"+ID+"_"+HeadImageVersion+":"+e);
			}
		}
	}

}
