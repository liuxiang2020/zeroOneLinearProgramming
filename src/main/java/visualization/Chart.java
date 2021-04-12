package visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;

public class Chart extends JFrame{

    private double[] y;
    private int figureHeight = 600;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Chart(double[] y){
        super();
        this.y = y;

        setTitle("Line Draw");
        setBounds(2, 100, 1500, figureHeight);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.red);
        int biasX = 15;
        int h = 2;
        int biasY = 100;
        g.drawLine(biasX, this.figureHeight-biasY, biasX+h*(y.length), this.figureHeight-biasY);
        g.drawLine(biasX,figureHeight-((int)y[0]+biasY), biasX+h, figureHeight-((int)y[1]+biasY));
        int x1,y1, x2, y2;
        for (int i = 1; i < y.length-1; i++) {
            x1 = biasX+h*i; y1 = figureHeight-((int)y[i]+biasY);
            x2=biasX+h*(i+1); y2=figureHeight-((int)y[i+1]+biasY);
            g.drawLine(x1, y1, x2, y2);
        }
        g.drawString("("+h*(y.length-1)+","+(int)y[y.length-1]+")", biasX+h*(y.length-1), this.figureHeight-((int)y[y.length-1]+biasY));
    }

    public static void main(String[] args) {
        double[] y = new double[700];
        Random rand = new Random();
        for (int i = 0; i < y.length; i++) {
            y[i] = rand.nextInt(200);
        }
        Chart rp = new Chart(y);
    }
}