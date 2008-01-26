package se.sics.mspsim.extutil.jfreechart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import se.sics.mspsim.chip.CC2420;
import se.sics.mspsim.core.MSP430;
import se.sics.mspsim.util.OperatingModeStatistics;
import se.sics.mspsim.util.StackMonitor;

@SuppressWarnings("serial")
public class DataChart extends JPanel {

  private TimeSeriesCollection dataset;
  
  public DataChart(String title, String yaxis) {
    DateAxis domain = new DateAxis("Time");
    NumberAxis range = new NumberAxis(yaxis);
    XYPlot xyplot = new XYPlot();
    xyplot.setDomainAxis(domain);
    xyplot.setRangeAxis(range);
 // xyplot.setBackgroundPaint(Color.black);
    xyplot.setDataset(dataset = new TimeSeriesCollection());

    DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
    renderer.setSeriesPaint(0, Color.red);
    renderer.setSeriesPaint(1, Color.green);
    renderer.setSeriesPaint(2, Color.blue);
    renderer.setSeriesPaint(3, Color.black);
//    renderer.setBaseStroke(
//        new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)
//    );
    renderer.setSeriesShapesVisible(0, false);
    renderer.setSeriesShapesVisible(1, false);
    renderer.setSeriesShapesVisible(2, false);
    renderer.setSeriesShapesVisible(3, false);
    xyplot.setRenderer(renderer);
    
    domain.setAutoRange(true);
    domain.setLowerMargin(0.0);
    domain.setUpperMargin(0.0);

    domain.setTickLabelsVisible(true);
    range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    JFreeChart chart = new JFreeChart(title, 
        JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
    ChartPanel chartPanel = new ChartPanel(chart);
    setLayout(new BorderLayout());
    add(chartPanel, BorderLayout.CENTER);
  }

  public void addTimeSeries(TimeSeries ts) {
    dataset.addSeries(ts);
  }
  
  public void openFrame(String name) {
    JFrame jw = new JFrame(name);
    jw.add(this);
    jw.setBounds(100, 100, 400, 200);
    jw.setVisible(true);
  }
  
  public void setupStackFrame(MSP430 cpu) {
    openFrame("Stack Monitor");
    StackMonitor sm = new StackMonitor(cpu);
    DataSourceSampler dss = new DataSourceSampler();
    TimeSeries ts = new TimeSeries("Max Stack", Millisecond.class);
    ts.setMaximumItemCount(200);
    addTimeSeries(ts);
    dss.addDataSource(sm.getMaxSource(), ts);
    ts = new TimeSeries("Stack", Millisecond.class);
    ts.setMaximumItemCount(200);
    addTimeSeries(ts);
    dss.addDataSource(sm.getSource(), ts);
  }
  
  public void setupChipFrame(OperatingModeStatistics oms) {
    openFrame("Duty-Cycle Monitor");
    DataSourceSampler dss = new DataSourceSampler();
    dss.setInterval(50);
    TimeSeries ts = new TimeSeries("LEDS", Millisecond.class);
    ts.setMaximumItemCount(200);
    addTimeSeries(ts);
    dss.addDataSource(oms.getMultiDataSource("Tmote Sky"), ts);

    ts = new TimeSeries("Listen", Millisecond.class);
    ts.setMaximumItemCount(200);
    addTimeSeries(ts);
    dss.addDataSource(oms.getDataSource("CC2420", CC2420.MODE_RX_ON), ts);

    ts = new TimeSeries("Transmit", Millisecond.class);
    ts.setMaximumItemCount(200);
    addTimeSeries(ts);
    dss.addDataSource(oms.getDataSource("CC2420", CC2420.MODE_TXRX_ON), ts);

    ts = new TimeSeries("CPU", Millisecond.class);
    ts.setMaximumItemCount(200);
    addTimeSeries(ts);
    dss.addDataSource(oms.getDataSource("MSP430 Core", MSP430.MODE_ACTIVE), ts);
    
  }
}
