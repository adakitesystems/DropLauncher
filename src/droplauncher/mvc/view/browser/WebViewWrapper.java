/**
 * Source: http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
 * Date: 2017-09-26
 *
 * Modified by: Adakite
 * Date: 2017-09-26
 */

package droplauncher.mvc.view.browser;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebViewWrapper extends Region {

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();

    public WebViewWrapper() {
        getChildren().add(this.webView);
    }

    public void load(String url) {
      this.webEngine.load(url);
    }

    @Override
    protected void layoutChildren() {
        layoutInArea(this.webView, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
    }

}
