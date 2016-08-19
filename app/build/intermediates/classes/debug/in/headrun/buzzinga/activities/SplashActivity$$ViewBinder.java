// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SplashActivity$$ViewBinder<T extends in.headrun.buzzinga.activities.SplashActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755249, "field 'splash_progress'");
    target.splash_progress = finder.castView(view, 2131755249, "field 'splash_progress'");
  }

  @Override public void unbind(T target) {
    target.splash_progress = null;
  }
}
