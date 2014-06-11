package lyc.iping;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static final int VERSION = 1;
	private Context ctx = null;
	public DatabaseHelper(Context context,String name,CursorFactory factory,int version)
	{		
		super(context,name,factory,version);
		ctx=context;
	}
	
	public DatabaseHelper(Context context,String name,int version)
	{
		this(context,name,null,version);
	}
	
	public DatabaseHelper(Context context,String name)
	{
		this(context,name,VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table LoginUser(ID varchar(9),username varchar(20),password varchar(32),sex varchar(10),telephone varchar(11),HeadImageVersion int(5),autoLogin boolean,auth int(1),StudentID varchar(9),PersonID varchar(6))");
		db.execSQL("create table info(GroupID varchar(9),info_ID varchar(9),info_HeadImageVersion int(5),info_username varchar(20),info_from varchar(20)," +
				"info_to varchar(20),info_date varchar(20),info_detail nvarchar(300),info_time varchar(20),memberCount varchar(1),memberList varchar(70))");
		db.execSQL("create table NearBy(info_ID varchar(9),info_HeadImageVersion int(5),info_username varchar(20),info_telnum varchar(11),info_distance varchar(5),info_destination varchar(20))");
		db.execSQL("create table discuss(GroupID varchar(9),info_ID varchar(9),info_HeadImageVersion int(5),info_username varchar(20),info_from varchar(20),info_to varchar(20),info_date varchar(20),info_detail nvarchar(300),memberCount varchar(1),memberList varchar(70))");
		db.execSQL("create table GroupMember(GroupID varchar(9),ID varchar(9),username varchar(20),sex varchar(10),telephone varchar(11),HeadImageVersion int(5))");
		db.execSQL("create table NearbyDestination(ID varchar(9),Destination varchar(20))");
		//db.execSQL("create table Version(Local varchar(9),Current varchar(9))");
		
		ContentValues values =new ContentValues();
		values.put("ID", "null");
		values.put("username", "null");
		values.put("password", "null");
		values.put("sex", "null");
		values.put("telephone", "null");
		values.put("HeadImageVersion", 0);
		values.put("autoLogin", false);
		db.insert("LoginUser",null,values);
		/*
		ContentValues values1 =new ContentValues();
		values1.put("Local",ctx.getString(R.string.Version));
		values1.put("Current","0.0");
		db.insert("Version", null, values1);
		*/
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
