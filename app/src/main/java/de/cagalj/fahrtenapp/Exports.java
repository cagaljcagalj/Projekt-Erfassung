package de.cagalj.fahrtenapp;

import android.content.*;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class Exports {
    static final String[] HEAD={"Datum","Kunde","Projekt","Von","Bis","Pause (min)","Stunden","Fahrt hin","Fahrt zurück","Fahrtzeit (min)","Kilometer","Fahrtkosten EUR","Zusatzkosten EUR","Verdienst EUR","Status","Notiz"};
    static ArrayList<String[]> rows(Db db){ArrayList<String[]> data=new ArrayList<>();Cursor c=db.query("SELECT e.day,c.name,p.name,e.start,e.end,e.break_min,e.travel_out,e.travel_back,e.travel_minutes,e.km,e.km_rate,e.extra,e.rate,e.status,e.note FROM entries e JOIN projects p ON p.id=e.project_id JOIN customers c ON c.id=p.customer_id ORDER BY e.day DESC,e.id DESC");while(c.moveToNext()){
        double hours=MainActivity.hours(c.getString(3),c.getString(4),c.getInt(5));double km=c.getDouble(9)*(c.getInt(6)+c.getInt(7));double travel=km*c.getDouble(10);double earned=hours*c.getDouble(12)+travel+c.getDouble(11);
        data.add(new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),""+c.getInt(5),MainActivity.money(hours),""+c.getInt(6),""+c.getInt(7),""+c.getInt(8),MainActivity.money(km),MainActivity.money(travel),MainActivity.money(c.getDouble(11)),MainActivity.money(earned),c.getString(13),c.getString(14)});
    }c.close();return data;}
    static void pdf(Context ctx,Db db,Uri uri)throws Exception{
        ArrayList<String[]> rows=rows(db);PdfDocument pdf=new PdfDocument();Paint p=new Paint(3);int page=1,y=0;PdfDocument.Page current=null;Canvas canvas=null;
        for(int i=-1;i<rows.size();i++){
            if(current==null||y>760){if(current!=null)pdf.finishPage(current);current=pdf.startPage(new PdfDocument.PageInfo.Builder(595,842,page++).create());canvas=current.getCanvas();canvas.drawColor(Color.WHITE);p.setColor(Color.BLACK);p.setTextSize(17);canvas.drawText("CAGALJ – Arbeits- und Fahrtnachweis",30,40,p);p.setTextSize(10);y=70;}
            if(i<0){canvas.drawText("Datum  |  Kunde / Projekt  |  Stunden  |  Kilometer  |  EUR  |  Status",30,y,p);y+=20;continue;}
            String[] r=rows.get(i);String line=r[0]+" | "+shorten(r[1]+" / "+r[2],28)+" | "+r[6]+" h | "+r[10]+" km | "+r[13]+" EUR | "+r[14];canvas.drawText(shorten(line,98),30,y,p);y+=19;
        }
        if(current==null){current=pdf.startPage(new PdfDocument.PageInfo.Builder(595,842,1).create());current.getCanvas().drawText("Keine Einträge",30,40,p);}pdf.finishPage(current);
        try(OutputStream out=ctx.getContentResolver().openOutputStream(uri)){pdf.writeTo(out);}finally{pdf.close();}
    }
    static String shorten(String s,int n){if(s==null)return "";return s.length()>n?s.substring(0,n-1)+"…":s;}
    static String esc(String s){if(s==null)return "";return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
    static void entry(ZipOutputStream z,String name,String value)throws IOException{z.putNextEntry(new ZipEntry(name));z.write(value.getBytes("UTF-8"));z.closeEntry();}
    static void xlsx(Context ctx,Db db,Uri uri)throws Exception{
        ArrayList<String[]> data=rows(db);try(ZipOutputStream z=new ZipOutputStream(ctx.getContentResolver().openOutputStream(uri))){
            entry(z,"[Content_Types].xml","<?xml version=\"1.0\" encoding=\"UTF-8\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/></Types>");
            entry(z,"_rels/.rels","<?xml version=\"1.0\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>");
            entry(z,"xl/workbook.xml","<?xml version=\"1.0\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"Arbeitsnachweis\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            entry(z,"xl/_rels/workbook.xml.rels","<?xml version=\"1.0\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/></Relationships>");
            StringBuilder b=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
            ArrayList<String[]> all=new ArrayList<>();all.add(HEAD);all.addAll(data);for(int i=0;i<all.size();i++){b.append("<row r=\"").append(i+1).append("\">");for(String val:all.get(i)){b.append("<c t=\"inlineStr\"><is><t xml:space=\"preserve\">").append(esc(val)).append("</t></is></c>");}b.append("</row>");}b.append("</sheetData></worksheet>");entry(z,"xl/worksheets/sheet1.xml",b.toString());
        }
    }
}
