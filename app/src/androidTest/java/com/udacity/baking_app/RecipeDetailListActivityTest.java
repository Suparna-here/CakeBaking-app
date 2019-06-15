package com.udacity.baking_app;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.udacity.baking_app.ui.detail.RecipeDetailListActivity;
import com.udacity.baking_app.ui.list.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * This test demos a user clicking on the ingredient list.
 */

@RunWith(JUnit4.class)
public class RecipeDetailListActivityTest {

    private IdlingResource mIdlingResource;

    private static final String RECIPE_ITEM = "Brownies";


    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResources() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    // this method which runs before each test and will stub all external
    // intents so all external intents will be blocked
    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickRecipe_LaunchRecipeDetailListActivityIntent() {
        onView(withId(R.id.recyclerview_recipes))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(RECIPE_ITEM)), click()));
        Context targetContext = InstrumentationRegistry.getTargetContext();
        targetContext.getResources().getBoolean(R.bool.tablet);
        Boolean isTablet = targetContext.getResources().getBoolean(R.bool.tablet);
        if (!isTablet) {
            //tablet is not used and RecipeDetailListActivity Opens
            intended(hasComponent(RecipeDetailListActivity.class.getName()));
        }

        if (isTablet) {
            //video_container_tab is present and master flow is correctly implemented
            onView(withId(R.id.video_container_tab)).check(matches(isDisplayed()));
        }
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
