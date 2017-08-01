package planer.shapes;

/**
 * Diese Klasse verwaltet die Koordinaten eines Quadrats und ermöglicht das Zeichnen und Verändern dieses.
 */
public class Square extends Rectangle {
    /**
     * In diesem Konstruktor werden die Koordinaten des Quadrats erstmals gesetzt. Dazu wird der Konstruktor der
     * Oberklasse {@link Rectangle} aufgerufen.
     *
     * @param x0 Die x0-Koordinate, die gesetzt werden soll
     * @param y0 Die y0-Koordinate, die gesetzt werden soll
     * @param x1 Die x1-Koordinate, die gesetzt werden soll
     * @param y1 Die y1-Koordinate, die gesetzt werden soll
     */
    public Square(double x0, double y0, double x1, double y1) {
        super(x0, y0, x1, y1);
    }

    /**
     * Diese Methode gibt einen für Menschen lesbaren Namen dieser Form aus.
     *
     * @return Ein für Menschen lesbarer Name dieser Form
     */
    @Override
    public String getShapeName() {
        return "Square";
    }

    /**
     * In dieser Methode werden die Koordinaten des Quadrats gesetzt.
     * <p>Je nachdem, ob die {@code x0}-/{@code y0}-Koordinate oder {@code x1}-/{@code y1}-Koordinaten größer sind, werden die
     * Koordinaten angepaßt, um ein Quadrat zu zeichnen.</p>
     *
     * @param x0 Die x0-Koordinate, die gesetzt werden soll
     * @param y0 Die y0-Koordinate, die gesetzt werden soll
     * @param x1 Die x1-Koordinate, die gesetzt werden soll
     * @param y1 Die y1-Koordinate, die gesetzt werden soll
     */
    @Override
    public void setCoords(double x0, double y0, double x1, double y1, boolean temporary) {
        if (!temporary) {
            this.x0 = x0;
            this.y0 = y0;
            if ((x1 > x0 && y1 > y0) || (x1 < x0 && y1 < y0)) {
                if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
                    this.y1 = y1;
                    this.x1 = x0 + (y1 - y0);
                } else if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
                    this.x1 = x1;
                    this.y1 = y0 + (x1 - x0);
                } else {
                    this.x1 = x1;
                    this.y1 = y1;
                }
            } else if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
                this.y1 = y1;
                this.x1 = x0 - (y1 - y0);
            } else if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
                this.x1 = x1;
                this.y1 = y0 - (x1 - x0);
            } else {
                this.x1 = x1;
                this.y1 = y1;
            }
            tmpx0 = this.x0;
            tmpy0 = this.y0;
            tmpx1 = this.x1;
            tmpy1 = this.y1;
        } else {
            tmpx0 = x0;
            tmpy0 = y0;
            if ((x1 > x0 && y1 > y0) || (x1 < x0 && y1 < y0)) {
                if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
                    tmpy1 = y1;
                    tmpx1 = x0 + (y1 - y0);
                } else if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
                    tmpx1 = x1;
                    tmpy1 = y0 + (x1 - x0);
                } else {
                    tmpx1 = x1;
                    tmpy1 = y1;
                }
            } else if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
                tmpy1 = y1;
                tmpx1 = x0 - (y1 - y0);
            } else if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
                tmpx1 = x1;
                tmpy1 = y0 - (x1 - x0);
            } else {
                tmpx1 = x1;
                tmpy1 = y1;
            }
        }
    }
}
