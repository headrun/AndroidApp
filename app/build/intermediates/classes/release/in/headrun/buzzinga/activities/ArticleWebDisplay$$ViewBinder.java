// Generated code from Butter Knife. Do not modify!
package in.headrun.buzzinga.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ArticleWebDisplay$$ViewBinder<T extends in.headrun.buzzinga.activities.ArticleWebDisplay> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624084, "field 'articlebrowser_close'");
    target.articlebrowser_close = finder.castView(view, 2131624084, "field 'articlebrowser_close'");
    view = finder.findRequiredView(source, 2131624086, "field 'article_url_disp'");
    target.article_url_disp = finder.castView(view, 2131624086, "field 'article_url_disp'");
    view = finder.findRequiredView(source, 2131624087, "field 'article_webview'");
    target.article_webview = finder.castView(view, 2131624087, "field 'article_webview'");
    view = finder.findRequiredView(source, 2131624088, "field 'article_progress'");
    target.article_progress = finder.castView(view, 2131624088, "field 'article_progress'");
  }

  @Override public void unbind(T target) {
    target.articlebrowser_close = null;
    target.article_url_disp = null;
    target.article_webview = null;
    target.article_progress = null;
  }
}
