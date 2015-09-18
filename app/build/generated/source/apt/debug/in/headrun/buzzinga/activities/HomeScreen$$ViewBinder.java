// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeScreen$$ViewBinder<T extends in.headrun.buzzinga.activities.HomeScreen> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755120, "field 'filterpanel'");
    target.filterpanel = view;
    view = finder.findRequiredView(source, 2131755126, "field 'webview_lay'");
    target.webview_lay = view;
    view = finder.findRequiredView(source, 2131755114, "field 'filtersource_lay'");
    target.filtersource_lay = view;
    view = finder.findRequiredView(source, 2131755116, "field 'filterdate_lay'");
    target.filterdate_lay = view;
    view = finder.findRequiredView(source, 2131755129, "field 'browsertitle'");
    target.browsertitle = finder.castView(view, 2131755129, "field 'browsertitle'");
    view = finder.findRequiredView(source, 2131755117, "field 'filtersourcebtn'");
    target.filtersourcebtn = finder.castView(view, 2131755117, "field 'filtersourcebtn'");
    view = finder.findRequiredView(source, 2131755130, "field 'webview'");
    target.webview = finder.castView(view, 2131755130, "field 'webview'");
    view = finder.findRequiredView(source, 2131755121, "field 'closebtn'");
    target.closebtn = finder.castView(view, 2131755121, "field 'closebtn'");
    view = finder.findRequiredView(source, 2131755128, "field 'closebrowser'");
    target.closebrowser = finder.castView(view, 2131755128, "field 'closebrowser'");
    view = finder.findRequiredView(source, 2131755125, "field 'bydatefilter'");
    target.bydatefilter = finder.castView(view, 2131755125, "field 'bydatefilter'");
    view = finder.findRequiredView(source, 2131755132, "field 'Listfooter'");
    target.Listfooter = view;
    view = finder.findRequiredView(source, 2131755111, "field 'progress'");
    target.progress = finder.castView(view, 2131755111, "field 'progress'");
    view = finder.findRequiredView(source, 2131755131, "field 'browser_progress'");
    target.browser_progress = finder.castView(view, 2131755131, "field 'browser_progress'");
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
    target.Listfooter = null;
    target.progress = null;
    target.browser_progress = null;
  }
}
