// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ArticleDisplay$$ViewBinder<T extends in.headrun.buzzinga.activities.ArticleDisplay> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755205, "field 'article_webview'");
    target.article_webview = finder.castView(view, 2131755205, "field 'article_webview'");
  }

  @Override public void unbind(T target) {
    target.article_webview = null;
  }
}
