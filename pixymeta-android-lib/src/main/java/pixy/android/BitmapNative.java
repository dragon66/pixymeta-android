package pixy.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.io.OutputStream;

import pixy.image.IBitmap;

/**
 * Abstraction layer for android.graphics.Bitmap.
 * This is the only place in the lib that has dependencies to android
 *
 * Created by k3b on 31.05.2016.
 */
public class BitmapNative implements IBitmap {
    private final Bitmap mBitmap;
    public BitmapNative(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    /**
     * Creates a new bitmap, scaled from an existing bitmap, when possible. If the
     * specified width and height are the same as the current width and height of
     * the source bitmap, the source bitmap is returned and no new bitmap is
     * created.
     *
     * @param dstWidth  The new bitmap's desired width.
     * @param dstHeight The new bitmap's desired height.
     * @param filter    true if the source should be filtered.
     * @return The new scaled bitmap or the source bitmap if no scaling is required.
     * @throws IllegalArgumentException if width is <= 0, or height is <= 0
     */
    @Override
    public IBitmap createScaledBitmap(int dstWidth, int dstHeight,
                                      boolean filter) {
        return create(Bitmap.createScaledBitmap(this.mBitmap, dstWidth, dstHeight, filter));
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
        return create(Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888));
    }

    /** Returns the bitmap's width */
    @Override
    public int getWidth() {
        return mBitmap.getWidth();
    }

    /** Returns the bitmap's height */
    @Override
    public int getHeight() {
        return mBitmap.getHeight();
    }

    /**
     * Write a compressed version of the bitmap to the specified outputstream.
     * If this returns true, the bitmap can be reconstructed by passing a
     * corresponding inputstream to BitmapFactory.decodeStream(). Note: not
     * all Formats support all bitmap configs directly, so it is possible that
     * the returned bitmap from BitmapFactory could be in a different bitdepth,
     * and/or may have lost per-pixel alpha (e.g. JPEG only supports opaque
     * pixels).
     *
     * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
     *                 small size, 100 meaning compress for max quality. Some
     *                 formats, like PNG which is lossless, will ignore the
     *                 quality setting
     * @param stream   The outputstream to write the compressed data.
     * @return true if successfully compressed to the specified stream.
     */
    @Override
    public void compressJPG(int quality, OutputStream stream) {
        mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
    }

    /**
     * Returns in pixels[] a copy of the data in the bitmap. Each value is
     * a packed int representing a Color. The stride parameter allows
     * the caller to allow for gaps in the returned pixels array between
     * rows. For normal packed results, just pass width for the stride value.
     * The returned colors are non-premultiplied ARGB values.
     *
     * @param pixels   The array to receive the bitmap's colors
     * @param offset   The first index to write into pixels[]
     * @param stride   The number of entries in pixels[] to skip between
     *                 rows (must be >= bitmap's width). Can be negative.
     * @param x        The x coordinate of the first pixel to read from
     *                 the bitmap
     * @param y        The y coordinate of the first pixel to read from
     *                 the bitmap
     * @param width    The number of pixels to read from each row
     * @param height   The number of rows to read
     *
     * @throws IllegalArgumentException if x, y, width, height exceed the
     *         bounds of the bitmap, or if abs(stride) < width.
     * @throws ArrayIndexOutOfBoundsException if the pixels array is too small
     *         to receive the specified number of pixels.
     */
    @Override
    public int[] getPixels(int[] pixels, int offset, int stride,
                          int x, int y, int width, int height) {
        mBitmap.getPixels(pixels, offset, stride,
                x, y, width, height);
        return pixels;
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
        return create(BitmapFactory.decodeStream(is));
    }

    private static IBitmap create(Bitmap bitmap) {
        if (bitmap != null) return new BitmapNative(bitmap);
        return null;
    }


    /**
     * usage
     *
     * IBitmap somePixyMetaBitmap = ....;
     *
     * android.graphics.Bitmap image = (android.graphics.Bitmap) somePixyMetaBitmap.getImage();
     *
     * @return the os-native image
     */
    public Object getImage() {
        return mBitmap;
    }

}
