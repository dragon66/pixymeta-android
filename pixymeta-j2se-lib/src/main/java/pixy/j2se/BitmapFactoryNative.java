package pixy.j2se;

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

import pixy.image.BitmapFactory;
import pixy.image.IBitmap;
import pixy.meta.adobe.ImageResourceID;
import pixy.util.ArrayUtils;

/**
 * Created by k3b on 31.05.2016.
 */
public class BitmapFactoryNative implements BitmapFactory.IBitmapFactory {
        static {
            BitmapFactory.register(new BitmapFactoryNative());
        }

        @Override
        public IBitmap createBitmap(int colors[], int width, int height, int totalSize, byte[] data, int paddedRowBytes, ImageResourceID id) {

            if (paddedRowBytes != -1) {
                return BitmapNative.createBitmap(colors, width, height, totalSize, data, paddedRowBytes, id);
            }

            //Create a BufferedImage
            int size = 3*width*height;
            DataBuffer db = new DataBufferByte(ArrayUtils.subArray(data, totalSize, size), size);
            int[] off = {0, 1, 2};//RGB band offset, we have 3 bands
            int numOfBands = 3;
            WritableRaster raster = Raster.createInterleavedRaster(db, width, height, 3*width, numOfBands, off, null);
            ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            return new BitmapNative(new BufferedImage(cm, raster, false, null));


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
    public IBitmap decodeStream(InputStream is) throws IOException {
        return BitmapNative.decodeStream(is);
    }

}
