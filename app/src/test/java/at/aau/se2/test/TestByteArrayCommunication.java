package at.aau.se2.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TestByteArrayCommunication {

    static byte id;
    static byte[][] someStone;
    static int coordx;
    static int coordy;
    static ByteArrayHelper helper;
    static byte[] calcByte;

    @Before
    public void initialise() {
        id = 2;
        someStone = new byte[][]{{id, id, id, id}, {id, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        coordx = 4;
        coordy = 9;
        helper = new ByteArrayHelper();
    }


    @Test
    public void testCreateOneDimensionMessageByteArray() {
        byte[] bt = new byte[]{id, id, id, id, 0, id, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, id, (byte) coordx, (byte) coordy};
        calcByte = helper.createNewByteArray(someStone, coordx, coordy, id);
        Assert.assertTrue(Arrays.equals(bt, calcByte));
    }

    /*@Test
    public void testFetchAdditionalInformationFromMessage() {
        helper.fetchInformationFromByteArray(calcByte);
        int c = helper.getColor();
        int y = helper.getIdx();
        int x = helper.getIdy();
        Assert.assertEquals((c == id) && (x == coordx) && (y == coordy), true);
    }*/

    @Test
    public void testFetchByteArrayFromMessage() {
        helper.fetchInformationFromByteArray(calcByte);
        byte[][] arr = helper.getByteStone();
        Assert.assertTrue(Arrays.deepEquals(arr, someStone));
    }

    @Test
    public void testConversionAndReconversion() {
        byte[] temp = helper.createNewByteArray(someStone, coordx, coordy, id);
        helper.fetchInformationFromByteArray(temp);
        byte[][] original = helper.getByteStone();
        Assert.assertTrue(Arrays.deepEquals(original, someStone));

    }

}
