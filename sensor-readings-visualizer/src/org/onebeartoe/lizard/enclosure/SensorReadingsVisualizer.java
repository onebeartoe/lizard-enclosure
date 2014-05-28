package org.onebeartoe.lizard.enclosure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import org.onebeartoe.lizard.enclosure.arduino.ArduinoMessage;

/**
 * A chart that fills in the area between a line of data points and the axes.
 * Good for comparing accumulated totals over time.
 * 
* @see javafx.scene.chart.Chart
 * @see javafx.scene.chart.Axis
 * @see javafx.scene.chart.NumberAxis
 * @related charts/line/LineChart
 * @related charts/scatter/ScatterChart
 */
public class SensorReadingsVisualizer extends Application 
{
    long lastId = 0;
    
    private static final int MAX_DATA_POINTS = 50;

    private Series series;
    private int xSeriesData = 0;
    private ConcurrentLinkedQueue<Number> internalTemperatureDataQueue = new ConcurrentLinkedQueue();
    private ExecutorService executor;
    private AddToQueue addToQueue;
//    private Timeline timeline2;
    private NumberAxis xAxis;    
    
    private Logger logger;

    private void init(Stage primaryStage) 
    {
        logger = Logger.getLogger(getClass().getName());
        
        xAxis = new NumberAxis(0,MAX_DATA_POINTS,MAX_DATA_POINTS/10);
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);

        // Chart
        final AreaChart<Number, Number> sc = new AreaChart<Number, Number>(xAxis, yAxis) 
        {
            @Override 
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) 
            {
                // Overriden to remove symbols on each data point
            }
        };
        sc.setAnimated(false);
        sc.setId("liveAreaChart");
        sc.setTitle("Internal Temperature Chart");

        //-- Chart Series
        series = new AreaChart.Series<Number, Number>();
        series.setName("Area Chart Series");
        sc.getData().add(series);
//        sc.getData().

        primaryStage.setScene(new Scene(sc));
    }

    @Override 
    public void start(Stage primaryStage) throws Exception 
    {
        init(primaryStage);
        primaryStage.show();

        //-- Prepare Executor Services
        executor = Executors.newCachedThreadPool();
        addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        
        prepareTimeline();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }

    private class AddToQueue implements Runnable 
    {
        public void run() 
        {
            try 
            {
                String u = "http://192.168.15.30:9080/lizard-enclosure/arduino/sensor/readings/raw";
                u = "http://localhost:8080/lizard-enclosure/arduino/sensor/readings/raw/mock";
                u += "?lastId=" + lastId;
                u += "&t=" + (new Date()).getTime();
                URL url = new URL(u);
                InputStream instream = url.openStream();
                InputStreamReader reader = new InputStreamReader(instream);
                BufferedReader in = new BufferedReader(reader);

                ArduinoMessage am = null;
                String inputLine;
                while ( (inputLine = in.readLine()) != null)
                {
                    am = ArduinoMessage.fromLine(inputLine);
                    
                    switch(am.sensorType)
                    {
                        case INTERNAL_TEMPERATURE:
                        {
                            internalTemperatureDataQueue.add(am.sensorValue);
                            System.out.println(inputLine);
                            
                            break;
                        }
                    }
                    
//                    System.out.println(inputLine);
                }
                in.close();
                
                if(am != null)
                {
                    lastId = am.id;
                }
                
//              internalTemperatureDataQueue.add(Math.random());
                
                // this is the delay between data refreshes
                Thread.sleep(2500);
                
                executor.execute(this);
            } 
            catch(Exception ex) 
            {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() 
    {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() 
        {
            @Override 
            public void handle(long now) 
            {
                addDataToSeries();
            }
        }.start();
    }

    private void addDataToSeries() 
    {
        for (int i = 0; i < 20; i++) 
        { //-- add 20 numbers to the plot+
            if (internalTemperatureDataQueue.isEmpty()) break;
            series.getData().add(new AreaChart.Data(xSeriesData++, internalTemperatureDataQueue.remove()));
        }
        
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (series.getData().size() > MAX_DATA_POINTS) 
        {
            series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
        }
        
        // update 
        xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData-1);
    }
}
