package neuron.android.com.neuron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import neuron.android.com.neuron.adapters.MainActivityPagerAdapter;
import neuron.android.com.neuron.state.VariableHolder;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int previousTabIndex; //used for updating icons

    private int ic_posts_full;
    private int ic_posts_empty;
    private int ic_home_full;
    private int ic_home_empty;
    private int ic_messages_full;
    private int ic_messages_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VariableHolder.mainActivitySupportFragmentManager = this.getSupportFragmentManager();

        ic_posts_full = R.drawable.ic_posts_full;
        ic_posts_empty = R.drawable.ic_posts_empty;
        ic_home_full = R.drawable.ic_home_full;
        ic_home_empty = R.drawable.ic_home_empty;
        ic_messages_full = R.drawable.ic_messages_full;
        ic_messages_empty = R.drawable.ic_messages_empty;

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        MainActivityPagerAdapter mapa = new MainActivityPagerAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(mapa);

        tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        configureViewPagerAndTabLayout();
    }

    private void configureViewPagerAndTabLayout() {
        /*
        tab 0: posts
        tab 1: home
        tab 2: messages
         */
        tabLayout.getTabAt(0).setText("");
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_posts_empty);

        tabLayout.getTabAt(1).setText("");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home_full);

        tabLayout.getTabAt(2).setText("");
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_messages_empty);

        viewPager.setCurrentItem(1);
        previousTabIndex = viewPager.getCurrentItem();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("[Neuron.MainActivity.configureViewPagerAndTabLayout]: in onTabSelected");
                updateTabIcons(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                System.out.println("[Neuron.MainActivity.configureViewPagerAndTabLayout]: in onTabUnselected");

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                System.out.println("[Neuron.MainActivity.configureViewPagerAndTabLayout]: inOnTabReselected");
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTabIcons(tabLayout.getTabAt(position), false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Updates the icons of the tablayout
     * @param tab
     * @param shouldUncheck Introduced because two listeners have to watch for page changes (viewpager and tablayout)... the second time, this method mustn't uncheck!
     */
    private void updateTabIcons(TabLayout.Tab tab, boolean shouldUncheck) {
        System.out.println("[Neuron.MainActivity.updateTabIcons]: Updating tab icons. Current item = " + viewPager.getCurrentItem());
        switch(viewPager.getCurrentItem()) {
            case 0:
                //posts tab
                tab.setIcon(ic_posts_full);
                break;
            case 1:
                //home tab
                tab.setIcon(ic_home_full);
                break;
            case 2:
                //messages tab
                tab.setIcon(ic_messages_full);
                break;

        }
        if(shouldUncheck == true) {
            uncheckPreviousTab(previousTabIndex);
        }
        previousTabIndex = viewPager.getCurrentItem();
    }

    /**
     * Unchecks the previously checked tab in tablayout
     * @param tabIndex
     */
    private void uncheckPreviousTab(int tabIndex) {
        System.out.println("[Neuron.MainActivity.uncheckPreviousTab]: Unchecking tab with index " + tabIndex);
        switch(tabIndex) {
            case 0:
                //posts tab
                tabLayout.getTabAt(0).setIcon(ic_posts_empty);
                break;
            case 1:
                //home tab
                tabLayout.getTabAt(1).setIcon(ic_home_empty);
                break;
            case 2:
                //messages tab
                tabLayout.getTabAt(2).setIcon(ic_messages_empty);
                break;
        }
    }
}
