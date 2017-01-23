package cn.hexing.fdm.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.hexing.fdm.protocol.bll.dlmsService.DataType;
import cn.hexing.fdm.protocol.comm.CommOpticalSerialPort;
import cn.hexing.fdm.protocol.comm.CommTcp;
import cn.hexing.fdm.protocol.dlms.HXHdlcDLMS;
import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.iprotocol.IProtocol;
import cn.hexing.fdm.protocol.model.CommPara;
import cn.hexing.fdm.protocol.model.HXFramePara;
import cn.hexing.fdm.protocol.model.HXFramePara.AuthMethod;
import cn.hexing.fdm.protocol.model.HXFramePara.AuthMode;
import cn.hexing.fdm.services.CommServer;

import com.android.SerialPort.SerialPort;

//import android.provider.Settings.System;

public class TestSerialportActivity extends Activity implements OnClickListener {
    FileDescriptor mfd;
    protected SerialPort mSerialPort = new SerialPort();
    protected OutputStream mOutputStream;
    private InputStream mInputStream;

    private Button buttonsend;
    private EditText EditTDisplay;

    boolean m_stop = false;
    final static int IOCTRL_PMU_RFID_ON = 0x03;
    final static int IOCTRL_PMU_RFID_OFF = 0x04;
    final static int IOCTRL_PMU_BARCODE_ON = 0x05;
    final static int IOCTRL_PMU_BARCODE_OFF = 0x06;
    final static int IOCTRL_PMU_BARCODE_TRIG_HIGH = 0x11;
    final static int IOCTRL_PMU_BARCODE_TRIG_LOW = 0x12;
    final static int IOCTRL_PMU_RS232_ON = 0x17;
    final static int IOCTRL_PMU_RS232_OFF = 0x18;
    final static int IOCTRL_PMU_RFID_GPIOEXT_HIGH = 0x13;
    final static int IOCTRL_PMU_RFID_GPIOEXT_LOW = 0x14;
    private String uartpath;
    private SharedPreferences localSharedPreferences;
    ICommucation icomm = new CommOpticalSerialPort();
    IProtocol DLMSProtocol = new HXHdlcDLMS();

    private boolean Openport = false;
    private ArrayAdapter<String> adapter;
    String baudrate;
    final String setting_file = "/mnt/sdcard/sendtext.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        EditTDisplay = (EditText) findViewById(R.id.EditTDisplay);
        buttonsend = (Button) findViewById(R.id.btn_send);



        buttonsend.setOnClickListener(this);
        localSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String strTransmit = "";
                CommServer commDlmsServer = null;

                try {
                    //执行上电操作
//                    RS232Controller mPower232 = new RS232Controller();
//                    mPower232.Rs232_PowerOn();

                    //执行RF上电操作
                    PowerSecurityController mPowerController = new PowerSecurityController();
                    mPowerController.openDevice();

                    // 光电头通讯参数设置
                    uartpath = "/dev/ttySAC1";// localSharedPreferences.getString("path",
                    // GetDeviceDefaultpath());
                    int baudrate = Integer.valueOf((localSharedPreferences
                            .getString("baudrate", "4800")));
                    int nBits = Integer.valueOf(localSharedPreferences.getString(
                            "nBits", "8"));
                    String sVerify = localSharedPreferences.getString("nVerify",
                            "N");
                    char cVerify = sVerify.charAt(0);
                    int nStop = Integer.valueOf(localSharedPreferences.getString(
                            "nStop", "1"));
                    icomm = new CommTcp();
//                    CommPara Cpara = new CommPara();
//                    Cpara.setComName(uartpath);
//                    Cpara.setBRate(baudrate);
//                    Cpara.setDBit(nBits);
//                    Cpara.setPty(cVerify);
//                    Cpara.setSbit(nStop);

                    // DLMS 通讯参数
                    HXFramePara FramePara = new HXFramePara();
                    FramePara.CommDeviceType = "Optical";// RF  Optical
                    FramePara.FirstFrame = true;
                    FramePara.Mode = AuthMode.HLS;
                    FramePara.enLevel = 0x00;
                    FramePara.SourceAddr = 0x03;
                    FramePara.strMeterNo = "014254455455";
                    FramePara.WaitT = 3000;
                    FramePara.ByteWaitT = 1500;
                    FramePara.Pwd = "00000000";
                    FramePara.aesKey = new byte[16];
                    FramePara.auKey = new byte[16];
                    FramePara.enKey = new byte[16];
                    String sysTstr = "4845430005000001";
                    FramePara.StrsysTitleC = "4845430005000001";
                    FramePara.encryptionMethod = AuthMethod.AES_GCM_128;
                    FramePara.sysTitleS = new byte[8];
                    FramePara.MaxSendInfo_Value = 255;
                    EditTDisplay.setText("");
                    commDlmsServer = new CommServer();
                    //icomm = commDlmsServer.OpenDevice(Cpara, icomm);
                    FramePara.OBISattri = "3#1.0.1.8.0.255#2";//Monthly Billing Date
                     FramePara.strDecDataType = "U32";
                    String strRead =commDlmsServer.Read(FramePara, icomm);
                    Toast.makeText(getApplicationContext(), strRead,
                           Toast.LENGTH_SHORT).show();
                    if (FramePara.CommDeviceType == "Optical") {
                        commDlmsServer.DiscFrame(icomm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (icomm != null) {

                        commDlmsServer.Close(icomm);

                    }
                }


                break;

        }
    }

    public static byte[] toByteArray(String hexString) {
        hexString = hexString.toUpperCase();
        final byte[] byteArray = new byte[(hexString.length() + 1) / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    char HexChar(char c) {
        if ((c >= '0') && (c <= '9'))
            return (char) (c - 0x30);
        else if ((c >= 'A') && (c <= 'F'))
            return (char) (c - 'A' + 10);
        else if ((c >= 'a') && (c <= 'f'))
            return (char) (c - 'a' + 10);
        else
            return 0x10;
    }

    // 将一个字符串作为十六进制串转化为一个字节数组，字节间可用空格分隔，
    // 返回转换后的字节数组长度，同时字节数组长度自动设置。
    byte[] Str2Hex(String str) {
        int t, t1;
        int rlen = 0, len = str.length();
        final byte[] byteArray = new byte[str.length()];
        // data.SetSize(len/2);
        for (int i = 0; i < len; ) {
            char l, h = str.charAt(i);
            if (h == ' ') {
                i++;
                continue;
            }
            i++;
            if (i >= len)
                break;
            l = str.charAt(i);
            t = HexChar(h);
            t1 = HexChar(l);
            if ((t == 16) || (t1 == 16))
                break;
            else
                t = t * 16 + t1;
            i++;
            byteArray[rlen] = (byte) t;
            rlen++;
        }

        final byte[] byteArray1 = new byte[rlen];
        System.arraycopy(byteArray, 0, byteArray1, 0, rlen);

        return byteArray1;

    }

    int dev_num = 0;
    private String deviceType = " ";

    public static String toHexString(byte[] byteArray, int size) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException(
                    "this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder(2 * size);
        for (int i = 0; i < size; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
            if (i != (byteArray.length - 1))
                hexString.append(" ");
        }
        return hexString.toString().toUpperCase();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("0122", keyCode + "");

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mSerialPort != null) {

                mSerialPort.SetPowerState(IOCTRL_PMU_BARCODE_OFF);

                mSerialPort.SetPowerState(IOCTRL_PMU_RFID_OFF);
                mSerialPort.SetPowerState(IOCTRL_PMU_RFID_GPIOEXT_LOW);
                mSerialPort.SetPowerState(IOCTRL_PMU_RS232_OFF);
                mSerialPort.SetPowerState(IOCTRL_PMU_BARCODE_TRIG_LOW);
                if (mOutputStream != null)
                    mSerialPort.close();
                mSerialPort = null;
            }
            m_stop = true;
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private String readFile(String filepath) {
        String path = filepath;
        String str = "";
        if (null == path) {
            Log.d("012", "Error: Invalid file name!");
            return null;
        }

        File f = new File(path);
        if (!f.exists()) {
            return null;
        } else if (f != null && f.exists()) {

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                // Log.d(TAG, "Error: Input File not find!");
                return null;
            }

            try {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(fis, "utf-8"));
                try {

                    String tempString = "";

                    // 一次读入一行，直到读入null为文件结束
                    while ((tempString = input.readLine()) != null) {
                        str += EncodingUtils.getString(
                                tempString.getBytes("utf-8"), "utf-8")
                                + "\n";

                    }
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return str;
    }

    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}