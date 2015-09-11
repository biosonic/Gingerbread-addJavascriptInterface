/**
 *
 *
 *  Fix for WebView JavascriptInterface ( addJavascriptInterface ) on Gingerbread Androids 2.3
 *
 *  Use it as decorator for WebView
 *
 *  KeyWords: "addJavascriptInterface not working error"
 *
 *  The MIT License (MIT) Copyright (c) 2015 biosonic (coa.develop@gmail.com)
 *
 * 
 */

package rs.street.scratch;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.dexmaker.stock.ProxyBuilder;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class WebViewFix extends WebView {


    public static Object interfaceObj;
    public static String interfaceNam;


    public WebViewFix(Context context) {
        super(context);
        this.setWebChromeClient(new WebChromeClient());
    }
    public WebViewFix(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWebChromeClient(new WebChromeClient());
    }
    public WebViewFix(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setWebChromeClient(new WebChromeClient());
    }


    @Override
    public void addJavascriptInterface(Object obj, String interfaceName) {
        interfaceObj = obj;
        interfaceNam = interfaceName;
        if (!Build.VERSION.RELEASE.startsWith("2.3")) {
            super.addJavascriptInterface(interfaceObj, interfaceNam);
        }
        else {
            Method[] methods = interfaceObj.getClass().getDeclaredMethods(); // this is reflection
            String jS = "javascript:window." + interfaceNam + " = {};";
            for (Method method : methods) {
                String methodName = method.getName();
                jS += "window." + interfaceNam + "." + methodName + " = function(txt){console.log('"+interfaceNam+"-"+methodName+"-'+txt);};";
            }
            this.loadUrl(jS);
        }
    }


    @Override
    public void setWebChromeClient(final WebChromeClient client) {
        if (!Build.VERSION.RELEASE.startsWith("2.3")) {
            super.setWebChromeClient(client);
        }
        else {
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String mName = method.getName();
                    Class[] mPT = method.getParameterTypes();
                    Method m = client.getClass().getMethod(mName, mPT);
                    if (mName.equals("onConsoleMessage")) {
                        onConsoleMessageHandler((ConsoleMessage) args[0]);
                    }
                    return m.invoke(client, args);
                }
            };
            WebChromeClient proxy = null;
            try {
                File file = this.getContext().getApplicationContext().getDir("dx", Context.MODE_PRIVATE);
                proxy = ProxyBuilder.forClass(WebChromeClient.class).dexCache(file).handler(handler).build();
            } catch (Throwable e) {}
            super.setWebChromeClient(proxy);
        }
    }


    public void onConsoleMessageHandler(ConsoleMessage cm) {
        try {
            String consoleMessageText = cm.message();
            String[] consoleMessageSplit = consoleMessageText.split("-");
            String consoleInterface = consoleMessageSplit[0];
            String consoleMethod = consoleMessageSplit[1];
            String consoleData = consoleMessageSplit[2];
            if (interfaceNam.equals(consoleInterface)) {
                Method msg = interfaceObj.getClass().getDeclaredMethod(consoleMethod, new Class[]{String.class});
                msg.invoke(interfaceObj, consoleData);
            }
        }catch(Exception e){}
    }


}

