import java.awt.*;
import java.util.*;

public class Steganography {
    /**
     * Clears the low bits of the pixel color.
     * The low bits are set to 0.
     *
     * @param px The pixel to modify.
     */
    public static void clearLow(Pixel px) {
        int r = px.getRed() & 0b11111100; // Clear the last two bits
        int g = px.getGreen() & 0b11111100; 
        int b = px.getBlue() & 0b11111100;
        // Set the pixel color with cleared low bits
        px.setColor(new Color(r, g, b));
    }
    /**
     * Clears the low bits of the pixel color.
     * The low bits are set to 0.
     *
     * @param img The picture whose pixels will be modified.
     * @return A new Picture object with the modified pixels.
     */
    public static Picture testClearLow(Picture img) {
        Picture copy = new Picture(img);
       for(Pixel px : copy.getPixels()) {
            clearLow(px);
        }
        return copy;
    }
    /**
     * Sets the low bits of the pixel color to the low bits of the given color.
     * The low bits are scaled to the range of 0-255.
     *
     * @param px The pixel to modify.
     * @param col The color whose low bits will be used.
     */
    public static void setLow(Pixel px, Color col) {
        int r = px.getRed() / 4 * 4; // Clear the last two bits
        int g = px.getGreen() / 4 * 4;
        int b = px.getBlue() / 4 * 4;
        
        int rLow = col.getRed() / 64;
        int gLow = col.getGreen() / 64;
        int bLow = col.getBlue() / 64;

        r += rLow;
        g += gLow; 
        b += bLow;

        px.setColor(new Color(r, g, b));
    }
    /**
     * Sets the low bits of all pixels in the picture to the low bits of the given color.
     * The low bits are scaled to the range of 0-255.
     *
     * @param img The picture whose pixels will be modified.
     * @param col The color whose low bits will be used.
     * @return A new Picture object with the modified pixels.
     */

    public static Picture testSetLow(Picture img, Color col) {
        Picture copy = new Picture(img);
        Pixel[][] grid = copy.getPixels2D();
        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                setLow(px, col);
            }
        }
        return copy;
    }
    /**
     * Reveals a hidden picture by extracting the low bits from the color values.
     * The low bits are scaled to the range of 0-255.
     *
     * @param hidden The picture with hidden information.
     * @return A new Picture object with the revealed image.
     */
    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] result = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();

        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[0].length; c++) {
                Color col = source[r][c].getColor();
                int rVal = (col.getRed() % 4) * 64;
                // Extract the low bits and scale them
                // to the range of 0-255
                int gVal = (col.getGreen() % 4) * 64;
                int bVal = (col.getBlue() % 4) * 64;
                result[r][c].setColor(new Color(rVal, gVal, bVal));
            }
        }
        return copy;
    }

    /**
     * Checks if a hidden picture can fit inside a base picture.
     *
     * @param base The base picture where the hidden picture will be placed.
     * @param hidden The hidden picture to be placed inside the base picture.
     * @return true if the hidden picture can fit inside the base picture, false otherwise.
     */

    public static boolean canHide(Picture base, Picture hidden) {
        return base.getWidth() >= hidden.getWidth() && base.getHeight() >= hidden.getHeight();
    }

    public static Picture hidePicture(Picture main, Picture secret, int rStart, int cStart) {
        Picture merged = new Picture(main);
        Pixel[][] mainPixels = merged.getPixels2D();
        Pixel[][] secretPixels = secret.getPixels2D();

        for (int r = 0; r < secretPixels.length; r++) {
            for (int c = 0; c < secretPixels[0].length; c++) {
                int rPos = rStart + r;
                int cPos = cStart + c;
                if (rPos < mainPixels.length && cPos < mainPixels[0].length) {
                    Color secretCol = secretPixels[r][c].getColor();
                    Color mainCol = mainPixels[rPos][cPos].getColor();
                    int rNew = (mainCol.getRed() / 4 * 4) + (secretCol.getRed() / 64);
                    int gNew = (mainCol.getGreen() / 4 * 4) + (secretCol.getGreen() / 64);
                    int bNew = (mainCol.getBlue() / 4 * 4) + (secretCol.getBlue() / 64);
                    mainPixels[rPos][cPos].setColor(new Color(rNew, gNew, bNew));
                }
            }
        }
        return merged;
    }

    /**
     * Compares two pictures to see if they are the same.
     * Two pictures are considered the same if they have the same dimensions
     * and all corresponding pixels have the same color.
     *
     * @param one The first picture to compare.
     * @param two The second picture to compare.
     * @return true if the pictures are the same, false otherwise.
     */

    public static boolean isSame(Picture one, Picture two) {
        if (one.getWidth() != two.getWidth() || one.getHeight() != two.getHeight()) {
            return false;
        }
        Pixel[][] grid1 = one.getPixels2D();
        Pixel[][] grid2 = two.getPixels2D();
        for (int r = 0; r < grid1.length; r++) {
            for (int c = 0; c < grid1[0].length; c++) {
                if (!grid1[r][c].getColor().equals(grid2[r][c].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Finds the differences between two pictures.
     * Returns a list of points where the colors differ.
     *
     * @param img1 The first picture to compare.
     * @param img2 The second picture to compare.
     * @return An ArrayList of Points representing the differing pixels.
     */

    public static ArrayList<Point> findDifferences(Picture img1, Picture img2) {
        ArrayList<Point> differences = new ArrayList<>();
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return differences;
        }
        Pixel[][] grid1 = img1.getPixels2D();
        Pixel[][] grid2 = img2.getPixels2D();
        for (int r = 0; r < grid1.length; r++) {
            for (int c = 0; c < grid1[0].length; c++) {
                if (!grid1[r][c].getColor().equals(grid2[r][c].getColor())) {
                    differences.add(new Point(c, r));
                }
            }
        }
        return differences;
    }

    /**
     * Highlights the area of difference between two pictures.
     * Draws a rectangle around the differing area in blue.
     *
     * @param img The original picture.
     * @param diffs The list of points where the pictures differ.
     * @return A new Picture object with the highlighted area.
     */

    public static Picture showDifferentArea(Picture img, ArrayList<Point> diffs) {
        Picture highlighted = new Picture(img);
        if (diffs.isEmpty()) {
            return highlighted;
        }

        int minR = Integer.MAX_VALUE, maxR = Integer.MIN_VALUE;
        int minC = Integer.MAX_VALUE, maxC = Integer.MIN_VALUE;

        for (Point pt : diffs) {
            int r = pt.y;
            int c = pt.x;
            minR = Math.min(minR, r);
            maxR = Math.max(maxR, r);
            minC = Math.min(minC, c);
            maxC = Math.max(maxC, c);
        }

        Graphics2D g = highlighted.createGraphics();
        g.setColor(Color.BLUE);
        g.drawRect(minC, minR, maxC - minC, maxR - minR);
        g.dispose();

        return highlighted;
    }

    /**
     * Encodes a string into an ArrayList of integers.
     * Each letter is represented by its position in the alphabet (A=1, B=2, ..., Z=26).
     * Spaces are represented by 27, and the end of the string is marked with 0.
     *
     * @param input The string to encode.
     * @return An ArrayList of integers representing the encoded string.
     */

    public static ArrayList<Integer> encodeString(String input) {
        input = input.toUpperCase();
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            String ch = input.substring(i, i + 1);
            if (ch.equals(" ")) {
                result.add(27);
            } else {
                result.add(letters.indexOf(ch) + 1);
            }
        }
        result.add(0);
        return result;
    }

    /**
     * Decodes an ArrayList of integers back into a string.
     * Each integer corresponds to a letter (1=A, 2=B, ..., 26=Z).
     * 27 represents a space, and 0 indicates the end of the string.
     *
     * @param values The ArrayList of integers to decode.
     * @return The decoded string.
     */

    public static String decodeString(ArrayList<Integer> values) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String output = "";
        for (int val : values) {
            if (val == 27) {
                output += " ";
            } else {
                output += letters.substring(val - 1, val);
            }
        }
        return output;
    }

    /**
     * Converts an integer value into an array of three integers representing the low bits.
     * Each integer in the array corresponds to a pair of bits (0-3).
     *
     * @param val The integer value to convert.
     * @return An array of three integers representing the low bits.
     */

    private static int[] getBitPairs(int val) {
        int[] bits = new int[3];
        for (int i = 0; i < 3; i++) {
            bits[i] = val % 4;
            val = val / 4;
        }
        return bits;
    }

    /**
     * Hides a message in a picture by modifying the low bits of the pixel colors.
     * Each character in the message is encoded into three pairs of bits,
     * which are then stored in the pixel colors.
     *
     * @param img The picture where the message will be hidden.
     * @param message The message to hide in the picture.
     */

    public static void hideText(Picture img, String message) {
        ArrayList<Integer> codes = encodeString(message);
        ArrayList<int[]> bits = new ArrayList<>();
        Pixel[][] grid = img.getPixels2D();
        

        for(int num : codes) {
            int[] bitPair = getBitPairs(num);
            bits.add(bitPair);
        }

        int i = 0;
        int j = 0;
            for (int c = 0; c < codes.size(); c++) {
                int redBit = bits.get(c)[0];
                int greenBit = bits.get(c)[1];
                int blueBit = bits.get(c)[2];

                Pixel px = grid[i][j];
                int rNew = (px.getRed() / 4) * 4 + redBit;
                int gNew = (px.getGreen() / 4) * 4 + greenBit;
                int bNew = (px.getBlue() / 4) * 4 + blueBit;

                px.setColor(new Color(rNew, gNew, bNew));
                j++;
                if(j == grid[0].length) {
                    j = 0;
                    i++;
                }
            }


    }

    /**
     * Reveals a hidden message in a picture by extracting the low bits from the pixel colors.
     * The low bits are decoded back into a string.
     *
     * @param img The picture with the hidden message.
     * @return The revealed message as a string.
     */

    public static String revealText(Picture img) {
        ArrayList<Integer> letters = new ArrayList<>();
        Pixel[][] grid = img.getPixels2D();

        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                Color col = px.getColor();
                int r = col.getRed() % 4;
                r +=  col.getGreen() % 4 * 4;
                r += col.getBlue() % 4 * 16;
                if(r == 0){
                    return decodeString(letters);
                }
                letters.add(r);
            }
        }
        return decodeString(letters);
    }
    /**
     * Main method to test the functionality of the Steganography class.
     * It demonstrates hiding and revealing pictures, comparing pictures,
     * finding differences, and hiding/revealing text in pictures.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Picture beach = new Picture("beach.jpg");
        Picture arch = new Picture("arch.jpg");
        beach.explore();
        Picture pink = testSetLow(beach, Color.PINK);
        pink.explore();
        Picture revealed = revealPicture(pink);
        revealed.explore();

        System.out.println("\nCan you hide the arch in the beach photo?: " + canHide(beach, arch));
        if (canHide(beach, arch)) {
            Picture merged = hidePicture(beach, arch, 0, 0);
            merged.explore();
            Picture unhidden = revealPicture(merged);
            unhidden.explore();
        }

        Picture swan1 = new Picture("swan.jpg");
        Picture swan2 = new Picture("swan.jpg");
        System.out.println("\nAre the swans the same?:  " + isSame(swan1, swan2));
        swan1 = testClearLow(swan1);
        System.out.println("After clearing low bits, are the swans still the same?: " + isSame(swan1, swan2));

        Picture a1 = new Picture("arch.jpg");
        Picture a2 = new Picture("arch.jpg");
        Picture k = new Picture("koala.jpg");
        Picture r = new Picture("robot.jpg");
        ArrayList<Point> diffs = findDifferences(a1, a2);
        System.out.println("\nWhat is the size of the diffs list after comparing two identical pictures: " + diffs.size());
        diffs = findDifferences(a1, k);
        System.out.println("What is the size of the diffs list after comparing two different sized pictures: " + diffs.size());
        a2 = hidePicture(a1, r, 65, 102);
        diffs = findDifferences(a1, a2);
        System.out.println("Diffs list size after hiding a picture: " + diffs.size());
        a1.show();
        a2.show();

        Picture hall = new Picture("femaleLionAndHall.jpg");
        Picture bot = new Picture("robot.jpg");
        Picture flower = new Picture("flower1.jpg");
        Picture h2 = hidePicture(hall, bot, 50, 300);
        Picture h3 = hidePicture(h2, flower, 115, 275);
        h3.explore();
        if (!isSame(hall, h3)) {
            Picture outline = showDifferentArea(hall, findDifferences(hall, h3));
            outline.show();
            Picture reveal = revealPicture(h3);
            reveal.show();
        }

        Picture msgPic = new Picture("beach.jpg");
        hideText(msgPic, "SECRET MESSAGE");
        String hiddenText = revealText(msgPic);
        System.out.println("\nMessage that's revealed: " + hiddenText);

        Picture bike = new Picture("blueMotorcycle.jpg");
        bike.explore();
        bike.explore();
    }
}
