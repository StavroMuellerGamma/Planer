package planer;

import ch.aplu.util.GPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import planer.shapes.Shape;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hauptklasse des Projekts.
 * In dieser Klasse wird das Erstellen eines GPanels, das Zeichnen der Formen und die verschiedenen
 * Bearbeitungsmöglichkeiten implementiert.
 */
public class Planer {

    /**
     * Die Konstante, die den Pfad zur Auflistung der Formen und ihrer korrespondierenden Klassen enthält
     */
    private static final String SHAPE_CLASSES_PATH = "shapeClasses.xml";
    /**
     * Der Dialog, der sich öffnet, wenn eine Datei geöffnet oder geschlossen werden soll
     */
    private final JFileChooser jfc;
    /**
     * Zeichenfarbe, initialisiert mit Standardfarbe
     */
    private Color penColor = Color.black;
    /**
     * Hintergrundfarbe, initialisiert mit Standardfarbe
     */
    private Color bgColor = Color.white;
    /**
     * Der Zustand, in dem sich das Programm befindet
     */
    private State state = State.DRAW;
    /**
     * Die Koordinaten, die zu Beginn einer Aktion angeklickt wurden
     */
    private double x0 = 0, y0 = 0;
    /**
     * Die Koordinaten, an denen die Maus losgelassen wurde
     */
    private double x1 = 0, y1 = 0;
    /**
     * Eine Liste, in der alle gezeichneten Formen verwaltet werden
     */
    private ArrayList<Shape> shapesList = new ArrayList<>();
    /**
     * Das Panel, in dem gezeichnet wird
     */
    private GPanel p;
    /**
     * Die Form, die im Moment gezeichnet oder bearbeitet wird
     */
    private Shape currentShape;
    /**
     * Die {@link java.util.Map Map}, in der alle verfügbaren Formen und ihre Namen gespeichert werden
     */
    private HashMap<String, Class> shapeClasses = new HashMap<>();
    /**
     * Der Name der aktuellen Form
     */
    private String currentShapeString;

    /**
     * Der Konstruktor der Hauptklasse.
     * <p>Es werden {@code Listener} für das Klicken der Maustasten, das Loslassen der Maustasten und das Bewegen der
     * Maus bei gedrückter Taste implementiert. In den {@code Listener}n wird, wenn es nötig ist, zwischen den
     * Zuständen des Programms unterschieden.</p>
     */
    private Planer() {
        loadShapeClasses();
        p = new GPanel(setMenu());
        currentShapeString = shapeClasses.keySet().toArray(new String[0])[0];
        p.addMouseListener(new MouseAdapter() {
            /**
             * In dieser Methode wird das Klicken der Maus behandelt.
             * <p>Die Koordinaten {@code x0}, {@code y0}, {@code x1} und {@code y1} werden auf die angeklickten
             * Koordinaten gesetzt.</p>
             * Daraufhin erfolgt eine Unterscheidung des Zustands.
             * <p>Sollte eine Form verschoben werden, wird über die Liste mit allen Formen iteriert und es wird
             * überprüft, ob die angeklickten Koordinaten in der aktuellen Form der Iteration liegen. Von mehreren
             * überlappenden Formen wird die letzte in der Liste ausgewählt und in {@link Planer#currentShape}
             * gespeichert.</p>
             * <p>Das gleiche Verhalten wird während einer Größenänderung verwendet.</p>
             * <p>Sollte eine Form gezeichnet werden, wird {@link Planer#currentShape} mit einer neuen Form, mit den
             * aktuellen Koordinaten, initialisiert und dieses Objekt wird in {@link Planer#shapesList}
             * gespeichert.</p>
             *
             * @param e Das {@link MouseEvent}, das den Aufruf dieser Methode ausgelöst hat.
             *          <p>Es wird dazu genutzt, die Koordinaten des Klicks zu bestimmen.</p>
             */
            @Override
            public void mousePressed(MouseEvent e) {
                x0 = x1 = p.toWindowX(e.getX());
                y0 = y1 = p.toWindowY(e.getY());
                switch (state) {
                    case DELETE:
                    case RESIZE:
                    case DRAG:
                        for (Shape s :
                                shapesList) {
                            if (s.isHit(x0, y0)) {
                                currentShape = s;
                            }
                        }
                        break;
                    case DRAW:
                        currentShape = getNewShapeInstance(currentShapeString, x0, y0, x1, y1);
                        shapesList.add(currentShape);
                        break;
                }
            }

            /**
             * In dieser Methode wird das loslassen der Maustaste behandelt.
             * <p>Der Modus des {@link GPanel} {@link Planer#p} wird auf zeichnen gesetzt und die Standardfarbe
             * {@link Planer#penColor} wird als die Farbe gesetzt, mit der gezeichnet wird.</p>
             * <p>Daraufhin erfolgt eine Unterscheidung des Zustands.</p>
             * <p>Sollte eine Form verschoben werden, werden die Koordinaten dieser Form verschoben.</p>
             * <p>Sollte die Größe einer Form verändert werden oder eine Form gezeichnet werden, werden die temporären Koordinaten
             * als aktuelle Koordinaten gesetzt.</p>
             * <p>Danach wird die Form permanent gezeichnet.</p>
             *
             * @param e Das {@link MouseEvent}, das den Aufruf dieser Methode ausgelöst hat.
             *          <p>Es wird in dieser Methode nicht verwendet, da die Veränderung der Koordinaaten in
             *          {@code mouseDragged()} erfolgt.</p>
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentShape != null) {
                    switch (state) {
                        case DELETE:
                            shapesList.remove(currentShape);
                            break;
                        case DRAG:
                            currentShape.move(x1 - x0, y1 - y0, false);
                            break;
                        case RESIZE:
                        case DRAW:
                            currentShape.setTmpCoords();
                            break;
                    }
                    p.setPaintMode();
                    p.setColor(penColor);
                    currentShape.draw(p, false);
                }
                currentShape = null;
                redraw();
            }
        });

        p.addMouseMotionListener(new MouseMotionAdapter() {
            /**
             * In dieser Methode wird die Bewegung der Maus bei gedrückter Maustaste behandelt.
             * <p>Das {@link GPanel} {@link Planer#p} wird in den XOR-Zeichenmodus versetzt. Dabei wird die
             * {@link Color Farbe} {@link Color#white weiß} als Parameter übergeben. Das bedeutet, dass bei jeder
             * Zeichenoperation über weißen Pixeln, mit der aktuellen {@link Planer#penColor Zeichenfarbe} des
             * {@link Planer#p Panels}, diese Pixel mit der Zeichenfarbe überschrieben werden und dass Pixel der
             * Zeichenfarbe mit weiß überschrieben werden, wenn auf ihnen gezeichnet wird. Dieses Verhalten sorgt
             * dafür, dass eine zu verändernde Form gelöscht wird und durch die provisprische Zeichnung ersetzt wird
             * und dass die provisorische Zeichnunng sich verändert.</p>
             * <p>Die darauf folgende Zeichnung der {@link Planer#currentShape aktuellen Form} löscht diese auf Grund
             * des festgelegten Zeichenverhaltens.</p>
             * <p>Die Koordinaten {@link Planer#x1} und {@link Planer#y1} werden auf die aktuelle Position des
             * Cursors gesetzt.</p>
             * <p>{@link Color#red Rot} wird als Zeichenfarbe für die provisorische Zeichnung der aktuellen Form
             * festgelegt.</p>
             * <p>Daraufhin folgt eine Unterscheidung des Zustands.</p>
             * <p>Sollte eine Form gezeichnet werden, werden die Koordinaten des Mausklicks und die aktuellen
             * Koordinaten des Cursors als die Koordinaten dieser Form gesetzt.</p>
             * <p>Sollte eine Form verschoben werden, werden ihre Koordinaten temporär in x-Richtung um die Distanz
             * zwischen {@link Planer#x1} und {@link Planer#x0} verschoben und in y-Richtung um die Distanz zwischen
             * {@link Planer#y1} und {@link Planer#y0}.</p>
             * <p>Sollte die Größe einer Form verändert werden, wird zusätzlich zwischen rechter und linker Maustaste
             * unterschieden. Mit der linken Maustaste wird eine Form vergrößert, mit der Rechten verkleinert. In x- und
             * y-Richtung wird jeweils unterschieden, ob die aktuelle Mausposition größer oder kleiner ist als die Startposition
             * der Maus. Das Ergebnis dieser Unterscheidung entscheidet dann über die Belegung der Koordinaten.</p>
             * <p>Nach der Unterscheidung wird die aktuelle Form mit den temporären Koordinaten gezeichnet.</p>
             *
             * @param e Das {@link MouseEvent}, das den Aufruf dieser Methode ausgelöst hat.
             *          <p>Es wird genutzt, um die aktuelle Position des Cursors zu ermitteln.</p>
             */
            public void mouseDragged(MouseEvent e) {
                p.setXORMode(bgColor);
                if (currentShape != null) {
                    currentShape.draw(p, true);
                    x1 = p.toWindowX(e.getX());
                    y1 = p.toWindowY(e.getY());
                    p.setColor(Color.red);
                    switch (state) {
                        case DRAW:
                            currentShape.setCoords(x0, y0, x1, y1, false);      // Koordinaten werden fest gesetzt, da kein Nutzen durch temporäre Setzung entsteht.
                            break;
                        case DRAG:
                            currentShape.move(x1 - x0, y1 - y0, true);
                            break;
                        case RESIZE:
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                currentShape.setCoords(
                                        (x0 < x1) ? currentShape.getX0() : currentShape.getX0() + (x1 - x0),
                                        (y0 < y1) ? currentShape.getY0() : currentShape.getY0() + (y1 - y0),
                                        (x0 < x1) ? currentShape.getX1() + (x1 - x0) : currentShape.getX1(),
                                        (y0 < y1) ? currentShape.getY1() + (y1 - y0) : currentShape.getY1(),
                                        true
                                );
                            } else {
                                currentShape.setCoords(
                                        (x0 < x1) ? currentShape.getX0() + (x1 - x0) : currentShape.getX0(),
                                        (y0 < y1) ? currentShape.getY0() + (y1 - y0) : currentShape.getY0(),
                                        (x0 < x1) ? currentShape.getX1() : currentShape.getX1() + (x1 - x0),
                                        (y0 < y1) ? currentShape.getY1() : currentShape.getY1() + (y1 - y0),
                                        true
                                );
                            }
                            break;
                    }
                    currentShape.draw(p, true);
                }
            }
        });
        jfc = new JFileChooser();
        jfc.setFileFilter(new FileNameExtensionFilter("XML files", "xml", "XML"));
    }

    /**
     * Main-Methode der Klasse.
     * <p>Einzige Funktion ist das erzeugen eines neuen {@link Planer}-Objekts.</p>
     *
     * @param args Kommandozeilenparameter.
     *             <p>Sie haben in dieser Klasse keine Funktion.</p>
     */
    public static void main(String... args) {
        new Planer();
    }

    /**
     * Diese Methode generiert eine neue Form, entsprechend des Parameters shape, der den Namen einer Form übergibt. Die Form wird
     * mit den übergebenen Koordinaten initialisiert.
     *
     * @param shape Der Name der Form, von der eine neue Instanz erzeugt werden soll
     * @param x0    Die x0-Koordinate, die gesetzt werden soll
     * @param y0    Die y0-Koordinate, die gesetzt werden soll
     * @param x1    Die x1-Koordinate, die gesetzt werden soll
     * @param y1    Die y1-Koordinate, die gesetzt werden soll
     * @return Eine neue Instanz der Form, deren Name übergeben wurde
     */
    private Shape getNewShapeInstance(String shape, double x0, double y0, double x1, double y1) {
        Shape s = null;
        try {
            s = (Shape) shapeClasses.get(shape).getConstructor(double.class, double.class, double.class, double.class).newInstance(x0, y0, x1, y1);
        } catch (InstantiationException e1) {
            JOptionPane.showMessageDialog(p.getPane(), "The class of this shape could not be instantiated.", "Instantiation Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalAccessException e1) {
            JOptionPane.showMessageDialog(p.getPane(), "The class of this shape can not be accessed.", "Illegal Access", JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchMethodException e1) {
            JOptionPane.showMessageDialog(p.getPane(), "The class of this shape does not follow the rules for classes of shapes.", "Lack of Constructor", JOptionPane.ERROR_MESSAGE);
        } catch (InvocationTargetException e1) {
            JOptionPane.showMessageDialog(p.getPane(), "The class of the shape triggered an error.", "Class Error", JOptionPane.ERROR_MESSAGE);
        }
        return s;
    }

    /**
     * Diese Methode liest aus der Datei, die auf dem Pfad {@link Planer#SHAPE_CLASSES_PATH} liegt, alle verfügbaren Form-Klassen
     * aus und schreibt diese Klassen in {@link Planer#shapeClasses}.
     */
    private void loadShapeClasses() {
        InputStream ist = getClass().getResourceAsStream(SHAPE_CLASSES_PATH);
        Reader r;
        try {
            r = new InputStreamReader(ist, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return;
        }
        InputSource iso;
        iso = new InputSource(r);
        iso.setEncoding("UTF-8");

        SAXParser saxp;
        try {
            saxp = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            JOptionPane.showMessageDialog(p.getPane(), "The file containing the usable shapes could not be parsed", "Parser Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DefaultHandler dh = new DefaultHandler() {
            ShapeClassLoader scl = new ShapeClassLoader();
            Class c;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("shape") || qName.equalsIgnoreCase("abstractShape")) {
                    try {
                        c = scl.loadClass(attributes.getValue("name"));
                    } catch (ClassNotFoundException e) {
                        JOptionPane.showMessageDialog(p.getPane(), "One of the shape classes could not be found.", "Shape Class not Found", JOptionPane.ERROR_MESSAGE);
                    }
                    shapeClasses.put(attributes.getValue("name"), c);
                }
            }
        };
        try {
            saxp.parse(iso, dh);
        } catch (SAXException e) {
            JOptionPane.showMessageDialog(p.getPane(), "An error ocurred during parsing the file containing the usable shapes.", "Parsing Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(p.getPane(), "The file containing the usable shapes could not be loaded.", "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Diese Methode zeichnet alle Formen neu, um zu verindern, dass durch den XOR-Zeichenmodus Teile nicht
     * betroffener Formen gelöscht werden.
     */
    private void redraw() {
        p.storeGraphics();      // ⸣
        p.clearStore(bgColor);  // ⸠ Diese Zeilen löschen alle vorherigen Zeichnungen ohne den Hintergrund zu löschen.
        p.recallGraphics();     // ⸥
        p.setPaintMode();
        p.setColor(penColor);
        for (Shape s :
                shapesList) {
            s.draw(p, false);
        }
    }

    /**
     * In dieser Methode wird ein Menü für das Zeichenfenster erzeugt.
     * <p>Als {@link Action} der Menüpunkte wird jeweils eine anonyme Klasse verwendet, um die dem Menüpubkt entsprechende Aktion
     * durchzuführen.</p>
     * <p>Der Menüpunkt {@code Shapes} wird automatisch aus der Datei, in der alle verfügbaren Formen und ihre Klassen aufgelistet
     * sind, mit allen verfügbaren Formen gefüllt.</p>
     *
     * @return Die Menüleiste, die von {@link Planer#p} verwendet wird.
     */
    private JMenuBar setMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(new AbstractAction("Open file...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jfc.showOpenDialog(p.getPane()) == JFileChooser.APPROVE_OPTION) {
                    File f = jfc.getSelectedFile();
                    replaceShapes(f);
                }
            }
        }));
        fileMenu.add(new JMenuItem(new AbstractAction("Save to...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jfc.showSaveDialog(p.getPane()) == JFileChooser.APPROVE_OPTION) {
                    File f = jfc.getSelectedFile();
                    saveShapes(f);
                }
            }
        }));

        final JMenu shapesMenu = new JMenu("Shapes");

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem(new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                state = State.DELETE;
            }
        }));
        editMenu.add(new JMenuItem(new AbstractAction("Drag") {
            @Override
            public void actionPerformed(ActionEvent e) {
                state = State.DRAG;
            }
        }));
        editMenu.add(new JMenuItem(new AbstractAction("Resize") {
            @Override
            public void actionPerformed(ActionEvent e) {
                state = State.RESIZE;
            }
        }));

        JMenu colorMenu = new JMenu("Color");
        colorMenu.add(new JMenuItem(new AbstractAction("Change Pen Color") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color nC = JColorChooser.showDialog(p.getPane(), "Pen Color", penColor);
                if (nC != null) {
                    penColor = nC;
                    p.setColor(nC);
                }
            }
        }));
        colorMenu.add(new JMenuItem(new AbstractAction("Change Background Color") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color nC = JColorChooser.showDialog(p.getPane(), "Background Color", bgColor);
                if (nC != null) {
                    bgColor = nC;
                    p.bgColor(bgColor);
                    redraw();
                }
            }
        }));

        InputStream ist = getClass().getResourceAsStream(SHAPE_CLASSES_PATH);
        Reader r;
        try {
            r = new InputStreamReader(ist, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return null;
        }
        InputSource iso;
        iso = new InputSource(r);
        iso.setEncoding("UTF-8");

        SAXParser saxp;
        try {
            saxp = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            JOptionPane.showMessageDialog(p.getPane(), "The file containing the usable shapes could not be parsed", "Parser Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        DefaultHandler dh = new DefaultHandler() {
            JMenu parent = null;
            JMenu current = shapesMenu;

            @Override
            public void startElement(String uri, String localName, String qName, final Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("shape")) {
                    String screenName = null;
                    final String shapeString;
                    Class<Shape> shape = shapeClasses.get(attributes.getValue("name"));
                    try {
                        screenName = (String) shape.getMethod("getShapeName").invoke(shape.getConstructor(double.class, double.class, double.class, double.class).newInstance(0.0, 0.0, 0.0, 0.0));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        JOptionPane.showMessageDialog(null, "The class " + shape.getCanonicalName() + " does not conform to the rules for shape classes", "Misformed Shape Class", JOptionPane.ERROR_MESSAGE);
                    }
                    shapeString = attributes.getValue("name");
                    current.add(new JMenuItem(new AbstractAction(screenName) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            state = State.DRAW;
                            currentShapeString = shapeString;
                        }
                    }));
                } else if (qName.equalsIgnoreCase("abstractShape")) {
                    JMenu abstractShapeMenu = new JMenu(attributes.getValue("name"));
                    current.add(abstractShapeMenu);
                    parent = current;
                    current = abstractShapeMenu;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (qName.equalsIgnoreCase("abstractShape")) {
                    current = parent;
                }
            }
        };
        try {
            saxp.parse(iso, dh);
        } catch (SAXException e) {
            JOptionPane.showMessageDialog(p.getPane(), "An error ocurred during parsing the file containing the usable shapes.", "Parsing Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(p.getPane(), "The file containing the usable shapes could not be loaded.", "IO Error", JOptionPane.ERROR_MESSAGE);
        }

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(shapesMenu);
        menuBar.add(editMenu);
        menuBar.add(colorMenu);

        return menuBar;
    }

    /**
     * Diese Methode speichert alle Formen, die sich in der {@link Planer#shapesList} befinden, in einer XML-Datei.
     * <p>Das Wurzelelement ist {@code <shapes>}.</p>
     * <p>Die Kindelemente heißen {@code <shape>}. Das Attribut {@code type} legt den Typ der Form fest. Die Attribute
     * {@code x0}, {@code y0}, {@code x1} und {@code y1} legen die Koordinaten der Form fest.</p>
     *
     * @param f Die Datei, in der die Formen gespeichert werden
     */
    private void saveShapes(File f) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            JOptionPane.showMessageDialog(p.getPane(), "Something in your parser configuration is wrong.", "Parser Configuratipon Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Document d = db.newDocument();
        Element rE = d.createElement("shapes");
        d.appendChild(rE);
        for (Shape s :
                shapesList) {
            Element e = d.createElement("shape");
            rE.appendChild(e);
            double[] coords = s.getCoords();
            e.setAttribute("type", s.getClassName());
            e.setAttribute("x0", String.valueOf(coords[0]));
            e.setAttribute("y0", String.valueOf(coords[1]));
            e.setAttribute("x1", String.valueOf(coords[2]));
            e.setAttribute("y1", String.valueOf(coords[3]));
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t;
        try {
            t = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            JOptionPane.showMessageDialog(p.getPane(), "Something in your configuration is wrong.", "Configuration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DOMSource ds = new DOMSource(d);
        StreamResult sr = new StreamResult(f);
        try {
            t.transform(ds, sr);
        } catch (TransformerException e) {
            JOptionPane.showMessageDialog(p.getPane(), "An error ocurred during writing.", "Transformation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Diese Methode parst eine XML-Datei und erstellt aus dem Inhalt Formen, speichert diese in der {@link Planer#shapesList}
     * und lässt alle Formen neu zeichnen.
     *
     * @param f Die Datei, aus der gelesen wird
     */
    private void replaceShapes(File f) {
        InputStream ist;
        try {
            ist = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(p.getPane(), "The file you chose could not be found.", "File not found", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Reader r;
        try {
            r = new InputStreamReader(ist, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return;
        }
        InputSource iso;
        iso = new InputSource(r);
        iso.setEncoding("UTF-8");

        SAXParser saxp;
        try {
            saxp = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            JOptionPane.showMessageDialog(p.getPane(), "The File you chose could not be parsed.", "Parser Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DefaultHandler dh = new DefaultHandler() {
            Shape s;
            double x0, y0, x1, y1;
            String coord = "";

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (qName.equalsIgnoreCase("shape")) {
                    if (shapeClasses.containsKey(attributes.getValue("type"))) {
                        s = getNewShapeInstance(attributes.getValue("type"), Double.parseDouble(attributes.getValue("x0")), Double.parseDouble(attributes.getValue("y0")), Double.parseDouble(attributes.getValue("x1")), Double.parseDouble(attributes.getValue("y1")));
                    }
//                    if (attributes.getValue("type").equalsIgnoreCase("rectangle")) {
//                        s = new Rectangle(Double.parseDouble(attributes.getValue("x0")), Double.parseDouble(attributes.getValue("y0")), Double.parseDouble(attributes.getValue("x1")), Double.parseDouble(attributes.getValue("y1")));
//                    } else if (attributes.getValue("type").equalsIgnoreCase("square")) {
//                        s = new Square(Double.parseDouble(attributes.getValue("x0")), Double.parseDouble(attributes.getValue("y0")), Double.parseDouble(attributes.getValue("x1")), Double.parseDouble(attributes.getValue("y1")));
//                    } else if (attributes.getValue("type").equalsIgnoreCase("right-triangle")) {
//                        s = new RightTriangle(Double.parseDouble(attributes.getValue("x0")), Double.parseDouble(attributes.getValue("y0")), Double.parseDouble(attributes.getValue("x1")), Double.parseDouble(attributes.getValue("y1")));
//                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                shapesList.add(s);
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                String s = new String(ch, start, length);
                switch (coord) {
                    case "x0":
                        x0 = Double.parseDouble(s);
                        break;
                    case "y0":
                        y0 = Double.parseDouble(s);
                        break;
                    case "x1":
                        x1 = Double.parseDouble(s);
                        break;
                    case "y1":
                        y1 = Double.parseDouble(s);
                        break;
                }
            }

            @Override
            public void endDocument() throws SAXException {
                redraw();
            }
        };

        try {
            saxp.parse(iso, dh);
        } catch (SAXException e) {
            JOptionPane.showMessageDialog(p.getPane(), "An error ocurred during parsing.", "Parsing Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(p.getPane(), "The file you chose could not be loaded.", "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Eine Auflistung aller möglichen Zustände dieser Klasse.
     */
    private enum State {
        DRAG, RECTANGLE, SQUARE, RIGHT_TRIANGLE, RESIZE, DRAW, DELETE // An dieser Stelle müssen neue Formen eingetragen werden.
    }


    /**
     * Ein {@link ClassLoader}, der nur Klassen aus dem {@code shapes}-Package lädt.
     */
    private final class ShapeClassLoader extends ClassLoader {
        /**
         * Diese Methode lädt nur Klassen, die im {@code shapes}-Package liegen.
         *
         * @param name Der Name, der Klasse, die geladen werden soll
         * @return Die Klasse, die geladen werden sollte, falls sie gefunden wurde
         * @throws ClassNotFoundException falls die Klasse nicht gefunden werden konnte
         */
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            name = "planer.shapes." + name;
            String file = name.replace('.', File.separatorChar) + ".class";
            byte[] b;
            try {
                b = loadClassFileData(file);
                Class c = defineClass(name, b, 0, b.length);
                resolveClass(c);
                return c;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(p.getPane(), name + "could not be loaded. Please provide the correct file or remove this class from shapes.xml", "File not found", JOptionPane.ERROR_MESSAGE);
            }
            throw new ClassNotFoundException();
        }

        /**
         * Diese Methode lädt die Binärdaten aus einer {@code .class}-Datei
         *
         * @param name Die Datei, die geladen werden soll
         * @return Die Binärdaten der Datei
         * @throws IOException falls die Datei nicht gelesen werden konnte
         */
        private byte[] loadClassFileData(String name) throws IOException {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(
                    name);
            int size = stream.available();
            byte buff[] = new byte[size];
            DataInputStream in = new DataInputStream(stream);
            in.readFully(buff);
            in.close();
            return buff;
        }
    }
}