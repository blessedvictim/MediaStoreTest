package com.theonlylies.mediastoretest;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    class MusicReadTask extends AsyncTask<Void,MusicFile,Void>{

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,};
        final String where = MediaStore.Audio.Media.IS_MUSIC + "!=0";

        @Override
        protected void onPreExecute(){
            snackProgressBarManager.show(determinateType, SnackProgressBarManager.LENGTH_INDEFINITE);
            snackProgressBarManager.setProgress(50);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Cursor cursor = getContentResolver().query(uri, cursor_cols, where, null,MediaStore.Audio.Media.ARTIST);
            int i=0;
            double count=cursor.getCount();
            Log.d("cursor count",String.valueOf(count));
            while ( cursor.moveToNext()) {
                HashMap<String,String> mapOfFields = new HashMap<>();
                MusicFile musicFile=new MusicFile();

                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                i++;
                /**
                  small workaround for snackbar stuck
                 */
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.decode(albumId) );
                musicFile.setAlbum(album);
                musicFile.setArtist(artist);
                musicFile.setRealPath(data);
                musicFile.setTitle(title);
                musicFile.setArtworkUri(albumArtUri);
                musicFile.progress= i/count * 100;


                Log.d("artist:", artist);
                Log.d("album:",album);
                Log.d("title:",title);
                Log.d("data:",data);
                Log.d("albumId:",String.valueOf(albumId));
                Log.d("albumArtUri:",albumArtUri.toString());
                Log.d("_ID:",id);
                Log.d("END:","END");

                publishProgress(musicFile);

            }

            cursor.close();
            return null;
        }

        @Override
        protected void onProgressUpdate(MusicFile... progress) {
            Log.d("artist:", progress[0].getArtist());
            Log.d("album:",progress[0].getAlbum());
            Log.d("title:",progress[0].getTitle());
            Log.d("data:",progress[0].getRealPath());
            Log.d("progress",String.valueOf(progress[0].progress));

            snackProgressBarManager.setProgress((int) progress[0].progress);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            snackProgressBarManager.dismissAll();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("PERMISSONS:","START");
        if(5==requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("PERMISSONS:","GRANTED!!!");
                readMediaStore();
            }else{
                Log.d("PERMISSONS:","UNGRANTED!!!");

            }
        }
    }

    SnackProgressBarManager snackProgressBarManager;
    SnackProgressBar determinateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context=this;
        snackProgressBarManager = new SnackProgressBarManager(findViewById(R.id.layout));
        determinateType = new SnackProgressBar(
                SnackProgressBar.TYPE_DETERMINATE, "Loading files...");







        Log.d("MediaStore version",MediaStore.getVersion(this));
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOL","QweQQW");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        5);
            }
        //readMediaStore();
        new MusicReadTask().execute();
    }



    public void readMediaStore() {

        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,};
        final String where = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        final Cursor cursor = getContentResolver().query(uri, cursor_cols, where, null,MediaStore.Audio.Media.ARTIST);

        Log.d("cursor count",String.valueOf(cursor.getCount()));
        while ( cursor.moveToNext()) {
            HashMap<String,String> mapOfFields = new HashMap<>();

            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

            String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

            String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

            mapOfFields.put(MediaStore.Audio.Media._ID,id);
            mapOfFields.put(MediaStore.Audio.Media.ARTIST,artist);
            mapOfFields.put( MediaStore.Audio.Media.ALBUM,album);
            mapOfFields.put(MediaStore.Audio.Media.TITLE,title);
            mapOfFields.put(MediaStore.Audio.Media.DATA,data);
            mapOfFields.put(MediaStore.Audio.Media.ALBUM_ID,albumId);

            //list.add(mapOfFields);

            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));



            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, Long.decode(albumId) );

            Log.d("artist:", artist);
            Log.d("album:",album);
            Log.d("title:",title);
            Log.d("data:",data);
            Log.d("albumId:",String.valueOf(albumId));
            Log.d("duration:",String.valueOf(duration));
            Log.d("albumArtUri:",albumArtUri.toString());
            Log.d("_ID:",id);
            Log.d("END:","END");

        }

        cursor.close();
    }
}