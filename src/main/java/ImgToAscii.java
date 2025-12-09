import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ImgToAscii {
    public static int[] imgToArray(String pathToImg) {
        BufferedImage image = null;

        try {
            File file = new File(pathToImg);
            image = ImageIO.read(file);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (image == null) {
            System.out.println("image was null");
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println(width + " : " + height);

        int[] pixelArray = new int[width * height ];
        image.getRGB(0, 0, width, height, pixelArray, 0, width);

        return pixelArray;
    }

    public static void pixelArrayToPng(int[] pixelArray, int width, int height) {
        String outputPath = "assets/out.png";
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixelArray, 0, width);

        try {
            File outputFile = new File(outputPath);
            ImageIO.write(image, "PNG", outputFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] toGrayscaleArray(int[] colorPixelArray) {
        // Create a new array to hold the grayscale pixels
        int[] grayscaleArray = new int[colorPixelArray.length];

        for (int i = 0; i < colorPixelArray.length; i++) {
            int p = colorPixelArray[i];

            // 1. Extract ARGB components using bitwise operations
            int alpha = (p >> 24) & 0xFF;
            int red = (p >> 16) & 0xFF;
            int green = (p >> 8) & 0xFF;
            int blue = p & 0xFF;

            // 2. Calculate the weighted Grayscale value (Luminosity Method)
            // Note: Using doubles for accurate calculation, then casting to int
            double grayDouble =
                    (0.2126 * red) +
                            (0.7152 * green) +
                            (0.0722 * blue);

            int gray = (int) grayDouble;

            // Ensure gray value is within the 0-255 range (optional, for safety)
            if (gray < 0) gray = 0;
            if (gray > 255) gray = 255;

            // 3. Pack the new ARGB value:
            // Alpha is kept, and the Gray value is set for R, G, and B
            int newPixel = (alpha << 24) | (gray << 16) | (gray << 8) | gray;

            grayscaleArray[i] = newPixel;
        }

        return grayscaleArray;
    }

    public static String convertToAsciiArt(int[] grayscaleArray, int width, int height) {
        final String ASCII_GRADIENT = " .:-=+*#%@";
        int gradientLength = ASCII_GRADIENT.length();

        StringBuilder asciiArt = new StringBuilder();

        for(int i = 0; i < grayscaleArray.length; i++) {
            int xCoordinate = i % width;
            int yCoordinate = i / width;

            int pixelValue = grayscaleArray[i];
            int lightIntensity = (pixelValue >> 16) & 0xFF;

            int index = (int) (lightIntensity / 255.0 * gradientLength);

            if (index >= gradientLength) {
                index = gradientLength - 1;
            }

            char asciiChar = ASCII_GRADIENT.charAt(index);
            asciiArt.append(asciiChar);

            if (xCoordinate == width - 1) {
                asciiArt.append('\n');
            }
        }

        return asciiArt.toString();
    }
    public static void main(String[] args) {

        int width = 744;
        int height = 597;

        int[] pixels = imgToArray("assets/sommerfugl.jpg");
        int[] grayscale = toGrayscaleArray(pixels);

        String asciiArt = convertToAsciiArt(grayscale, width, height);
        System.out.println(asciiArt);
    }
}


