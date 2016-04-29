package ismartdev.mn.darkhanwindow;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import ismartdev.mn.darkhanwindow.util.WalkItemFragment;

public class Walkthrough extends AppCompatActivity {
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private CirclePageIndicator circlePageIndicator;
    private String[] walk_title;
    private String[] walk_desc;
    private int[] images = {R.drawable.ic_walk_one, R.drawable.ic_walk_two, R.drawable.ic_walk_three, R.drawable.ic_walk_four, R.drawable.ic_walk_five};
    private TextView skip;
    private TextView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walktrough);
        mPager = (ViewPager) findViewById(R.id.pager);


        walk_title = getResources().getStringArray(R.array.walk_title);
        walk_desc = getResources().getStringArray(R.array.walk_desc);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.walk_indicator);
        circlePageIndicator.setCentered(true);
        circlePageIndicator.setViewPager(mPager);

        skip = (TextView) findViewById(R.id.walk_skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Walkthrough.this, MainActivity.class));
            }
        });
        next = (TextView) findViewById(R.id.walk_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
            }
        });

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            return WalkItemFragment.newInstance(images[position], walk_title[position], walk_desc[position]);
        }

        @Override
        public int getCount() {
            return 5;
        }


    }

}
