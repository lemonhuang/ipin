package lyc.iping;

import android.util.Log;
import lyc.iping.Group;
import lyc.iping.InfoListActivity.InfoRefreshTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.readystatesoftware.viewbadger.BadgeView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
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
import android.graphics.Color;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PostMgrActivity extends ListActivity {

	String GroupID = null;
	String info_ID = null;
	String info_HeadImageVersion = null;
	String info_username = null;
	String info_from = null;
	String info_to = null;
	String info_date = null;
	String info_detail = null;
	String LoginUser = null;
	BadgeView badge = null;

	private ImageView img_info = null;
	private ImageView img_address = null;
	private ImageView img_setting = null;
	private ImageView img_nearby = null;
	private List<DiscussListMsgEntity> mDataArrays = null;
	private Map<String,Bitmap> headImage = null;
	private PostListViewAdapter mAdapter = null;
	private DiscussRefreshTask mDiscussRefreshTask = null;
	private String ClickID = null; 
	
	// add by hx
		private int[] flag = new int[100];
		private static String chat_groupid = null;
		private String[] MgrChoice = new String[]{"减少1人","增加1人","取消拼车","完成拼车"};  
		private boolean[] ChoiceState=new boolean[]{false, false,false, false};  
		
		private RadioOnClick radioOnClick = new RadioOnClick(1);
		private ListView ChoiceRadioListView = null;
	// add

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_postmgr);
		headImage = new HashMap<String,Bitmap>();
		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		initView();
	}

	public void initView() {
		mDataArrays = new ArrayList<DiscussListMsgEntity>();
		for (int i = 0; i < 100; i++) {
			flag[i] = -1;
		}
		
		mAdapter = new PostListViewAdapter(this, mDataArrays, flag,headImage);
		setListAdapter(mAdapter);
			
		DatabaseHelper dbHelper = new DatabaseHelper(PostMgrActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("LoginUser",
				new String[] { "ID", "username", "password", "sex",
						"telephone", "HeadImageVersion", "autoLogin" },
				"username<>?", new String[] { "null" }, null, null, null);
		while (cursor.moveToNext()) {
			LoginUser = cursor.getString(cursor.getColumnIndex("username"));
		}
		db.close();
		dbHelper.close();

		img_info = (ImageView) findViewById(R.id.img_weixin_1);
		img_address = (ImageView) findViewById(R.id.img_address_1);
		img_nearby = (ImageView) findViewById(R.id.img_nearby_1);
		img_setting = (ImageView) findViewById(R.id.img_settings_1);

		mDiscussRefreshTask = new DiscussRefreshTask();
		mDiscussRefreshTask.execute((Void) null);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		//查询数据库，找出组号
	    DatabaseHelper dbHelper = new DatabaseHelper(PostMgrActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("discuss", new String[] { "GroupID", "info_ID",
				"info_HeadImageVersion", "info_username", "info_from",
				"info_to", "info_date", "info_detail",
				"memberCount", "memberList" }, null, null, null, null, null);
		if (cursor.move(position+1)) {
			ClickID = cursor.getString(cursor.getColumnIndex("GroupID"));
		}
		db.close();
		dbHelper.close();
	    
		//弹出对话框
		AlertDialog ad =new AlertDialog.Builder(PostMgrActivity.this).setTitle("请选择")  
				   .setSingleChoiceItems(MgrChoice,radioOnClick.getIndex(),radioOnClick).create();  
				   ChoiceRadioListView=ad.getListView();  
				   ad.show(); 
	}
	
	class RadioOnClick implements DialogInterface.OnClickListener{  
		  private int index;  
		  
		  public RadioOnClick(int index){  
		   this.index = index;  
		  }  
		  public void setIndex(int index){  
		   this.index=index;  
		  }  
		  public int getIndex(){  
		   return index;  
		  }  
		  
		  public void onClick(DialogInterface dialog, int whichButton){  
						  
			setIndex(whichButton);  
		    Toast.makeText(PostMgrActivity.this, "您已经选择了： " + index + ":" + MgrChoice[index]+ " 组号:" + ClickID, Toast.LENGTH_LONG).show();      
		    //上报给服务器		    
		    	//减少一人情况
		    	if(index == 0)
		    	{
		    		new Thread(new Runnable() {
				        public void run() {
				        	Socket mysocket = null;
							PrintWriter myout = null;
							try{
				        	mysocket = new Socket(getString(R.string.Server_IP),
									Integer.parseInt(getString(R.string.Server_Port)));
						    myout = new PrintWriter(mysocket.getOutputStream(), true);
						    myout.print("PostMemberDecr " + ClickID);
						    myout.flush();
					    	mysocket.close();
					    	} catch(Exception e) {
								 System.out.println("socket上报出错"+e);
							}
				        }
				    }).start();
		    		
		    	}
		    	else if(index == 1)
		    	{
		    		new Thread(new Runnable() {
				        public void run() {
				        	Socket mysocket = null;
							PrintWriter myout = null;
							try{
					        	mysocket = new Socket(getString(R.string.Server_IP),
										Integer.parseInt(getString(R.string.Server_Port)));
							    myout = new PrintWriter(mysocket.getOutputStream(), true);
							    myout.print("PostMemberIncr " + ClickID);
							    myout.flush();
						    	mysocket.close();
						    	} catch(Exception e) {
									 System.out.println("socket上报出错"+e);
								}
				        }
				    }).start();
		    	}
		    	else if(index == 2)
		    	{

		    		new AlertDialog.Builder(  
                            PostMgrActivity.this)  
                            .setTitle("您确定要执行:" + MgrChoice[index])  
                            .setPositiveButton(  
                                    "确定",  
                                    new DialogInterface.OnClickListener() {              
                                    	
                                        public void onClick(  
                                                DialogInterface dialog,  
                                                int which) {  
                                            // 这里是你点击确定之后可以进行的操作
                                        	
                                        	DatabaseHelper dbHelper = new DatabaseHelper(PostMgrActivity.this,
                                 					"iPin");
                                 			SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        	String whereClause="GroupID=?";  
                             	            String [] whereArgs = {String.valueOf(PostMgrActivity.this.ClickID)};    
                             	            //调用delete方法，删除数据                                	           
                             	            db.delete("discuss", whereClause, whereArgs); 
                             	            
                             	           Cursor cursor = db.query("discuss", new String[] { "GroupID", "info_ID",
                             						"info_HeadImageVersion", "info_username", "info_from",
                             						"info_to", "info_date", "info_detail",
                             						"memberCount", "memberList" }, null, null, null, null, null);
                             				HeadImageDownloader downloader = new HeadImageDownloader();
                             				List<DiscussListMsgEntity> mDataArrays1 = new ArrayList<DiscussListMsgEntity>();
                             				while (cursor.moveToNext()) {
                             					DiscussListMsgEntity entity = new DiscussListMsgEntity();
                             					String GroupID, ID, HeadImageVersion, username, from, to, date, detail, memberCount, memberList;
                             					GroupID = cursor.getString(cursor.getColumnIndex("GroupID"));
                             					ID = cursor.getString(cursor.getColumnIndex("info_ID"));
                             					HeadImageVersion = cursor.getString(cursor.getColumnIndex("info_HeadImageVersion"));
                             					username = cursor.getString(cursor.getColumnIndex("info_username"));
                             					from = cursor.getString(cursor.getColumnIndex("info_from"));
                             					to = cursor.getString(cursor.getColumnIndex("info_to"));
                             					date = cursor.getString(cursor.getColumnIndex("info_date"));
                             					detail = cursor.getString(cursor.getColumnIndex("info_detail"));
                             					memberCount = cursor.getString(cursor.getColumnIndex("memberCount"));
                             					memberList = cursor.getString(cursor.getColumnIndex("memberList"));
                             					entity.setID(ID);
                             					entity.setHeadImageVersion(HeadImageVersion);
                             					entity.setUsername(username);
                             					entity.setFrom(from);
                             					entity.setTo(to);
                             					entity.setDate(date);
                             					entity.setMemberCount(memberCount);
                             					mDataArrays1.add(entity);
                             					downloader.download(PostMgrActivity.this, ID,
                             							HeadImageVersion);
                             					
                             					System.out.println("The memberCount is "+ entity.getMemberCount());
                             				}
                             				db.close();
                             				dbHelper.close();
                             			        
                             				mDataArrays.clear();
                             				mDataArrays.addAll(mDataArrays1);
                             				
                             				
                             				new Thread(new Runnable() {
                             			        public void run() {
                             			        	PostMgrActivity.this.runOnUiThread(new Runnable() {
                             							@Override
                             							public void run() {
                             							// refresh UI 
                             							mAdapter.notifyDataSetChanged();
                             							}
                             						});
                             			        }
                             			    }).start();
                                        	                                     
                                        	
                                        	new Thread(new Runnable() {
                        				        public void run() {
                        				        	Socket mysocket = null;
                        							PrintWriter myout = null;
                        							try{
                        					        	mysocket = new Socket(getString(R.string.Server_IP),
                        										Integer.parseInt(getString(R.string.Server_Port)));
                        							    myout = new PrintWriter(mysocket.getOutputStream(), true);
                        							    myout.print("PostCameOver " + ClickID);
                        							    myout.flush();
                        						    	mysocket.close();
                        						    	} catch(Exception e) {
                        									 System.out.println("socket上报出错"+e);
                        								}
                        							
                        							
                        				        }
                        				    }).start();
                                        	
                                        	
                                        }  
                                    })  
                            .setNegativeButton(  
                                    "取消",  
                                    new DialogInterface.OnClickListener() {  

                                        public void onClick(  
                                                DialogInterface dialog,  
                                                int which) {  
                                            // 这里点击取消之后可以进行的操作   
                                        	
                                        }  
                                    }).show(); 
		    		
		    	}
		    	else
		    	{
		    		System.out.println("0 The clickID is "+PostMgrActivity.this.ClickID);
		    		new AlertDialog.Builder(  
                            PostMgrActivity.this)  
                            .setTitle("您确定要执行:" + MgrChoice[index])  
                            .setPositiveButton(  
                                    "确定",  
                                    new DialogInterface.OnClickListener() {  

                                        public void onClick(  
                                                DialogInterface dialog,  
                                                int which) {  
                                            // 这里是你点击确定之后可以进行的操作  
                                        	System.out.println("The clickID is "+PostMgrActivity.this.ClickID);
                                        	
                                        	DatabaseHelper dbHelper = new DatabaseHelper(PostMgrActivity.this,
                                 					"iPin");
                                 			SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        	String whereClause="GroupID=?";  
                             	            String [] whereArgs = {String.valueOf(PostMgrActivity.this.ClickID)};    
                             	            //调用delete方法，删除数据                                	           
                             	            db.delete("discuss", whereClause, whereArgs); 
                             	           Cursor cursor = db.query("discuss", new String[] { "GroupID", "info_ID",
                            						"info_HeadImageVersion", "info_username", "info_from",
                            						"info_to", "info_date", "info_detail",
                            						"memberCount", "memberList" }, null, null, null, null, null);
                            				HeadImageDownloader downloader = new HeadImageDownloader();
                            				List<DiscussListMsgEntity> mDataArrays1 = new ArrayList<DiscussListMsgEntity>();
                            				while (cursor.moveToNext()) {
                            					DiscussListMsgEntity entity = new DiscussListMsgEntity();
                            					String GroupID, ID, HeadImageVersion, username, from, to, date, detail, memberCount, memberList;
                            					GroupID = cursor.getString(cursor.getColumnIndex("GroupID"));
                            					ID = cursor.getString(cursor.getColumnIndex("info_ID"));
                            					HeadImageVersion = cursor.getString(cursor.getColumnIndex("info_HeadImageVersion"));
                            					username = cursor.getString(cursor.getColumnIndex("info_username"));
                            					from = cursor.getString(cursor.getColumnIndex("info_from"));
                            					to = cursor.getString(cursor.getColumnIndex("info_to"));
                            					date = cursor.getString(cursor.getColumnIndex("info_date"));
                            					detail = cursor.getString(cursor.getColumnIndex("info_detail"));
                            					memberCount = cursor.getString(cursor.getColumnIndex("memberCount"));
                            					memberList = cursor.getString(cursor.getColumnIndex("memberList"));
                            					entity.setID(ID);
                            					entity.setHeadImageVersion(HeadImageVersion);
                            					entity.setUsername(username);
                            					entity.setFrom(from);
                            					entity.setTo(to);
                            					entity.setDate(date);
                            					entity.setMemberCount(memberCount);
                            					mDataArrays1.add(entity);
                            					downloader.download(PostMgrActivity.this, ID,
                            							HeadImageVersion);
                            					
                            					System.out.println("The memberCount is "+ entity.getMemberCount());
                            				}
                            				db.close();
                            				dbHelper.close();
                            			        
                            				mDataArrays.clear();
                            				mDataArrays.addAll(mDataArrays1);
                            				
                            				
                            				new Thread(new Runnable() {
                            			        public void run() {
                            			        	PostMgrActivity.this.runOnUiThread(new Runnable() {
                            							@Override
                            							public void run() {
                            							// refresh UI 
                            							mAdapter.notifyDataSetChanged();
                            							}
                            						});
                            			        }
                            			    }).start();
                                        	
                                        	
                                        	new Thread(new Runnable() {
                        				        public void run() {
                        				        	Socket mysocket = null;
                        							PrintWriter myout = null;
                        							try{
                        					        	mysocket = new Socket(getString(R.string.Server_IP),
                        										Integer.parseInt(getString(R.string.Server_Port)));
                        							    myout = new PrintWriter(mysocket.getOutputStream(), true);
                        							    myout.print("PostFinished " + ClickID);
                        							    myout.flush();
                        						    	mysocket.close();
                        						    	} catch(Exception e) {
                        									 System.out.println("socket上报出错"+e);
                        								}
                        							
                        							
                        				        }
                        				    }).start();
                                        	
                                        	
                                        	
                                        }  
                                    })  
                            .setNegativeButton(  
                                    "取消",  
                                    new DialogInterface.OnClickListener() {  

                                        public void onClick(  
                                                DialogInterface dialog,  
                                                int which) {  
                                            // 这里点击取消之后可以进行的操作
                                        	
                                        }  
                                    }).show(); 
		    	}
		    	
		 
		    dialog.dismiss();
		       
		    
        	
		    
		    
		    //更改本地的数据库
		    DatabaseHelper dbHelper = new DatabaseHelper(PostMgrActivity.this,
					"iPin");
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.query("discuss", new String[] {
					"memberCount", "memberList" }, "GroupID=?", new String[]{ClickID}, null, null, null);

			int process_count;
			String process_count_string = null;
			if(cursor.moveToFirst()){
				   process_count_string = cursor.getString(cursor.getColumnIndex("memberCount"));
			}
			process_count =  Integer.parseInt(process_count_string);
			
			System.out.println("The membercount is "+process_count);
			
			if(index == 0)
			{
				if(process_count <= 0)
					 Toast.makeText(PostMgrActivity.this, "当前拼车人数不足", Toast.LENGTH_LONG).show();
				else
				{
					process_count--;
					ContentValues cv = new ContentValues();  
		            cv.put("memberCount", ""+process_count);    
		            String whereClause="GroupID=?";  
		            String [] whereArgs = {String.valueOf(ClickID)};  
		            //参数1 是要更新的表名  
		            //参数2 是一个ContentValeus对象  
		            //参数3 是where子句  
		            db.update("discuss", cv, whereClause, whereArgs);  
				}
			}
			else if(index == 1)
			{
				if(process_count == 4)
					Toast.makeText(PostMgrActivity.this, "当前拼车人数已满", Toast.LENGTH_LONG).show();
				else
				{
					process_count++;
					ContentValues cv = new ContentValues();  
		            cv.put("memberCount", ""+process_count);    
		            String whereClause="GroupID=?";  
		            String [] whereArgs = {String.valueOf(ClickID)};  
		            //参数1 是要更新的表名  
		            //参数2 是一个ContentValeus对象  
		            //参数3 是where子句  
		            db.update("discuss", cv, whereClause, whereArgs);  
				}
			}
			else
			{
				      			
         			            	
				 
			}
			
			
			//从数据库中读出所有数据,刷新ListView
			cursor = db.query("discuss", new String[] { "GroupID", "info_ID",
					"info_HeadImageVersion", "info_username", "info_from",
					"info_to", "info_date", "info_detail",
					"memberCount", "memberList" }, null, null, null, null, null);
			HeadImageDownloader downloader = new HeadImageDownloader();
			List<DiscussListMsgEntity> mDataArrays1 = new ArrayList<DiscussListMsgEntity>();
			while (cursor.moveToNext()) {
				DiscussListMsgEntity entity = new DiscussListMsgEntity();
				String GroupID, ID, HeadImageVersion, username, from, to, date, detail, memberCount, memberList;
				GroupID = cursor.getString(cursor.getColumnIndex("GroupID"));
				ID = cursor.getString(cursor.getColumnIndex("info_ID"));
				HeadImageVersion = cursor.getString(cursor.getColumnIndex("info_HeadImageVersion"));
				username = cursor.getString(cursor.getColumnIndex("info_username"));
				from = cursor.getString(cursor.getColumnIndex("info_from"));
				to = cursor.getString(cursor.getColumnIndex("info_to"));
				date = cursor.getString(cursor.getColumnIndex("info_date"));
				detail = cursor.getString(cursor.getColumnIndex("info_detail"));
				memberCount = cursor.getString(cursor.getColumnIndex("memberCount"));
				memberList = cursor.getString(cursor.getColumnIndex("memberList"));
				entity.setID(ID);
				entity.setHeadImageVersion(HeadImageVersion);
				entity.setUsername(username);
				entity.setFrom(from);
				entity.setTo(to);
				entity.setDate(date);
				entity.setMemberCount(memberCount);
				mDataArrays1.add(entity);
				downloader.download(PostMgrActivity.this, ID,
						HeadImageVersion);
				
				System.out.println("The memberCount is "+ entity.getMemberCount());
			}
			db.close();
			dbHelper.close();
		        
			mDataArrays.clear();
			mDataArrays.addAll(mDataArrays1);
			
			
			new Thread(new Runnable() {
		        public void run() {
		        	PostMgrActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
						// refresh UI 
						mAdapter.notifyDataSetChanged();
						}
					});
		        }
		    }).start();
						
	//	    ClickID = null; 
		    
		  }  
	} 

	public class DiscussRefreshTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("DiscussRefresh " + LoginUser);
				out.flush();
				mDataArrays.clear();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[2048];
				int readSize = br.read(buffer);
				if (readSize > 0) {
					String DiscussRefreshMsg = new String(buffer, 0, readSize);
					if (DiscussRefreshMsg.contains("DiscussRefreshNone"))
						return false;
					String[] temp = DiscussRefreshMsg.split("`");

					HeadImageDownloader downloader = new HeadImageDownloader();

					DatabaseHelper dbHelper = new DatabaseHelper(
							PostMgrActivity.this, "iPin");
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					db.delete("discuss", null, null);
					for (int i = 0; i < Integer.parseInt(temp[0]); i++) {
						DiscussListMsgEntity entity = new DiscussListMsgEntity();
						String GroupID, ID, HeadImageVersion, username, from, to, date, detail, memberCount, memberList;
						GroupID = temp[i * 10 + 1];
						ID = temp[i * 10 + 2];
						HeadImageVersion = temp[i * 10 + 3];
						username = temp[i * 10 + 4];
						from = temp[i * 10 + 5];
						to = temp[i * 10 + 6];
						date = temp[i * 10 + 7];
						detail = temp[i * 10 + 8];
						memberCount = temp[i * 10 + 9];
						memberList = temp[i * 10 + 10];
						ContentValues values = new ContentValues();
						values.put("GroupID", GroupID);
						values.put("info_ID", ID);
						values.put("info_HeadImageVersion", HeadImageVersion);
						values.put("info_username", username);
						values.put("info_from", from);
						values.put("info_to", to);
						values.put("info_date", date);
						values.put("info_detail", detail);
						values.put("memberCount", memberCount);
						values.put("memberList", memberList);
						db.insert("discuss", null, values);
						entity.setID(ID);
						entity.setHeadImageVersion(HeadImageVersion);
						entity.setUsername(username);
						entity.setFrom(from);
						entity.setTo(to);
						entity.setDate(date);
						entity.setMemberCount(memberCount);
						mDataArrays.add(entity);
						downloader.download(PostMgrActivity.this, ID,
								HeadImageVersion);
					}
					db.close();
					dbHelper.close();
					socket.close();
					return true;
				}

			} catch (Exception e) {
				return false;
			}
			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mDiscussRefreshTask = null;
			if (success) {
				mAdapter.notifyDataSetChanged();
			} else {

			}

		}

		@Override
		protected void onCancelled() {
			mDiscussRefreshTask = null;
		}
	}

/*
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(PostMgrActivity.this, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		this.finish();
		// super.onBackPressed();

	}
*/
	public void btn_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void btn_infolist(View v) {
		Intent intent = new Intent(PostMgrActivity.this, InfoListActivity.class);
		startActivity(intent);
		PostMgrActivity.this.finish();
	}
	
	public void btn_discuss(View v) {
		Intent intent = new Intent(PostMgrActivity.this, DiscussActivity.class);
		startActivity(intent);
		PostMgrActivity.this.finish();
	}

	public void btn_nearby(View v) {
		Intent intent = new Intent(PostMgrActivity.this, NearByActivity.class);
		intent.putExtra("fromActivity", 2);
		startActivity(intent);
		PostMgrActivity.this.finish();
	}

	public void btn_setting(View v) {
		Intent intent = new Intent(PostMgrActivity.this, SettingActivity.class);
		startActivity(intent);
		PostMgrActivity.this.finish();
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
