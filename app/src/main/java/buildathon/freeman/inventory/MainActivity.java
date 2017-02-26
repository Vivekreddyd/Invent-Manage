package buildathon.freeman.inventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_IP = "192.168.240.1"; //server IP address
    public static final int SERVER_PORT = 5555;
    public String response = "";
    public String input_rfid="";
    public boolean conn_to_server = false;
    BlockingQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //new ServerTask().execute("");
        thread.start();
//        AnotherClass object= new AnotherClass (this);
//        object.start();
    }

    public void start_intent(){
        IntentIntegrator qrscan_intent = new IntentIntegrator(this);
        qrscan_intent.initiateScan();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //int result=Integer.parseInt(response);
        //String sb;
        //sb=Integer.toString(result);

        String scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Context context = getApplicationContext();
        System.out.println("Scan Result"+scanResult);
        System.out.println("Input" + input_rfid);

        if(scanResult!=null) {
            if (input_rfid.equals(scanResult)) {
                CharSequence text = "Matches with RFID";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.GREEN);
                toast.show();
            } else if (!input_rfid.equals(scanResult)) {
                CharSequence text = "Does not match with RFID";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.RED);
                toast.show();
            }
        }
        IntentIntegrator qrscan_intent = new IntentIntegrator(this);
        qrscan_intent.initiateScan();


    }

    public void setInput(String str){
        input_rfid = str;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView myTextView =
                    (TextView)findViewById(R.id.myTextView);
            myTextView.setText("Button Pressed");
        }
    };

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                //Log.e("TCP Client", "C: Connecting...");

                //create a socket to make the connection with the server
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);

                byte[] buffer = new byte[8];

                final InputStream inputStream = socket.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
                while ((inputStream.read(buffer)) != -1) {
                    response = null;
                    response = bytesToHex(buffer);
                    System.out.println(response);
                    start_intent();
                }
			/*
             * notice: inputStream.read() will block if no data return
			 */

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars,0,8);
    }
    public class AnotherClass extends Thread {

        MainActivity mainActivity;

        public AnotherClass (MainActivity mainActivity) {
            // TODO Auto-generated constructor stub

            this.mainActivity = mainActivity;
        }

        public void run() {
            try{
            //write other logic
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);

            byte[] buffer = new byte[8];

            InputStream inputStream = socket.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
            while ((inputStream.read(buffer)) != -1) {
                response = null;
                response = bytesToHex(buffer);
                System.out.println(response);
                start_intent();
                mainActivity.setInput(response);
                queue.put(response);
            }
			/*
             * notice: inputStream.read() will block if no data return
			 */

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //write other logic
        }
    }

    private class ServerTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                //Log.e("TCP Client", "C: Connecting...");

                //create a socket to make the connection with the server
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);

                byte[] buffer = new byte[8];

                final InputStream inputStream = socket.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
                while ((inputStream.read(buffer)) != -1) {
                    response = null;
                    response = bytesToHex(buffer);
                    System.out.println(response);
                    publishProgress(response);
                    start_intent();
                }
			/*
             * notice: inputStream.read() will block if no data return
			 */

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Executed";
        }



        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(String... values) {
            System.out.println(Arrays.toString(values));
            input_rfid = Arrays.toString(values);
            System.out.println(input_rfid);
        }
    }
}
