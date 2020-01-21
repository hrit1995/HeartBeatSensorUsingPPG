package com.example.heartrate;

import android.os.Bundle;
import com.alibaba.fastjson.JSON;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


public class MainActivity extends AppCompatActivity {

    LineChartView lineChartView;
    String str;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawThread();
    }

    private void drawThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                lineChartView = findViewById(R.id.chart);
                List yAxisValues = new ArrayList();
                List axisValues = new ArrayList();
                startNetThread();
                Map map = JSON.parseObject(str);
                TreeMap mapTypes = new TreeMap<Integer, String>();
                System.out.println("这个是用JSON类的parseObject来解析JSON字符串!!!");
                for (Object obj : map.keySet()) {
                    mapTypes.put(Integer.valueOf(String.valueOf(obj)), map.get(obj));
                    System.out.println("key为：" + obj + "值为：" + map.get(obj));
                }
                Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

                int counter = 0;
                for (Object obj : mapTypes.keySet()) {
                    axisValues.add(counter, new AxisValue(counter).setLabel(String.valueOf(obj)));
                    System.out.println(Float.valueOf((String) JSON.parseArray(mapTypes.get(obj).toString()).get(0)));
                    yAxisValues.add(new PointValue(counter, Float.valueOf((String) JSON.parseArray(mapTypes.get(obj).toString()).get(0))));
                    counter++;
                }

                List lines = new ArrayList();
                lines.add(line);

                LineChartData data = new LineChartData();
                data.setLines(lines);

                Axis axis = new Axis();
                axis.setValues(axisValues);
                axis.setTextSize(16);
                axis.setTextColor(Color.parseColor("#03A9F4"));
                data.setAxisXBottom(axis);

                Axis yAxis = new Axis();
                yAxis.setName("Sales in millions");
                yAxis.setTextColor(Color.parseColor("#03A9F4"));
                yAxis.setTextSize(16);
                data.setAxisYLeft(yAxis);

                lineChartView.setLineChartData(data);
                lineChartView.setViewportCalculationEnabled(false);
                lineChartView.setZoomEnabled(false);
                Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
                viewport.top = 200;
                lineChartView.setMaximumViewport(viewport);
                lineChartView.setCurrentViewport(viewport);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List yAxisValues = new ArrayList();
                        List axisValues = new ArrayList();
                        startNetThread();
                        Map map = JSON.parseObject(str);
                        TreeMap mapTypes = new TreeMap<Integer, String>();
                        for (Object obj : map.keySet()) {
                            mapTypes.put(Integer.valueOf(String.valueOf(obj)), map.get(obj));
                            //System.out.println("key：" + obj + "value：" + map.get(obj));
                        }
                        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

                        int counter = 0;
                        for (Object obj : mapTypes.keySet()) {
                            axisValues.add(counter, new AxisValue(counter).setLabel(String.valueOf(obj)));
                            System.out.println(Float.valueOf((String) JSON.parseArray(mapTypes.get(obj).toString()).get(0)));
                            yAxisValues.add(new PointValue(counter, Float.valueOf((String) JSON.parseArray(mapTypes.get(obj).toString()).get(0))));
                            counter++;
                        }

                        List lines = new ArrayList();
                        lines.add(line);

                        LineChartData data = new LineChartData();
                        data.setLines(lines);

                        Axis axis = new Axis();
                        axis.setValues(axisValues);
                        axis.setTextSize(16);
                        axis.setTextColor(Color.parseColor("#03A9F4"));
                        data.setAxisXBottom(axis);

                        Axis yAxis = new Axis();
                        yAxis.setName("BPM");
                        yAxis.setTextColor(Color.parseColor("#03A9F4"));
                        yAxis.setTextSize(16);
                        data.setAxisYLeft(yAxis);

                        lineChartView.setLineChartData(data);
                        handler.postDelayed(this, 2000);
                    }
                });
            }
        };
        thread.start();
    }

    private void startNetThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("18.188.7.129", 8081);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(("test").getBytes());
                    outputStream.flush();
                    socket.shutdownOutput();
                    InputStream is = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int n = is.read(bytes);
                    str = new String(bytes, 0, n);
                    //updateTextView(str);
                    is.close();
                    socket.close();
                } catch (Exception e) {
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    public void updateTextView(String toThis) {
//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText(toThis);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
