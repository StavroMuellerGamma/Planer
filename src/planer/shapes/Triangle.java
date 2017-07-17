package planer.shapes;

/**
 * Diese Klasse ist eine Oberklasse für alle Klassen, die ein Dreieck verwalten.
 */
abstract class Triangle extends Shape {
    /**
     * In diesem Konstruktor werden die Koordinaten der Form erstmals gesetzt. Dafür wird die Methode
     * {@link Shape#setCoords(double, double, double, double, boolean)} verwendet.
     *
     * @param x0 Die x0-Koordinate, die gesetzt werden soll
     * @param y0 Die y0-Koordinate, die gesetzt werden soll
     * @param x1 Die x1-Koordinate, die gesetzt werden soll
     * @param y1 Die y1-Koordinate, die gesetzt werden soll
     */
    Triangle(double x0, double y0, double x1, double y1) {
        super(x0, y0, x1, y1);
    }
}
