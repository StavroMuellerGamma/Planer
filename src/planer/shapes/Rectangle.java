package planer.shapes;

import ch.aplu.util.GPanel;

/**
 * Diese Klasse verwaltet die Koordinaten eines Rechtecks und ermöglicht das Zeichnen und Verändern dieses.
 */
public class Rectangle extends Shape {

    /**
     * In diesem Konstruktor werden die Koordinaten des Rechtecks erstmals gesetzt. Dazu wird der Konstruktor der
     * Oberklasse {@link Shape} aufgerufen.
     *
     * @param x0 Die x0-Koordinate, die gesetzt werden soll
     * @param y0 Die y0-Koordinate, die gesetzt werden soll
     * @param x1 Die x1-Koordinate, die gesetzt werden soll
     * @param y1 Die y1-Koordinate, die gesetzt werden soll
     */
    public Rectangle(double x0, double y0, double x1, double y1) {
        super(x0, y0, x1, y1);
    }

    /**
     * Diese Methode überprüft, ob die übergebenen Koordinaten in den Koordinaten des Rechtecks liegen.
     * <p>Dabei wird durch den ternären Operator {@code ?:} festgelegt welche Intervalle überprüft werden müssen.</p>
     *
     * @param x Die x-Koordinate, die überprüft werden soll
     * @param y Die y-Koordinate, die überprüft werden soll
     * @return {@code true} falls die Koordinaten im Rechteck liegen oder {@code false} falls sie nicht im Rechteck
     * liegen.
     */
    @Override
    public boolean isHit(double x, double y) {
        boolean xCoords, yCoords;
        xCoords = x0 < x1 ? x >= x0 && x <= x1 : x >= x1 && x <= x0;
        yCoords = y0 < y1 ? y >= y0 && y <= y1 : y >= y1 && y <= y0;
        return xCoords && yCoords;
    }

    /**
     * Diese Methode zeichnet das Rechteck, welches durch diese Klasse beschrieben wird.
     * <p>Sie macht sich {@link GPanel#rectangle(double, double, double, double)} zunutze und verwendet entsprechend des Parameters
     * {@code temporary} die aktuellen oder die temporären Koordinaten.</p>
     *
     * @param panel     Das {@link GPanel} auf dem gezeichnet werden soll
     * @param temporary Ob die temporären oder aktuellen Koordianten verwendet werden sollen
     */
    @Override
    public void draw(GPanel panel, boolean temporary) {
        if (temporary) {
            panel.rectangle(tmpx0, tmpy0, tmpx1, tmpy1);
        } else {
            panel.rectangle(x0, y0, x1, y1);
        }
    }

    @Override
    public String getName() {
        return "rectangle";
    }
}
