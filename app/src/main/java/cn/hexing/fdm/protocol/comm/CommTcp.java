package cn.hexing.fdm.protocol.comm;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cn.hexing.fdm.protocol.icomm.ICommucation;
import cn.hexing.fdm.protocol.model.CommPara;

/**
 * @Title: Tcp通讯类
 * @Description: Tcp Socket
 * @Copyright: Copyright (c) 2016
 * @Company 杭州海兴电力科技
 * @author 王昌豹
 * @version 1.0
 */
public class CommTcp implements ICommucation {
    private Socket socket=null;
    @Override
    public boolean OpenDevice(CommPara cpara) {
        return false;
    }

    @Override
    public boolean Close() {
        return false;
    }

    @Override
    public byte[] ReceiveByt(int SleepT, int WaitT) {

        byte[] strResult;

        GetLogTask task = new GetLogTask();
        try {
            strResult = task.execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public boolean SendByt(byte[] sndByte)  {
        try {
if(socket==null) {
    socket = new Socket("10.10.100.254", 8899);
}
            OutputStream out = socket.getOutputStream();
            out.write(sndByte);
        }
        catch(IOException ex)
        {
            String str= ex.getMessage();
        }

        return true;
    }

    @Override
    public void SetBaudRate(int Baudrate) {

    }
    public class GetLogTask extends AsyncTask<Void, Void, byte[]> {

        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {

        }


        @Override
        protected byte[] doInBackground(Void... param) {
            ArrayList arrList= new ArrayList<Byte>();
            byte[] bytResult = new byte[0];
            try {

                Socket s = new Socket("10.10.100.254", 8899);
                s.setSoTimeout(3000);
                InputStream inputStream = s.getInputStream();
                DataInputStream input = new DataInputStream(inputStream);

                byte[] b = new byte[10000];
                int length = 0;
                while ( (length =input.read(b))>0) {

                    for(int i=0;i<b.length;i++)
                    {
                        arrList.add(b[i]);
                    }

                }

                Object[] arr = arrList.toArray();
                bytResult = new byte[arr.length];

                for (int i = 0; i < arr.length; i++) {
                    bytResult[i] = (Byte) arr[i];
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return bytResult;
        }



    }


}
