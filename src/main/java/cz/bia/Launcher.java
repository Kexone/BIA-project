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
    private final JPanel populationPanel;
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
    private JSlider speedSlider;
    private Population population;
    private Thread thread;
    private int temperatureNow;
    private TextField temperatureNowFField;
    private TextField temperatureAfterField;
    private int temperatureAfter;
    private TextField radiusCRField;
    private int radius;
    private JLabel theBestOfBest;
    private Coord3d best;
    private boolean generateNewPopulation = true;
    private boolean diff;
    private JLabel radiusCRText;
    private JLabel finalTemperatureFText;
    private float crRadius;
    private float fOrFinalTemp;
    private float temperature;

    public static void main(String[] args) throws Exception {
        Launcher frame = new Launcher();
        frame.setVisible(true);
    }

    public Launcher() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new JPanel(new BorderLayout()));
        setSize(750, 600);
        //northPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        northPanel = new JPanel(new WrapLayout());
        populationPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        //secondNorthPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        centerPanel = new JPanel(new BorderLayout());
        this.add(northPanel, BorderLayout.BEFORE_FIRST_LINE);
        this.add(centerPanel, BorderLayout.CENTER);
        //this.add(secondNorthPanel, BorderLayout.PAGE_END);
        this.setPopulationPanel();
        this.setNorthPanel();
        this.setCenterPanel();
        this.resetChart();
    }



    public IFunction getSelectedFunction() {
        return funs[functions.getSelectedIndex()];
    }

    private void setBoundsAlgoritmData() {
        try {
            minRange = Float.parseFloat(rangeFromText.getText());
            maxRange = Float.parseFloat(rangeToText.getText());
            crRadius = Float.parseFloat(radiusCRField.getText());
            fOrFinalTemp = Float.parseFloat(temperatureNowFField.getText());
            temperature = Float.parseFloat(temperatureAfterField.getText());
            if (Integer.parseInt(popField.getText()) > 0) {
                popMax = Integer.parseInt(popField.getText());
                popGenerations = Integer.parseInt(popCountField.getText());
            } else {
                popMax = 0;
            }
        } catch (Exception e) {
            popGenerations = 0;
            crRadius = 0.5f;
            fOrFinalTemp = 0.5f;
            temperature = 0;
            System.out.println("Error NaN! " + e);
            minRange = (float) funs[functions.getSelectedIndex()].getMinRange();
            maxRange = (float) funs[functions.getSelectedIndex()].getMaxRange();
        }
    }

    private void invalidateCanvas() {
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
                        if(generateNewPopulation) {
                            this.population.randomPopulation(popMax);
                        }
                        this.selectAlgorithm();
                        try {
                            Thread.sleep(speedSlider.getValue());
                            SwingUtilities.invokeAndWait(() -> {
                                if(best != null)
                                    drawPoint(best);

                            });
                        } catch (InterruptedException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    if(algorithms.getSelectedIndex() == 2) {
                        if (population.isCompleted()) {
                            break;
                        } else {
                            i--;
                        }
                    }
                }
                this.enableSettings(true);
                best = null;
                generateNewPopulation = true;
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
        radiusCRField.setEnabled(b);
        temperatureAfterField.setEnabled(b);
        temperatureNowFField.setEnabled(b);
    }

    private void selectAlgorithm() {
        int selectedAlg = algorithms.getSelectedIndex();
        diff = false;
        switch (selectedAlg) {
            case 0:             // Without algorithm
                this.drawPopulation(diff);
                break;
            case 1:             // Blind search
                if(best == null) {
                    best = this.population.getBest(this.population.getPopulation());
                }
                best = isBest(this.best);
                System.out.println(" X:" + best.x + " Y:" + best.y + " Z:" + best.z);
                theBestOfBest.setText("The best is X:" + best.x + " Y:" + best.y + " Z:" + best.z);
               // drawPoint(best);
                break;
            case 2:             // Simulation Annealing
                generateNewPopulation = false;
                if(best == null) {
                    best =  population.generateInvidual();
                    System.out.println("NEW X:" + best.x + " Y:" + best.y + " Z:" + best.z);

                }
                //isBest(this.best);
                best = this.population.annealing(popMax, best);
               // Coord3d annealing = this.population.annealing(popMax);
                System.out.println("BEST X:" + best.x + " Y:" + best.y + " Z:" + best.z);
                theBestOfBest.setText("The best is X:" + best.x + " Y:" + best.y + " Z:" + best.z);
            //     drawPoint(best);
                break;
            case 3:             // Differencial evolution
                generateNewPopulation = false;
                diff = true;
                this.population.differencial();
                if(best == null) {
                    best = this.population.getBest(this.population.getMutation());
                }
                //best = this.population.getBest(this.population.getFor(this.discrete.isSelected(), this.getSelectedFunction()));
                //population.getFor(this.discrete.isSelected(), this.getSelectedFunction());
                best = isBest(best);
               // best = this.population.getBest(this.population.getPopulation());
                System.out.println("BEST X:" + best.x + " Y:" + best.y + " Z:" + best.z);
                theBestOfBest.setText("The best is X:" + best.x + " Y:" + best.y + " Z:" + best.z);
//                drawPoint(best);
                break;
            case 4:             // SOMA
                Coord3d soma = this.population.soma();
                drawPoint(soma);
                break;

        }

    }

    private Coord3d isBest(Coord3d latest) {
        Coord3d newBest;
        if(this.population.getMutation() != null) {
            newBest = this.population.getBest(population.getMutation());
        }
        else {
            newBest = this.population.getBest(population.getPopulation());
        }
        System.out.println("Old best " + latest.z);
        System.out.println("New best " + newBest.z);

        if(newBest.z < latest.z) {
            System.out.println("NEW is best");
            return newBest;
        }
        System.out.println("OLD is best");
        return latest;
    }
    private void drawPopulation(boolean diff) {
        Scatter scatter, mutation;
      //  System.out.println("Adding scatter.");
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        if(diff) {
            mutation = new Scatter(population.getMutation(), Color.RED, 4);
            chart.getScene().add(mutation);
            surface.add(mutation);
        }
        else {
            scatter = new Scatter(population.getPopulation(), Color.GREEN, 4);
            chart.getScene().add(scatter);
            surface.add(scatter);
        }
    }

    protected void drawPoint(Coord3d point) {
        this.centerPanel.removeAll();
        invalidateCanvas();
       // System.out.println("Adding minimum.");
        Coord3d[] oneM = new Coord3d[1];
        oneM[0] = point;
        Scatter minimum = new Scatter(oneM, Color.BLACK, 10.5f);
        surface.remove(minimum);
        chart.getScene().remove(minimum);
        repaintSurface();
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        this.drawPopulation(diff);
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
        resetGraph = new JButton("R");
        resetGraph.addActionListener(e -> resetChart());
        discrete = new JCheckBox("Discrete", false);
        functions = new JComboBox(funsToMenu);
        functions.setForeground(java.awt.Color.gray);
        functions.setFont(new Font("Arial", Font.PLAIN, 14));
        functions.setMaximumRowCount(21);
        algorithms = new JComboBox(algToMenu);
        algorithms.addActionListener( e-> enableAdditionalFields());
        algorithms.setForeground(java.awt.Color.gray);
        algorithms.setFont(new Font("Arial", Font.PLAIN, 14));
        drawIt = new JButton("Draw it");
        drawIt.addActionListener(e -> {
            setBoundsAlgoritmData();
            this.population = new Population(minRange,maxRange, fOrFinalTemp, crRadius, temperature, this.discrete.isSelected(), this.getSelectedFunction());
            invalidateCanvas();
            startGeneration();
        });

        JLabel rangeFromLabel = new JLabel("From:");
        JLabel rangeToLabel = new JLabel("To:");
        JLabel speedLabel = new JLabel("Speed:");
        JLabel algorithmText = new JLabel("Algorithm:");
        finalTemperatureFText = new JLabel("Final temp:");
        JLabel temperatureNowText = new JLabel("Temperature:");
        radiusCRText = new JLabel("Radius:");
        theBestOfBest = new JLabel("The best is...");
        rangeFromText = new TextField(String.valueOf(minRange));
        rangeToText = new TextField(String.valueOf(maxRange));
        radiusCRField = new TextField(String.valueOf(radius));
        temperatureNowFField = new TextField(String.valueOf(temperatureNow));
        temperatureAfterField = new TextField(String.valueOf(temperatureAfter));
        temperatureNowFField.setEnabled(false);
        temperatureAfterField.setEnabled(false);
        radiusCRField.setEnabled(false);


        speedSlider = new JSlider(0,3000, 1000);
        speedSlider.setMajorTickSpacing(1000);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        this.northPanel.add(functions);
        this.northPanel.add(algorithmText);
        this.northPanel.add(algorithms);
        this.northPanel.add(drawIt);
        this.northPanel.add(discrete);
        this.northPanel.add(populationPanel);
        this.northPanel.add(radiusCRText);
        this.northPanel.add(radiusCRField);
        this.northPanel.add(finalTemperatureFText);
        this.northPanel.add(temperatureNowFField);
        this.northPanel.add(temperatureNowText);
        this.northPanel.add(temperatureAfterField);
        this.northPanel.add(Box.createRigidArea(new Dimension(65, 0)));
        this.northPanel.add(speedLabel);
        this.northPanel.add(speedSlider);
        //  this.northPanel.add(speedNumberLabel);
        this.northPanel.add(rangeFromLabel);
        this.northPanel.add(rangeFromText);
        this.northPanel.add(rangeToLabel);
        this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        this.northPanel.add(rangeToText);
        //this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        //this.northPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        //this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));

        this.northPanel.add(resetGraph);
        this.northPanel.add(theBestOfBest);
    }

    private void enableAdditionalFields() {
        if( algorithms.getSelectedIndex() == 2) {
            radiusCRText.setText("Radius:");
            finalTemperatureFText.setText("Final temp:");
            temperatureNowFField.setEnabled(true);
            temperatureAfterField.setEnabled(true);
            radiusCRField.setEnabled(true);

        }
        else if( algorithms.getSelectedIndex() == 3) {
            temperatureNowFField.setEnabled(true);
            temperatureAfterField.setEnabled(false);
            radiusCRField.setEnabled(true);
            radiusCRText.setText("CR:");
            finalTemperatureFText.setText("F:");
            temperatureNowFField.setText("0.5");
            radiusCRField.setText("0.5");
        }
        else {
            temperatureNowFField.setEnabled(false);
            temperatureAfterField.setEnabled(false);
            radiusCRField.setEnabled(false);

        }
    }

    private void setPopulationPanel() {
        JLabel popLabel = new JLabel("Population:");
        popField = new TextField("0");
        JLabel popCountLabel = new JLabel("Generations:");
        popCountField = new TextField("0");
        this.populationPanel.add(popLabel);
        this.populationPanel.add(popField);
        this.populationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        this.populationPanel.add(popCountLabel);
        this.populationPanel.add(popCountField);
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
