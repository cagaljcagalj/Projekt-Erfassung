package de.cagalj.fahrtenapp;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import org.json.*;
import java.io.*;
import java.text.*;
import java.util.*;
import android.content.Context;
import android.content.Intent;

public class BackupReceiver extends android.content.BroadcastReceiver {
 public static void schedule(Context c){
  AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
  Intent i=new Intent(c,BackupReceiver.class);
  PendingIntent pi=PendingIntent.getBroadcast(c,101,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  am.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+3*60*60*1000L,3*60*60*1000L,pi);
 }
 @Override public void onReceive(Context c, Intent intent) {final PendingResult pending=goAsync();new Thread(()->{try{write(c);}finally{pending.finish();}}).start();}
 public static void write(Context c){
  String folder=c.getSharedPreferences("auto_backup",0).getString("folder","");if(folder.isEmpty())return;
  try{
   Uri tree=Uri.parse(folder);android.content.ContentResolver resolver=c.getContentResolver();
   Uri dir=DocumentsContract.buildDocumentUriUsingTree(tree,DocumentsContract.getTreeDocumentId(tree));
   Uri file=null;
   Uri children=DocumentsContract.buildChildDocumentsUriUsingTree(tree,DocumentsContract.getTreeDocumentId(tree));
   try(android.database.Cursor cur=resolver.query(children,new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID,DocumentsContract.Document.COLUMN_DISPLAY_NAME},null,null,null)){
    if(cur!=null)while(cur.moveToNext())if("Projekt-Erfassung-Autosicherung.zip".equals(cur.getString(1))){file=DocumentsContract.buildDocumentUriUsingTree(tree,cur.getString(0));break;}
   }
   // First create and close a valid archive in private storage, before touching the existing backup.
   File staged=File.createTempFile("projekt-erfassung-backup-",".zip",c.getCacheDir());
   try{
    BackupArchive.writeToFile(c,staged);
    if(staged.length()==0)throw new IOException("Leere Sicherung");
    if(file==null)file=DocumentsContract.createDocument(resolver,dir,"application/zip","Projekt-Erfassung-Autosicherung.zip");
    if(file==null)throw new IOException("Sicherungsdatei konnte nicht angelegt werden");
    try(InputStream in=new FileInputStream(staged);OutputStream out=resolver.openOutputStream(file,"wt")){
     if(out==null)throw new IOException("Sicherungsdatei konnte nicht geöffnet werden");
     byte[] buffer=new byte[16384];int count;while((count=in.read(buffer))!=-1)out.write(buffer,0,count);out.flush();
    }
   }finally{staged.delete();}
   String stamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.GERMANY).format(new Date());
   c.getSharedPreferences("auto_backup",0).edit().putString("last_backup",stamp).apply();
  }catch(Exception e){android.util.Log.e("ProjektErfassung","Automatische Sicherung fehlgeschlagen",e);}
 }
}
