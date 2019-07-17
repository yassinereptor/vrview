package io.github.ponnamkarthik.nativeweb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.platform.PlatformView;

import static io.flutter.plugin.common.MethodChannel.MethodCallHandler;

public class FlutterWeb implements PlatformView, MethodCallHandler {

    Context context;
    Registrar registrar;
    WebView webView;
    String url = "";
    MethodChannel channel;
    View view;
    VrPanoramaView panoramaView;
    ParamObj paramObj = new ParamObj();


    @SuppressLint("SetJavaScriptEnabled")
    FlutterWeb(Context context, Registrar registrar, int id) {
        this.context = context;
        this.registrar = registrar;
        webView = getWebView(registrar);
        this.view = LayoutInflater.from(registrar.activity()).inflate(R.layout.la, null);
        this.panoramaView = view.findViewById(R.id.pano_view);
        channel = new MethodChannel(registrar.messenger(), "nativeweb_" + id);
        channel.setMethodCallHandler(this);
        paramObj.vr = this.panoramaView;

    }

    public static Bitmap getBitmapFromAsset(Context context, String path) {
        AssetManager assetManager = context.getAssets();

        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = assetManager.open(path);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {

        }

        return bitmap;
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void dispose() {

    }

    private WebView getWebView(Registrar registrar) {
        WebView webView = new WebView(registrar.context());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        return webView;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "loadUrl":
                url = call.arguments.toString();
                paramObj.url = url;
                new GetImage().execute(paramObj);

                //webView.loadUrl(url);
                break;
            default:
                result.notImplemented();
        }

    }

    private class GetImage extends AsyncTask<ParamObj, Integer, Integer> {

        protected void onProgressUpdate(Integer... progress) {
        }


        protected void onPostExecute(Long result) {
        }

        @Override
        protected Integer doInBackground(ParamObj... paramObjs) {
            VrPanoramaView.Options op = new VrPanoramaView.Options();
            op.inputType = 1;

            paramObjs[0].vr.loadImageFromBitmap(getBitmapFromURL(paramObjs[0].url), op);
            return 0;
        }
    }


}


class ParamObj {
    String url;
    VrPanoramaView vr;
}