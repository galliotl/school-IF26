package utt.if26.bardcamp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import utt.if26.bardcamp.bdd.AppDB;
import utt.if26.bardcamp.bdd.AppDBTable;
import utt.if26.bardcamp.fragments.AccountFragment;
import utt.if26.bardcamp.fragments.FeedFragment;
import utt.if26.bardcamp.fragments.MusicFragment;
import utt.if26.bardcamp.models.Music;
import utt.if26.bardcamp.models.User;
import utt.if26.bardcamp.services.MusicService;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private SQLiteDatabase db;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new AppDB(this).getWritableDatabase();
        setContentView(R.layout.activity_main);
        this.configureBottomView();
        user = fetchUser();
        loadFragment(new FeedFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            //bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //startService(playIntent);
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(new ArrayList<Music>());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    public void musicPicked(View view){
        musicSrv.setMusic(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
    }

    private void configureBottomView(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_feed:
                        fragment = new FeedFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_music:
                        fragment = new MusicFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_account:
                        fragment = new AccountFragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main_frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public User getUser() {
        return user;
    }

    public void initSomeData() {
        addUser("Lucas", "Galliot", "https://scontent-cdt1-1.xx.fbcdn.net/v/t1.0-9/49001852_1941748439256418_6127957719006576640_n.jpg?_nc_cat=102&_nc_oc=AQlCTIYBxNtMHlP6LKYQn0qA5aS796PUglPOX2ArmMm_PmatSkaH6KcshlvaEPxeqf0&_nc_ht=scontent-cdt1-1.xx&oh=b4ab1562225aa4c807c51e820bf0d201&oe=5E5E04AD");
        addMusic("lalaland",1, "../path/to/music", "http://www.clker.com/cliparts/n/T/x/Z/f/L/music-note-th.png");
        addFav(1, 1);
        addMusic("laland",1, "../path/to/music", "http://www.clker.com/cliparts/n/T/x/Z/f/L/music-note-th.png");
        addMusic("laldsaland",1, "../path/to/music", "http://www.clker.com/cliparts/n/T/x/Z/f/L/music-note-th.png");
        addMusic("fdand",1, "../path/to/music", "http://www.clker.com/cliparts/n/T/x/Z/f/L/music-note-th.png");
        addMusic("ftreslaland",1, "../path/to/music", "http://www.clker.com/cliparts/n/T/x/Z/f/L/music-note-th.png");
    }

    public User fetchUser(){
        Cursor cursor = db.rawQuery("SELECT * FROM " + AppDBTable.User.TABLE_NAME + " WHERE " + AppDBTable.User._ID + "=?", new String[]{"1"});
        User user = null;
        if(cursor.getCount() > 0) {
            Log.d("DB","There's a record");
            cursor.moveToFirst();
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTable.User.COLUMN_PIC_PATH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTable.User.COLUMN_FIRSTNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(AppDBTable.User.COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AppDBTable.User._ID)),
                    "dsjdjs"
            );
        }
        cursor.close();
        return user;
    }

    public void addUser(String firstName, String name, String picPath) {
        ContentValues cv = new ContentValues();
        cv.put(AppDBTable.User.COLUMN_FIRSTNAME, firstName);
        cv.put(AppDBTable.User.COLUMN_NAME, name);
        cv.put(AppDBTable.User.COLUMN_PIC_PATH, picPath);
        db.insert(AppDBTable.User.TABLE_NAME, null, cv);
    }

    public void addFav(int music, int user) {
        ContentValues cv = new ContentValues();
        cv.put(AppDBTable.Favourite.COLUMN_MUSIC, music);
        cv.put(AppDBTable.Favourite.COLUMN_USER, user);
        db.insert(AppDBTable.Favourite.TABLE_NAME, null, cv);
    }

    public void addMusic(String title, int artistId, String path, String picPath){
        ContentValues cv = new ContentValues();
        cv.put(AppDBTable.Music.COLUMN_ARTIST, artistId);
        cv.put(AppDBTable.Music.COLUMN_PATH, path);
        cv.put(AppDBTable.Music.COLUMN_PIC_PATH, picPath);
        cv.put(AppDBTable.Music.COLUMN_TITLE, title);
        db.insert(AppDBTable.Music.TABLE_NAME, null, cv);
    }
}
