package lyc.iping;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.readystatesoftware.viewbadger.BadgeView;

import lyc.iping.EditActivity.publishInfoTask;
import lyc.iping.InfoListActivity.InfoRefreshTask;
import lyc.iping.InfoListActivity.Listrcv;

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
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class NearByActivity extends ListActivity {

	String nearby_username = null;
	String nearby_distance = null;
	String nearby_telnum = null;
	View target = null;
	BadgeView badge = null;

	String user_destination = null;// 用来暂存用户输入的目的地

	String Longitude = null;
	String Latitude = null;

	public String ID, userHead, user_name, user_telnum;

	private List<NearByMsgEntity> NearByArrays = null;
	private NearByMsgViewAdapter NearByAdapter = null;
	private NearByRefreshTask NearByRefreshTask = null;

	private ImageView img_setting = null;
	private ImageView img_discuss = null;
	private ImageView img_info = null;
	private ImageView img_setDestination = null;
	private ImageView img_refresh = null;

	private LocationManager lm;
	private static final String TAG = "GpsActivity";

	/* 百度 */
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();

	/* 指示标志-返回值为空或请求失败 */
	private int flag = 0;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationClient.stop();// 停止定位
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_nearby);

		// 百度
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.setAccessKey("8VL6wch7r1inf8o1WYMEa4iW"); // V4.1
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		setLocationOption();
		//mLocationClient.start();// 开始定位

		// 退出时清除所有Activity
		ActivityManager.getInstance().addActivity(this);

		// add by hx
		Nearrcv rcv = new Nearrcv();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.rcv");
		this.registerReceiver(rcv, filter);
		System.out.println("注册Near广播完成！");
		// end

		target = findViewById(R.id.img_address_2);
		badge = new BadgeView(NearByActivity.this, target);

		DatabaseHelper dbHelper = new DatabaseHelper(NearByActivity.this,
				"iPin");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("LoginUser", new String[] { "ID", "username",
				"password", "sex", "telephone", "autoLogin" }, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			ID = cursor.getString(cursor.getColumnIndex("ID"));
			user_name = cursor.getString(cursor.getColumnIndex("username"));
			user_telnum = cursor.getString(cursor.getColumnIndex("telephone"));
		}

		cursor = db.query("NearbyDestination", new String[] { "Destination" },
				"ID=?", new String[] { ID }, null, null, null);
		while (cursor.moveToNext()) {
			user_destination = cursor.getString(cursor
					.getColumnIndex("Destination"));
		}
		db.close();
		dbHelper.close();

		img_info = (ImageView) findViewById(R.id.img_weixin_2);
		img_discuss = (ImageView) findViewById(R.id.img_address_2);
		img_setting = (ImageView) findViewById(R.id.img_settings_2);

		img_setDestination = (ImageView) findViewById(R.id.nearby_setDestination);
		img_setDestination.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final EditText input = new EditText(NearByActivity.this);
				Dialog dialog = new AlertDialog.Builder(NearByActivity.this)
						.setTitle("修改目的地")
						.setMessage("请输入您的目的地")
						.setView(input)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// Toast.makeText(NearByActivity.this,
										// "我很喜欢他的电影。",
										// Toast.LENGTH_LONG).show();
										String value = input.getText()
												.toString();
										if (TextUtils.isEmpty(value)) {
											Toast.makeText(NearByActivity.this,
													"目的地不能为空",
													Toast.LENGTH_LONG).show();
											try {
												Field field = dialog
														.getClass()
														.getSuperclass()
														.getDeclaredField(
																"mShowing");
												field.setAccessible(true);
												field.set(dialog, false);
											} catch (Exception e) {
												e.printStackTrace();
											}
											// lm = (LocationManager)
											// getSystemService(Context.LOCATION_SERVICE);
											// NearByActivity.this.finish();
											// input.setError("出发日期不能为空");
											// input.requestFocus();
											// return;
										} else {
											// System.out.println("fff");
											// System.out.println(value.toString());
											user_destination = value;// 将目的地点赋给变量以便于传输给服务器
											Toast.makeText(
													NearByActivity.this,
													"目的地已设定为："
															+ user_destination,
													Toast.LENGTH_LONG).show();
											try {
												Field field = dialog
														.getClass()
														.getSuperclass()
														.getDeclaredField(
																"mShowing");
												field.setAccessible(true);
												field.set(dialog, true);
											} catch (Exception e) {
												e.printStackTrace();
											}
											ContentValues values = new ContentValues();
											values.put("ID", ID);
											values.put("Destination",
													user_destination);
											DatabaseHelper dbHelper = new DatabaseHelper(
													NearByActivity.this, "iPin");
											SQLiteDatabase db = dbHelper
													.getWritableDatabase();
											db.update("NearbyDestination",
													values, "ID=?",
													new String[] { ID });
											// setTitle(value.toString());
											mLocationClient.start();// 开始定位
											Locate();
										}

									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// Toast.makeText(NearByActivity.this,
										// "我不喜欢他的电影。",
										// Toast.LENGTH_LONG)
										// .show();
										// lm = (LocationManager)
										// getSystemService(Context.LOCATION_SERVICE);
										// NearByActivity.this.finish();
									}
								}).setCancelable(false).show();
				Window window = dialog.getWindow();
				window.setGravity(Gravity.CENTER);
			}
		});

		img_refresh = (ImageView) findViewById(R.id.nearby_refresh);
		img_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Locate();
			}
		});

		if (TextUtils.isEmpty(user_destination)) {
			final EditText input = new EditText(this);
			Dialog dialog = new AlertDialog.Builder(this)
					.setTitle("I拼提示")
					.setMessage("请输入您的目的地并开启GPS定位功能")
					.setView(input)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// Toast.makeText(NearByActivity.this,
									// "我很喜欢他的电影。",
									// Toast.LENGTH_LONG).show();
									String value = input.getText().toString();
									if (TextUtils.isEmpty(value)) {

										Toast.makeText(NearByActivity.this,
												"目的地不能为空", Toast.LENGTH_LONG)
												.show();
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, false);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// lm = (LocationManager)
										// getSystemService(Context.LOCATION_SERVICE);
										// NearByActivity.this.finish();
										// input.setError("出发日期不能为空");
										// input.requestFocus();
										// return;
									} else {

										// System.out.println("fff");
										// System.out.println(value.toString());
										user_destination = value;// 将目的地点赋给变量以便于传输给服务器
										Toast.makeText(NearByActivity.this,
												"目的地已设定为：" + user_destination,
												Toast.LENGTH_LONG).show();
										try {
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
										} catch (Exception e) {
											e.printStackTrace();
										}
										ContentValues values = new ContentValues();
										values.put("ID", ID);
										values.put("Destination",
												user_destination);
										DatabaseHelper dbHelper = new DatabaseHelper(
												NearByActivity.this, "iPin");
										SQLiteDatabase db = dbHelper
												.getWritableDatabase();
										db.insert("NearbyDestination", null,
												values);
										// setTitle(value.toString());
										mLocationClient.start();// 开始定位
										Locate();
									}

								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// Toast.makeText(NearByActivity.this,
									// "我不喜欢他的电影。",
									// Toast.LENGTH_LONG)
									// .show();
									// lm = (LocationManager)
									// getSystemService(Context.LOCATION_SERVICE);
									int fromActivity = NearByActivity.this
											.getIntent().getIntExtra(
													"fromActivity", 1);
									switch (fromActivity) {
									case 1:
										Intent intent1 = new Intent(
												NearByActivity.this,
												InfoListActivity.class);
										startActivity(intent1);
										break;
									case 2:
										Intent intent2 = new Intent(
												NearByActivity.this,
												DiscussActivity.class);
										startActivity(intent2);
										break;
									case 4:
										Intent intent4 = new Intent(
												NearByActivity.this,
												SettingActivity.class);
										startActivity(intent4);
										break;
									default:
										break;
									}

									NearByActivity.this.finish();
								}
							}).setCancelable(false).show();
			Window window = dialog.getWindow();
			window.setGravity(Gravity.CENTER);
		} else {
			Toast.makeText(NearByActivity.this, "当前设定目的地为：" + user_destination,
					Toast.LENGTH_LONG).show();
			mLocationClient.start();// 开始定位
			Locate();
		}

		// 显示消息数目
		if (service.group_msg_num != 0) {
			badge.setText(service.group_msg_num + "");
			// badge.setTextColor(Color.RED);
			badge.setTextSize(10);
			badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			badge.setBadgeMargin(0, 0);
			badge.show();
		}
	}

	public void Locate() {
		if (mLocationClient != null) {
			// if(mLocationClient.isStarted()){
			mLocationClient.requestLocation();
			System.out.println("定位成功");
			// }
		} else {
			System.out.println("定位不成功");
			Log.d(TAG, "locClient is null or not started");
		}
	}

	/**
	 * 设置相关参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		// option.setScanSpan(50000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("当前时间 : ");
			sb.append(location.getTime());
			sb.append("\n错误码 : ");
			sb.append(location.getLocType());
			sb.append("\n纬度 : ");
			sb.append(location.getLatitude());
			sb.append("\n经度 : ");
			sb.append(location.getLongitude());
			sb.append("\n半径 : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\n速度 : ");
				sb.append(location.getSpeed());
				sb.append("\n卫星数 : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\n地址 : ");
				sb.append(location.getAddrStr());
			}
			// mText.setText(sb.toString());
			Longitude = String.valueOf(location.getLongitude());
			Latitude = String.valueOf(location.getLatitude());
			updateView(location);
			System.out.println("Longitude = " + Longitude);
			System.out.println("Latitude = " + Latitude);
			Log.d(TAG, "onReceiveLocation " + sb.toString());
		}

		public void onReceivePoi(BDLocation poiLocation) {
			// 将在下个版本中去除poi功能
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
			// mTextPoi.setText(sb.toString());
			Log.d(TAG, "onReceivePoi " + sb.toString());
		}
	}

	/**
	 * 实时更新文本内容
	 * 
	 * @param location
	 */
	private void updateView(BDLocation location) {

		NearByArrays = new ArrayList<NearByMsgEntity>();
		NearByAdapter = new NearByMsgViewAdapter(this, NearByArrays);
		setListAdapter(NearByAdapter);
		// if (location != null) {
		if (String.valueOf(location.getLongitude()) != "4.9E-324") {
			// 先将用户数据以及目的地信息取出来，然后一起发送给服务器，再从服务器下载
			Longitude = String.valueOf(location.getLongitude());
			Latitude = String.valueOf(location.getLatitude());

			NearByRefreshTask = new NearByRefreshTask();
			NearByRefreshTask.execute((Void) null);
		} else {
			Toast.makeText(NearByActivity.this, "定位失败", Toast.LENGTH_SHORT)
					.show();
		}

	}

	/**
	 * 返回查询条件
	 * 
	 * @return
	 */
	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 设置是否要求速度
		criteria.setSpeedRequired(false);
		// 设置是否允许运营商收费
		criteria.setCostAllowed(false);
		// 设置是否需要方位信息
		criteria.setBearingRequired(false);
		// 设置是否需要海拔信息
		criteria.setAltitudeRequired(false);
		// 设置对电源的需求
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		final int object_position = position;
		Dialog dialog = new AlertDialog.Builder(NearByActivity.this)
				.setTitle("I拼提示")
				.setMessage("是否拨打对方电话")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Intent intent = new Intent();

							intent.setAction(Intent.ACTION_CALL);
							intent.setData(Uri.parse("tel:"
									+ NearByArrays.get(object_position)
											.getTelnum()));
							startActivity(intent);
						} catch (Exception e) {

						}
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

	public class NearByRefreshTask extends AsyncTask<Void, Void, Boolean> {
		private Socket socket = null;
		PrintWriter out = null;

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				socket = new Socket(getString(R.string.Server_IP),
						Integer.parseInt(getString(R.string.Server_Port)));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.print("NearByRefresh " + ID + " " + Longitude + " "
						+ Latitude + " " + user_destination);
				out.flush();
				NearByArrays.clear();
				InputStream br = socket.getInputStream();
				byte[] buffer = new byte[2048];
				int readSize = br.read(buffer);

				flag = 0;// 重新初始化flag

				if (readSize > 0) {
					String NearByRefreshMsg = new String(buffer, 0, readSize);
					System.out.println("NearByMsg:" + NearByRefreshMsg);
					if (NearByRefreshMsg.contains("NearByRefreshNone")) {
						flag = 1;
						return true;
					}
					String[] temp = NearByRefreshMsg.split("`");

					HeadImageDownloader downloader = new HeadImageDownloader();

					DatabaseHelper dbHelper = new DatabaseHelper(
							NearByActivity.this, "iPin");
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					db.delete("NearBy", null, null);
					for (int i = 0; i < Integer.parseInt(temp[0]); i++) {
						NearByMsgEntity entity = new NearByMsgEntity();
						String ID, HeadImageVersion, username, telnum, distance, destination;
						ID = temp[i * 6 + 1];
						HeadImageVersion = temp[i * 6 + 2];
						username = temp[i * 6 + 3];
						telnum = temp[i * 6 + 4];
						distance = temp[i * 6 + 5];
						destination = temp[i * 6 + 6];
						entity.setID(ID);
						entity.setHeadImageVersion(HeadImageVersion);
						entity.setUsername(username);
						entity.setTelnum(telnum);
						entity.setDistance(distance);
						entity.setDestination(destination);
						NearByArrays.add(entity);
						ContentValues values = new ContentValues();
						values.put("info_ID", ID);
						values.put("info_HeadImageVersion", HeadImageVersion);
						values.put("info_username", username);
						values.put("info_telnum", telnum);
						values.put("info_distance", distance);
						values.put("info_destination", destination);
						db.insert("NearBy", null, values);
						downloader.download(NearByActivity.this, ID,
								HeadImageVersion);
					}
					db.close();
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
			NearByRefreshTask = null;
			if (success) {
				if (flag == 1) {
					Toast.makeText(NearByActivity.this, "您的附近没有拼客哦",
							Toast.LENGTH_SHORT).show();
				}
				NearByAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(NearByActivity.this, "请求数据失败",
						Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			NearByRefreshTask = null;
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, InfoListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		this.finish();
		// super.onBackPressed();

	}

	// 设置标题栏右侧按钮的作用
	public void nearby_btn_right(View v) {
		Locate();
	}

	public void btn_infolist(View v) {
		Intent intent = new Intent(NearByActivity.this, InfoListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		NearByActivity.this.finish();
	}

	public void btn_discuss(View v) {
		Intent intent = new Intent(NearByActivity.this, DiscussActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		NearByActivity.this.finish();
	}

	public void btn_setting(View v) {
		Intent intent = new Intent(NearByActivity.this, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		NearByActivity.this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 可以根据多个请求代码来作相应的操作
		if (20 == resultCode) {

		} else if (0 == resultCode) {
			Locate();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class Nearrcv extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 显示消息数目
			badge.setText(service.group_msg_num + "");
			// badge.setTextColor(Color.RED);
			badge.setTextSize(10);
			badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			badge.setBadgeMargin(0, 0);
			badge.show();

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