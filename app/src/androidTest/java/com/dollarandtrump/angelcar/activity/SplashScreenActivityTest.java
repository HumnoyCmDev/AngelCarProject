package com.dollarandtrump.angelcar.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.dollarandtrump.angelcar.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SplashScreenActivityTest {

    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);

    @Test
    public void splashScreenActivityTest() {
        pressBack();

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_chat), withContentDescription("chat"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.tv_tab_title), withText("คุยกับคนขาย"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.listConversation),
                        withParent(allOf(withId(R.id.frame_layout_view_group),
                                withParent(withId(R.id.viewpager)))),
                        isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

    }

}
