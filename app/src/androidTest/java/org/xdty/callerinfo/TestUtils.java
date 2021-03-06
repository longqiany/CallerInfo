package org.xdty.callerinfo;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestUtils {
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null) {
                    v.performClick();
                }
            }
        };
    }

    public static ViewAction swipeUp() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.BOTTOM_CENTER,
                GeneralLocation.TOP_CENTER, Press.FINGER);
    }

    public static ViewAction swipeDown() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER,
                GeneralLocation.BOTTOM_CENTER, Press.FINGER);
    }

    public static Matcher<View> isWindowAtPosition(final int x, final int y) {
        return new TypeSafeMatcher<View>() {

            int rootX = -1;
            int rootY = -1;

            @Override
            protected boolean matchesSafely(View view) {
                if (view.getRootView().getLayoutParams() instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getRootView()
                            .getLayoutParams();
                    rootX = params.x;
                    rootY = params.y;
                    return rootX == x && rootY == y;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("view is at position: (" + rootX + " ," + rootY
                        + "), but should at x=" + x + ", y=" + y + "");
            }
        };
    }

    public static Matcher<View> atPosition(final int position,
            @NonNull final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(
                        position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static ViewAssertion itemsCountIs(final int count) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException e) {
                if (!(view instanceof RecyclerView)) {
                    throw e;
                }
                RecyclerView rv = (RecyclerView) view;
                assertThat(rv.getAdapter().getItemCount(), is(count));
            }
        };
    }

    public static Matcher<View> withTextColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView textView) {
                return color == textView.getCurrentTextColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }

    public static Matcher<View> childWithBackgroundColor(final int childId, final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, View>(View.class) {

            int mColor = color;

            @Override
            public boolean matchesSafely(View view) {
                View child = view.findViewById(childId);
                if (child != null) {
                    if (child instanceof CardView) {
                        return mColor == ((CardView) child).getCardBackgroundColor()
                                .getDefaultColor();
                    } else {
                        Drawable background = child.getBackground();
                        return background instanceof ColorDrawable
                                && mColor == ((ColorDrawable) background).getColor();
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: " + mColor);
            }
        };
    }
}
