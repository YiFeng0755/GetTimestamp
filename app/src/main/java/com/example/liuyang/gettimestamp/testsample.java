package com.example.liuyang.gettimestamp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Date;
import org.apache.commons.net.time.TimeTCPClient;

/**
 * Created by LiuYang on 2019/1/28.
 */

public class testsample {
    public static void main (String [] args) throws IOException {
        DecimalFormat df=new DecimalFormat("0.00");
        while (true){
            int a = 4;
            int b =10;
            int c =20;
            int d= 11;
            String e = df.format(100*(double)(b-a)/(c-d));
//            TimeTCPClient client = new TimeTCPClient();
//            client.setDefaultTimeout(60000);
//            client.connect("time.windows.com");
//            String webUrl = "http://www.google.com";
//            URL url = new URL(webUrl);
//            URLConnection uc = url.openConnection();
//            uc.setReadTimeout(5000);
//            uc.setConnectTimeout(5000);
//            uc.connect();
//            long correctTime = uc.getDate();
//            long correctTime = client.getTime();
//            System.out.println(correctTime);
            System.out.println(e);
        }

    }
}
