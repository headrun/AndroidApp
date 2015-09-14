// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TrackKeyWord$$ViewBinder<T extends in.headrun.buzzinga.activities.TrackKeyWord> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755177, "field 'keyword'");
    target.keyword = finder.castView(view, 2131755177, "field 'keyword'");
    view = finder.findRequiredView(source, 2131755178, "field 'trackbtn'");
    target.trackbtn = finder.castView(view, 2131755178, "field 'trackbtn'");
  }

  @Override public void unbind(T target) {
    target.keyword = null;
    target.trackbtn = null;
  }
}
