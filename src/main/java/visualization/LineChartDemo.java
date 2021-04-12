package visualization;

/**
 * 使用 categoryDataset 数据集创建折线图
 * 当数据多时，在JPanel中无法完全看到横坐标的值，显示为省略号。
 * 解决方法：
 * 方法1、将报表保存为图片时，设置图片的宽度足够大（2000或3000），图片可以显示横坐标值。
 *  这种方法治标不治本，所以有了第2种方法
 * 方法2、设置X轴上的Lable让其45度倾斜。
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class LineChartDemo extends ApplicationFrame {

    private static final long serialVersionUID = -6354350604313079793L;
    static Class class$demo$LineChartDemo;
    private static DefaultCategoryDataset defaultcategorydataset;

    public LineChartDemo(String string, int[] fitnessIter) {
        super(string);
        createDataset(fitnessIter);
        JPanel jpanel = createDemoPanel();
        jpanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(jpanel);
    }

    /**
     * 如何区分不同的图例：根据DefaultCategoryDataset.addValue()的第二个参数是否相同来区分。
     * 同一个图例的数据的添加顺序影响最终的显示；不同图例的数据的添加顺序不影响最终的显示。
     * @return
     */
    private static CategoryDataset createDataset(int[] fitnessIter) {
        defaultcategorydataset = new DefaultCategoryDataset();
        //defaultcategorydataset.addValue()的参数解析：（数值，图例名，横坐标值）

        for(int i=0;i<fitnessIter.length;i++){
            defaultcategorydataset.addValue(fitnessIter[i], "BFA",String.valueOf(i));
        }
        return defaultcategorydataset;
    }

    private static JFreeChart createChart(CategoryDataset categorydataset) {
        JFreeChart jfreechart = ChartFactory.createLineChart(
                "jfreechart test",// 图表标题
                "X", // 主轴标签（x轴）
                "Y",// 范围轴标签（y轴）
                categorydataset, // 数据集
                PlotOrientation.VERTICAL,// 方向
                false, // 是否包含图例
                true, // 提示信息是否显示
                false);// 是否使用urls

        // 改变图表的背景颜色
        jfreechart.setBackgroundPaint(Color.white);
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setBackgroundPaint(Color.lightGray);
        categoryplot.setRangeGridlinePaint(Color.white);
        categoryplot.setRangeGridlinesVisible(false);

        //修改范围轴。 我们将默认刻度值 （允许显示小数） 改成只显示整数的刻度值。
        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // 设置X轴上的Lable让其45度倾斜
        CategoryAxis domainAxis = categoryplot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // 设置X轴上的Lable让其45度倾斜
        domainAxis.setLowerMargin(0.0); // 设置距离图片左端距离
        domainAxis.setUpperMargin(0.0); // 设置距离图片右端距离
        domainAxis.setVisible(false);

        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
        lineandshaperenderer.setShapesVisible(true);
        lineandshaperenderer.setDrawOutlines(true);
        lineandshaperenderer.setUseFillPaint(true);
        lineandshaperenderer.setBaseFillPaint(Color.white);
        lineandshaperenderer.setSeriesStroke(0, new BasicStroke(3.0F));
        lineandshaperenderer.setSeriesOutlineStroke(0, new BasicStroke(2.0F));
        lineandshaperenderer.setSeriesShape(0, new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
        lineandshaperenderer.setItemMargin(0.4); //设置x轴每个值的间距（不起作用？？）

        // 显示数据值
        DecimalFormat decimalformat1 = new DecimalFormat("##.##");// 数据点显示数据值的格式
        lineandshaperenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", decimalformat1));// 设置数据项标签的生成器
        lineandshaperenderer.setBaseItemLabelsVisible(true);// 基本项标签显示
        lineandshaperenderer.setBaseShapesFilled(true);// 在数据点显示实心的小图标
        return jfreechart;
    }

    public static JPanel createDemoPanel() {

        JFreeChart jfreechart = createChart(defaultcategorydataset);

        try {
            ChartUtilities.saveChartAsJPEG(
                    new File("D:/LineChartDemo.png"), //文件保存物理路径包括路径和文件名
                    // 1.0f, //图片质量 ，0.0f~1.0f
                    jfreechart, //图表对象
                    1024, //图像宽度 ，这个将决定图表的横坐标值是否能完全显示还是显示省略号
                    768);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //图像高度

        return new ChartPanel(jfreechart);
    }

//    public static void main(String[] strings) {
//        int[] testValue = new int[100];
//        Random random = new Random();
//        for(int i=0; i<100; i++){
//            testValue[i] = random.nextInt(100);
//        }
//        LineChartDemo linechartdemo = new LineChartDemo("JFreeChart - Line Chart Demo 1", testValue);
//        linechartdemo.pack();
//        RefineryUtilities.centerFrameOnScreen(linechartdemo);
//        linechartdemo.setVisible(true);
//    }

    /* synthetic */
    static Class class$(String string) {
        Class var_class;
        try {
            var_class = Class.forName(string);
        } catch (ClassNotFoundException classnotfoundexception) {
            throw new NoClassDefFoundError(classnotfoundexception.getMessage());
        }
        return var_class;
    }
}
