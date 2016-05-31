package pixy.image;

import java.io.InputStream;

/**
 * Abstract factory to create a new Bitmap.
 *
 * Created by k3b on 31.05.2016.
 */
public class BitmapFactory {
    private static IBitmapFactory mFactory;

    public static void register(IBitmapFactory factory) {
        BitmapFactory.mFactory = factory;
    }

    /**  */
    public interface IBitmapFactory {
        /**
         * Returns a immutable bitmap with the specified width and height, with each
         * pixel value set to the corresponding value in the colors array.  Its
         * initial density is as per getDensity.
         *
         * @param colors Array of Color used to initialize the pixels.
         *               This array must be at least as large as width * height.
         * @param width  The width of the bitmap
         * @param height The height of the bitmap
         * @throws IllegalArgumentException if the width or height are <= 0, or if
         *                                  the color array's length is less than the number of pixels.
         */
        IBitmap createBitmap(int colors[], int width, int height);

        /**
         * Decode an input stream into a bitmap. If the input stream is null, or
         * cannot be used to decode a bitmap, the function returns null.
         * The stream's position will be where ever it was after the encoded data
         * was read.
         *
         * @param is The input stream that holds the raw data to be decoded into a
         *           bitmap.
         * @return The decoded bitmap, or null if the image data could not be decoded.
         */
        IBitmap decodeStream(InputStream is);
    }

    /**
     * Returns a immutable bitmap with the specified width and height, with each
     * pixel value set to the corresponding value in the colors array.  Its
     * initial density is as per getDensity.
     *
     * @param colors   Array of Color used to initialize the pixels.
     *                 This array must be at least as large as width * height.
     * @param width    The width of the bitmap
     * @param height   The height of the bitmap
     * @throws IllegalArgumentException if the width or height are <= 0, or if
     *         the color array's length is less than the number of pixels.
     */
    public static IBitmap createBitmap(int colors[], int width, int height) {
        return mFactory.createBitmap(colors, width, height);
    }

    /**
     * Decode an input stream into a bitmap. If the input stream is null, or
     * cannot be used to decode a bitmap, the function returns null.
     * The stream's position will be where ever it was after the encoded data
     * was read.
     *
     * @param is The input stream that holds the raw data to be decoded into a
     *           bitmap.
     * @return The decoded bitmap, or null if the image data could not be decoded.
     */
    public static IBitmap decodeStream(InputStream is) {
        return mFactory.decodeStream(is);
    }


}
