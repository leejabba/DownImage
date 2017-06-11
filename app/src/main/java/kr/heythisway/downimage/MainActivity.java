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
                        // URL 객체로부터 스트림을 열고
                        InputStream is = new URL(addr).openStream();
                        // BitmapFactory의 decodeStream 메서드를 호출하면
                        // 원격지의 이미지 스트림을 읽어 비트맵 객체로 변환한다.
                        Bitmap bit = BitmapFactory.decodeStream(is);
                        is.close();

                        // 변환된 비트맵을 핸들러로 보내 레이아웃의 이미지뷰에 출력
                        Message message = mAfterDown.obtainMessage();
                        // 비트맵 값을 메시지로 넘기기 위해 오브젝트 형식으로 담아보낸다.
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
