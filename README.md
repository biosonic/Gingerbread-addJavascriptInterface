# Gingerbread addJavascriptInterface

Fix for WebView JavascriptInterface ( addJavascriptInterface ) on Gingerbread Androids 2.3 .

On Gingerbread this fix uses JavaScript's console.log as fail-safe. On Not-Gingerbread devices stadard JavascriptInterface is in use.

KeyWords: "addJavascriptInterface not working broken error"

## Requirement

Beside standard libraries require Dexmaker.

in build.gradle
```
dependencies {
    ...
    compile 'com.google.dexmaker:dexmaker:1.2'
    ...
}
```

## Usage

Download WebViewFix.java to your project.

Use it as decorator for WebView. 

In [some layout].xlm instead of using  WebView use WebViewFix.
```
// xml  
< WebViewFix
  android:id="@+id/wv"
  android:layout_width="match_parent"
  android:layout_height="match_parent"/>
```

In activity use it as you use regular JavaScriptInterface
```
// Java
WebViewFix mWebView = (WebViewFix) findViewById(R.id.wv);
mWebView.getSettings().setJavaScriptEnabled(true);
mWebView.addJavascriptInterface(new Object(){
    @JavascriptInterface
    public void exampleMethod(String txt) {
        Log.d("+++ exampleInterface - exampleMethod :: +++", txt);
    }
}, "exampleInterface");
```

On the JavaScript side use it as regular JavaScriptInterface by calling object's method:
```
// JavaScript
window.exampleInterface.exampleMethod("It works");
```


## License

The MIT License (MIT) Copyright (c) 2015 biosonic (coa.develop@gmail.com)

