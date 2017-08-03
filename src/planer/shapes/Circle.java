package planer.shapes;

import ch.aplu.util.GPanel;

/**
 * Diese Klasse zeichnet einen Kreis
 * <p>
 * Created by Ben Weber on 01.08.2017.
 */
public class Circle extends Shape {
    /**
     * In diesem Konstruktor werden die Koordinaten der Form erstmals gesetzt. Dafür wird die Methode
     * {@link Shape#setCoords(double, double, double, double, boolean)} verwendet.
     *
     * @param x0 Die x0-Koordinate, die gesetzt werden soll
     * @param y0 Die y0-Koordinate, die gesetzt werden soll
     * @param x1 Die x1-Koordinate, die gesetzt werden soll
     * @param y1 Die y1-Koordinate, die gesetzt werden soll
     */
    public Circle(double x0, double y0, double x1, double y1) {
        super(x0, y0, x1, y1);
    }

    @Override
    public boolean isHit(double x, double y) {
        return Math.sqrt(Math.pow(y - (y0 + (y1 - y0) / 2), 2) + Math.pow(x - (x0 + (x1 - x0) / 2), 2)) <= Math.sqrt(Math.pow(x0 - (x0 + (x1 - x0) / 2), 2) + Math.pow(y0 - (y0 + (y1 - y0) / 2), 2));
    }

    @Override
    public void draw(GPanel panel, boolean temporary) {
        if (temporary) {
            panel.move(tmpx0 + (tmpx1 - tmpx0) / 2, tmpy0 + (tmpy1 - tmpy0) / 2);
            panel.circle(Math.sqrt(Math.pow((tmpy1 - tmpy0), 2) + Math.pow((tmpx1 - tmpx0), 2)) / 2);
        } else {
            panel.move(x0 + (x1 - x0) / 2, y0 + (y1 - y0) / 2);
            panel.circle(Math.sqrt(Math.pow((y1 - y0), 2) + Math.pow((x1 - x0), 2)) / 2);
        }
    }

    @Override
    public String getShapeName() {
        return "Circle";
    }
}
