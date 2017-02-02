package kr.ac.ks.ap;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.xmlpull.v1.XmlPullParser;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
public class Tab_OptionsMenu_Dialog_AddRemoveTab_etnewsActivity extends TabActivity {
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.main);
  tabHost = getTabHost();
  defaultTab(); //초기의 3개(뉴스속보, 통신방송, 컴퓨팅)
 }
 TabSpec getNewTabSpec(final int tabID){ //탭의 내용물
  TabSpec tabSpec = tabHost.newTabSpec("TAB"+(tabID));
  tabSpec.setIndicator(newsCategory[tabID-1]);
  tabSpec.setContent(new TabHost.TabContentFactory() {
   public View createTabContent(String tag) {
    ListView listview = new ListView(Tab_OptionsMenu_Dialog_AddRemoveTab_etnewsActivity.this);
    list.clear(); //한 list를 함께 사용하기 때문에 clear한다.
    getWebXml(tabID);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
      Tab_OptionsMenu_Dialog_AddRemoveTab_etnewsActivity.this,
      R.layout.rowform, 
      list);
    listview.setAdapter(adapter);
    listview.setOnItemClickListener(listener); //리스트 클릭
    return listview;
   }
  });
  return tabSpec;
 }
 void getWebXml(int num){
  try {
   URL url = new URL(newsXml[num-1]);
   XmlPullParser parser = Xml.newPullParser(); // new 를 사용하지 않고  만들수 있음
   parser.setInput(url.openStream(), "UTF-8");
   int eventType = parser.getEventType(); //event를 읽는 변수
   boolean inItem = false, inTitle = false, inLink = false;
   int count = 0;
   while(eventType!=XmlPullParser.END_DOCUMENT){
    switch(eventType){
    case  XmlPullParser.START_TAG:
     String tag=parser.getName();
     if(tag.equals("item")) inItem=true;
     else if(inItem==true && tag.equals("title")) inTitle = true;
     else if(inItem==true && tag.equals("link")) inLink = true;
     break;
    case  XmlPullParser.TEXT:
     if(inTitle==true){
      list.add(parser.getText());  //타이틀 저장
      inTitle=false;
     }
     else if(inLink==true){
      url_link[count]=parser.getText();
      url_link[count]=url_link[count++].replace("HTTP", "http");
      inLink=false;  
     }
    }
    eventType=parser.next();
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
 
 OnItemClickListener listener=new OnItemClickListener() { //리스트뷰 아이템 클릭시
  public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    long arg3) {
   Intent intent=new Intent( //링크 
     Intent.ACTION_VIEW,
     Uri.parse(url_link[(int)arg2]));
   startActivity(intent);
  }
 };
 void defaultTab(){ //처음 기본 탭들 생성
  for(int i = 1; i <= 3; i++){
   tabSpec = getNewTabSpec(i);
   list_tabSpec.add(tabSpec);
   tabHost.addTab(tabSpec);
  }
 }
 void refreshTab(){
  tabHost.clearAllTabs();
  for(int i = 0; i < newsCategory.length; i++){
   if(newsCategorySelection[i] == true){
    tabSpec = getNewTabSpec(i+1);
    list_tabSpec.add(tabSpec);
    tabHost.addTab(tabSpec);
   }
  }
 }
 public boolean onCreateOptionsMenu(Menu menu) {//메뉴
  MenuInflater menuInflater = new MenuInflater(this);
  menuInflater.inflate(R.menu.menu, menu);
  return super.onCreateOptionsMenu(menu);
 }
 boolean no_check_error(){
  for(int i = 0; i < newsCategory.length; i++){
   if(newsCategorySelection[i] == true)
    return true; //하나이상 선택된 것이 있다.
  }
  //없을 시 변화가 없도록
  return false;
 }
 public boolean onOptionsItemSelected(MenuItem item) {
  switch(item.getItemId()){
  case R.id.itemcategory:
   AlertDialog.Builder dialog = new AlertDialog.Builder(this);
   dialog.setTitle("카테고리 선택");
   dialog.setMultiChoiceItems(newsCategory, newsCategorySelection, new DialogInterface.OnMultiChoiceClickListener() {
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
     newsCategorySelection[which] = isChecked;
    }
   });
   dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
     if(no_check_error())
      refreshTab();
    }
   });
   dialog.show();
   break;
  case R.id.itemexit:
   finish();
   break;
  }
  return super.onOptionsItemSelected(item);
 }
 int tabID;
 TabSpec tabSpec;
 TabHost tabHost;
 int count;
 String[] url_link = new String[100];
 LinkedList<String> list = new LinkedList<String>();
 ArrayList<TabHost.TabSpec> list_tabSpec = new ArrayList<TabHost.TabSpec>();
 String[] newsCategory = new String[] {
   "뉴스속보", 
   "통신방송", 
   "컴퓨팅", 
   "디바이스-신성장", 
   "홈&모바일", 
   "콘텐츠", 
   "경제-교육-과학", 
   "국제", 
 "열린마당"};
 boolean newsCategorySelection[] = new boolean[newsCategory.length];
 String[] newsXml = new String[] {
   "http://rss.etnews.com/Section902.xml", 
   "http://rss.etnews.com/Section03.xml", 
   "http://rss.etnews.com/Section04.xml", 
   "http://rss.etnews.com/Section06.xml", 
   "http://rss.etnews.com/Section60.xml", 
   "http://rss.etnews.com/Section10.xml", 
   "http://rss.etnews.com/Section02.xml", 
   "http://rss.etnews.com/Section05.xml", 
 "http://rss.etnews.com/Section11.xml" };
}
