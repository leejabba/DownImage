package kr.heythisway.downimage;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
                String imageUrl = "http://www.soen.kr/data/child3.jpg";
                // url에서 경로를 빼고 파일명만 추출하여 전달하는 과정
                int idx = imageUrl.lastIndexOf('/');
                String localImage = imageUrl.substring(idx + 1);
                String path = Environment.getDataDirectory().getAbsolutePath();
                path = path + "/data/kr.heythisway.downimage/files/" + localImage;

                // 파일이 없을때만 네트워크에 연결해 다운로드 받고 이미지뷰에 출력한다.
                if (new File(path).exists()) {
                    Toast.makeText(MainActivity.this, "비트맵이 존재합니다.", Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                } else {
                    Toast.makeText(MainActivity.this, "비트맵이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    (new DownThread(imageUrl, localImage)).start();
                }
            }

            // 핸들러
            Handler mAfterDown = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.obj != null) {
                        String path = Environment.getDataDirectory().getAbsolutePath();
                        path = path + "/data/kr.heythisway.downimage/files/" + msg.obj;
                        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                    } else {
                        Toast.makeText(MainActivity.this, "파일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            
            // 커스텀 스레드 클래스 설정
            class DownThread extends Thread {
                String addr;

                String fileName;

                public DownThread(String addr, String fileName) {
                    this.addr = addr;
                    this.fileName = fileName;
                }

                @Override
                public void run() {
                    super.run();
                    URL imageUrl;
                    int read;

                    try {
                        imageUrl = new URL(addr);
                        HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                        // getContentLength는 응답 결과의 크기를 바이트 단위로 조사하는데
                        int len = conn.getContentLength();
                        // 조사된 길이만큼 바이트 배열을 할당하고 이 배열에 읽어들인다.
                        byte[] raster = new byte[len];
                        // 로컬 파일 스트림에 기록하여 복사
                        InputStream is = conn.getInputStream();
                        FileOutputStream fos = openFileOutput(fileName, 0);
                        
                        for (;;) {
                            read = is.read(raster);
                            if (read <= 0) {
                                break;
                            }
                            fos.write(raster, 0, read);
                        }
                        
                        is.close();
                        fos.close();
                        conn.disconnect();
                        
                    } catch (java.io.IOException e) {
                        fileName = null;
                    }

                    // 다운로드에 성공했으면 핸들러로 신호를 보내 로컬에 저장된 비트맵을 이미지뷰에 출력
                    Message msg = mAfterDown.obtainMessage();
                    msg.obj = fileName;
                    mAfterDown.sendMessage(msg);
                }
            }
        });
    }
}
