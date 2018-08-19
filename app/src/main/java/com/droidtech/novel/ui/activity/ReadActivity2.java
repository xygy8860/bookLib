/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.droidtech.novel.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.ListPopupWindow;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseActivity;
import com.droidtech.novel.base.Constant;
import com.droidtech.novel.bean.BookMixAToc;
import com.droidtech.novel.bean.BookSource;
import com.droidtech.novel.bean.ChapterRead;
import com.droidtech.novel.bean.Recommend;
import com.droidtech.novel.bean.support.BookMark;
import com.droidtech.novel.bean.support.DownloadMessage;
import com.droidtech.novel.bean.support.DownloadProgress;
import com.droidtech.novel.bean.support.DownloadQueue;
import com.droidtech.novel.bean.support.ReadTheme;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerBookComponent;
import com.droidtech.novel.manager.CacheManager;
import com.droidtech.novel.manager.CollectionsManager;
import com.droidtech.novel.manager.EventManager;
import com.droidtech.novel.manager.SettingManager;
import com.droidtech.novel.manager.ThemeManager;
import com.droidtech.novel.service.DownloadBookService;
import com.droidtech.novel.ui.adapter.BookMarkAdapter;
import com.droidtech.novel.ui.adapter.TocListAdapter;
import com.droidtech.novel.ui.contract.BookReadContract;
import com.droidtech.novel.ui.easyadapter.ReadThemeAdapter;
import com.droidtech.novel.ui.presenter.BookReadPresenter;
import com.droidtech.novel.utils.FileUtils;
import com.droidtech.novel.utils.FormatUtils;
import com.droidtech.novel.utils.LogUtils;
import com.droidtech.novel.utils.ScreenUtils;
import com.droidtech.novel.utils.SharedPreferencesUtil;
import com.droidtech.novel.utils.ToastUtils;
import com.droidtech.novel.utils.VarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by lfh on 2016/9/18.
 */
public class ReadActivity2 extends BaseActivity implements BookReadContract.View {

    @BindView(R2.id.ivBack)
    ImageView mIvBack;
    @BindView(R2.id.tvBookReadReading)
    TextView mTvBookReadReading;
    @BindView(R2.id.tvBookReadCommunity)
    TextView mTvBookReadCommunity;
    @BindView(R2.id.tvBookReadIntroduce)
    TextView mTvBookReadChangeSource;
    @BindView(R2.id.tvBookReadSource)
    TextView mTvBookReadSource;

    @BindView(R2.id.flReadWidget)
    FrameLayout flReadWidget;

    // 章节信息
    @BindView(R2.id.chapter_scrollview)
    ScrollView mChapterScrollView;
    @BindView(R2.id.chapter_title)
    TextView mChapterTitle;
    @BindView(R2.id.chapter_content)
    TextView mChapterContent;
    @BindView(R2.id.chapter_pre)
    TextView mChapterPre;
    @BindView(R2.id.chapter_next)
    TextView mChapterNext;

    @BindView(R2.id.llBookReadTop)
    LinearLayout mLlBookReadTop;
    @BindView(R2.id.tvBookReadTocTitle)
    TextView mTvBookReadTocTitle;
    @BindView(R2.id.tvBookReadMode)
    TextView mTvBookReadMode;
    @BindView(R2.id.tvBookReadSettings)
    TextView mTvBookReadSettings;
    @BindView(R2.id.tvBookReadDownload)
    TextView mTvBookReadDownload;
    @BindView(R2.id.tvBookReadToc)
    TextView mTvBookReadToc;
    @BindView(R2.id.llBookReadBottom)
    LinearLayout mLlBookReadBottom;
    @BindView(R2.id.rlBookReadRoot)
    RelativeLayout mRlBookReadRoot;
    @BindView(R2.id.tvDownloadProgress)
    TextView mTvDownloadProgress;

    @BindView(R2.id.rlReadAaSet)
    LinearLayout rlReadAaSet;
    @BindView(R2.id.ivBrightnessMinus)
    ImageView ivBrightnessMinus;
    @BindView(R2.id.seekbarLightness)
    SeekBar seekbarLightness;
    @BindView(R2.id.ivBrightnessPlus)
    ImageView ivBrightnessPlus;
    @BindView(R2.id.tvFontsizeMinus)
    TextView tvFontsizeMinus;
    @BindView(R2.id.seekbarFontSize)
    SeekBar seekbarFontSize;
    @BindView(R2.id.tvFontsizePlus)
    TextView tvFontsizePlus;

    @BindView(R2.id.rlReadMark)
    LinearLayout rlReadMark;
    @BindView(R2.id.tvAddMark)
    TextView tvAddMark;
    @BindView(R2.id.lvMark)
    ListView lvMark;

    @BindView(R2.id.cbVolume)
    CheckBox cbVolume;
    @BindView(R2.id.cbAutoBrightness)
    CheckBox cbAutoBrightness;
    @BindView(R2.id.gvTheme)
    GridView gvTheme;

    private View decodeView;

    @Inject
    BookReadPresenter mPresenter;

    private List<BookMixAToc.mixToc.Chapters> mChapterList = new ArrayList<>();
    private ListPopupWindow mTocListPopupWindow;
    private TocListAdapter mTocListAdapter;

    private List<BookMark> mMarkList;
    private BookMarkAdapter mMarkAdapter;

    private int currentChapter = 1;

    /**
     * 是否开始阅读章节
     **/
    private boolean startRead = false;
    //private BaseReadView mPageWidget;
    private int curTheme = -1;
    private List<ReadTheme> themes;
    private ReadThemeAdapter gvAdapter;
    private Receiver receiver = new Receiver();
    private IntentFilter intentFilter = new IntentFilter();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public static final String INTENT_BEAN = "recommendBooksBean";
    public static final String INTENT_SD = "isFromSD";

    private Recommend.RecommendBooks recommendBooks;
    private String bookId;

    private boolean isAutoLightness = false; // 记录其他页面是否自动调整亮度
    private boolean isFromSD = false;

    private String charset = "UTF-8";

    //添加收藏需要，所以跳转的时候传递整个实体类
    public static void startActivity(Context context, Recommend.RecommendBooks recommendBooks) {
        startActivity(context, recommendBooks, false);
    }

    public static void startActivity(Context context, Recommend.RecommendBooks recommendBooks, boolean isFromSD) {
        context.startActivity(new Intent(context, ReadActivity2.class)
                .putExtra(INTENT_BEAN, recommendBooks)
                .putExtra(INTENT_SD, isFromSD));
    }

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        statusBarColor = ContextCompat.getColor(this, R.color.reader_menu_bg_color);
        return R.layout.activity_read;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initDatas(boolean savedInstanceState) {
        recommendBooks = (Recommend.RecommendBooks) getIntent().getSerializableExtra(INTENT_BEAN);
        bookId = recommendBooks._id;
        isFromSD = getIntent().getBooleanExtra(INTENT_SD, false);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            String filePath = Uri.decode(getIntent().getDataString().replace("file://", ""));
            String fileName;
            if (filePath.lastIndexOf(".") > filePath.lastIndexOf("/")) {
                fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
            } else {
                fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            }

            CollectionsManager.getInstance().remove(fileName);
            // 转存
            File desc = FileUtils.createWifiTranfesFile(fileName);
            FileUtils.fileChannelCopy(new File(filePath), desc);
            // 建立
            recommendBooks = new Recommend.RecommendBooks();
            recommendBooks.isFromSD = true;
            recommendBooks._id = fileName;
            recommendBooks.title = fileName;

            isFromSD = true;
        }
        EventBus.getDefault().register(this);
        showDialog();

        mTvBookReadTocTitle.setText(recommendBooks.title);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);

        CollectionsManager.getInstance().setRecentReadingTime(bookId);
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //延迟1秒刷新书架
                        EventManager.refreshCollectionList();
                    }
                });
    }

    @Override
    public void configViews() {
        hideStatusBar();
        decodeView = getWindow().getDecorView();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLlBookReadTop.getLayoutParams();
        params.topMargin = ScreenUtils.getStatusBarHeight(this) - 2;
        mLlBookReadTop.setLayoutParams(params);

        initPagerWidget();
        initAASet();
        initTocList();

        mPresenter.attachView(this);
        // 本地收藏  直接打开
        if (isFromSD) {
            BookMixAToc.mixToc.Chapters chapters = new BookMixAToc.mixToc.Chapters();
            chapters.title = recommendBooks.title;
            mChapterList.add(chapters);
            showChapterRead(null, currentChapter);
            //本地书籍隐藏社区、简介、缓存按钮
            gone(mTvBookReadCommunity, mTvBookReadChangeSource, mTvBookReadDownload);
            return;
        }
        mPresenter.getBookMixAToc(bookId, "chapters");
    }

    private void initTocList() {
        mTocListAdapter = new TocListAdapter(this, mChapterList, bookId, currentChapter);
        mTocListPopupWindow = new ListPopupWindow(this);
        mTocListPopupWindow.setAdapter(mTocListAdapter);
        mTocListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mTocListPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mTocListPopupWindow.setAnchorView(mLlBookReadTop);
        mTocListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTocListPopupWindow.dismiss();
                currentChapter = position + 1;
                mTocListAdapter.setCurrentChapter(currentChapter);
                startRead = false;
                showDialog();
                readCurrentChapter();
                hideReadBar();
            }
        });
        mTocListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                gone(mTvBookReadTocTitle);
                visible(mTvBookReadReading, mTvBookReadCommunity, mTvBookReadChangeSource);
            }
        });
    }

    private void initAASet() {
        curTheme = SettingManager.getInstance().getReadTheme();
        ThemeManager.setReaderTheme(curTheme, mRlBookReadRoot);

        seekbarFontSize.setMax(10);
        //int fontSizePx = SettingManager.getInstance().getReadFontSize(bookId);
        int fontSizePx = SettingManager.getInstance().getReadFontSize();
        int progress = (int) ((ScreenUtils.pxToDpInt(fontSizePx) - 12) / 1.7f);
        seekbarFontSize.setProgress(progress);
        seekbarFontSize.setOnSeekBarChangeListener(new SeekBarChangeListener());

        seekbarLightness.setMax(100);
        seekbarLightness.setOnSeekBarChangeListener(new SeekBarChangeListener());
        seekbarLightness.setProgress(SettingManager.getInstance().getReadBrightness());
        isAutoLightness = ScreenUtils.isAutoBrightness(this);
        if (SettingManager.getInstance().isAutoBrightness()) {
            startAutoLightness();
        } else {
            stopAutoLightness();
        }

        cbVolume.setChecked(SettingManager.getInstance().isVolumeFlipEnable());
        cbVolume.setOnCheckedChangeListener(new ChechBoxChangeListener());

        cbAutoBrightness.setChecked(SettingManager.getInstance().isAutoBrightness());
        cbAutoBrightness.setOnCheckedChangeListener(new ChechBoxChangeListener());

        gvAdapter = new ReadThemeAdapter(this, (themes = ThemeManager.getReaderThemeData(curTheme)), curTheme);
        gvTheme.setAdapter(gvAdapter);
        gvTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < themes.size() - 1) {
                    changedMode(false, position);
                } else {
                    changedMode(true, position);
                }
            }
        });
    }

    private void initPagerWidget() {
        registerReceiver(receiver, intentFilter);
        if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
            int color = ContextCompat.getColor(mContext, R.color.chapter_content_night);
            mChapterContent.setTextColor(color);
            mChapterTitle.setTextColor(color);
        }

        float size = SharedPreferencesUtil.getInstance().getFloat("size", 0);
        if (size >= 12) {
            mChapterContent.setTextSize(size);
            mChapterTitle.setTextSize(size);
        }

        mChapterContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReadBar();
            }
        });

        mChapterNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentChapter < mChapterList.size() - 1) {
                    mTocListPopupWindow.dismiss();
                    currentChapter = currentChapter - 1;
                    mTocListAdapter.setCurrentChapter(currentChapter);
                    startRead = false;
                    showDialog();
                    readCurrentChapter();
                    hideReadBar();

                    if (VarUtils.listener != null) {
                        VarUtils.listener.click(ReadActivity2.this);
                    }
                } else {
                    ToastUtils.showLongToast("没有下一章了");
                }
            }
        });

        mChapterPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentChapter > 1) {
                    mTocListPopupWindow.dismiss();
                    currentChapter = currentChapter - 1;
                    mTocListAdapter.setCurrentChapter(currentChapter);
                    startRead = false;
                    showDialog();
                    readCurrentChapter();
                    hideReadBar();

                    if (VarUtils.listener != null) {
                        VarUtils.listener.click(ReadActivity2.this);
                    }
                } else {
                    ToastUtils.showLongToast("已经是第一章了");
                }
            }
        });
    }

    /**
     * 加载章节列表
     *
     * @param list
     */
    @Override
    public void showBookToc(List<BookMixAToc.mixToc.Chapters> list) {
        mChapterList.clear();
        mChapterList.addAll(list);

        readCurrentChapter();
    }

    /**
     * 获取当前章节。章节文件存在则直接阅读，不存在则请求
     */
    public void readCurrentChapter() {
        if (CacheManager.getInstance().getChapterFile(bookId, currentChapter) != null) {
            showChapterRead(null, currentChapter);
        } else {
            mPresenter.getChapterRead(mChapterList.get(currentChapter - 1).link, currentChapter);
        }
    }

    @Override
    public synchronized void showChapterRead(ChapterRead.Chapter data, int chapter) { // 加载章节内容
        if (data != null) {
            CacheManager.getInstance().saveChapterFile(bookId, chapter, data);
        }

        if (!startRead) {
            startRead = true;
            setContent(chapter);
            hideDialog();
        }

    }

    private void setContent(int chapter) {

        if (chapter == 1) {
            int oldChapter = SharedPreferencesUtil.getInstance().getInt(bookId + "chapter", 1);
            if (oldChapter > chapter) {
                chapter = oldChapter;
            }
        }

        currentChapter = chapter;

        File file = FileUtils.getChapterFile(bookId, chapter);
        if (file != null && file.length() > 10) {
            // 解决空文件造成编码错误的问题
            charset = FileUtils.getCharset(file.getAbsolutePath());
            String txt = null;
            try {
                txt = new String(FileUtils.getBytesFromFile(file), charset);

                mChapterTitle.setText(mChapterList.get(chapter - 1).title);
                mChapterContent.setText(txt);

                // 保存阅读进度
                SharedPreferencesUtil.getInstance().putInt(bookId + "chapter", chapter);

                // 滑动到顶部
                mChapterScrollView.scrollTo(0, 0);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void netError(int chapter) {
        hideDialog();//防止因为网络问题而出现dialog不消失
        if (Math.abs(chapter - currentChapter) <= 1) {
            ToastUtils.showToast(R.string.net_error);
        }
    }

    @Override
    public void showError() {
        hideDialog();
    }

    @Override
    public void complete() {
        hideDialog();
    }

    private synchronized void hideReadBar() {
        gone(mTvDownloadProgress, mLlBookReadBottom, mLlBookReadTop, rlReadAaSet, rlReadMark);
        hideStatusBar();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    private synchronized void showReadBar() { // 显示工具栏
        visible(mLlBookReadBottom, mLlBookReadTop);
        showStatusBar();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private synchronized void toggleReadBar() { // 切换工具栏 隐藏/显示 状态
        if (isVisible(mLlBookReadTop)) {
            hideReadBar();
        } else {
            showReadBar();
        }
    }

    /***************Title Bar*****************/

    @OnClick(R2.id.ivBack)
    public void onClickBack() {
        if (mTocListPopupWindow.isShowing()) {
            mTocListPopupWindow.dismiss();
        } else {
            finish();
        }
    }

    @OnClick(R2.id.tvBookReadReading)
    public void readBook() {
        gone(rlReadAaSet, rlReadMark);
        ToastUtils.showToast("正在拼命开发中...");
    }

    @OnClick(R2.id.tvBookReadCommunity)
    public void onClickCommunity() {
        gone(rlReadAaSet, rlReadMark);
        BookDetailCommunityActivity.startActivity(this, bookId, mTvBookReadTocTitle.getText().toString(), 0);
    }

    @OnClick(R2.id.tvBookReadIntroduce)
    public void onClickIntroduce() {
        gone(rlReadAaSet, rlReadMark);
        BookDetailActivity.startActivity(mContext, bookId);
    }

    @OnClick(R2.id.tvBookReadSource)
    public void onClickSource() {
        BookSourceActivity.start(this, bookId, 1);
    }

    /***************Bottom Bar*****************/

    @OnClick(R2.id.tvBookReadMode)
    public void onClickChangeMode() { // 日/夜间模式切换
        gone(rlReadAaSet, rlReadMark);

        boolean isNight = !SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false);
        changedMode(isNight, -1);
    }

    private void changedMode(boolean isNight, int position) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, isNight);
        AppCompatDelegate.setDefaultNightMode(isNight ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        if (position >= 0) {
            curTheme = position;
        } else {
            curTheme = SettingManager.getInstance().getReadTheme();
        }
        gvAdapter.select(curTheme);

        int color = ContextCompat.getColor(mContext, isNight ? R.color.chapter_content_night : R.color.chapter_content_day);
        mChapterContent.setTextColor(color);
        mChapterTitle.setTextColor(color);

        mTvBookReadMode.setText(getString(isNight ? R.string.book_read_mode_day_manual_setting
                : R.string.book_read_mode_night_manual_setting));
        Drawable drawable = ContextCompat.getDrawable(this, isNight ? R.drawable.ic_menu_mode_day_manual
                : R.drawable.ic_menu_mode_night_manual);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mTvBookReadMode.setCompoundDrawables(null, drawable, null, null);

        ThemeManager.setReaderTheme(curTheme, mRlBookReadRoot);
    }

    @OnClick(R2.id.tvBookReadSettings)
    public void setting() {
        if (isVisible(mLlBookReadBottom)) {
            if (isVisible(rlReadAaSet)) {
                gone(rlReadAaSet);
            } else {
                visible(rlReadAaSet);
                gone(rlReadMark);
            }
        }
    }

    @OnClick(R2.id.tvBookReadDownload)
    public void downloadBook() {
        gone(rlReadAaSet);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("缓存多少章？")
                .setItems(new String[]{"后面五十章", "后面全部", "全部"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                DownloadBookService.post(new DownloadQueue(bookId, mChapterList, currentChapter + 1, currentChapter + 50));
                                break;
                            case 1:
                                DownloadBookService.post(new DownloadQueue(bookId, mChapterList, currentChapter + 1, mChapterList.size()));
                                break;
                            case 2:
                                DownloadBookService.post(new DownloadQueue(bookId, mChapterList, 1, mChapterList.size()));
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }

    @OnClick(R2.id.tvBookMark)
    public void onClickMark() {
        if (isVisible(mLlBookReadBottom)) {
            if (isVisible(rlReadMark)) {
                gone(rlReadMark);
            } else {
                gone(rlReadAaSet);

                updateMark();

                visible(rlReadMark);
            }
        }
    }

    @OnClick(R2.id.tvBookReadToc)
    public void onClickToc() {
        gone(rlReadAaSet, rlReadMark);
        if (!mTocListPopupWindow.isShowing()) {
            visible(mTvBookReadTocTitle);
            gone(mTvBookReadReading, mTvBookReadCommunity, mTvBookReadChangeSource);
            mTocListPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            mTocListPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mTocListPopupWindow.show();
            mTocListAdapter.setCurrentChapter(currentChapter);
            mTocListPopupWindow.setSelection(currentChapter - 1);
            mTocListPopupWindow.getListView().setFastScrollEnabled(true);
        }
    }

    /***************Setting Menu*****************/

    @OnClick(R2.id.ivBrightnessMinus)
    public void brightnessMinus() {
        int curBrightness = SettingManager.getInstance().getReadBrightness();
        if (curBrightness > 2 && !SettingManager.getInstance().isAutoBrightness()) {
            seekbarLightness.setProgress((curBrightness = curBrightness - 2));
            ScreenUtils.setScreenBrightness(curBrightness, ReadActivity2.this);
            SettingManager.getInstance().saveReadBrightness(curBrightness);
        }
    }

    @OnClick(R2.id.ivBrightnessPlus)
    public void brightnessPlus() {
        int curBrightness = SettingManager.getInstance().getReadBrightness();
        if (curBrightness < 99 && !SettingManager.getInstance().isAutoBrightness()) {
            seekbarLightness.setProgress((curBrightness = curBrightness + 2));
            ScreenUtils.setScreenBrightness(curBrightness, ReadActivity2.this);
            SettingManager.getInstance().saveReadBrightness(curBrightness);
        }
    }

    @OnClick(R2.id.tvFontsizeMinus)
    public void fontsizeMinus() {
        calcFontSize(seekbarFontSize.getProgress() - 1);
    }

    @OnClick(R2.id.tvFontsizePlus)
    public void fontsizePlus() {
        calcFontSize(seekbarFontSize.getProgress() + 1);
    }

    @OnClick(R2.id.tvClear)
    public void clearBookMark() {
        SettingManager.getInstance().clearBookMarks(bookId);

        updateMark();
    }

    /***************Book Mark*****************/

    @OnClick(R2.id.tvAddMark)
    public void addBookMark() {
        /*int[] readPos = mChapterContent.getReadPos();
        BookMark mark = new BookMark();
        mark.chapter = readPos[0];
        mark.startPos = readPos[1];
        mark.endPos = readPos[2];
        if (mark.chapter >= 1 && mark.chapter <= mChapterList.size()) {
            mark.title = mChapterList.get(mark.chapter - 1).title;
        }
        mark.desc = mPageWidget.getHeadLine();
        if (SettingManager.getInstance().addBookMark(bookId, mark)) {
            ToastUtils.showSingleToast("添加书签成功");
            updateMark();
        } else {
            ToastUtils.showSingleToast("书签已存在");
        }*/

        ToastUtils.showSingleToast("书签暂不支持");
    }

    private void updateMark() {
        if (mMarkAdapter == null) {
            mMarkAdapter = new BookMarkAdapter(this, new ArrayList<BookMark>());
            lvMark.setAdapter(mMarkAdapter);
            lvMark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BookMark mark = mMarkAdapter.getData(position);
                    if (mark != null) {
                        //mPageWidget.setPosition(new int[]{mark.chapter, mark.startPos, mark.endPos});
                        hideReadBar();
                    } else {
                        ToastUtils.showSingleToast("书签无效");
                    }
                }
            });
        }
        mMarkAdapter.clear();

        mMarkList = SettingManager.getInstance().getBookMarks(bookId);
        if (mMarkList != null && mMarkList.size() > 0) {
            Collections.reverse(mMarkList);
            mMarkAdapter.addAll(mMarkList);
        }
    }

    /***************Event*****************/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDownProgress(DownloadProgress progress) {
        if (bookId.equals(progress.bookId)) {
            if (isVisible(mLlBookReadBottom)) { // 如果工具栏显示，则进度条也显示
                visible(mTvDownloadProgress);
                // 如果之前缓存过，就给提示
                mTvDownloadProgress.setText(progress.message);
            } else {
                gone(mTvDownloadProgress);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadMessage(final DownloadMessage msg) {
        if (isVisible(mLlBookReadBottom)) { // 如果工具栏显示，则进度条也显示
            if (bookId.equals(msg.bookId)) {
                visible(mTvDownloadProgress);
                mTvDownloadProgress.setText(msg.message);
                if (msg.isComplete) {
                    mTvDownloadProgress.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gone(mTvDownloadProgress);
                        }
                    }, 2500);
                }
            }
        }
    }

    /**
     * 显示加入书架对话框
     *
     * @param bean
     */
    private void showJoinBookShelfDialog(final Recommend.RecommendBooks bean) {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.book_read_add_book))
                .setMessage(getString(R.string.book_read_would_you_like_to_add_this_to_the_book_shelf))
                .setPositiveButton(getString(R.string.book_read_join_the_book_shelf), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        bean.recentReadingTime = FormatUtils.getCurrentTimeString(FormatUtils.FORMAT_DATE_TIME);
                        CollectionsManager.getInstance().add(bean);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.book_read_not), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    BookSource bookSource = (BookSource) data.getSerializableExtra("source");
                    bookId = bookSource._id;
                }
                //mPresenter.getBookMixAToc(bookId, "chapters");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mTocListPopupWindow != null && mTocListPopupWindow.isShowing()) {
                    mTocListPopupWindow.dismiss();
                    gone(mTvBookReadTocTitle);
                    visible(mTvBookReadReading, mTvBookReadCommunity, mTvBookReadChangeSource);
                    return true;
                } else if (isVisible(rlReadAaSet)) {
                    gone(rlReadAaSet);
                    return true;
                } else if (isVisible(mLlBookReadBottom)) {
                    hideReadBar();
                    return true;
                } else if (!CollectionsManager.getInstance().isCollected(bookId)) {
                    showJoinBookShelfDialog(recommendBooks);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                toggleReadBar();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (SettingManager.getInstance().isVolumeFlipEnable()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (SettingManager.getInstance().isVolumeFlipEnable()) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (SettingManager.getInstance().isVolumeFlipEnable()) {
                mPageWidget.nextPage();
                return true;// 防止翻页有声音
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (SettingManager.getInstance().isVolumeFlipEnable()) {
                mPageWidget.prePage();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventManager.refreshCollectionIcon();
        EventManager.refreshCollectionList();
        EventBus.getDefault().unregister(this);

        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            LogUtils.e("Receiver not registered");
        }

        if (isAutoLightness) {
            ScreenUtils.startAutoBrightness(ReadActivity2.this);
        } else {
            ScreenUtils.stopAutoBrightness(ReadActivity2.this);
        }

        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getId() == seekbarFontSize.getId() && fromUser) {
                calcFontSize(progress);
            } else if (seekBar.getId() == seekbarLightness.getId() && fromUser
                    && !SettingManager.getInstance().isAutoBrightness()) { // 非自动调节模式下 才可调整屏幕亮度
                ScreenUtils.setScreenBrightness(progress, ReadActivity2.this);
                SettingManager.getInstance().saveReadBrightness(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private class ChechBoxChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == cbVolume.getId()) {
                SettingManager.getInstance().saveVolumeFlipEnable(isChecked);
            } else if (buttonView.getId() == cbAutoBrightness.getId()) {
                if (isChecked) {
                    startAutoLightness();
                } else {
                    stopAutoLightness();
                }
            }
        }
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*if (mPageWidget != null) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);
                    mPageWidget.setBattery(100 - level);
                } else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    mPageWidget.setTime(sdf.format(new Date()));
                }
            }*/
        }
    }

    private void startAutoLightness() {
        SettingManager.getInstance().saveAutoBrightness(true);
        ScreenUtils.startAutoBrightness(ReadActivity2.this);
        seekbarLightness.setEnabled(false);
    }

    private void stopAutoLightness() {
        SettingManager.getInstance().saveAutoBrightness(false);
        ScreenUtils.stopAutoBrightness(ReadActivity2.this);
        int value = SettingManager.getInstance().getReadBrightness();
        seekbarLightness.setProgress(value);
        ScreenUtils.setScreenBrightness(value, ReadActivity2.this);
        seekbarLightness.setEnabled(true);
    }

    private void calcFontSize(int progress) {
        // progress range 1 - 10
        if (progress >= 0 && progress <= 10) {
            seekbarFontSize.setProgress(progress);
            float size = 12 + 1.7f * progress;
            mChapterContent.setTextSize(size);
            mChapterTitle.setTextSize(size);

            SharedPreferencesUtil.getInstance().putFloat("size", size);
        }
    }

}
