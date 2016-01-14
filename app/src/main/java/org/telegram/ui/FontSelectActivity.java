/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2015.
 */

package org.telegram.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import com.negahetazehco.cafetelegram.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextFontCell;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FontSelectActivity extends BaseFragment {
    private BaseFragmentAdapter listAdapter;
    private ListView listView;
    private boolean searchWas;
    private boolean searching;
    private BaseFragmentAdapter searchListViewAdapter;
    private TextView emptyTextView;

    private Timer searchTimer;
    public ArrayList<LocaleController.FontInfo> searchResult;

    @Override
    public View createView(final Context context) {
        searching = false;
        searchWas = false;

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("FontFamily", R.string.FontFamily));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                searching = true;
            }

            @Override
            public void onSearchCollapse() {
                search(null);
                searching = false;
                searchWas = false;
                if (listView != null) {
                    emptyTextView.setVisibility(View.GONE);
                    listView.setAdapter(listAdapter);
                }
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                search(text);
                if (text.length() != 0) {
                    searchWas = true;
                    if (listView != null) {
                        listView.setAdapter(searchListViewAdapter);
                    }
                }
            }
        });
        item.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));

        listAdapter = new ListAdapter(context);
        searchListViewAdapter = new SearchAdapter(context);

        fragmentView = new FrameLayout(context);

        LinearLayout emptyTextLayout = new LinearLayout(context);
        emptyTextLayout.setVisibility(View.INVISIBLE);
        emptyTextLayout.setOrientation(LinearLayout.VERTICAL);
        ((FrameLayout) fragmentView).addView(emptyTextLayout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emptyTextLayout.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP;
        emptyTextLayout.setLayoutParams(layoutParams);
        emptyTextLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        emptyTextView = new TextView(context);
        emptyTextView.setTextColor(0xff808080);
        emptyTextView.setTextSize(20);
        emptyTextView.setGravity(Gravity.CENTER);
        emptyTextView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        emptyTextLayout.addView(emptyTextView);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) emptyTextView.getLayoutParams();
        layoutParams1.width = LayoutHelper.MATCH_PARENT;
        layoutParams1.height = LayoutHelper.MATCH_PARENT;
        layoutParams1.weight = 0.5f;
        emptyTextView.setLayoutParams(layoutParams1);

        FrameLayout frameLayout = new FrameLayout(context);
        emptyTextLayout.addView(frameLayout);
        layoutParams1 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams1.width = LayoutHelper.MATCH_PARENT;
        layoutParams1.height = LayoutHelper.MATCH_PARENT;
        layoutParams1.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams1);

        listView = new ListView(context);
        listView.setEmptyView(emptyTextLayout);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setAdapter(listAdapter);
        ((FrameLayout) fragmentView).addView(listView);
        layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        listView.setLayoutParams(layoutParams);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LocaleController.FontInfo localeInfo = null;
                if (searching && searchWas) {
                    if (i >= 0 && i < searchResult.size()) {
                        localeInfo = searchResult.get(i);
                    }
                } else {
                    if (i >= 0 && i < LocaleController.getInstance().sortedFonts.size()) {
                        localeInfo = LocaleController.getInstance().sortedFonts.get(i);
                    }
                }
                if (localeInfo != null) {
                    LocaleController.getInstance().applyFont(localeInfo, context);
                    parentLayout.rebuildAllFragmentViews(false);
                }
                finishFragment();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_TOUCH_SCROLL && searching && searchWas) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void search(final String query) {
        if (query == null) {
            searchResult = null;
        } else {
            try {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        searchTimer.cancel();
                        searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e("tmessages", e);
                    }
                    processSearch(query);
                }
            }, 100, 300);
        }
    }

    private void processSearch(final String query) {
        Utilities.searchQueue.postRunnable(new Runnable() {
            @Override
            public void run() {

                String q = query.trim().toLowerCase();
                if (q.length() == 0) {
                    updateSearchResults(new ArrayList<LocaleController.FontInfo>());
                    return;
                }
                long time = System.currentTimeMillis();
                ArrayList<LocaleController.FontInfo> resultArray = new ArrayList<>();

                for (LocaleController.FontInfo c : LocaleController.getInstance().sortedFonts) {
                    if (c.name.toLowerCase().startsWith(query)) {
                        resultArray.add(c);
                    }
                }

                updateSearchResults(resultArray);
            }
        });
    }

    private void updateSearchResults(final ArrayList<LocaleController.FontInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                searchResult = arrCounties;
                searchListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private class SearchAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public SearchAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            if (searchResult == null) {
                return 0;
            }
            return searchResult.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LocaleController.FontInfo localeInfo = LocaleController.getInstance().sortedFonts.get(i);
            if (view == null) {
                view = new TextFontCell(mContext, localeInfo.value);
            }

            LocaleController.FontInfo c = searchResult.get(i);
            ((TextFontCell) view).setText(c.name, i != searchResult.size() - 1);

            return view;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return searchResult == null || searchResult.size() == 0;
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            if (LocaleController.getInstance().sortedFonts == null) {
                return 0;
            }
            return LocaleController.getInstance().sortedFonts.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LocaleController.FontInfo localeInfo = LocaleController.getInstance().sortedFonts.get(i);
            if (view == null) {
                view = new TextFontCell(mContext, localeInfo.value);
            }
            ((TextFontCell) view).setText(localeInfo.name, i != LocaleController.getInstance().sortedFonts.size() - 1);
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return LocaleController.getInstance().sortedFonts == null || LocaleController.getInstance().sortedFonts.size() == 0;
        }
    }
}
