/*
 * Copyright (c) 2014 Roy Six
 * Font Awesome by Dave Gandy - http://fontawesome.io
 */

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code FontAwesome} converts Font Awesome icons to image files, such as in PNG or GIF format.
 * <p/>
 * For usage and examples, see the accompanying README.MD file.
 *
 * @author  Roy Six
 * @version 1.0
 */
public class FontAwesome {

    int size; float padding; Font ffont; float fsize; Color fcolor; Font sfont; float ssize; Color scolor; String sicon; String format;

    /**
     * Main method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        validateArgs(args);
        System.out.format( "\n%30s", "Initializing font... ");
        Font font = initFont();
        System.out.print("Done!\n");
        System.out.format("%30s", "Initializing icons... ");
        Map<String, Character> icons = initIcons();
        System.out.print("Done!\n");
        System.out.format("%30s", "Initializing properties... ");
        FontAwesome properties = initProperties(font, icons, args);
        System.out.print("Done!\n");
        System.out.format("%30s", "Saving " + icons.size() + " images... ");
        buildAndSaveImages(icons, properties);
        System.out.print("Done!\n");
    }

    /**
     * Validates the command line arguments.
     *
     * @param args the command line arguments
     */
    private static void validateArgs(String[] args) {
        if (args.length != 4 && args.length != 7) {
            System.out.print("\n\tFor regular icons, please enter 4 arguments:\n");
            System.out.print("\tjava FontAwesome [size] [color] [padding] [format]\n");
            System.out.print("\tex: \"java FontAwesome 48 0 1/8 png\"\n\n");
            System.out.print("\tFor stacked icons, please enter 7 arguments:\n");
            System.out.print("\tjava FontAwesome [size] [color] [padding] [format] [sicon] [ssize] [scolor]\n");
            System.out.print("\tex: \"java FontAwesome 24 ffffff 0 png square 48 0\"\n");
            System.exit(0);
        }
    }

    /**
     * Initializes the Font Awesome font via the provided TTF file.
     *
     * @return the font
     */
    private static Font initFont() {
        Font font = null;
        try (FileInputStream fis = new FileInputStream("fontawesome-webfont.ttf")) {
            font = Font.createFont(Font.TRUETYPE_FONT, fis);
        } catch (IOException | FontFormatException e) {
            System.err.print(e.getMessage() + "\n");
            System.exit(1);
        }
        return font;
    }

    /**
     * Initializes the Font Awesome icon map, a mapping of icon name keys to unicode characters via the provided CSS 
     * file.
     *
     * @return the icon map
     */
    private static Map<String, Character> initIcons() {
        Map<String, Character> icons = new HashMap<>(1000);
        try {
            List<String> lines = Files.readAllLines(Paths.get("font-awesome.css"), StandardCharsets.UTF_8);
            Pattern pvalue = Pattern.compile("(?<=\\\\).*(?=\")"); // (?<=\).*(?=")
            Pattern pkey = Pattern.compile("(?<=.fa-).*(?=:before)"); // (?<=.fa-).*(?=:before)
            for (int i = 0; i < lines.size(); i++) { // Check each line if it has a unicode value
                Matcher mvalue = pvalue.matcher(lines.get(i));
                if (mvalue.find()) {
                    Character value = (char) Integer.parseInt(mvalue.group(), 16);
                    for (int j = i - 1; j >= 0; j--) { // Check previous lines for the keys to this unicode value
                        Matcher mkey = pkey.matcher(lines.get(j));
                        if (mkey.find()) {
                            icons.put(mkey.group(), value);
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.print(e.getMessage() + "\n");
            System.exit(1);
        }
        return icons;
    }

    /**
     * Initializes the FontAwesome properties via the provided program arguments.
     *
     * @param font  the font
     * @param icons the icon map
     * @param args  the command line arguments
     * @return      the FontAwesome properties
     */
    private static FontAwesome initProperties(Font font, Map<String, Character> icons, String[] args) {
        FontAwesome properties = new FontAwesome();
        properties.fsize = args.length > 0 ? Integer.parseInt(args[0]) : 48;
        properties.fcolor = args.length > 1 ? new Color(Integer.parseInt(args[1], 16)) : new Color(0);
        properties.padding = args.length > 2 && args[2].contains("/") ? Float.parseFloat(args[2].split("/")[0]) / Float.parseFloat(args[2].split("/")[1]) : 0;
        properties.format = args.length > 3 ? args[3] : "png";
        properties.sicon = args.length > 4 ? icons.get(args[4]).toString() : null;
        properties.ssize = args.length > 5 ? Integer.parseInt(args[5]) : 0;
        properties.scolor = args.length > 6 ? new Color(Integer.parseInt(args[6], 16)) : null;
        properties.size = properties.fsize > properties.ssize ? (int) properties.fsize : (int) properties.ssize;
        properties.ffont = font.deriveFont(properties.fsize - properties.fsize * properties.padding);
        properties.sfont = properties.sicon != null ? font.deriveFont(properties.ssize - properties.ssize * properties.padding) : null;
        return properties;
    }

    /**
     * Builds and saves all images in the icons map based on the FontAwesome properties in the images folder.
     *
     * @param icons      the icon map
     * @param properties the FontAwesome properties
     */
    private static void buildAndSaveImages(Map<String, Character> icons, FontAwesome properties) {
        File images = new File("images");
        if (!images.exists()) { // Create the images folder if it doesn't already exit
            images.mkdir();
        }
        for (Map.Entry<String, Character> entry : icons.entrySet()) {
            saveImage(buildImage(properties.size, properties.sfont, properties.sicon, properties.scolor, properties.ffont,
                                 entry.getValue().toString(), properties.fcolor), "images/" + entry.getKey(), properties.format);
        }
    }

    /**
     * Builds a BufferedImage with the drawn icon graphic based on the font, size, and color.
     *
     * @param size   the encapsulating image size
     * @param sfont  the stacked font
     * @param sicon  the stacked icon unicode value
     * @param scolor the stacked color
     * @param ffont  the front font
     * @param ficon  the front icon unicode value
     * @param fcolor the front color
     * @return       the BufferedImage containing the drawn icon graphic
     */
    private static BufferedImage buildImage(int size,
                                           Font sfont, String sicon, Color scolor,
                                           Font ffont, String ficon, Color fcolor) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // AA
        if (sicon != null) { // Then draw stacked icon first
            graphics.setFont(sfont);
            graphics.setColor(scolor);
            Point spoint = calcDrawPoint(sfont, sicon, size, graphics);
            graphics.drawString(sicon, spoint.x, spoint.y);
        }
        graphics.setFont(ffont);
        graphics.setColor(fcolor);
        Point fpoint = calcDrawPoint(ffont, ficon, size, graphics);
        graphics.drawString(ficon, fpoint.x, fpoint.y);
        graphics.dispose();
        return image;
    }

    /**
     * Saves the generated images to the hard drive in the images folder.
     *
     * @param image      the BufferedImage containing the drawn icon graphic
     * @param fileName   the file name including path, but not including the file extension e.g. "square" or "images/square"
     * @param formatName the format name (file extension) to use e.g. "png" or "gif"
     */
    private static void saveImage(BufferedImage image, String fileName, String formatName) {
        try {
            ImageIO.write(image, formatName, new File(fileName + "." + formatName));
        } catch (IOException e) {
            System.err.print(e.getMessage() + "\n");
            System.exit(1);
        }
    }

    /**
     * Calculates the point to draw this icon with this Graphics2D. This will be the baseline lower-left point (X,Y)
     * coordinates and will be centered. The calculation is done using the FontMetrics and GlyphVector rectangle bounds.
     * <p/>
     * The centering technique used is based off of Exploding Pixels's "Drawing Text About Its Visual Center."
     *
     * @param font     the font to use (differs only in size)
     * @param icon     the icon unicode value to draw
     * @param size     the encapsulating image size of the bounding box
     * @param graphics the Graphics2D from the BufferedImage
     * @return         the point containing the x and y coordinates needed to draw the icon centered
     * @see            <a href="http://explodingpixels.wordpress.com/2009/01/29/drawing-text-about-its-visual-center/">
     *                 Exploding Pixels Drawing Text About Its Visual Center</a>
     */
    private static Point calcDrawPoint(Font font, String icon, int size, Graphics2D graphics) {
        int center = size / 2; // Center X and Center Y are the same
        Rectangle stringBounds = graphics.getFontMetrics().getStringBounds(icon, graphics).getBounds();
        Rectangle visualBounds = font.createGlyphVector(graphics.getFontRenderContext(), icon).getVisualBounds().getBounds();
        return new Point(center - stringBounds.width / 2, center - visualBounds.height / 2 - visualBounds.y);
    }

}