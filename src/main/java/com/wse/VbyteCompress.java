package com.wse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;


/**
 * Created by chaoqunhuang on 10/15/17.
 */
public class VbyteCompress {

    public static byte[] encode(int value) {
        List<Byte> res = new ArrayList<>();
        while(value > 127) {
            res.add((byte)(value & 127));
            value>>>=7;
        }
        res.add((byte)(value | 0x80));
        Byte[] bytes = res.toArray(new Byte[res.size()]);
        return ArrayUtils.toPrimitive(bytes);
    }

    public static int[] decode(byte[] in, int count) {
        int[] res = new int[count];
        int i = 0;
        for (int j = 0; j < count; j++) {
            int out = 0;
            int shift = 0;
            byte readByte = in[i++];

            while ((readByte & 0x80) == 0) {
                if (shift >= 50) { // We read more bytes than required to load the max long
                    throw new IllegalArgumentException();
                }
                out |= (readByte & 127) << shift;
                readByte = in[i++];

                shift += 7;
            }
            out |= (readByte & 127) << shift;
            res[j] = out;
        }
        return res;
    }
}
