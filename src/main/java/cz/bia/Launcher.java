package cz.bia;

import javax.swing.*;

import cz.bia.model.IFunction;
import cz.bia.Yao.*;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class Launcher extends JFrame {
    IFunction[] funs = {new FirstYao(), new SecondYao(), new ThirdYao(), new FourthYao(), new FifthYao(), new SixthYao(), new SeventhYao(), new EighthYao(), new NinthYao(), new TenthYao(),
            new EleventhYao(), new TwelfthYao(), new ThirteenthYao(), new FourthteenthYao(), new FifteenthYao(), new SixteenthYao(), new SeventeenthYao(), new EighteenthYao(),
            new NineteenthYao(), new TwentiethYao(), new TwentyFirstYao()};

    String[] funsToMenu = {"1 Yao", "2 Yao", "3 Yao", "4 Yao", "5 Yao", "6 Yao", "7 Yao", "8 Yao", "9 Yao", "10 Yao", "11 Yao", "12 Yao", "13 Yao", "14 Yao NI",
            "15 Yao NI", "16 Yao", "17 Yao", "18 Yao", "19 Yao NI", "20 Yao NI", "21 Yao NI"};
    String[] algToMenu = {"", "Blind algorithm", "Simulated annealing", "Differential evolution", "SOMA" };

    private Chart chart;
    private final JPanel northPanel;
    private final JPanel centerPanel;
    private JComboBox functions;
    private JComboBox algorithms;
    private ChartScene scene;
    private Shape surface;
    private TextField popField;
    private TextField rangeFromText;
    private TextField rangeToText;
    private JCheckBox discrete;
    private int popMax;
    private int popGenerations;
    private float minRange;
    private float maxRange;
    private JButton drawIt;
    private JButton resetGraph;
    private int steps;
    private Mapper mapper;
    private Range range;
    private TextField popCountField;

    private Population population;
    private Thread thread;

    public static void main(String[] args) throws Exception {
        Launcher frame = new Launcher();
        frame.setVisible(true);
    }

    public Launcher() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new JPanel(new BorderLayout()));
        setSize(750, 600);
        northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.add(northPanel, BorderLayout.BEFORE_FIRST_LINE);
        centerPanel = new JPanel(new BorderLayout());
        this.add(centerPanel, BorderLayout.CENTER);
        this.setNorthPanel();
        this.setCenterPanel();
        this.resetChart();
        this.population = new Population();
    }

    public IFunction getSelectedFunction() {
        return funs[functions.getSelectedIndex()];
    }

    private void invalidateCanvas() {
        try {
            minRange = (float) Float.parseFloat(rangeFromText.getText());
            maxRange = (float) Float.parseFloat(rangeToText.getText());
            if (Integer.parseInt(popField.getText()) > 0) {
                popMax = Integer.parseInt(popField.getText());
                popGenerations = Integer.parseInt(popCountField.getText());
            } else {
                popMax = 0;
            }
        } catch (Exception e) {
            popGenerations = 0;
            System.out.println("Error NaN! " + e);
            minRange = (float) funs[functions.getSelectedIndex()].getMinRange();
            maxRange = (float) funs[functions.getSelectedIndex()].getMaxRange();
        }
        range = new Range(minRange, maxRange);
        steps = 50;
        mapper = new Mapper() {
            public double f(double x, double y) {
                double[] param = {x, y};
                return getSelectedFunction().calculate(param);
            }
        };
        repaintSurface();
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        this.scene.add(surface);
        this.centerPanel.revalidate();

    }

    private void startGeneration() {
        if (popGenerations > 0) {
            this.enableSettings(false);
            thread = new Thread(() -> {
                for (int i = 0; i < popGenerations; i++) {
                    System.out.println(i + ". GENERATION");
                    if (popMax > 0) {
                        this.population.randomPopulation(minRange, maxRange, popMax,this.discrete.isSelected(), this.getSelectedFunction());
                        try {
                            Thread.sleep(1000);
                            SwingUtilities.invokeAndWait(() -> {
                                this.selectAlgorithm();
                            });
                        } catch (InterruptedException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.enableSettings(true);
            });
            thread.start();
        }
    }

    private void enableSettings(boolean b) {
        algorithms.setEnabled(b);
        functions.setEnabled(b);
        discrete.setEnabled(b);
        popField.setEnabled(b);
        popCountField.setEnabled(b);
        rangeFromText.setEnabled(b);
        rangeToText.setEnabled(b);
    }

    private void selectAlgorithm() {
        int selectedAlg = algorithms.getSelectedIndex();
        boolean diff = false;
        switch (selectedAlg) {
            case 0:
                break;
            case 1:
                Coord3d best = this.population.getBest();
                drawPoint(best);
                break;
            case 2:
                diff = true;
                break;
            case 3:
                Coord3d annealing = this.population.annealing();
                drawPoint(annealing);
                break;
            case 4:
                Coord3d soma = this.population.soma();
                drawPoint(soma);
                break;

        }
        this.drawPopulation(diff);
    }

    private void drawPopulation(boolean diff) {
        Scatter scatter;
        System.out.println("Adding scatter.");
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        if(diff) {
           scatter = new Scatter(population.getFor(this.discrete.isSelected(), this.getSelectedFunction()), Color.CYAN, 4);
        }
        else
            scatter = new Scatter(population.getPopulation(), Color.CYAN, 4);
        chart.getScene().add(scatter);
        surface.add(scatter);
    }

    protected void drawPoint(Coord3d point) {
        this.centerPanel.removeAll();
        invalidateCanvas();
        System.out.println("Adding minimum.");
        Coord3d[] oneM = new Coord3d[1];
        oneM[0] = point;
        Scatter minimum = new Scatter(oneM, Color.BLACK, 10.5f);
        surface.remove(minimum);
        chart.getScene().remove(minimum);
        repaintSurface();
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        chart.getScene().add(minimum);
        surface.add(minimum);


    }

    private void repaintSurface() {
        this.centerPanel.removeAll();
        chart = AWTChartComponentFactory.chart(Quality.Advanced, IChartComponentFactory.Toolkit.swing);
        scene = chart.getScene();
        surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new org.jzy3d.colors.Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        chart.add(surface);
        AWTCameraMouseController controller = new AWTCameraMouseController(chart);
        Component canvas = (Component) chart.getCanvas();

        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        canvas.addMouseWheelListener(controller);
        this.centerPanel.add(canvas, BorderLayout.CENTER);
    }

    private void setCenterPanel() {
        Dimension d = new Dimension(640, 480);
        this.centerPanel.setSize(d);
        this.centerPanel.setMaximumSize(d);
        this.centerPanel.setMinimumSize(d);
        this.centerPanel.setPreferredSize(d);
        chart = AWTChartComponentFactory.chart(Quality.Advanced, IChartComponentFactory.Toolkit.swing);
        scene = chart.getScene();
        invalidateCanvas();
        AWTCameraMouseController controller = new AWTCameraMouseController(chart);
        Component canvas = (Component) chart.getCanvas();
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
        canvas.addMouseWheelListener(controller);
        this.centerPanel.add(canvas, BorderLayout.CENTER);
    }

    private void setNorthPanel() {
        JLabel rangeFromLabel = new JLabel("From:");
        JLabel rangeToLabel = new JLabel("To:");
        rangeFromText = new TextField(String.valueOf(minRange));
        rangeToText = new TextField(String.valueOf(maxRange));
        resetGraph = new JButton("R");
        resetGraph.addActionListener(e -> resetChart());
        discrete = new JCheckBox("Discrete", false);
        functions = new JComboBox(funsToMenu);
        functions.setForeground(java.awt.Color.gray);
        functions.setFont(new Font("Arial", Font.PLAIN, 14));
        functions.setMaximumRowCount(21);
        algorithms = new JComboBox(algToMenu);
        algorithms.setForeground(java.awt.Color.gray);
        algorithms.setFont(new Font("Arial", Font.PLAIN, 14));
        drawIt = new JButton("Draw it");
        drawIt.addActionListener(e -> {
            invalidateCanvas();
            startGeneration();
        });
        JLabel popLabel = new JLabel("Population:");
        popField = new TextField("0");
        JLabel popCountLabel = new JLabel("Generations:");
        popCountField = new TextField("0");
        //this.northPanel.add(Box.createRigidArea(new Dimension(45, 0)));
        this.northPanel.add(functions);
        this.northPanel.add(algorithms);
        this.northPanel.add(drawIt);
        this.northPanel.add(discrete);
        this.northPanel.add(popLabel);
        this.northPanel.add(popField);
        this.northPanel.add(popCountLabel);
        this.northPanel.add(popCountField);
        //this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        //this.northPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        this.northPanel.add(rangeFromLabel);
        //this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        this.northPanel.add(rangeFromText);
        this.northPanel.add(rangeToLabel);
        this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        this.northPanel.add(rangeToText);
        this.northPanel.add(resetGraph);

    }

    private void resetChart() {
        rangeFromText.setText(String.valueOf(funs[functions.getSelectedIndex()].getMinRange()));
        rangeToText.setText(String.valueOf(funs[functions.getSelectedIndex()].getMaxRange()));
        if(thread != null){
            thread.stop();
        }
        this.enableSettings(true);
    }

}
