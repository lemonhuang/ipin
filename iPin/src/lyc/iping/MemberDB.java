package lyc.iping;


import java.util.ArrayList;
import java.util.List;

import lyc.iping.ChatMsgEntity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MemberDB {
	private SQLiteDatabase db;

	public MemberDB(Context context) {
		db = context.openOrCreateDatabase("mymember",
				Context.MODE_PRIVATE, null);
	}

	public void saveMember(int id, MemberEntity entity) {
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ id
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, email TEXT,phone TEXT,isCreator INTEGER,other TEXT)");
		
		db.execSQL(
				"insert into _" + id
						+ " (name,email,phone,isCreator,other) values(?,?,?,?,?)",
				new Object[] { entity.getName(), entity.getEmail(),
						entity.getPhone(), entity.getCreator(), entity.getOther() });
	}

	public List<MemberEntity> getMember(int id) {
		List<MemberEntity> list = new ArrayList<MemberEntity>();
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ id
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, email TEXT,phone TEXT,isCreator TEXT,other TEXT)");
		Cursor c = db.rawQuery("SELECT * from _" + id + " ORDER BY _id DESC LIMIT 5", null);
		while (c.moveToNext()) {
			String name = c.getString(c.getColumnIndex("name"));
			String email = c.getString(c.getColumnIndex("email"));
			String phone = c.getString(c.getColumnIndex("phone"));
			int isCreator = c.getInt(c.getColumnIndex("isCreator"));
			String other = c.getString(c.getColumnIndex("other"));
			
		}
		c.close();
		return list;
			
			/*
			boolean isComMs = false;
			if (isCome == 1) {
				isComMsg = true;
			}
			MemberEntity entity = new MemberEntity(name, date, message, img,
					isComMsg);
			list.add(entity);
		}
		c.close();
		return list;
		*/
	}

	public void close() {
		if (db != null)
			db.close();
	}
}


