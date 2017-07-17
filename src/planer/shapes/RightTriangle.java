package planer.shapes;

import ch.aplu.util.GPanel;

/**
 * Diese Klasse verwaltet die Koordinaten eines rechtwinkligen Dreiecks und ermöglicht das Zeichnen desselben.
 */
public class RightTriangle extends Triangle {
    /**
     * In diesem Konstruktor werden die Koordinaten des Dreiecks erstmals gesetzt. Dafür wird die Methode
     * {@link Shape#setCoords(double, double, double, double, boolean)} verwendet.
     *
     * @param x0 Die x0-Koordinate, die gesetzt werden soll
     * @param y0 Die y0-Koordinate, die gesetzt werden soll
     * @param x1 Die x1-Koordinate, die gesetzt werden soll
     * @param y1 Die y1-Koordinate, die gesetzt werden soll
     */
    public RightTriangle(double x0, double y0, double x1, double y1) {
        super(x0, y0, x1, y1);
    }

    /**
     * In dieser Methode wird ermittelt, ob die übergebenen Koordinaten im Dreieck liegen.
     * <p>Zu diesem Zweck wird der Strahlensatz verwendet, um herauszufinden, wie groß die {@link Shape#y1 y1-Koordinate} an einer
     * bestimmten x-Position sein darf.</p>
     *
     * @param x Die x-Koordinate, die überprüft werden soll
     * @param y Die y-Koordinate, die überprüft werden soll
     * @return {@code true} falls die Koordinaten im Dreieck liegen, {@code false} falls die Koordinaten nicht im Dreiek liegen.
     */
    @Override
    public boolean isHit(double x, double y) {
        if (x0 < x1 && y0 < y1) {
            return (x >= x0 && x <= x1) && (y >= y0 && y <= y0 + (y1 - y0) * ((x - x0) / (x1 - x0)));
        } else if (x0 < x1 && y0 > y1) {
            return (x >= x0 && x <= x1) && (y <= y0 && y >= y0 + (y1 - y0) * ((x - x0) / (x1 - x0)));
        } else if (x0 > x1 && y0 < y1) {
            return (x <= x0 && x >= x1) && (y >= y0 && y <= y0 + (y1 - y0) * ((x0 - x) / (x0 - x1)));
        } else {
            return (x <= x0 && x >= x1) && (y <= y0 && y >= y0 + (y1 - y0) * ((x0 - x) / (x0 - x1)));
        }
    }

    /**
     * Diese Methode zeichnet das Dreieck auf dem übergebenen {@link GPanel}.
     * <p>Je nach Zustand des Parameters {@code temporary} werden die aktuellen oder temporären Koordinaten verwendet.</p>
     *
     * @param panel     Das {@link GPanel} auf dem gezeichnet werden soll
     * @param temporary Ob die temporären oder aktuellen Koordianten verwendet werden sollen
     */
    @Override
    public void draw(GPanel panel, boolean temporary) {
        panel.triangle(
                temporary ? tmpx0 : x0,
                temporary ? tmpy0 : y0,
                temporary ? tmpx1 : x1,
                temporary ? tmpy0 : y0,
                temporary ? tmpx1 : x1,
                temporary ? tmpy1 : y1
        );
    }

    @Override
    public String getName() {
        return "right-triangle";
    }
}
