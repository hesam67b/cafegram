package com.negahetazehco.cafetelegram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class C2D2 {

    public static final int PROTOCOL_EXCEPTION = -1;
    public static final int IO_EXCEPTION       = -2;
    public static final int UNKNOWN_EXCEPTION  = -3;

    public static final int NETWORK_ALWAYS     = 1;
    public static final int NETWORK_JUST_WIFI  = 2;


    public static interface Listener {

        public void onDataReceive(String data, Boolean status);
    }

    private Listener                 listener;
    private ArrayList<String>        label          = new ArrayList<String>();
    private ArrayList<String>        value          = new ArrayList<String>();
    private long                     interval;
    private int                      network;
    private String                   url;
    private boolean                  multiTime      = true;
    private Context context;

    private ArrayList<NameValuePair> inputArguments = new ArrayList<NameValuePair>();

    private boolean                  enable         = true;


    public C2D2 listener(Listener value) {
        listener = value;
        return this;
    }


    public C2D2 interval(long value) {
        interval = value;
        return this;
    }


    public C2D2 multiTime(Boolean value) {
        multiTime = value;
        return this;
    }


    public C2D2 network(int value) {
        network = value;
        return this;
    }


    public C2D2 url(String value) {
        url = value;
        return this;
    }


    public C2D2 label(ArrayList<String> _value) {
        label = _value;
        return this;
    }


    public C2D2 value(ArrayList<String> _value) {
        value = _value;
        return this;
    }

    public C2D2 context(Context _context) {
        context = _context;
        return this;
    }


    public void disable() {
        enable = false;
    }


    public void enable() {
        if (enable) {
            return;
        }

        enable = true;
        start();
    }


    public C2D2 start() {
        if (multiTime == true) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (enable) {
                        try {
                            check();
                            Thread.sleep(interval);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            thread.start();
        } else {
            check();
        }
        return this;
    }


    public void check() {
        int status = readNetworkStatus();
        if (status == DISCONNECTED) {
            return;
        }

        if (network == NETWORK_JUST_WIFI && status == MOBILE) {
            return;
        }
        //Log.i("readNetworkStatus", "status= " + status);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpParams params = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(params, 3000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpClient client = new DefaultHttpClient(params);
                    HttpPost request = new HttpPost(url);
                    inputArguments.clear();
                    //String action = label.get(0);
                    for (int i = 0; i < label.size(); i++) {
                        inputArguments.add(new BasicNameValuePair(label.get(i), value.get(i)));
                    }
                    /*
                    if ("checkBuyToken".equals(action)) {
                        inputArguments.add(new BasicNameValuePair("action", key[0]));
                        inputArguments.add(new BasicNameValuePair("package", key[1]));
                        inputArguments.add(new BasicNameValuePair("product", key[2]));
                        inputArguments.add(new BasicNameValuePair("tokenid", key[3]));
                    }
                    */
                    request.setEntity(new UrlEncodedFormEntity(inputArguments, "UTF-8"));
                    HttpResponse httpResponse = (HttpResponse) client.execute(request);

                    String data = streamToString(httpResponse.getEntity().getContent());
                    // Log.i("data", data);

                    if (listener != null) {
                        listener.onDataReceive(data, true);
                        return;
                    }
                }
                catch (ClientProtocolException e) {
                    Log.i("errNetworkStatus", "ClientProtocolException");
                    if (listener != null) {
                        listener.onDataReceive("", false);
                    }
                    e.printStackTrace();
                }
                catch (IOException e) {
                    Log.i("errNetworkStatus", "IOException ");
                    if (listener != null) {
                        listener.onDataReceive("", false);
                    }
                    e.printStackTrace();
                }
                catch (Exception e) {
                    Log.i("errNetworkStatus", "Exception");
                    if (listener != null) {
                        listener.onDataReceive("", false);
                    }
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    private String streamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuffer = new StringBuilder();

        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append((line + "\n"));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }

    private static final int DISCONNECTED = 0;
    private static final int WIFI         = 1;
    private static final int MOBILE       = 2;


    private int readNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo.isConnected();

        if (isConnected) {
            int networkType = networkInfo.getType();
            if (networkType == ConnectivityManager.TYPE_WIFI) {
                return WIFI;
            } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE;
            }
        }

        return DISCONNECTED;
    }
}