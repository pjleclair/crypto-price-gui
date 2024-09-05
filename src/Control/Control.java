package Control;

import Requests.Service;
import Model.Model;
import View.View;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.json.JSONArray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.Thread.sleep;

/**
 * Controls the Requests, Model, and View components
 */
public class Control {
    Service service;
    Model model;
    View view;
    String jsonString;
    String selectedCoin;
    JFreeChart chart;
    DefaultHighLowDataset dataset;
    ZonedDateTime timestamp;
    int viewInterval;

    /**
     * Instantiates a network service object and fetches data,
     * creates a chart with the fetched data,
     * then instantiates a View object with the JSON data & chart.
     * Adds an action listener to View object's Coin ComboBox to fetch data for the chosen asset.
     * Adds an action listener to View object's Interval ComboBox to fetch data for the chosen interval.
     */
    public Control() {

        this.service = new Service();
        this.selectedCoin = "Bitcoin";
        this.viewInterval = 2;
        this.fetchAllData();
        this.dataset = createDataset();
        this.chart = createChart(dataset);
        JSONArray jsonArray = new JSONArray(this.jsonString);
        this.view = new View(jsonArray, this.chart);

        this.view.getCoinComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCoin = view.getCoinComboBox().getSelectedItem().toString();
                if (model.isAlphaDecayed(timestamp)) {
                    System.out.println("Alpha Decayed! Fetching more data...");
                    fetchAllData();
                } else {
                    String[][] data = {
                            model.getDataCache()[0],
                            model.getDataCache()[1],
                            model.getDataCache()[2]
                    };
                    switch (selectedCoin.toLowerCase()) {
                        case "bitcoin":
                            jsonString = data[0][viewInterval];
                            break;
                        case "ethereum":
                            jsonString = data[1][viewInterval];
                            break;
                        case "solana":
                            jsonString = data[2][viewInterval];
                            break;
                        default:
                            jsonString = data[0][viewInterval];
                            break;
                    }
                    model = new Model(data, selectedCoin.toLowerCase(), viewInterval);
                }
                dataset = createDataset();
                chart = createChart(dataset);
                view.repaint(chart);
            }
        });

        this.view.getTimeIntervalComboBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedInterval = view.getTimeIntervalComboBox().getSelectedItem().toString();
                selectedCoin = view.getCoinComboBox().getSelectedItem().toString();
                String interval;
                switch (selectedInterval.toLowerCase()) {
                    case "7d":
                        interval = "7";
                        viewInterval = 0;
                        break;
                    case "30d":
                        interval = "30";
                        viewInterval = 1;
                        break;
                    case "1y":
                        interval = "365";
                        viewInterval = 2;
                        break;
                    default:
                        interval = "365";
                        viewInterval = 2;
                        break;
                }
                fetchData(interval);
                dataset = createDataset();
                chart = createChart(dataset);
                view.repaint(chart);
            }
        });
    }

    /**
     * Converts the fetched JSON Array into a dataset usable for JFreeChart
     * @return DefaultHighLowDataset
     */
    private DefaultHighLowDataset createDataset(){
        int itemCount = this.model.getJsonArray().length();
        Date[] date = new Date[itemCount];
        double[] high = new double[itemCount];
        double[] low = new double[itemCount];
        double[] open = new double[itemCount];
        double[] close = new double[itemCount];
        double[] volume = new double[itemCount];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        for (int i = 0; i < itemCount; i++) {
            JSONArray obj = this.model.getJsonArray().getJSONArray(i);
            date[i] = new Date(obj.getLong(0));
            open[i] = obj.getDouble(1);
            high[i] = obj.getDouble(2);
            low[i] = obj.getDouble(3);
            close[i] = obj.getDouble(4);
            volume[i] = 0; //not included in API response data
        }

        return new DefaultHighLowDataset("OHLC Data", date, high, low, close, open, volume);
    }

    /**
     * Creates a candlestick (OHLC) chart using the reformatted data from createDataset
     * @param dataset DefaultHighLowDataset
     * @return JFreeChart
     */
    private JFreeChart createChart(DefaultHighLowDataset dataset) {
        JFreeChart chart = ChartFactory.createCandlestickChart(
                this.selectedCoin + " OHLC Data", "Time", "Price",
                dataset, false
        );
        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(new CandlestickRenderer());

        return chart;
    }

    /**
     * Fetches a JSON string using the instantiated Service object & instantiates Model
     * Updates only one data point for the specified time interval
     * @param interval String
     */
    public void fetchData(String interval) {
        try {
            String[][] data = {
                    model.getDataCache()[0],
                    model.getDataCache()[1],
                    model.getDataCache()[2]
            };

            if (model.isAlphaDecayed(this.timestamp)) {
                this.jsonString = this.service.fetchData(this.selectedCoin.toLowerCase(), interval);
                this.timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
                switch (this.selectedCoin.toLowerCase()) {
                    case "bitcoin":
                        data[0][viewInterval] = this.jsonString;
                        break;
                    case "ethereum":
                        data[1][viewInterval] = this.jsonString;
                        break;
                    case "solana":
                        data[2][viewInterval] = this.jsonString;
                        break;
                }
            }

            this.model = new Model(data, this.selectedCoin.toLowerCase(), viewInterval);

        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Fetches data for all coins at all intervals & instantiates a Model
     */
    public void fetchAllData() {
        try {
            String[][] dataArray = new String[3][3];
            String[] coins = {"bitcoin", "ethereum", "solana"};
            String[] intervals = {"7", "30", "365"};
            System.out.println("Fetching all data...");
            for (int i = 0; i < coins.length; i++) {
                System.out.println("Fetching coin: " + coins[i]);
                for (int j = 0; j < intervals.length; j++) {
                    System.out.println("Fetching interval: " + intervals[j]);
                    dataArray[i][j] = this.service.fetchData(coins[i], intervals[j]);
                    sleep(20000); // Optionally sleep in between each batch to mitigate rate limit
                }
            }

            this.timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
            switch (this.selectedCoin.toLowerCase()) {
                case "bitcoin":
                    this.jsonString = dataArray[0][viewInterval];
                    break;
                case "ethereum":
                    this.jsonString = dataArray[1][viewInterval];
                    break;
                case "solana":
                    this.jsonString = dataArray[2][viewInterval];
                    break;
                default:
                    this.jsonString = dataArray[1][viewInterval];
                    break;
            }
            this.model = new Model(dataArray, this.selectedCoin.toLowerCase(), viewInterval);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

}
