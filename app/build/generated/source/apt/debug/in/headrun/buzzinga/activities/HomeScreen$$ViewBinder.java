// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeScreen$$ViewBinder<T extends in.headrun.buzzinga.activities.HomeScreen> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755091, "field 'filterpanel'");
    target.filterpanel = view;
    view = finder.findRequiredView(source, 2131755097, "field 'webview_lay'");
    target.webview_lay = view;
    view = finder.findRequiredView(source, 2131755130, "field 'filtersource_lay'");
    target.filtersource_lay = view;
    view = finder.findRequiredView(source, 2131755132, "field 'filterdate_lay'");
    target.filterdate_lay = view;
    view = finder.findRequiredView(source, 2131755100, "field 'browsertitle'");
    target.browsertitle = finder.castView(view, 2131755100, "field 'browsertitle'");
    view = finder.findRequiredView(source, 2131755133, "field 'filtersourcebtn'");
    target.filtersourcebtn = finder.castView(view, 2131755133, "field 'filtersourcebtn'");
    view = finder.findRequiredView(source, 2131755102, "field 'webview'");
    target.webview = finder.castView(view, 2131755102, "field 'webview'");
    view = finder.findRequiredView(source, 2131755092, "field 'closebtn'");
    target.closebtn = finder.castView(view, 2131755092, "field 'closebtn'");
    view = finder.findRequiredView(source, 2131755099, "field 'closebrowser'");
    target.closebrowser = finder.castView(view, 2131755099, "field 'closebrowser'");
    view = finder.findRequiredView(source, 2131755096, "field 'bydatefilter'");
    target.bydatefilter = finder.castView(view, 2131755096, "field 'bydatefilter'");
    view = finder.findRequiredView(source, 2131755127, "field 'progress'");
    target.progress = finder.castView(view, 2131755127, "field 'progress'");
  }

  @Override public void unbind(T target) {
    target.filterpanel = null;
    target.webview_lay = null;
    target.filtersource_lay = null;
    target.filterdate_lay = null;
    target.browsertitle = null;
    target.filtersourcebtn = null;
    target.webview = null;
    target.closebtn = null;
    target.closebrowser = null;
    target.bydatefilter = null;
    target.progress = null;
  }
}
