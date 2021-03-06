package com.guineatech.CareC;


import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;



public class wifi extends AppCompatActivity {

    EditText nickname;
    private Context context = this;
    private WifiManager wifiManager;
    private Spinner spinnerWifis;
    private String bcode;
    private Button btstep;
    private String wifissid="",wifipwd="",endpoint=AppHelper.CUSTOMER_SPECIFIC_ENDPOINT;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private EditText ed_pwd;

    //開GPS
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        Intent it=this.getIntent();
         bcode=it.getStringExtra("bcode");
        spinnerWifis = findViewById(R.id.spinner);
        ed_pwd=findViewById(R.id.edit_pwd);
        btstep = findViewById(R.id.bt_step);
        if (ActivityCompat.checkSelfPermission(wifi.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(wifi.this,new String[]{ACCESS_COARSE_LOCATION},1);
        }
        nickname = findViewById(R.id.edit_nickname);

//Next step
        btstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.putExtra("deviceid", bcode);
                it.putExtra("nickname", nickname.getText().toString());
                Log.e("log", nickname.getText().toString());
                it.setClass(wifi.this, setDevice.class);
                startActivity(it);
                //連上Device
                /*
                wifissid=spinnerWifis.getSelectedItem().toString();
                wifipwd=ed_pwd.getText().toString();
               Adapter allwifi= spinnerWifis.getAdapter();
                int c=allwifi.getCount();

                boolean checkf=false;
                for(int v =0 ;v<c;) {
                    Log.e("log"," "+bcode);
                    Log.e("log"," "+allwifi.getItem(v).toString());
                    if(bcode.equals(allwifi.getItem(v).toString())){
                        int ope=0;
                       while (Connect(bcode,"12345678",WifiCipherType.WIFICIPHER_WPA))
                       {
Log.e("log",ope+"");
ope++;
                           try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……..
                               Thread.currentThread();
                               Thread.sleep(100);
                           } catch (InterruptedException ie)
                           {
                               Log.e("log","time out");
                               checkf=false;
                               break;
                           }
                       }
                       checkf=true;
                    break;
                    }
                    v++;
                }
                if(checkf)
                    new  conndecives().execute();
                else

                Log.e("log","Can't no find Device\nPlease Check your Device switch to Seting Mode");
            */
            }
        });
        openGPS(context);
        IsEnable();
        scan();
    }

//跳出的視窗
    private void showDialogMessage(String title, String body, final int Whatis) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               switch (Whatis)
               {
                   case 3:
                       Intent it=new Intent();
                       it.setClass(wifi.this,setDevice.class);
                       startActivity(it);
                   default:

               }



            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void IsEnable(){
        //首先取得Wi-Fi服務控制Manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){

        }else {

            //wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(true);
            Toast.makeText(wifi.this, "Wi-Fi開啟中", Toast.LENGTH_SHORT).show();
        }


    }

    //搜尋WIFI
    private void scan() {
        // Register the Receiver in some part os fragment...
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiScanReceive();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();

        // Inside the receiver:
    }

    private void wifiScanReceive(){
        // the result.size() is 0 after update to Android v6.0, same code working in older devices.
        List<ScanResult> scanResultList =  wifiManager.getScanResults();

        int size = scanResultList.size();
        final List<String> dataList = new ArrayList<String>(size);
        Toast.makeText(context,"scan result :" + size,Toast.LENGTH_SHORT).show();

         for(int i = 0 ; i <size  ; i++ )
        {
            //手機目前周圍的Wi-Fi環境
            String SSID  = scanResultList.get(i).SSID ;
          //  int LEVEL = scanResultList.get(i).level;
            String item = String.format(SSID);
            Log.d("wifi",item);
            dataList.add(item);
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item,dataList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerWifis.setAdapter(adapter);
            }
        });
    }

    //有連過的wifi
    public boolean Connect(WifiConfiguration wf) {
        IsEnable();
// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句，即当状态为WIFI_STATE_ENABLING时，让程序在while里面跑
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }
        boolean bRet = wifiManager.enableNetwork(wf.networkId, true);
        wifiManager.saveConfiguration();
        return bRet;
    }
    //玫璉過的wifi
    public boolean Connect(String SSID, String Password, WifiCipherType Type) {
        IsEnable();
// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig =this
                .CreateWifiInfo(SSID, Password, Type);
        int netID = wifiManager.addNetwork(wifiConfig);
        boolean bRet = wifiManager.enableNetwork(netID, true);
        wifiManager.saveConfiguration();
        return bRet;
    }

    //玫璉過的wifi
    private WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {

        WifiConfiguration config = new WifiConfiguration();

        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + SSID + "\"";

        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);

            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WPA) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }

    //參數
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

//連到裝置
    public class conndecives extends AsyncTask<Void,Void,String>
    {
        conndecives()
        {
            try {
                waitDialog.dismiss();
            }catch (Exception e)
            {
                e.toString();
            }
            waitDialog = new ProgressDialog(wifi.this);
            waitDialog.setTitle("Load......");
            waitDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection=null;


            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "---------WebKitFormBoundaryfsjCanVImiBm0CDt";
            String [] a=new String[]{"ssid","password","endpoint"};
            String [] b=new String[]{wifissid,wifipwd,endpoint};
            String [] c=new String[]{"ac8cert.pem","ac8pr.key","cahans.pem"};

            try
            {
                FileReader[] in = new FileReader[]{new FileReader(getFilesDir()+"/ac8cert.pem"),new FileReader(getFilesDir()+"/ac8pr.key") };
                URL url=new URL("http://192.168.1.1/upload.php");//php的位置
                urlConnection=(HttpURLConnection) url.openConnection();//對資料庫打開連結

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setReadTimeout(2000);
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);


                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                wr.writeBytes(lineEnd);


                //----------------------------------------------------

                for(int j=0;j<2;j++) {

                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\"file"+j+"\"; filename=\""+c[j]+"\"" + lineEnd);
                    wr.writeBytes("Content-Type: text/plain" + lineEnd);

                    wr.writeBytes(lineEnd);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(in[j]);

                        // do reading, usually loop until end of file reading
                        String mLine = null;

                        while ((mLine = reader.readLine()) != null) {

                            wr.writeBytes(mLine+lineEnd);

                            Log.e("log_tag",mLine+lineEnd);
                        }

                    } catch (IOException e) {
                        //log the exception
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                //log the exception
                            }
                        }
                    }


                }

                //  for(int i=0;i<3;i++)
                {
                    int i=2;
                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\"file"+i+"\"; filename=\""+c[i]+"\"" + lineEnd);
                    wr.writeBytes("Content-Type: text/plain" + lineEnd);

                    wr.writeBytes(lineEnd);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(
                                new InputStreamReader(getAssets().open(c[i])));


                        // do reading, usually loop until end of file reading
                        String mLine;
                        while ((mLine = reader.readLine()) != null) {

                            wr.writeBytes(mLine+lineEnd);

                            Log.e("log_tag",mLine);
                        }
                        wr.writeBytes(lineEnd);
                    } catch (IOException e) {
                        //log the exception
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                //log the exception
                            }
                        }
                    }
                    // wr.writeBytes(lineEnd);
                    //inputStream.close();

                }
//----------------------------------------------------
                for(int i=0;i<a.length;i++)
                {
                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\""+a[i]+"\"" + lineEnd);
                    wr.writeBytes(lineEnd);
                    wr.writeBytes(b[i]);
                    wr.writeBytes(lineEnd);
                    // Log.e("log_tag", twoHyphens + boundary + lineEnd+"Content-Disposition: form-data; name=\""+a[i]+"\"" + lineEnd  +lineEnd+b[i]+lineEnd);
                }

                wr.writeBytes(twoHyphens+boundary+twoHyphens);
                Log.e("log_tag", "pre flush");
                wr.flush();
                //OutputStream output = urlConnection.getOutputStream();
                Log.e("log_tag", "finsih flush");
                wr.close();
                Log.e("log_tag", "close");

                //接通資料庫

                int res = urlConnection.getResponseCode();
                Log.e("log_tag", "response code:"+res);




            }
            catch(Exception e)
            {

                Log.e("log_tag", e.toString());
                if(e.toString().equals("java.io.IOException: unexpected end of stream on Connection{192.168.1.1:80, proxy=DIRECT hostAddress=192.168.1.1 cipherSuite=none protocol=http/1.1} (recycle count=0)"))
                    return "Sucess!!";
                else return "Fail "+e.toString();

            }
            finally {

                urlConnection.disconnect();
            }
            return "Fail";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            try {
                waitDialog.dismiss();
            }catch (Exception e)
            {
                e.toString();
            }
            if(aVoid.equals("Sucess!!"))
            {
               showDialogMessage("Connect Over","Switch Device On N",3);
            }
            else if(aVoid.equals("Fail"))
            {
                Log.e("log",aVoid);
            }
            else
                {
                    Log.e("log",aVoid);
                }

        }
    }


    //建立金鑰




}
