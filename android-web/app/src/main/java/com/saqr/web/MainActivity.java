package com.saqr.web;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** صقر: loads the hosted web game full screen, landscape, with the system bars hidden. */
public class MainActivity extends Activity {
  private static final String GAME_URL = "https://aboudalah-stack.github.io/saqr/web/";
  private static final String GAME_HOST = "aboudalah-stack.github.io";
  private static final int PICK_FILE = 41;
  private WebView web;
  private ValueCallback<Uri[]> pickCb;

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);
    Window w = getWindow();
    w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    if (Build.VERSION.SDK_INT >= 28) {
      w.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }
    web = new WebView(this);
    web.setBackgroundColor(Color.parseColor("#1f100c"));
    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setDatabaseEnabled(true);
    s.setMediaPlaybackRequiresUserGesture(false);
    s.setCacheMode(WebSettings.LOAD_DEFAULT);
    // lets the game know it runs inside the app (Facebook login then returns via saqr://login)
    s.setUserAgentString(s.getUserAgentString() + " SaqrApp/1.2");
    web.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
        Uri u = r.getUrl();
        if (GAME_HOST.equals(u.getHost())) return false;
        try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
        return true;
      }
    });
    // lets the game open the phone's gallery (profile picture)
    web.setWebChromeClient(new WebChromeClient() {
      @Override
      public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p) {
        if (pickCb != null) pickCb.onReceiveValue(null);
        pickCb = cb;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        try { startActivityForResult(Intent.createChooser(i, "اختر صورة"), PICK_FILE); }
        catch (Exception e) { pickCb = null; cb.onReceiveValue(null); return false; }
        return true;
      }
    });
    setContentView(web);
    String back = loginReturnUrl(getIntent());
    if (back != null) web.loadUrl(back);
    else if (state != null) web.restoreState(state);
    else web.loadUrl(GAME_URL);
    hideBars();
  }

  /** Facebook login opens in the phone's browser and comes back as saqr://login#access_token=… */
  private String loginReturnUrl(Intent i) {
    if (i == null || i.getData() == null) return null;
    Uri u = i.getData();
    if (!"saqr".equals(u.getScheme())) return null;
    String frag = u.getEncodedFragment(), q = u.getEncodedQuery();
    String url = GAME_URL;
    if (q != null && !q.isEmpty()) url += "?" + q;
    if (frag != null && !frag.isEmpty()) url += "#" + frag;
    return url;
  }

  @Override
  protected void onNewIntent(Intent i) {
    super.onNewIntent(i);
    setIntent(i);
    String back = loginReturnUrl(i);
    if (back != null && web != null) web.loadUrl(back);
  }

  private void hideBars() {
    web.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) hideBars();
  }

  @Override
  protected void onSaveInstanceState(Bundle out) {
    super.onSaveInstanceState(out);
    web.saveState(out);
  }

  @Override
  protected void onActivityResult(int req, int res, Intent data) {
    super.onActivityResult(req, res, data);
    if (req != PICK_FILE || pickCb == null) return;
    Uri[] out = null;
    if (res == RESULT_OK && data != null && data.getData() != null) out = new Uri[]{ data.getData() };
    pickCb.onReceiveValue(out);
    pickCb = null;
  }

  @Override
  protected void onPause() { super.onPause(); web.onPause(); }

  @Override
  protected void onResume() { super.onResume(); web.onResume(); hideBars(); }

  @Override
  public void onBackPressed() {
    if (web.canGoBack()) web.goBack(); else super.onBackPressed();
  }
}
