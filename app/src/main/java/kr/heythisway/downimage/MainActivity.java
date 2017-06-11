package kr.heythisway.downimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnDown = (Button) findViewById(R.id.btnDown);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownThread("http://ppss.kr/wp-content/uploads/2015/05/창업성공51.jpg").start();
            }

            class DownThread extends Thread {
                String addr;

                public DownThread(String addr) {
                    this.addr = addr;
                }

                @Override
                public void run() {
                    super.run();
                    try {
                        InputStream is = new URL(addr).openStream();
                        Bitmap bit = BitmapFactory.decodeStream(is);
                        is.close();

                        Message message = mAfterDown.obtainMessage();
                        message.obj = bit;
                        mAfterDown.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Handler mAfterDown = new Handler() {
                    public void handleMessage(Message msg) {
                        Bitmap bit = (Bitmap) msg.obj;
                        if (bit == null) {
                            Toast.makeText(MainActivity.this, "bitmap is null", Toast.LENGTH_SHORT).show();
                        } else {
                            imageView.setImageBitmap(bit);
                        }
                    }
                };
            }
        });
    }
}
