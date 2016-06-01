package pixy.j2se;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import pixy.image.IBitmap;
import pixy.meta.adobe.ImageResourceID;
import pixy.util.MetadataUtils;

/**
 * Created by k3b on 31.05.2016.
 */
public class BitmapNative  implements IBitmap {
    private final BufferedImage mBitmap;
    public BitmapNative(BufferedImage bitmap) {
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
        BufferedImage thumbnail = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnail.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(this.mBitmap, 0, 0, dstWidth, dstHeight, null);

        return create(thumbnail);
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
    public static IBitmap createBitmap(int colors[], int width, int height, int totalSize, byte[] thumbnailData, int paddedRowBytes, ImageResourceID id) {
        DataBuffer db = new DataBufferByte(thumbnailData, totalSize);
        int[] off = {0, 1, 2};//RGB band offset, we have 3 bands
        if(id == ImageResourceID.THUMBNAIL_RESOURCE_PS4)
            off = new int[]{2, 1, 0}; // RGB band offset for BGR for photoshop4.0 BGR format
        int numOfBands = 3;
        int trans = Transparency.OPAQUE;

        WritableRaster raster = Raster.createInterleavedRaster(db, width, height, paddedRowBytes, numOfBands, off, null);
        ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), false, false, trans, DataBuffer.TYPE_BYTE);

        return create(new BufferedImage(cm, raster, false, null));
    }

    private static IBitmap create(BufferedImage bitmap) {
        if (bitmap != null) return new BitmapNative(bitmap);
        return null;
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
    public void compressJPG(int quality, OutputStream stream) throws IOException {
        saveAsJPEG(this.mBitmap, stream, quality);
    }

    public static void saveAsJPEG(BufferedImage image, OutputStream os, int quality) throws IOException {
        if ((quality < 0) || (quality > 100)) {
            throw new IllegalArgumentException("Quality out of bounds!");
        }
        float writeQuality = quality / 100f;
        ImageWriter jpgWriter = null;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
        if (iter.hasNext()) {
            jpgWriter = (ImageWriter) iter.next();
        }
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(writeQuality);
        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        jpgWriter.setOutput(ios);
        IIOImage outputImage = new IIOImage(image, null, null);
        jpgWriter.write(null, outputImage, jpgWriteParam);
        ios.flush();
        jpgWriter.dispose();
        ios.close();
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
        pixels = mBitmap.getRGB(0, 0, width, height, null, 0, width);

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
    public static IBitmap decodeStream(InputStream is) throws IOException {
        return create(javax.imageio.ImageIO.read(is));
    }
}
