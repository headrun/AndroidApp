// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TrackKeyWord$$ViewBinder<T extends in.headrun.buzzinga.activities.TrackKeyWord> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624075, "field 'Trackkeyword'");
    target.Trackkeyword = finder.castView(view, 2131624075, "field 'Trackkeyword'");
    view = finder.findRequiredView(source, 2131624076, "field 'trackbtn'");
    target.trackbtn = finder.castView(view, 2131624076, "field 'trackbtn'");
    view = finder.findRequiredView(source, 2131624074, "field 'trak_progress'");
    target.trak_progress = finder.castView(view, 2131624074, "field 'trak_progress'");
  }

  @Override public void unbind(T target) {
    target.Trackkeyword = null;
    target.trackbtn = null;
    target.trak_progress = null;
  }
}
