// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeScreen$$ViewBinder<T extends in.headrun.buzzinga.activities.HomeScreen> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755170, "field 'newarticle'");
    target.newarticle = finder.castView(view, 2131755170, "field 'newarticle'");
    view = finder.findRequiredView(source, 2131755171, "field 'txt_info'");
    target.txt_info = finder.castView(view, 2131755171, "field 'txt_info'");
    view = finder.findRequiredView(source, 2131755166, "field 'progressbar'");
    target.progressbar = finder.castView(view, 2131755166, "field 'progressbar'");
    view = finder.findRequiredView(source, 2131755168, "field 'swipeRefreshLayout'");
    target.swipeRefreshLayout = finder.castView(view, 2131755168, "field 'swipeRefreshLayout'");
    view = finder.findRequiredView(source, 2131755169, "field 'display_data'");
    target.display_data = finder.castView(view, 2131755169, "field 'display_data'");
  }

  @Override public void unbind(T target) {
    target.newarticle = null;
    target.txt_info = null;
    target.progressbar = null;
    target.swipeRefreshLayout = null;
    target.display_data = null;
  }
}
