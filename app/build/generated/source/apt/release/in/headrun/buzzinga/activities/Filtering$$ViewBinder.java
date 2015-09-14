// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Filtering$$ViewBinder<T extends in.headrun.buzzinga.activities.Filtering> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755165, "field 'filter_titles'");
    target.filter_titles = finder.castView(view, 2131755165, "field 'filter_titles'");
    view = finder.findRequiredView(source, 2131755167, "field 'filter_sourceslist'");
    target.filter_sourceslist = finder.castView(view, 2131755167, "field 'filter_sourceslist'");
    view = finder.findRequiredView(source, 2131755168, "field 'filter_sentiment'");
    target.filter_sentiment = finder.castView(view, 2131755168, "field 'filter_sentiment'");
    view = finder.findRequiredView(source, 2131755169, "field 'filter_gender'");
    target.filter_gender = finder.castView(view, 2131755169, "field 'filter_gender'");
    view = finder.findRequiredView(source, 2131755170, "field 'filter_location'");
    target.filter_location = finder.castView(view, 2131755170, "field 'filter_location'");
    view = finder.findRequiredView(source, 2131755171, "field 'filter_language'");
    target.filter_language = finder.castView(view, 2131755171, "field 'filter_language'");
    view = finder.findRequiredView(source, 2131755173, "field 'clearfilter'");
    target.clearfilter = finder.castView(view, 2131755173, "field 'clearfilter'");
    view = finder.findRequiredView(source, 2131755174, "field 'applyfilter'");
    target.applyfilter = finder.castView(view, 2131755174, "field 'applyfilter'");
    view = finder.findRequiredView(source, 2131755166, "field 'autosearch'");
    target.autosearch = finder.castView(view, 2131755166, "field 'autosearch'");
  }

  @Override public void unbind(T target) {
    target.filter_titles = null;
    target.filter_sourceslist = null;
    target.filter_sentiment = null;
    target.filter_gender = null;
    target.filter_location = null;
    target.filter_language = null;
    target.clearfilter = null;
    target.applyfilter = null;
    target.autosearch = null;
  }
}
