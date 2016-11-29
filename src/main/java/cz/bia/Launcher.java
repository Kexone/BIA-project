package cz.bia;

import javax.swing.*;
import javax.xml.soap.Text;

import cz.bia.algorithm.*;
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
    String[] algToMenu = {"", "Blind algorithm", "Simulated annealing", "Differential evolution", "SOMA", "Evolution Strategy" };

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
    private JLabel theBestOfBest;
    public Coord3d best;
    private boolean generateNewPopulation = true;
    private Algorithm alg;
    //annealing
    private TextField temperatureNowField;
    private TextField temperatureAfterField;
    private TextField radiusField;
    private float radius;
    private float finalTemp;
    private float temperature;
    private boolean isCompleted;

    //differencial
    private TextField FField;
    private TextField CRField;
    private float cr;
    private float f;
    private boolean diff;

    //soma
    private TextField betaField;
    private TextField stepField;
    private TextField pathLengthField;
    private TextField PRTField;
    private TextField minDivField;
    private TextField fitnessValueField;
    private TextField gaussDeviationField;
    private JCheckBox typeOfMode;
    private JCheckBox cpsols;

    //es


    public static void main(String[] args) throws Exception {
        Launcher frame = new Launcher();
        frame.setVisible(true);
    }

    public Launcher() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new JPanel(new BorderLayout()));
        setSize(750, 600);
        northPanel = new JPanel(new WrapLayout());
        populationPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        centerPanel = new JPanel(new BorderLayout());
        this.add(northPanel, BorderLayout.BEFORE_FIRST_LINE);
        this.add(centerPanel, BorderLayout.CENTER);
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
            //crRadius = Float.parseFloat(radiusCRField.getText());
            //fOrFinalTemp = Float.parseFloat(temperatureNowFField.getText());
            //temperature = Float.parseFloat(temperatureAfterField.getText());
            if (Integer.parseInt(popField.getText()) > 0) {
                popMax = Integer.parseInt(popField.getText());
                popGenerations = Integer.parseInt(popCountField.getText());
            } else {
                popMax = 0;
            }
        } catch (Exception e) {
            popGenerations = 0;
            //crRadius = 0.5f;
            //fOrFinalTemp = 0.5f;
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
                        if (isCompleted) {
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
//        radiusField.setEnabled(b);
 //       CRField.setEnabled(b);
 //       temperatureAfterField.setEnabled(b);
 //       FField.setEnabled(b);
  //      temperatureNowField.setEnabled(b);
    }

    private void selectAlgorithm() {
        int selectedAlg = algorithms.getSelectedIndex();
        diff = false;
        Coord3d bestTmp;
        switch (selectedAlg) {
            case 0:             // Without algorithm
                this.drawPopulation(diff);
                break;
            case 1:             // Blind search
                alg = new BlindSearch();
                if(best == null) {
                    best =  alg.start(this.population);
                    break;
                }
                bestTmp = alg.start(population);
                if(best.z > bestTmp.z) {
                    best = bestTmp;
                }
                break;
            case 2:             // Simulation Annealing
                generateNewPopulation = false;
                alg = new SimulatingAnnealing(funs[functions.getSelectedIndex()], maxRange,
                        minRange, this.discrete.isSelected(), Double.parseDouble(radiusField.getText()), Double.parseDouble(temperatureNowField.getText()),
                        Double.parseDouble(temperatureAfterField.getText()), Double.parseDouble(betaField.getText()));
                if(best == null) {
                    best =  alg.generateIndividual();
                    System.out.println("NEW X:" + best.x + " Y:" + best.y + " Z:" + best.z);
                }
                alg.setIndividual(best);
                bestTmp = alg.start(population);
                if(best.z > bestTmp.z) {
                    best = bestTmp;
                }
                isCompleted = SimulatingAnnealing.isCompleted;
                break;
            case 3:             // Differencial evolution
                generateNewPopulation = false;
                diff = true;
                alg = new DifferentialEvolution(funs[functions.getSelectedIndex()], maxRange,
                        minRange, this.discrete.isSelected(), Double.parseDouble(FField.getText()), Double.parseDouble(CRField.getText()));
                alg.start(population);
                if(best == null) {
                    best = alg.getBest(this.population.getPopulation());
                    break;
                }
                best = alg.getBest(this.population.getPopulation());
                break;
            case 4:             // SOMA
                generateNewPopulation = false;
                alg = new SomaAllToOne(funs[functions.getSelectedIndex()], maxRange,
                        minRange, this.discrete.isSelected(), Double.parseDouble(PRTField.getText()), Double.parseDouble(stepField.getText()),
                        Double.parseDouble(pathLengthField.getText()), Double.parseDouble(minDivField.getText()));
                if(best == null) {
                    best = alg.getBest(population.getPopulation());
                }
                System.out.println("BEST X:" + best.x + " Y:" + best.y + " Z:" + best.z);
                alg.setIndividual(best);
                bestTmp = alg.start(population);
                if(bestTmp.z < best.z) {
                    best = bestTmp;
                }
                if(SomaAllToOne.toClose) {
                    System.out.println("moc u sebe");
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.resetChart();
                }
                System.out.println("BEST X:" + best.x + " Y:" + best.y + " Z:" + best.z);
                theBestOfBest.setText("The best is X:" + best.x + " Y:" + best.y + " Z:" + best.z);
                break;
            case 5:         // Evolution Strategy
                generateNewPopulation = false;
                alg = new EvolutionStrategy(funs[functions.getSelectedIndex()], maxRange,
                        minRange, this.discrete.isSelected(), Double.parseDouble(fitnessValueField.getText()),
                        Double.parseDouble(gaussDeviationField.getText()), typeOfMode.isSelected() );
                if(best == null) {
                    best = alg.getBest(population.getPopulation());
                }
                bestTmp = alg.start(population);
                if(bestTmp.z < best.z) {
                    best = bestTmp;
                }
                //isCompleted = SomaAllToOne.toClose;
        }
        System.out.println("BEFORE X:" + best.x + " Y:" + best.y + " Z:" + best.z);
        if(cpsols.isSelected()) {
            alg = new NaiveDirectedSearch(funs[functions.getSelectedIndex()], maxRange,
                    minRange, this.discrete.isSelected());
            bestTmp = alg.start(population);
            if(bestTmp.z < best.z) {
                best = bestTmp;
            }
        }
        System.out.println(" AFTER X:" + best.x + " Y:" + best.y + " Z:" + best.z);
        theBestOfBest.setText("The best is X:" + best.x + " Y:" + best.y + " Z:" + best.z);

    }

    private void drawPopulation(boolean diff) {
        Scatter scatter;
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
            scatter = new Scatter(population.getPopulation(), Color.GREEN, 4);
            chart.getScene().add(scatter);
            surface.add(scatter);
    }

    protected void drawPoint(Coord3d point) {
        this.centerPanel.removeAll();
        invalidateCanvas();
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
        cpsols = new JCheckBox("CPSOLS", false);
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
            this.population = new Population(minRange,maxRange, this.discrete.isSelected(), this.getSelectedFunction());
            invalidateCanvas();
            startGeneration();
        });

        JLabel rangeFromLabel = new JLabel("From:");
        JLabel rangeToLabel = new JLabel("To:");
        JLabel speedLabel = new JLabel("Speed:");
        JLabel algorithmText = new JLabel("Algorithm:");

        theBestOfBest = new JLabel("The best is...");
        rangeFromText = new TextField(String.valueOf(minRange));
        rangeToText = new TextField(String.valueOf(maxRange));
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
        this.northPanel.add(cpsols);
        this.northPanel.add(Box.createRigidArea(new Dimension(65, 0)));
        this.northPanel.add(speedLabel);
        this.northPanel.add(speedSlider);
        this.northPanel.add(rangeFromLabel);
        this.northPanel.add(rangeFromText);
        this.northPanel.add(rangeToLabel);
        this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        this.northPanel.add(rangeToText);
        //this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        //this.northPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        //this.northPanel.add(Box.createRigidArea(new Dimension(4, 0)));

        this.northPanel.add(resetGraph);
        this.northPanel.add(populationPanel);
        this.northPanel.add(theBestOfBest);
    }

    private void enableAdditionalFields() {
        this.populationPanel.removeAll();
        setPopulationPanel();
        if( algorithms.getSelectedIndex() == 2) {
            setAnnealingPanel();
        }
        else if( algorithms.getSelectedIndex() == 3) {
            setDifferencialPanel();
        }
        else  if( algorithms.getSelectedIndex() == 4){
            setSomaPanel();
        }
        else if(algorithms.getSelectedIndex() == 5) {
            setStrategyPanel();
        }
        this.populationPanel.revalidate();

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
        generateNewPopulation = true;
        this.enableSettings(true);
    }


    private void setAnnealingPanel() {
        JLabel betaText = new JLabel("Beta:");
        JLabel finalTemperatureText = new JLabel("Final temp:");
        JLabel temperatureNowText = new JLabel("Temperature:");
        JLabel radiusText = new JLabel("Radius:");
        betaField = new TextField();
        radiusField = new TextField();
        temperatureNowField = new TextField();
        temperatureAfterField = new TextField();
        this.populationPanel.add(betaText);
        this.populationPanel.add(betaField);
        this.populationPanel.add(radiusText);
        this.populationPanel.add(radiusField);
        this.populationPanel.add(finalTemperatureText);
        this.populationPanel.add(temperatureNowField);
        this.populationPanel.add(temperatureNowText);
        this.populationPanel.add(temperatureAfterField);
    }

    private void setDifferencialPanel() {
        JLabel CRText = new JLabel("CR:");
        JLabel FText = new JLabel("F:");
        CRField = new TextField(String.valueOf(radius));
        FField = new TextField();
        this.populationPanel.add(CRText);
        this.populationPanel.add(CRField);
        this.populationPanel.add(FText);
        this.populationPanel.add(FField);
    }

    private void setSomaPanel() {

        JLabel stepText = new JLabel("Step:");
        JLabel pathLengthText = new JLabel("Path Length:");
        JLabel PRTText = new JLabel("PRT:");
        JLabel minDivText = new JLabel("MinDiv:");
        stepField = new TextField();
        pathLengthField = new TextField();
        PRTField = new TextField();
        minDivField = new TextField();
        this.populationPanel.add(stepText);
        this.populationPanel.add(stepField);
        this.populationPanel.add(pathLengthText);
        this.populationPanel.add(pathLengthField);
        this.populationPanel.add(PRTText);
        this.populationPanel.add(PRTField);
        this.populationPanel.add(minDivText);
        this.populationPanel.add(minDivField);
    }

    private void setStrategyPanel() {
        JLabel bestFitness = new JLabel("Best Fitness:");
        fitnessValueField = new TextField("0.99");
        JLabel gaussDeviation = new JLabel("Gauss deviation:");
        gaussDeviationField = new TextField("0.5");
        typeOfMode = new JCheckBox("ES(μ+λ)");
        this.populationPanel.add(bestFitness);
        this.populationPanel.add(fitnessValueField);
        this.populationPanel.add(gaussDeviation);
        this.populationPanel.add(gaussDeviationField);
        this.populationPanel.add(typeOfMode);
    }
}
