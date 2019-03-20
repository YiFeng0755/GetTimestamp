package com.example.liuyang.gettimestamp;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by LiuYang on 2019/1/22.
 */

public class FloatView extends Service {
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private TextView tv1;
    private TextView tv2;
    RelativeLayout fvlayout;
    private int timer_delaytime = 100;
    private int sp_interval = 1000;
    private Thread timer_thread;
    private Thread cpu_thread;
    public static boolean refresh_timer_flag = false;
    private String pkgName = "im.youme.talk.sample";
    private int kernelNum;
    String Tag = "1111111111111111：";
    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.width = 700;
        wmParams.height = 100;
        wmParams.x = 300;
        wmParams.y = 300;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        fvlayout = (RelativeLayout) inflater.inflate(R.layout.floatview,null);
        tv1 = fvlayout.findViewById(R.id.tv1);
        tv2 = fvlayout.findViewById(R.id.tv2);
        wm.addView(fvlayout, wmParams);
        fvlayout.setOnTouchListener(new FloatingOnTouchListener());
        tv2.setClickable(true);
        tv2.setOnClickListener(operatTimer);
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    wmParams.x = wmParams.x + movedX;
                    wmParams.y = wmParams.y + movedY;
                    wm.updateViewLayout(fvlayout, wmParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private View.OnClickListener operatTimer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(Tag,"0");
            int key = v.getId();
            switch(key){
            case R.id.tv2:
                Log.d(Tag,"2");
                refresh_timer_flag = true;
                timer_thread = new Thread(new TimerRunnable());
                timer_thread.start();
//                cpu_thread = new Thread(new CpuRunnable());
//                cpu_thread.start();
                break;
            }

        }
    };

    Handler fv_handler = new Handler(){
        public void handleMessage(Message msg) {
            Log.d(Tag,"3");
            switch (msg.what) {
                case 0 :
                    Log.d(Tag,"4");
                    wm.updateViewLayout(fvlayout, wmParams);
            }
        }
    };



    public class TimerRunnable implements Runnable {
        @Override
        public void run() {
            while (refresh_timer_flag){
                try {
                    Thread.sleep(timer_delaytime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    refreshTimer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message msg = fv_handler.obtainMessage();
                msg.what = 0;
                msg.sendToTarget();
            }
        }
    }

    public class CpuRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(sp_interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            calPercent();
        }
    }

    private void refreshTimer() throws IOException {
        long curTime = System.currentTimeMillis();
        Log.d(Tag,String.valueOf(curTime));
//        long currentTime= Calendar.getInstance().getTimeInMillis();
//        String webUrl = "https://www.qq.com/";
//        URL url = new URL(webUrl);
//        URLConnection uc = url.openConnection();
//        uc.setReadTimeout(5000);
//        uc.setConnectTimeout(5000);
//        uc.connect();
//        long correctTime = uc.getDate();
//        Log.d(Tag,String.valueOf(currentTime));
        tv2.setText(String.valueOf(curTime));
        tv2.postInvalidate();

    }

    private long getTotalCpuTime(){
        String[] cpuInfos = null;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
            Log.d("cpuINfos:", Arrays.toString(cpuInfos));
            Log.d("cpuINfos1:",cpuInfos[1]);
            Log.d("cpuINfos2:",cpuInfos[2]);
            Log.d("cpuINfos3:",cpuInfos[3]);
            Log.d("cpuINfos4:",cpuInfos[4]);
            Log.d("cpuINfos5:",cpuInfos[5]);
        }catch (IOException e){
            e.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    private int getKernelNum(){
        int kernelNum = -1;
        String[] cpuInfos = null;
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
            for(String s:cpuInfos){
                if (s.contains("cpu")){
                    kernelNum +=1;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return kernelNum;
    }

    private String pID (String pkgName){
        String pid = null;
        String out = null;
//        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
//        Log.d("pidlist",pids.toString());
//        int processid = 0;
//        for (int i = 0; i < pids.size(); i++) {
//            ActivityManager.RunningAppProcessInfo info = pids.get(i);
//            Log.d("pid:",info.processName);
//            if (info.processName.equalsIgnoreCase(pkgName)) {
//                processid = info.pid;
//            }
//        }
//        return processid;
        Log.d("pkgName:",pkgName);
//        String cmd = "ps -A|grep "+pkgName;
        String cmd = "ps";
        Log.d("cmd:",cmd);

        try{
            Process process = Runtime.getRuntime().exec(cmd);
            Log.d("pid:","111");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Log.d("pid:","222");
            String line = null;
            String spcLine = null;
            while(true){
                line =reader.readLine();
                Log.d("line:",line);
                if (line.contains("im.youme.talk.sample")){
                    spcLine = line;
                    break;
                }
            }
            process.waitFor();
            Log.d("spcLine:",spcLine);
            String [] l = spcLine.split("\\s");
            pid = l[1];
            reader.close();

            Log.d("pid:",pid);
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return pid;
    }

    private long getAppCpuTime(String pid)
    { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
            Log.d("cpuInfos:", Arrays.toString(cpuInfos));
            Log.d("cpuInfos1:",cpuInfos[13]);
            Log.d("cpuInfos2:",cpuInfos[14]);
            Log.d("cpuInfos3:",cpuInfos[15]);
            Log.d("cpuInfos4:",cpuInfos[16]);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    private String calPercent (){
        Boolean spFlag = true;
        String pid = pID(pkgName);
        long f_tcpu = getTotalCpuTime();
        long f_acpu = getAppCpuTime(pid);
        long c_tcpu = 0l;
        long c_acpu = 0l;
        while (spFlag){
            try{
                Thread.sleep(50);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            c_tcpu = getTotalCpuTime();
            c_acpu = getAppCpuTime(pid);
            if (f_tcpu!=c_tcpu && f_acpu!=c_acpu ){
                spFlag = false;
            }
        }
        DecimalFormat df=new DecimalFormat("0.00");
        kernelNum = getKernelNum();
        String pt = df.format(100*kernelNum*(double)(c_acpu-f_acpu)/(c_tcpu-f_tcpu));
        return pt;
    }
}
