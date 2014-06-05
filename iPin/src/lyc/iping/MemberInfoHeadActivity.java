package lyc.iping;



import java.io.File;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

public class MemberInfoHeadActivity extends Activity{
	private String ImageFile;
	private ImageView imageView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_memberinfohead);
		ImageFile=getIntent().getStringExtra("ImageFile");
		imageView=(ImageView)findViewById(R.id.MemberInfoHead);
		String AppPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
	    String ImgPath = AppPath+"HeadImage/"+ImageFile;
        File ImgFile = new File(ImgPath);
        if(ImgFile.exists())
        {
      	  Bitmap Image = null;
      	  Image = BitmapFactory.decodeFile(ImgPath,null);
      	  imageView.setImageBitmap(Image);
        }
        else
        {
        	imageView.setImageResource(R.drawable.default_head);
        }
   }
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
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