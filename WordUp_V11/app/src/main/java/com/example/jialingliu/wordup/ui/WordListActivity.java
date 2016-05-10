package com.example.jialingliu.wordup.ui;

import android.app.ActionBar;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.entities.MyAdapter;
import com.example.jialingliu.wordup.entities.MyAdapter0;
import com.example.jialingliu.wordup.model.FavoriteWord;
import com.example.jialingliu.wordup.model.Word;
import com.example.jialingliu.wordup.ui.actionbar.AlphaForegroundColorSpan;
import com.example.jialingliu.wordup.ui.actionbar.KenBurnsView;
import com.example.jialingliu.wordup.util.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jialingliu on 4/14/16.
 */
public class WordListActivity extends BaseActivity {

    private int actionbarcolor;
    private int actionbarheight;
    private int headerheight;
    private int minheadertrans;
    private ListView listview;
    private ImageView headerlogo;
    private View header;
    private View placeholderview;
    private AccelerateDecelerateInterpolator interpolator;

    private RectF rect1 = new RectF();
    private RectF rect2 = new RectF();

    private AlphaForegroundColorSpan foregroundcolorspan;
    private SpannableString spannablestr;

    private TypedValue typedvalue = new TypedValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        interpolator = new AccelerateDecelerateInterpolator();
        headerheight = getResources().getDimensionPixelSize(R.dimen.header_height);
        minheadertrans = -headerheight + getActionBarHeight();

        setContentView(R.layout.activity_wordlist);

        listview = (ListView) findViewById(R.id.listview);
        header = findViewById(R.id.header);
        KenBurnsView headerpic = (KenBurnsView) findViewById(R.id.header_picture);
        headerpic.setResourceIds(R.drawable.picture0, R.drawable.picture1);
        headerlogo = (ImageView) findViewById(R.id.header_logo);

        actionbarcolor = getResources().getColor(R.color.actionbar_title_color);

        spannablestr = new SpannableString(getString(R.string.noboringactionbar_title));
        foregroundcolorspan = new AlphaForegroundColorSpan(actionbarcolor);

        setupActionBar();
        setupListView();
    }

    List<FavoriteWord> wordlist = null;
    List<Word> wordlist0 = null;
    private void setupListView() {
        String title = getIntent().getStringExtra("title");
        System.out.println("-->" + title);
        ArrayList<String> data = new ArrayList<String>();
        if (title.equals("Memorized")) {
            wordlist0 = DataSupport.findAll(Word.class);
            for (Word word : wordlist0) {
                data.add(word.getWord() + " " + word.getSpeech() + " " + word.getExplanation());
            }
            Collections.reverse(data);
        } else if (title.equals("My Favorite")) {
            wordlist = DataSupport.findAll(FavoriteWord.class);
            for (FavoriteWord word : wordlist) {
                data.add(word.getWord() + " " + word.getSpeech() + " " + word.getExplanation());
            }
            Collections.reverse(data);
        }

        placeholderview = getLayoutInflater().inflate(R.layout.view_header_placeholder, listview, false);
        listview.addHeaderView(placeholderview);

        final MyAdapter ma = new MyAdapter(this, wordlist);
        final MyAdapter0 ma0 = new MyAdapter0(this, wordlist0);

        if (title.equals("Memorized")) {
            Collections.reverse(wordlist0);
            listview.setAdapter(ma0);
        } else {
            Collections.reverse(wordlist);
            listview.setAdapter(ma);
        }
        final List<FavoriteWord> finalWordList = wordlist;
        final List<Word> finalWordList0 = wordlist0;

        if (title.equals("My Favorite")){
            listview.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            final MaterialDialog m = new MaterialDialog(WordListActivity.this);
                            m.setTitle("Put " + finalWordList.get(position - 1).getWord() + " to Memorized for later review?");
                            m.setMessage("Press Ok to confirm");

                            m.setNegativeButton("Cancel",new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            m.dismiss();
                                        }
                                    }
                            );
                            m.setPositiveButton("Ok",new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ToastUtils.showShort("Find it in Memorized");
                                            FavoriteWord wordmemorized = DataSupport.find(FavoriteWord.class, finalWordList.get(position - 1).getId());
                                            Word wordtemp = new Word(wordmemorized.getWord(),wordmemorized.getPhonetic(),wordmemorized.getSpeech(),wordmemorized.getExplanation(),wordmemorized.getExample());
                                            wordtemp.save();
                                            DataSupport.delete(FavoriteWord.class, finalWordList.get(position - 1).getId());
                                            wordlist.remove(position - 1);
                                            ma.notifyDataSetChanged();
                                            m.dismiss();
                                        }
                                    }
                            );
                            m.show();
                            return false;
                        }
                    }
            );
        }else if (title.equals("Memorized")){
            listview.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            final MaterialDialog m = new MaterialDialog(WordListActivity.this);
                            m.setTitle("Are you sure to delete it?");
                            m.setMessage("Press Ok to confirm");

                            m.setNegativeButton("Cancel",new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            m.dismiss();
                                        }
                                    }
                            );
                            m.setPositiveButton("Ok",new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ToastUtils.showShort("Deleted");
                                            DataSupport.delete(Word.class, finalWordList0.get(position - 1).getId());
                                            wordlist0.remove(position - 1);
                                            ma0.notifyDataSetChanged();
                                            m.dismiss();
                                        }
                                    }
                            );
                            m.show();
                            return false;
                        }
                    }
            );
        }

        listview.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int scrollY = getScrollY();
                        header.setTranslationY(Math.max(-scrollY, minheadertrans));
                        float ratio = clamp(header.getTranslationY() / minheadertrans, 0.0f, 1.0f);
                        interpolate(headerlogo, getActionBarIconView(), interpolator.getInterpolation(ratio));
                        setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
                    }
                }
        );
    }

    private void setTitleAlpha(float al) {
        foregroundcolorspan.setAl(al);
        spannablestr.setSpan(foregroundcolorspan, 0, spannablestr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        String title = getIntent().getStringExtra("title");
        getActionBar().setTitle(title);
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(rect1, view1);
        getOnScreenRect(rect2, view2);

        float scaleX = 1.0F + interpolation * (rect2.width() / rect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (rect2.height() / rect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (rect2.left + rect2.right - rect1.left - rect1.right));
        float translationY = 0.5F * (interpolation * (rect2.top + rect2.bottom - rect1.top - rect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - header.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    public int getScrollY() {
        View c = listview.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = listview.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = placeholderview.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();

        actionBar.setIcon(R.drawable.ic_transparent);
    }

    private ImageView getActionBarIconView() {
        return (ImageView) findViewById(android.R.id.home);
    }

    public int getActionBarHeight() {
        if (actionbarheight != 0) {
            return actionbarheight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, typedvalue, true);
        actionbarheight = TypedValue.complexToDimensionPixelSize(typedvalue.data, getResources().getDisplayMetrics());
        return actionbarheight;
    }


}
