package blinkingsquares;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.application.Platform;

/**
 *  A MosaicPanel object represents a grid containing rows
 *  and columns of colored rectangles.  There can be "grouting"
 *  between the rectangles.  (The grouting is just drawn as a 
 *  one-pixel outline around each rectangle.)  The rectangles can
 *  optionally be drawn as raised 3D-style rectangles; this effect
 *  works much better with some colors than with others.  Methods are 
 *  provided for getting and setting the colors of the rectangles.
 */

public class MosaicCanvas extends Canvas {
	//------------------ private instance variables --------------------


    private int rows;       // The number of rows of rectangles in the grid.
    private int columns;    // The number of columns of rectangles in the grid.
    private Color defaultColor;   // Color used for any rectangle whose color
                                  //    has not been set explicitly.  This
                                  //    can never be null.
    private Color groutingColor;  // The color for "grouting" between 
                                  //    rectangles.  If this is null, no
                                  //    grouting is drawn.
    private boolean alwaysDrawGrouting;  // Grouting is drawn around default-
                                         //    colored rects if this is true.
    private boolean use3D = true;   // If true, 3D rects are used; if false, 
                                    //       flat rects are used to draw the rectangles.
    private boolean autopaint = true;  // If true, then when a square's color is set, 
                                       //     repaint is called automatically.
    private Color[][] grid; // An array that contains the rectangles' colors.
                            //   If a null occurs in this array, the rectangle
                            //   is drawn in the default color, and "grouting"
                            //   will be drawn around that rectangle only if
                            //   alwaysDrawGrouting is true.  Also, the 
                            //   rectangle is drawn as a flat rectangle rather
                            //   than as a 3D rectangle.
    private GraphicsContext g; // The graphics context for drawing on this canvas.


    //------------------------ constructors -----------------------------


    /**
     *  Construct a MosaicPanel with 42 rows and 42 columns of rectangles,
     *  and with preferred rectangle height and width both set to 16.
     */
    public MosaicCanvas() {
        this(42,42);
    }

    /**
     *  Construct a MosaicPanel with specified numbers of rows and columns of rectangles,
     *  and with preferred rectangle height and width both set to 16.
     */
    public MosaicCanvas(int rows, int columns) {
        this(rows,columns,16,16);
    }



    /**
     *  Construct a MosaicPanel with the specified number of rows and
     *  columns of rectangles, and with a specified preferred size for the  
     *  rectangle.  The default rectangle color is black, the
     *  grouting color is gray, and alwaysDrawGrouting is set to false.
     *  If a non-null border color is specified, then a border of that color is added
     *  to the panel, and its width is taken into account in the computation of the preferred
     *  size of the panel.
     *  @param rows the mosaic will have this many rows of rectangles.  This must be a positive number.
     *  @param columns the mosaic will have this many columns of rectangles.  This must be a positive number.
     *  @param preferredBlockHeight the preferred height of the mosaic will be set to this value times the number of
     *  rows.  The actual height is set by the component that contains the mosaic, and so might not be
     *  equal to the preferred height.  Size is measured in pixels.  The value should not be less than about 5,
     *  and any smaller value will be increased to 5.
     *  @param preferredBlockWidth the preferred width of the mosaic will be set to this value times the number of
     *  columns.  The actual width is set by the component that contains the mosaic, and so might not be
     *  equal to the preferred width.  Size is measured in pixels.  The value should not be less than about 5,
     *  and any smaller value will be increased to 5.
     */
    public MosaicCanvas(int rows, int columns, int preferredBlockHeight, int preferredBlockWidth) {
        
        this.rows = rows;
        this.columns = columns;
        if (rows <= 0 || columns <= 0)
            throw new IllegalArgumentException("Rows and Columns must be greater than zero.");
        preferredBlockHeight = Math.max( preferredBlockHeight, 5);
        preferredBlockWidth = Math.max( preferredBlockWidth, 5);
        grid = new Color[rows][columns];
        defaultColor = Color.BLACK;
        groutingColor = Color.GRAY;
        alwaysDrawGrouting = false;
        setWidth(preferredBlockWidth*columns);
        setHeight(preferredBlockHeight*rows);
        g = getGraphicsContext2D();
    }



    /**
     *  Set the color of the "grouting" that is drawn between rectangles.
     *  If the value is null, no grouting is drawn and the rectangles
     *  fill the entire grid.   When a mosaic is first created, the
     *  groutingColor is gray.
     */
    public void setGroutingColor(Color c) {
        if (c == null || !c.equals(groutingColor)) {
            groutingColor = c;
            forceRedraw();
        }
    }



    /**
     *  Return the red component of color of the rectangle in the
     *  specified row and column, as a double value in the range
     *  0.0 to 1.0.  If the specified rectangle lies outside 
     *  the grid or if no color has been specified for the rectangle,
     *  then the red component of the defaultColor is returned.
     */
    public double getRed(int row, int col) {
        if (row >=0 && row < rows && col >= 0 && col < columns && grid[row][col] != null)
            return grid[row][col].getRed();
        else
            return defaultColor.getRed();
    }


    /**
     *  Return the green component of color of the rectangle in the
     *  specified row and column, as a double value in the range
     *  0.0 to 1.0.  If the specified rectangle lies outside 
     *  the grid or if no color has been specified for the rectangle,
     *  then the green component of the defaultColor is returned.
     */
    public double getGreen(int row, int col) {
        if (row >=0 && row < rows && col >= 0 && col < columns && grid[row][col] != null)
            return grid[row][col].getGreen();
        else
            return defaultColor.getGreen();
    }


    /**
     *  Return the blue component of color of the rectangle in the
     *  specified row and column, as a double value in the range
     *  0.0 to 1.0.  If the specified rectangle lies outside 
     *  the grid or if no color has been specified for the rectangle,
     *  then the blue component of the defaultColor is returned.
     */
    public double getBlue(int row, int col) {
        if (row >=0 && row < rows && col >= 0 && col < columns && grid[row][col] != null)
            return grid[row][col].getBlue();
        else
            return defaultColor.getBlue();
    }

    
    /**
     * Set the use3D property.  When this property is true, the rectangles are
     * drawn as "3D" rects, which are supposed appear to be raised.  When use3D
     * is false, they are drawn as regular "flat" rects.  Note that flat rects
     * are always used for background squares that have not been assigned a color.
     * The default value of use3D is true;
     */
    public void setUse3D(boolean use3D) {
        this.use3D = use3D;
    }

    /**
     *  Set the color of the rectangle in the specified row and column,
     *  where the RGB color components are given as double values in 
     *  the range 0.0 to 1.0. Values are clamped to lie in that range.
     *  If the rectangle lies outside the grid, this is simply ignored.
     */
    public void setColor(int row, int col, double red, double green, double blue) {
        if (row >=0 && row < rows && col >= 0 && col < columns) {
            red = (red < 0)? 0 : ( (red > 1)? 1 : red );
            green = (green < 0)? 0 : ( (green > 1)? 1 : green );
            blue = (blue < 0)? 0 : ( (blue > 1)? 1 : blue );
            grid[row][col] = Color.color(red,green,blue);
            drawSquare(row,col);
        }
    }


    /**
     * This method can be called to force redrawing of the entire mosaic.  The only
     * time it might be necessary for users of this class to call this method is
     * while the autopaint property is set to false, and it is desired to show
     * all the changes that have been made to the mosaic, without resetting
     * the autopaint property to true.
     * @see #setAutopaint(boolean)
     */
    final public void forceRedraw() {
        drawAllSquares();
    }

   

    // private implementation section -- the only part that actually draws squares
    
    private void drawSquare(int row, int col) {
        if ( autopaint ) {
            if (Platform.isFxApplicationThread()) {
                drawOneSquare(row,col);
            }
            else {
                Platform.runLater( () -> drawOneSquare(row,col) );
            }
            try { // to avoid overwhelming the application thread with draw operations...
                Thread.sleep(1);
            }
            catch (InterruptedException e) {
            }
        }
    }
    
    private void drawAllSquares() {
        if (Platform.isFxApplicationThread()) {
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < columns; c++)
                    drawOneSquare(r,c);
        }
        else {
            Platform.runLater( () -> {
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < columns; c++)
                    drawOneSquare(r,c);
            } );
        }
        try { // to avoid overwhelming the application thread with draw operations...
            Thread.sleep(1);
        }
        catch (InterruptedException e) {
        }
    }
    
    private void drawOneSquare(int row, int col) {
           // only called from two previous methods
        double rowHeight = getHeight() / rows;
        double colWidth = getWidth() / columns;
        int y = (int)Math.round(rowHeight*row);
        int h = Math.max(1, (int)Math.round(rowHeight*(row+1)) - y);
        int x = (int)Math.round(colWidth*col);
        int w = Math.max(1, (int)Math.round(colWidth*(col+1)) - x);
        Color c = grid[row][col];
        g.setFill( (c == null)? defaultColor : c );
        if (groutingColor == null || (c == null && !alwaysDrawGrouting)) {
            if (!use3D || c == null)
                g.fillRect(x,y,w,h);
            else
                fill3DRect(c,x,y,w,h);
        }
        else {
            if (!use3D || c == null)
                g.fillRect(x+1,y+1,w-2,h-2);
            else
                fill3DRect(c,x+1,y+1,w-2,h-2);
            g.setStroke(groutingColor);
            g.strokeRect(x+0.5,y+0.5,w-1,h-1);
        }
    }
    
    /**
     * 
     * */

    private void fill3DRect(Color color, int x, int y, int width, int height) {
        double h = color.getHue();
        double b = color.getBrightness();
        double s = color.getSaturation();
        if (b > 0.8) {
            b = 0.8;
            g.setFill(Color.hsb(h,s,b));
        }
        else if (b < 0.2) {
            b = 0.2;
            g.setFill(Color.hsb(h,s,b));
        }
        g.fillRect(x,y,width,height);
        g.setStroke(Color.hsb(h,s,b+0.2));
        g.strokeLine(x+0.5,y+0.5,x+width-0.5,y+0.5);
        g.strokeLine(x+0.5,y+0.5,x+0.5,y+height-0.5);
        g.setStroke(Color.hsb(h,s,b-0.2));
        g.strokeLine(x+width-0.5,y+1.5,x+width-0.5,y+height-0.5);
        g.strokeLine(x+1.5,y+height-0.5,x+width-0.5,y+height-0.5);
    }
    
    /**
     * */
    


} //end of MosaicCanvas
