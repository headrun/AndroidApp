// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeScreen$$ViewBinder<T extends in.headrun.buzzinga.activities.HomeScreen> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624015, "field 'filterpanel'");
    target.filterpanel = view;
    view = finder.findRequiredView(source, 2131624031, "field 'webview_lay'");
    target.webview_lay = view;
    view = finder.findRequiredView(source, 2131624025, "field 'filtersource_lay'");
    target.filtersource_lay = view;
    view = finder.findRequiredView(source, 2131624027, "field 'filterdate_lay'");
    target.filterdate_lay = view;
    view = finder.findRequiredView(source, 2131624028, "field 'filtersourcebtn'");
    target.filtersourcebtn = finder.castView(view, 2131624028, "field 'filtersourcebtn'");
    view = finder.findRequiredView(source, 2131624032, "field 'webview'");
    target.webview = finder.castView(view, 2131624032, "field 'webview'");
    view = finder.findRequiredView(source, 2131624016, "field 'closebtn'");
    target.closebtn = finder.castView(view, 2131624016, "field 'closebtn'");
    view = finder.findRequiredView(source, 2131624021, "field 'bydatefilter'");
    target.bydatefilter = finder.castView(view, 2131624021, "field 'bydatefilter'");
    view = finder.findRequiredView(source, 2131624035, "field 'Listfooter'");
    target.Listfooter = view;
    view = finder.findRequiredView(source, 2131624022, "field 'progress'");
    target.progress = finder.castView(view, 2131624022, "field 'progress'");
    view = finder.findRequiredView(source, 2131624034, "field 'browser_progress'");
    target.browser_progress = finder.castView(view, 2131624034, "field 'browser_progress'");
  }

  @Override public void unbind(T target) {
    target.filterpanel = null;
    target.webview_lay = null;
    target.filtersource_lay = null;
    target.filterdate_lay = null;
    target.filtersourcebtn = null;
    target.webview = null;
    target.closebtn = null;
    target.bydatefilter = null;
    target.Listfooter = null;
    target.progress = null;
    target.browser_progress = null;
  }
}
