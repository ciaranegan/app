package com.fightingmongooses.fightingmongooses;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Database{
	/* these variables are never going to change*/
	public static final String KEY_ROWID ="_id";// this variable is going to give us the row id, every time we put something in our data base its going to create a row 0,1,2,3..n
	public static final String KEY_NAME ="name";
	public static final String KEY_DATE ="date";
	public static final String KEY_TIME ="time";
	public static final String KEY_END_TIME="end_time";
	public static final String KEY_LOCATION ="location";
	public static final String KEY_DESCRIPTION ="description";
	public static final String KEY_START_DATE ="start_date";
	public static final String KEY_END_DATE ="end_date";
	public static final String KEY_CONFERENCE="conference";
	public static final String KEY_WEBSITE="website";
	public static final String KEY_GUESTS="guests";
	public static final String KEY_TWITTER="twitter";
	public static final String KEY_GMAPS="gmaps";
	public static final String KEY_PICTURE="picture";
	
	/* only this class can access these variables*/
	private static final String DATABASE_NAME = "androkon_database"; // data base name is going to reference our database
	private static final String EVENT_TABLE = "event_table";// in our database we can store different tables such as "Share_table"
	private static final String CONFERENCE_TABLE = "conference_table";
	private static final String MAP_TABLE = "map_table";
	private static final int DATABASE_VERSION = 1;// to give the database a version, to allow updates
	
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase; // reference database class
	
	/* set up data base sub class, we just need a class to pull everything together hence DbHelper*/
	
	private static class DbHelper extends SQLiteOpenHelper{
		public DbHelper(Context context){
		
			super(context,DATABASE_NAME, null,DATABASE_VERSION ); // settings for super class, factory = null,
		}
		/* implement methods from SQLiteOpenHelper */
		@Override
		/* the first time we ever call a database is the only time this onCreate method is called, this accesses database 
		and executes sql code*/
		public void onCreate(SQLiteDatabase db){
			db.execSQL("CREATE TABLE " + CONFERENCE_TABLE + "( " +
					KEY_ROWID + " INTEGER PRIMARY KEY NOT NULL, " + // Django provides unique ids
					KEY_NAME + " TEXT NOT NULL, " +
					KEY_DESCRIPTION + " TEXT NOT NULL, " +
					KEY_START_DATE+ " DATE NOT NULL, " +
					KEY_END_DATE+ " DATE, " + 
					KEY_WEBSITE+ " TEXT NOT NULL, " +
					KEY_GUESTS+ " TEXT NOT NULL, " +
					KEY_TWITTER+ " TEXT NOT NULL, " +
					KEY_GMAPS+ " TEXT NOT NULL);"); 
			
			
			db.execSQL("CREATE TABLE " + EVENT_TABLE + "( " +
					KEY_ROWID + " INTEGER PRIMARY KEY NOT NULL, " + // Django provides unique ids
					KEY_NAME + " TEXT NOT NULL, " +
					KEY_TIME+ " DATE NOT NULL, " +
					KEY_END_TIME+ " DATE, " +
					KEY_LOCATION+ " TEXT NOT NULL, "+
					KEY_DESCRIPTION +" TEXT NOT NULL, "+
					KEY_CONFERENCE+" INTEGER, "+
					" FOREIGN KEY ("+KEY_CONFERENCE+") REFERENCES "+CONFERENCE_TABLE+" ("+KEY_ROWID+"));");
			
			db.execSQL("CREATE TABLE " + MAP_TABLE + " ( " +
					 KEY_ROWID + " INTEGER PRIMARY KEY NOT NULL, " +
					 KEY_PICTURE + " TEXT NOT NULL, " +
					 KEY_CONFERENCE+" INTEGER, "+
					 " FOREIGN KEY ("+KEY_CONFERENCE+") REFERENCES "+CONFERENCE_TABLE+" ("+KEY_ROWID+"));");
					
		}
		
		/* if our table exists then we are going to drop it then call our onCreate method*/
		@Override 
		public void onUpgrade(SQLiteDatabase db, int oldversion, int newvesion){
		
				db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE + ";");
				db.execSQL("DROP TABLE IF EXISTS " + CONFERENCE_TABLE + ";");
				db.execSQL("DROP TABLE IF EXISTS " + MAP_TABLE + ";");
			
				onCreate(db); // upgrade data base
		
		}
	}
	
	/* constructor for Database class*/
	public Database(Context c){
		ourContext = c;
	}
	
	public Database open() throws SQLException{
		ourHelper = new DbHelper(ourContext); // give a new instance of that object through ourContext
		ourDatabase = ourHelper.getWritableDatabase(); // going to open up data base through our helper

		return this;
	}
	
	public void close(){
		ourHelper.close();
	}
	
	public void clear()
	{
		cons = null;
		events = null;
		ourHelper.onUpgrade(ourDatabase, 0, 0);
	}
	

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat DJANGOdateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat SQLdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static Date parseSQLDate(String d)
	{
		if(d == null)
			return null;
		Date retval = null;
		try {
			retval = SQLdateFormat.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retval;
	}
	public static Date parseDjangoDate(String d)
	{
		if(d == "null"){
			return null;
		}
		
		Date retval = null;
		
		try {
			retval = DJANGOdateFormat.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return retval;
	}
	
	public long createConferenceEntry(int id, String con_name, String con_description, 
			String con_start_date, String con_end_date, String website, String guests, String twitter, String gmaps){
		cons = null;
		events = null; 
		
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEY_NAME, con_name); // put stuff in like a bundle to finalise it before its placed in the table
		cv.put(KEY_DESCRIPTION, con_description ); //cv.put(where we want to save it in our data base, our value we wish to store);
		cv.put(KEY_START_DATE, SQLdateFormat.format(parseDjangoDate(con_start_date)));
		
		Date djangoDate = parseDjangoDate(con_end_date);
		if(djangoDate != null)
			cv.put(KEY_END_DATE, SQLdateFormat.format(djangoDate));
		
		cv.put(KEY_WEBSITE, website);
		cv.put(KEY_GUESTS, guests);
		cv.put(KEY_TWITTER, twitter);
		cv.put(KEY_GMAPS, gmaps);
		return ourDatabase.insert(CONFERENCE_TABLE, null, cv); // inserts our puts into table
	}
	
	/* write to data base */
	public long createEventEntry(int id, String event_name, String event_time, String event_end_time, String event_location, String event_description, int conference){
		cons = null;
		events = null;
		
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEY_NAME, event_name); // put stuff in like a bundle to finalise it before its placed in the table
		cv.put(KEY_TIME, SQLdateFormat.format(parseDjangoDate(event_time))); //cv.put(where we want to save it in our data base, our value we wish to store);
		
		Date djangoDate = parseDjangoDate(event_end_time);
		if(djangoDate != null)
			cv.put(KEY_END_TIME, SQLdateFormat.format(djangoDate));
		cv.put(KEY_LOCATION, event_location);
		cv.put(KEY_DESCRIPTION, event_description);
		cv.put(KEY_CONFERENCE, conference);
		return ourDatabase.insert(EVENT_TABLE, null, cv); // inserts our puts into table
	}
	
	public long createMapEntry(int id, String path, int conference)
	{
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, id);
		cv.put(KEY_PICTURE, path);
		cv.put(KEY_CONFERENCE, conference);
		
		return ourDatabase.insert(MAP_TABLE, null, cv);
	}
	
	private Conference[] cons = null;
	public Conference getCon(int id){
		
		returnConference(); // This makes sure cons is up to date
		
		for(int i = 0; i != cons.length; i++)
			if(cons[i].getId() == id)
				return cons[i];
		
		return null;
	}
	
	public Conference[] returnConference(){
		//String[] con_temp = new String[]{KEY_NAME, KEY_};
		
		if(cons != null)
			return cons;
	
		Cursor c = ourDatabase.query(CONFERENCE_TABLE, null, null, null, null, null, null);
	
		int iId = c.getColumnIndex(KEY_ROWID);
		int iName = c.getColumnIndex(KEY_NAME);
		int iDesc = c.getColumnIndex(KEY_DESCRIPTION);
		int iStart = c.getColumnIndex(KEY_START_DATE);
		int iEnd = c.getColumnIndex(KEY_END_DATE);
		int iWebsite = c.getColumnIndex(KEY_WEBSITE);
		int iGuests = c.getColumnIndex(KEY_GUESTS);
		int iTwitter = c.getColumnIndex(KEY_TWITTER);
		int iGmaps = c.getColumnIndex(KEY_GMAPS);
		
		Conference[] return_conference = new Conference[c.getCount()];
		
		int rowcount = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			return_conference[rowcount] = new Conference(Integer.parseInt(c.getString(iId)), c.getString(iName),
					                                     c.getString(iDesc), parseSQLDate(c.getString(iStart)),
					                                     parseSQLDate(c.getString(iEnd)), c.getString(iWebsite),
					                                     c.getString(iGuests), c.getString(iTwitter), c.getString(iGmaps));
		    rowcount++;
		}
		
		cons = return_conference;
		return return_conference;	
	}
	
	private ConEvent[] events = null;
	public ConEvent[] getConEvents(int conId){
		returnEvents();
		
		ArrayList<ConEvent> conEvents = new ArrayList<ConEvent>();
		for(int i = 0; i != events.length; i++){
			if(events[i].getCon() == conId)
				conEvents.add(events[i]);
		}
		
		return conEvents.toArray(new ConEvent[conEvents.size()]);
	}
	
	public ConEvent[] returnEvents(){
		//String[] event_temp = new String[]{KEY_NAME};
		
		if(events != null)
			return events;
	
		Cursor c = ourDatabase.query(EVENT_TABLE, null, null, null, null, null, null);
	
		int iId = c.getColumnIndex(KEY_ROWID);
		int iName = c.getColumnIndex(KEY_NAME);
		int iTime = c.getColumnIndex(KEY_TIME);
		int iLocation = c.getColumnIndex(KEY_LOCATION);
		int iEndTime = c.getColumnIndex(KEY_END_TIME);
		int iDesc = c.getColumnIndex(KEY_DESCRIPTION);
		int iCon = c.getColumnIndex(KEY_CONFERENCE);
	
		ConEvent[] return_events = new ConEvent[c.getCount()];
		
		int rowcount = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			return_events[rowcount] = new ConEvent(Integer.parseInt(c.getString(iId)), c.getString(iName), 
					                               parseSQLDate(c.getString(iTime)), c.getString(iLocation), 
												   c.getString(iDesc), parseSQLDate(c.getString(iEndTime)),
												   Integer.parseInt(c.getString(iCon)));
		    rowcount++;
		}
	
		events = return_events;
		
		return return_events;	
	}
	
	private ConMap[] maps = null;
	public ConMap[] getConMaps(int conId){
		returnMaps();
		
		ArrayList<ConMap> conMaps = new ArrayList<ConMap>();
		for(int i = 0; i != maps.length; i++){
			if(maps[i].getCon() == conId)
				conMaps.add(maps[i]);
		}
		
		return conMaps.toArray(new ConMap[conMaps.size()]);
	}
	
	public ConMap[] returnMaps(){
		
		if(maps != null)
			return maps;
	
		Cursor c = ourDatabase.query(MAP_TABLE, null, null, null, null, null, null);
	
		int iId = c.getColumnIndex(KEY_ROWID);
		int iPath = c.getColumnIndex(KEY_PICTURE);
		int iCon = c.getColumnIndex(KEY_CONFERENCE);
	
		ConMap[] return_maps = new ConMap[c.getCount()];
		
		int rowcount = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			return_maps[rowcount] = new ConMap(Integer.parseInt(c.getString(iId)), c.getString(iPath),
												   Integer.parseInt(c.getString(iCon)));
		    rowcount++;
		}
	
		maps = return_maps;
		
		return return_maps;	
	}
	
//	/* This is for if we want to expand to include more tables*/
//	public String genericGetEntry(String tableName, String[] columns, String where){
//		Cursor c = ourDatabase.query(tableName,columns, where , null, null, null,  null); 
//		int[] iA=new int[columns.length];
//		
//		for (int i=0; i<columns.length; i++){
//			iA[i]=c.getColumnIndex(columns[i]);
//		}
//		
//		String result="";
//		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
//			for(int i=0; i<columns.length; i++){
//				result = result + c.getString(iA[i]);/* + c.getString(iA[i]) + " "+ c.getString(iA[i])+ " "+ c.getString(iA[i]) + " "*/
//			}
//		}
	
//		return result;
//		
//	}
	
//	public String genericGetNameEntry(String tableName, String where){
//		String[] a={"name"};
//		Cursor c= ourDatabase.query(tableName, a, where, null, null, null, null);
//		int i=c.getColumnIndex("name");
//		Log.i("cursor index", "index="+i);
//		Log.i("cursor size", "size="+c.getCount());
//		return c.getString(i);
//	}
//	
//	public String getALLDataFromConferenceTable(){	
//		String[] columns = new String[]{KEY_ROWID,KEY_NAME, KEY_DESCRIPTION, KEY_START_DATE, KEY_END_DATE};
//		Cursor c = ourDatabase.query(CONFERENCE_TABLE, columns, null, null,null,null ,null);
//	
//		int iRow = c.getColumnIndex(KEY_ROWID);
//		int iName = c.getColumnIndex(KEY_NAME);
//		int idescription = c.getColumnIndex(KEY_DESCRIPTION);
//		int istartdate = c.getColumnIndex(KEY_START_DATE);
//		int ienddate = c.getColumnIndex(KEY_END_DATE);
//		String result="";
//		while(c.moveToNext()){
//		result = result + c.getString(iRow)+ " " + c.getString(iName) + " "+ c.getString(idescription) + " "+ c.getString(istartdate) + " "+ c.getString(ienddate)+ " ";
//		}
//	
//		return result;
//	}
//	
//	public String getALLDataFromEventTable(){	
//		String[] columns = new String[]{KEY_ROWID,KEY_NAME, KEY_DATE, KEY_PLACE, KEY_DURATION, KEY_DESCRIPTION};//, KEY_TIME};
//		Cursor c = ourDatabase.query(EVENT_TABLE, columns, null, null,null,null ,null);
//	
//		int iRow = c.getColumnIndex(KEY_ROWID);
//		int iName = c.getColumnIndex(KEY_NAME);
//		int idate = c.getColumnIndex(KEY_DATE);
//		int iplace = c.getColumnIndex(KEY_PLACE);
//		int iduration = c.getColumnIndex(KEY_DURATION);
//		int idescription = c.getColumnIndex(KEY_DESCRIPTION);
//		//int itime = c.getColumnIndex(KEY_TIME);
//		String result="";
//		while(c.moveToNext()){
//			result = result + c.getString(iRow)+ " " + c.getString(iName) + " "+
//					 c.getString(idate) + " "+ c.getString(iplace) + " "+ c.getString(iduration) 
//					 + " " + c.getString(idescription);// + " "+ c.getString(itime);
//		}
//	
//		return result;
//	}
}