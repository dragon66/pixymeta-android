package pixy.image;

import java.io.IOException;
import java.io.InputStream;

import pixy.meta.adobe.ImageResourceID;

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

    public static IBitmapFactory getFactory() {
        if (mFactory == null) {
            throw new RuntimeException("BitmapFactory.register() has not been called. Forgot to call BitmapFactoryNative.init() for j2se or android?");
        }
        return mFactory;
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
         * @param width1
         *@param i
         * @param width  The width of the bitmap
         * @param thumbnailData
         * @param height The height of the bitmap   @throws IllegalArgumentException if the width or height are <= 0, or if
         *                                  the color array's length is less than the number of pixels.
         */
        IBitmap createBitmap(int colors[], int width1, int i, int width, byte[] thumbnailData, int height, ImageResourceID id);

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
        IBitmap decodeStream(InputStream is)  throws IOException;
    }

    /**
     * Returns a immutable bitmap with the specified width and height, with each
     * pixel value set to the corresponding value in the colors array.  Its
     * initial density is as per getDensity.
     *  @param colors   Array of Color used to initialize the pixels.
     *                 This array must be at least as large as width * height.
     * @param width    The width of the bitmap
     * @param height   The height of the bitmap
     * @param totalSize
     * @param thumbnailData @throws IllegalArgumentException if the width or height are <= 0, or if
     * @param paddedRowBytes
     */
    public static IBitmap createBitmap(int colors[], int width, int height, int totalSize, byte[] thumbnailData, int paddedRowBytes, ImageResourceID id) {
        return getFactory().createBitmap(colors, width, height, totalSize, thumbnailData, paddedRowBytes, id);
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
    public static IBitmap decodeStream(InputStream is)  throws IOException  {
        return getFactory().decodeStream(is);
    }


}
