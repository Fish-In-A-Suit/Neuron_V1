package neuron.android.com.neuron.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import neuron.android.com.neuron.fragments.HomeFragment;
import neuron.android.com.neuron.fragments.MessagesFragment;
import neuron.android.com.neuron.fragments.PostsFragment;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {
    int numberOfFragments;

    private HomeFragment homeFragment;
    private PostsFragment postsFragment;
    private MessagesFragment messagesFragment;

    public MainActivityPagerAdapter(FragmentManager fm, int numberOfFragments) {
        super(fm);
        System.out.println("[Neuron.MainActivityPagerAdapter]: Creating a new pager adapter with " + numberOfFragments + " fragments.");

        this.numberOfFragments = numberOfFragments;

        homeFragment = new HomeFragment();
        postsFragment = new PostsFragment();
        messagesFragment = new MessagesFragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case 0:
                System.out.println("[Neuron.MainActivityPagerAdapter]: Selected 0. Returning posts fragment.");
                return postsFragment;
            case 1:
                System.out.println("[Neuron.MainActivityPagerAdapter]: Selected 1. Returning home fragment.");
                return homeFragment;
            case 2:
                System.out.println("[Neuron.MainActivityPagerAdapter]: Selected 2. Returning messages fragment");
                return messagesFragment;
            default:
                System.out.println("[Neuron.MainActivityPagerAdapter]: Selected " + i + ". Returning null!");
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfFragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Posts";
            case 1:
                return "Home";
            case 2:
                return "Messages";
            default:
                return null;
        }
    }
}
