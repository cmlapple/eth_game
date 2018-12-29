package com.example.a347780452.ethgame;


import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCHttpClient;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button showButton,loginButton,registerButton,openButton,checkButton,balanceButton;
    private TextView textview;
    private EditText user,psd;
    private Credentials credentials;
    private Web3j web3j;
    private String address = "0xfab104e06313b46bc1af7bb5d8d6d6cbb0cbe004";
    private Mycontract mycontract;
    public static final int SHOW = 0;
    private Thread newThread;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    String a = (String) msg.obj;
                    textview.setText(textview.getText().toString()+ a + "\n");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview=(TextView)findViewById(R.id.textview);
        showButton = (Button) findViewById(R.id.showView);
        showButton.setOnClickListener(this);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        openButton = findViewById(R.id.opencase);
        checkButton = findViewById(R.id.check);
        balanceButton = findViewById(R.id.balance);
        user = findViewById(R.id.userstring);
        psd = findViewById(R.id.psdstring);
        try {
            credentials = WalletUtils.loadCredentials("", "/mnt/sdcard/Documents/keystore/UTC--2018-12-29T10-24-49.641797800Z--cef070272b4dcbeba6b6a42b12e6f771d5843693");
            // 第一个变量填入账户的密码，第二个变量填入账户文件的 path
            Toast.makeText(MainActivity.this,"钱包加载成功",Toast.LENGTH_LONG).show();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void contractload() {
            mycontract = Mycontract.load(address,web3j,credentials,Mycontract.GAS_PRICE,Mycontract.GAS_LIMIT);
            Toast.makeText(MainActivity.this,"合约加载完成",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        web3j = Web3j.build(new HttpService("http://10.0.2.2:8545"));
        Log.d("web3j","finished");
        contractload();
        conn();
    }


    private void conn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://10.0.2.2:8545";   //注意，不是127.0.0.1
                    JSONRPCHttpClient client = new JSONRPCHttpClient(url);
                    Map<String, Object> map = new HashMap();
                    List paramsList = new ArrayList();
                    map.put("json-rpc", "2.0");
                    map.put("method", "web3_clientVersion");
                    map.put("params", paramsList);
                    map.put("id", "67");
                    String st = client.callString("web3_clientVersion", map);
                    Message msg = new Message();
                    msg.what =SHOW;
                    msg.obj = st;
                    handler.sendMessage(msg);
                } catch (JSONRPCException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void login(View view) {

        try {
            final Boolean flag = mycontract.Login(user.getText().toString(), psd.getText().toString()).sendAsync().get();
            if (flag) {
                textview.setText(textview.getText().toString() + "Login succees!\n");
            }
            else  {
                textview.setText(textview.getText().toString() + "user or psd mismatched\n");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void getBalance(View view) {
        EthGetBalance ethGetBalance = null;
        try {
            ethGetBalance = web3j.ethGetBalance("0xcef070272b4dcbeba6b6a42b12e6f771d5843693", DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(ethGetBalance!=null){
            // 打印账户余额
            System.out.println(ethGetBalance.getBalance());
            // 将单位转为以太，方便查看
            System.out.println(Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER));
            textview.setText(textview.getText().toString() + Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER) + "\n");
        }
    }

    public void register(View view) {
        try {
            final TransactionReceipt transactionReceipt = mycontract.Register(user.getText().toString(), psd.getText().toString()).sendAsync().get();
            textview.setText(textview.getText().toString() + transactionReceipt.toString() + "\n");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void OpenCase(View view) {
        try {
            final TransactionReceipt transactionReceipt = mycontract.Opencase(user.getText().toString(), psd.getText().toString()).sendAsync().get();
            textview.setText(textview.getText().toString() + transactionReceipt.toString() + "\n");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void checkgun(View view) {
        try {
            final String s = mycontract.Getmygun(user.getText().toString(), psd.getText().toString()).sendAsync().get();
            textview.setText(textview.getText().toString() + s + "\n");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
