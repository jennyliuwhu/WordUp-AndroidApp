package com.example.jialingliu.wordup.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.entities.MainViewPagerAdapter;
import com.example.jialingliu.wordup.entities.PullScrollView;
import com.example.jialingliu.wordup.entities.WordViewHoder;
import com.example.jialingliu.wordup.model.FavoriteWord;
import com.example.jialingliu.wordup.model.Item;
import com.example.jialingliu.wordup.model.Word;
import com.example.jialingliu.wordup.ui.menu.MyMenuDrawer;
import com.example.jialingliu.wordup.util.MySharedpreference;
import com.example.jialingliu.wordup.util.ToastUtils;
import com.example.jialingliu.wordup.ws.local.NotificatService;
import com.google.gson.Gson;
import com.lurencun.cfuture09.androidkit.utils.ui.ExitDoubleClick;

import net.simonvt.menudrawer.Position;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jialingliu on 4/14/16.
 */
public class MainActivity extends MyMenuDrawer implements PullScrollView.OnTurnListener, View.OnClickListener, View.OnLongClickListener {

    public static final String NOTE = "NOTE";

    public static boolean ispause = false;
    public static final int YESTERDAY = 0, TODAY = 1;

    public static Handler todaywordhandler;
    public static Handler yesterdaywordhandler;

    public static Word tw;
    private static Word yw;

    private TextView usetimestextview;
    private Intent serviceintent;
    private WordViewHoder ywviewhoder;
    private WordViewHoder twviewhoder;
    private PullScrollView scrollview;
    private ImageView headimage;
    private ViewPager mainviewpager;
    private PagerTitleStrip titlestrip;
    private ProgressBar yesterdaybar;
    private ProgressBar todaybar;

    private List<View> viewlist;
    private List<String> titlelist;

//    private String timestr;
//    private String notestr;
    private boolean isbound;
    private NotificatService noticeservice;
    private NotificatService.LocalBinder localbinder;

    private TextView usernametextview;
    private String username;
    private MySharedpreference sharedpreference;

    private int wordchangetimes = 3;

    //GesturesListener gListener;
    // bind activity and service
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isbound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            localbinder = (NotificatService.LocalBinder) service;
            noticeservice = localbinder.getService();
            isbound = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreference = new MySharedpreference(this);
        initWord();

        todaywordhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                tw = (Word) msg.obj;
                if (tw != null) {
                    setWordViewContent(twviewhoder, tw);
                }
            }
        };
        serviceintent = new Intent(this, NotificatService.class);
        startService(serviceintent);
        bindService(serviceintent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onStart() {
        super.onStart();
        Boolean isFromNotification = getIntent().getBooleanExtra("is_from_notification", false);
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ispause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ispause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isbound) {
            this.unbindService(mServiceConnection);
        }
    }

    /**
     * init View and ViewHoder
     */
    protected void initView() {
        menudrawer.setContentView(R.layout.activity_main);
        scrollview = (PullScrollView) findViewById(R.id.scroll_view);
        headimage = (ImageView) findViewById(R.id.background_img);
        scrollview.setOnTurnListener(this);
        scrollview.init(headimage);
        mainviewpager = (ViewPager) findViewById(R.id.viewpage_main);
        titlestrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip_main);
        usernametextview = (TextView) findViewById(R.id.user_name);
        TextView noteTextView = (TextView) findViewById(R.id.tv_note);

        usernametextview.setOnClickListener(this);
        username = sharedpreference.getString("username");
        if (username != null) {
            usernametextview.setText(username);
        }

        final View viewYesterday = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_main, null);
        final View viewToday = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_main, null);
        viewToday.setTag("today");
        viewYesterday.setTag("yesterday");
        viewToday.setOnLongClickListener(this);
        viewYesterday.setOnLongClickListener(this);
        yesterdaybar = (ProgressBar) viewYesterday.findViewById(R.id.content_progressbar);
        todaybar = (ProgressBar) viewToday.findViewById(R.id.content_progressbar);

        viewlist = new ArrayList<>();
        viewlist.add(viewYesterday);
        viewlist.add(viewToday);
        titlelist = new ArrayList<>();
        titlelist.add("Yesterday");
        titlelist.add("Today");
        mainviewpager.setAdapter(new MainViewPagerAdapter(viewlist, titlelist));
        mainviewpager.setCurrentItem(1);

        ywviewhoder = getView(viewYesterday);
        twviewhoder = getView(viewToday);
        ywviewhoder.setId(YESTERDAY);
        twviewhoder.setId(TODAY);

        mainviewpager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {

                    int position;

                    @Override
                    public void onPageScrolled(int i, float v, int i2) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        position = i;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                        if (position == 0 && yw != null) {
                            setWordViewContent(ywviewhoder, yw);
                        } else if (position == 1 && tw != null) {
                            setWordViewContent(twviewhoder, tw);
                        }
                    }
                }
        );

        mainviewpager.requestFocus();
        mainviewpager.setFocusableInTouchMode(true);
        if (tw != null) {
            setWordViewContent(twviewhoder, tw);
        }
    }

    /**
     * init the word data from sharedpreference
     */
    private void initWord() {
        try {
            Map<String, String> map;
            Gson gson = new Gson();
            map = sharedpreference.getWordJson();
            String todayGsonString = map.get("today_json");
            String yesterdayGsonString = map.get("yesterday_json");
            tw = gson.fromJson(todayGsonString, Word.class);
            yw = gson.fromJson(yesterdayGsonString, Word.class);
            System.out.println("today="+todayGsonString);
            System.out.println("yesterday="+yesterdayGsonString);
        } catch (Exception e) {
            e.printStackTrace();
            sharedpreference.saveTodayJson("");
            sharedpreference.saveYesterdayJson("");
        }
    }

    /**
     * set the content of the WordView
     */
    public void setWordViewContent(WordViewHoder wordViewHoder, Word word) {
        initWord();
        try {
            wordViewHoder.wordTextView.setText(word.getWord());
            wordViewHoder.phoneticTextView.setText(word.getPhonetic());
            wordViewHoder.speechTextView.setText(word.getSpeech());
            wordViewHoder.explanationTextView.setText(word.getExplanation());

            wordViewHoder.exampleTextView.setText(Html.fromHtml(boldWordParser(word.getWord().trim(), word.getExample().trim())));
            if (wordViewHoder.getId() == YESTERDAY)
                yesterdaybar.setVisibility(View.INVISIBLE);
            else
                todaybar.setVisibility(View.INVISIBLE);
        } catch (NullPointerException e) {
            ToastUtils.showShort("Failed to get word " + sharedpreference.getCurrentWordId());
        }
    }

    private String boldWordParser(String word, String example) {
        String string = "";
        int index_of_ln = example.indexOf("\n");
        for (int i = 0; i < index_of_ln; i++) {
            string += example.charAt(i) + "";
        }
        string += "<br>";
        for (int i = index_of_ln + "\n".length(); i < example.length(); i++) {
            string += example.charAt(i) + "";
        }
        example = string;
        string = "";
        int index = example.indexOf(word);
        for (int i = 0; i < index; i++) {
            string += example.charAt(i) + "";
        }
        string = string + "<b>" + word + "</b>";
        for (int i = index + word.length(); i < example.length(); i++) {
            string += example.charAt(i) + "";
        }
        return string;
    }

    /**
     * init the ViewHoder
     */
    private WordViewHoder getView(View view) {
        WordViewHoder wordViewHoder = new WordViewHoder();
        wordViewHoder.wordTextView = (TextView) view.findViewById(R.id.content_word);
        wordViewHoder.phoneticTextView = (TextView) view.findViewById(R.id.content_phonetic);
        wordViewHoder.speechTextView = (TextView) view.findViewById(R.id.content_speech);
        wordViewHoder.explanationTextView = (TextView) view.findViewById(R.id.content_explanation);
        wordViewHoder.exampleTextView = (TextView) view.findViewById(R.id.content_example);
        return wordViewHoder;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.user_name:
                updataUserName();
                break;
        }
    }

    private void updataUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setTitle("Modify User Name");
        editText.setHint("Type in your new name");
        editText.setPadding(30, 80, 16, 30);
        InputFilter[] filters = {new InputFilter.LengthFilter(10)};
        editText.setFilters(filters);
        builder.setView(editText);
        builder.setPositiveButton(
                "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedpreference.saveString("username", editText.getText().toString());
                        usernametextview.setText(editText.getText().toString());
                    }
                }
        );
        builder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onLongClick(View v) {
        Word word = null;
        String tag = (String) v.getTag();
        if (tag.equals("today")) {
            word = tw;
        } else if (tag.equals("yesterday")) {
            word = yw;
        }
        List<FavoriteWord> words = DataSupport.where("word = ?", word.getWord()).find(FavoriteWord.class);
        if (!words.isEmpty()) {
            ToastUtils.showShort("Already in your favorite");
            return true;
        }

        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setTitle("Add to which list?");
        materialDialog.setMessage("Press a button to confirm");

        if (tag.equals("today")) {
            materialDialog.setNegativeButton("Memorized", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveWord0(materialDialog, tw);
                        }
                    }
            );
            materialDialog.setPositiveButton("Favorite", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveWord(materialDialog, tw);
                        }
                    }
            );
            materialDialog.show();
        } else if (tag.equals("yesterday")) {
            materialDialog.setNegativeButton("Memorized", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveWord0(materialDialog, tw);
                        }
                    }
            );
            materialDialog.setPositiveButton("Favorite", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveWord(materialDialog, yw);
                        }
                    }
            );
            materialDialog.show();
        }
        return false;
    }

    private void saveWord(MaterialDialog materialDialog, Word word) {
        ToastUtils.showShort("Add to Favorite");
        materialDialog.dismiss();
        FavoriteWord favoriteWord = new FavoriteWord(word);
        favoriteWord.save();
    }

    private void saveWord0(MaterialDialog materialDialog, Word word) {
        ToastUtils.showShort("Add to Memorized");
        materialDialog.dismiss();
        word.save();
    }

    private void onRefresh(boolean change) {
        todaybar.setVisibility(View.VISIBLE);
        Parcel data = Parcel.obtain();
        data.writeInt(wordchangetimes);
        Bundle bundle = new Bundle();
        bundle.putBoolean("change", change);
        data.writeBundle(bundle);
        Parcel reply = Parcel.obtain();

        try {
            localbinder.transact(
                    IBinder.LAST_CALL_TRANSACTION, data,
                    reply, 0
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTurn() {
        ToastUtils.showShort("refreshing...");
        onRefresh(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            menudrawer.toggleMenu();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            menudrawer.toggleMenu();
            ExitDoubleClick.getInstance(this).doDoubleClick(1500, getString(R.string.double_click_exit));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * on share item click
     */
    public void onClickShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    @Override
    protected void onMenuItemClicked(int position, Item item) {
        String title = item.title;
        if (title.equals("Contact")) {
            onClickShare();
        } else if (title.equals("Search")){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        } else if (title.equals("About")) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (title.equals("Memorized")) {
            Intent intent = new Intent(MainActivity.this, WordListActivity.class);
            intent.putExtra("title", "Memorized");
            startActivity(intent);
        } else if (title.equals("My Favorite")) {
            Intent intent = new Intent(MainActivity.this, WordListActivity.class);
            intent.putExtra("title", "My Favorite");
            startActivity(intent);
        } else if (title.equals("Settings")) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        } else if (title.equals(username) || title.equals("Login/Register")) {
            updataUserName();
            ToastUtils.showLong("Update next time :)");
        } else if (title.equals("Exit")) {
            menudrawer.closeMenu();
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), item.title + " Still coding~", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getDragMode() {
        return menudrawer.MENU_DRAG_CONTENT;
    }

    @Override
    protected Position getDrawerPosition() {
        if (sharedpreference != null && sharedpreference.getBoolean("hand_switch")) {
            return Position.START;
        }
        return Position.END;
    }

}