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
    view = finder.findRequiredView(source, 2131624033, "field 'webview_lay'");
    target.webview_lay = view;
    view = finder.findRequiredView(source, 2131624025, "field 'filtersource_lay'");
    target.filtersource_lay = view;
    view = finder.findRequiredView(source, 2131624026, "field 'filter_image'");
    target.filter_image = finder.castView(view, 2131624026, "field 'filter_image'");
    view = finder.findRequiredView(source, 2131624027, "field 'filtersource'");
    target.filtersource = finder.castView(view, 2131624027, "field 'filtersource'");
    view = finder.findRequiredView(source, 2131624028, "field 'filterdate_lay'");
    target.filterdate_lay = view;
    view = finder.findRequiredView(source, 2131624029, "field 'sort_iamge'");
    target.sort_iamge = finder.castView(view, 2131624029, "field 'sort_iamge'");
    view = finder.findRequiredView(source, 2131624030, "field 'filtersourcebtn'");
    target.filtersourcebtn = finder.castView(view, 2131624030, "field 'filtersourcebtn'");
    view = finder.findRequiredView(source, 2131624034, "field 'webview'");
    target.webview = finder.castView(view, 2131624034, "field 'webview'");
    view = finder.findRequiredView(source, 2131624016, "field 'closebtn'");
    target.closebtn = finder.castView(view, 2131624016, "field 'closebtn'");
    view = finder.findRequiredView(source, 2131624021, "field 'bydatefilter'");
    target.bydatefilter = finder.castView(view, 2131624021, "field 'bydatefilter'");
    view = finder.findRequiredView(source, 2131624037, "field 'Listfooter'");
    target.Listfooter = view;
    view = finder.findRequiredView(source, 2131624022, "field 'progress'");
    target.progress = finder.castView(view, 2131624022, "field 'progress'");
    view = finder.findRequiredView(source, 2131624036, "field 'browser_progress'");
    target.browser_progress = finder.castView(view, 2131624036, "field 'browser_progress'");
  }

  @Override public void unbind(T target) {
    target.filterpanel = null;
    target.webview_lay = null;
    target.filtersource_lay = null;
    target.filter_image = null;
    target.filtersource = null;
    target.filterdate_lay = null;
    target.sort_iamge = null;
    target.filtersourcebtn = null;
    target.webview = null;
    target.closebtn = null;
    target.bydatefilter = null;
    target.Listfooter = null;
    target.progress = null;
    target.browser_progress = null;
  }
}
