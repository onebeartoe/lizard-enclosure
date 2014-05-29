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
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.onebeartoe.lizard.enclosure.arduino.ArduinoMessage;

/**
 * A chart that fills in the multiple areas between lines of data points and the axes.
 * Allows for comparing sensor values totals over time.
 */
public class SensorReadingsVisualizer extends Application 
{
    long lastId = 0;
    
    private static final int MAX_DATA_POINTS = 50;

    private Series internalTemperatureSeries;
    private ConcurrentLinkedQueue<Number> internalTemperatureMessageQueue = new ConcurrentLinkedQueue();
    
    private Series internalHumiditySeries;
    private ConcurrentLinkedQueue<Number> internalHumidityMessageQueue = new ConcurrentLinkedQueue();
    
    private Series externalTemperatureSeries;
    private ConcurrentLinkedQueue<Number> externalTemperatureMessageQueue = new ConcurrentLinkedQueue();
    
    private int xSeriesData = 0;
        
    private ExecutorService executor;
    private AddToQueue addToQueue;

    private NumberAxis xAxis;    
    
    private Logger logger;

    private final long dataRefreshDelay = 2000;
    
    private void addDataToSeries()
    {
        if( !internalTemperatureMessageQueue.isEmpty() )
        {
            AreaChart.Data data = new AreaChart.Data(xSeriesData++, internalTemperatureMessageQueue.remove());
            internalTemperatureSeries.getData().add(data);
        }        
        // remove points to keep us at no more than MAX_DATA_POINTS
        if(internalTemperatureSeries.getData().size() > MAX_DATA_POINTS) 
        {
            internalTemperatureSeries.getData().remove(0, internalTemperatureSeries.getData().size() - MAX_DATA_POINTS);
        }
        
        if( !internalHumidityMessageQueue.isEmpty() )
        {
            AreaChart.Data data = new AreaChart.Data(xSeriesData-1, internalHumidityMessageQueue.remove() );
            internalHumiditySeries.getData().add(data);
        }
        if(internalHumiditySeries.getData().size() > MAX_DATA_POINTS)
        {
            internalHumiditySeries.getData().remove(0, internalHumiditySeries.getData().size() - MAX_DATA_POINTS);
        }
        
        if( !externalTemperatureMessageQueue.isEmpty() )
        {
            AreaChart.Data data = new AreaChart.Data(xSeriesData-1, externalTemperatureMessageQueue.remove() );
            externalTemperatureSeries.getData().add(data);
        }
        if(externalTemperatureSeries.getData().size() > MAX_DATA_POINTS)
        {
            externalTemperatureSeries.getData().remove(0, externalTemperatureSeries.getData().size() - MAX_DATA_POINTS);
        }
                
        // update 
        xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData-1);
    }
    
    private void addDataToSeriesOriginal() 
    {
        // add 20 numbers to the plot+
        for(int i = 0; i < 20; i++) 
        { 
            if( internalTemperatureMessageQueue.isEmpty() ) 
            {
                break;
            }
                   
            AreaChart.Data data = new AreaChart.Data(xSeriesData++, internalTemperatureMessageQueue.remove());
            internalTemperatureSeries.getData().add(data);
        }
        
        // remove points to keep us at no more than MAX_DATA_POINTS
        if(internalTemperatureSeries.getData().size() > MAX_DATA_POINTS) 
        {
            internalTemperatureSeries.getData().remove(0, internalTemperatureSeries.getData().size() - MAX_DATA_POINTS);
        }
        
        // update 
        xAxis.setLowerBound(xSeriesData-MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData-1);
    }
    
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
        sc.setTitle("Lizard Enclosure Sensor Readings");

        // internal temperature Series
        internalTemperatureSeries = new AreaChart.Series<Number, Number>();
        internalTemperatureSeries.setName("Internal Temperature");
        sc.getData().add(internalTemperatureSeries);        
        
        externalTemperatureSeries = new AreaChart.Series<Number, Number>();
        externalTemperatureSeries.setName("External Temperature");
        sc.getData().add(externalTemperatureSeries);
        
        internalHumiditySeries = new AreaChart.Series<Number, Number>();
        internalHumiditySeries.setName("Internal Humidity");
        sc.getData().add(internalHumiditySeries);

        primaryStage.setScene(new Scene(sc));
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() 
        {
            @Override
            public void handle(WindowEvent t) 
            {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @Override 
    public void start(Stage primaryStage) throws Exception 
    {
        init(primaryStage);
        primaryStage.show();

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
        @Override
        public void run() 
        {
            try 
            {
                String u = "http://192.168.15.30:9080/lizard-enclosure/arduino/sensor/readings/raw";
//                u = "http://localhost:8080/lizard-enclosure/arduino/sensor/readings/raw/mock";
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
                            internalTemperatureMessageQueue.add(am.sensorValue);
                            System.out.println(inputLine);
                            
                            break;
                        }
                        case EXTERNAL_TEMPERATURE:
                        {
                            externalTemperatureMessageQueue.add(am.sensorValue);
                            System.out.println(inputLine);
                            
                            break;
                        }
                        case INTERNAL_HUMIDITY:
                        {
                            internalHumidityMessageQueue.add(am.sensorValue);
                            System.out.println(inputLine);
                            
                            break;
                        }
                    }
                }
                in.close();
                
                if(am != null)
                {
                    lastId = am.id;
                }

                Thread.sleep(dataRefreshDelay);
                
                executor.execute(this);
            } 
            catch(Exception ex) 
            {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Timeline gets called in the JavaFX Main thread
     */ 
    private void prepareTimeline() 
    {        
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() 
        {
            // is this the proper Thread to kill in primaryStage.setOnCloseRequest() ????
            @Override 
            public void handle(long now) 
            {
                addDataToSeries();
//                addDataToSeriesOriginal();
            }
        }.start();
    }

}
