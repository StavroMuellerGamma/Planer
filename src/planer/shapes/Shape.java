package planer.shapes;

import ch.aplu.util.GPanel;

/**
 * Diese abstrakte Klasse schreibt die Methoden und Variablen vor, die nötig sind, um das Bearbeiten von Formen im
 * {@link planer.Planer} zu ermöglichen.
 */
public abstract class Shape {
    /**
     * Die aktuellen Koordinaten
     */
    protected double x0, y0, x1, y1;
    /**
     * Die temporären Koordinaten
     */
    protected double tmpx0, tmpy0, tmpx1, tmpy1;

    /**
     * In diesem Konstruktor werden die Koordinaten der Form erstmals gesetzt. Dafür wird die Methode
     * {@link Shape#setCoords(double, double, double, double, boolean)} verwendet.
     *
     * @param x0 Die x0-Koordinate, die gesetzt werden soll
     * @param y0 Die y0-Koordinate, die gesetzt werden soll
     * @param x1 Die x1-Koordinate, die gesetzt werden soll
     * @param y1 Die y1-Koordinate, die gesetzt werden soll
     */
    public Shape(double x0, double y0, double x1, double y1) {
        setCoords(x0, y0, x1, y1, false);
    }

    /**
     * Diese Methode überprüft, ob die übergebenen Koordinaten in der Form liegen, auf der diese Methode aufgerufen
     * wurde.
     *
     * @param x Die x-Koordinate, die überprüft werden soll
     * @param y Die y-Koordinate, die überprüft werden soll
     * @return {@code true} falls die Koordinaten innerhalb der Form liegen oder {@code false} falls die Koordinaten
     * nicht in der Form liegen.
     */
    public abstract boolean isHit(double x, double y);

    /**
     * Diese Methode zeichnet die Form, auf der sie aufgerufen wurde, auf dem übergebenen {@link GPanel}.
     * <p>Dies kann entweder mit den aktuellen oder den temporären Koordinaten geschehen.</p>
     *
     * @param panel     Das {@link GPanel} auf dem gezeichnet werden soll
     * @param temporary Ob die temporären oder aktuellen Koordianten verwendet werden sollen
     */
    public abstract void draw(GPanel panel, boolean temporary);

    /**
     * Diese Methode setzt die Koordinaten der Form, auf der sie aufgerufen wurde.
     * <p>Es kann zwischen temporären und aktuellen und temporären Koordinaten unterschieden werden.</p>
     *
     * @param x0        Die x0-Koordinate, die gesetzt werden soll
     * @param y0        Die y0-Koordinate, die gesetzt werden soll
     * @param x1        Die x1-Koordinate, die gesetzt werden soll
     * @param y1        Die y1-Koordinate, die gesetzt werden soll
     * @param temporary Ob nur die temporären oder zusätzlich auch die aktuellen Koordinaten gesetzt werden sollen
     */
    public void setCoords(double x0, double y0, double x1, double y1, boolean temporary) {
        if (temporary) {
            tmpx0 = x0;
            tmpy0 = y0;
            tmpx1 = x1;
            tmpy1 = y1;
        } else {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            tmpx0 = x0;
            tmpy0 = y0;
            tmpx1 = x1;
            tmpy1 = y1;
        }
    }

    /**
     * Diese Methode gibt die aktuellen Koordinaten der Form zurück.
     * <p>Die Koordinaten sind in einem {@code double}-Array gespeichert. Index 0 entspricht x0, 1 entspricht y0, 2
     * entspricht x1 und 3 entspricht y1.</p>
     *
     * @return Die aktuellen Koordinaten der Form
     */
    public double[] getCoords() {
        return new double[]{x0, y0, x1, y1};
    }

    /**
     * Diese Methode verschiebt die Form, auf der sie aufgerufen wurde, in der x-Richtung um die Distanz {@code x} und
     * in y-Richtung um die Distanz {@code y}.
     * <p>Sollte die Verschiebung nur temporär erfolgen werden nur die temporären Koordinaten verändert.</p>
     *
     * @param x         Die Distanz, um die in x-Richtung verschoben wird
     * @param y         DIe Distanz, um die in y-RIchtung verschoben wird
     * @param temporary Ob die temporären Koordinaten oder die aktuellen Koordinaten verändert werden sollen
     */
    public void move(double x, double y, boolean temporary) {
        if (temporary) {
            tmpx0 = x0 + x;
            tmpy0 = y0 + y;
            tmpx1 = x1 + x;
            tmpy1 = y1 + y;
        } else {
            x0 += x;
            y0 += y;
            x1 += x;
            y1 += y;
        }
    }

    /**
     * Getter für die x1-Koordinate.
     *
     * @return Die aktuelle x1-Koordinate
     */
    public double getX1() {
        return x1;
    }

    /**
     * Getter für die x0-Koordinate.
     *
     * @return Die aktuelle x0-Koordinate
     */
    public double getX0() {
        return x0;
    }

    /**
     * Getter für die y0-Koordinate.
     *
     * @return Die aktuelle y0-Koordinate
     */
    public double getY0() {
        return y0;
    }

    /**
     * Getter für die y1-Koordinate.
     *
     * @return Die aktuelle y1-Koordinate
     */
    public double getY1() {
        return y1;
    }

    /**
     * Diese Methode setzt die temporären Koordinaten als die aktuellen Koordinaten.
     */
    public void setTmpCoords() {
        x0 = tmpx0;
        y0 = tmpy0;
        x1 = tmpx1;
        y1 = tmpy1;
    }

    public abstract String getName();
}
