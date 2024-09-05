package View;

import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.json.JSONArray;

/**
 * Renders the chart created by Control,
 * allows for user input via ComboBox
 */
public class View {
    JFrame jframe;
    String[] assetList;
    String[] timeIntervals;
    JComboBox<String> comboBox;
    JComboBox<String> timeBox;
    JPanel jpanel;
    JSONArray json;
    String coin;
    ChartPanel chartPanel;

    /**
     * Creates a JFrame displaying the OHLC chart passed as parameter,
     * allows users to select assets via ComboBox
     * @param json JSONArray
     * @param chart JFreeChart
     */
    public View(JSONArray json, JFreeChart chart){
        this.json = json;
        this.jframe = new JFrame("OHLC Data Plot");
        this.jframe.setSize(800, 600);
        this.assetList = new String[]{
                "Bitcoin",
                "Ethereum",
                "Solana"
        };

        this.timeIntervals = new String[]{
                "7d", "30d", "1y"
        };
        this.coin = "Bitcoin";
        this.comboBox = new JComboBox<String>(assetList);
        this.timeBox = new JComboBox<>(timeIntervals);
        this.timeBox.setSelectedIndex(2);
        this.jpanel = new JPanel();
        this.jpanel.add(comboBox);
        this.jpanel.add(timeBox);

        this.chartPanel = new ChartPanel(chart);
        this.jframe.add(chartPanel, BorderLayout.CENTER);
        this.jframe.add(this.jpanel, BorderLayout.NORTH);
        this.jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jframe.setLocationRelativeTo(null);
        this.jframe.validate();
        this.jframe.setVisible(true);
    }

    /**
     * Exposes the asset selection input to attach an event listener via Control
     * @return JComboBox<String>
     */
    public JComboBox<String> getCoinComboBox() {
        return this.comboBox;
    }

    public JComboBox<String> getTimeIntervalComboBox() {return this.timeBox;}

    /**
     * Removes the existing chart panel & adds a new one with the chart passed as param
     * @param chart JFreeChart
     */
    public void repaint(JFreeChart chart) {
        this.jframe.remove(chartPanel);
        this.chartPanel = new ChartPanel(chart);
        this.jframe.add(chartPanel, BorderLayout.CENTER);
        this.jpanel.revalidate();
        this.jpanel.repaint();
    }
}
