package ioto.cmp.service.sms.service;

import java.nio.charset.Charset;
import java.util.Random;

public class MessageUtil {

    public static final Charset DEFAULT_CHARSET  = new Gsm7BitCharset("GSM0338", null);
    public static final Charset UCS2_CHARSET     = Charset.forName("UTF-16BE");

    //Determine how to split the message
    public static final int MAX_MULTIPART_MSG_SEGMENT_SIZE_UCS2 = 134;
    public static final int MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2    = 140;
    public static final int MAX_MULTIPART_MSG_SEGMENT_SIZE_7BIT = 153;
    public static final int MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT    = 160;

    //Message Headers
    private static final byte UDHIE_HEADER_LENGTH  = 0x05;
    private static final byte UDHIE_IDENTIFIER_SAR = 0x00;
    private static final byte UDHIE_SAR_LENGTH     = 0x03;

    public static byte[][] segmentize(String message) {


        Charset charset;
        byte[]  messageByteArray;
        int     segmentSize;

        if(is7bitEncodeable(message)){
            charset          = DEFAULT_CHARSET;
            messageByteArray = message.getBytes(charset);

            if(messageByteArray.length > MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT){
                segmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_7BIT;
            }else{
                return new byte[][] { messageByteArray};
            }

        }else{
            charset          = UCS2_CHARSET;
            messageByteArray = message.getBytes(charset);

            if(messageByteArray.length > MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2){
                segmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_UCS2;
            }else{
                return new byte[][] { messageByteArray};
            }
        }


        // determine how many messages have to be sent
        int numberOfSegments = messageByteArray.length / segmentSize;
        int messageLength    = messageByteArray.length;
        if (numberOfSegments > 255) {
            numberOfSegments = 255;
            messageLength = numberOfSegments * segmentSize;
        }
        if ((messageLength % segmentSize) > 0) {
            numberOfSegments++;
        }

        // prepare array for all of the msg segments
        byte[][] segments = new byte[numberOfSegments][];

        int lengthOfData;

        // generate new reference number
        byte[] referenceNumber = new byte[1];
        new Random().nextBytes(referenceNumber);

        // split the message adding required headers
        for (int i = 0; i < numberOfSegments; i++) {
            if (numberOfSegments - i == 1) {
                lengthOfData = messageLength - i * segmentSize;
            } else {
                lengthOfData = segmentSize;
            }

            // new array to store the header
            segments[i] = new byte[6 + lengthOfData];

            // UDH header
            // doesn't include itself, its header length
            segments[i][0] = UDHIE_HEADER_LENGTH;
            // SAR identifier
            segments[i][1] = UDHIE_IDENTIFIER_SAR;
            // SAR length
            segments[i][2] = UDHIE_SAR_LENGTH;
            // reference number (same for all messages)
            segments[i][3] = referenceNumber[0];
            // total number of segments
            segments[i][4] = (byte) numberOfSegments;
            // segment number
            segments[i][5] = (byte) (i + 1);

            // copy the data into the array
            System.arraycopy(messageByteArray, (i * segmentSize), segments[i], 6, lengthOfData);

        }
        return segments;
    }

    public static boolean is7bitEncodeable(String message) {

        for (int i = 0; i < message.length(); i++){
            String character = "" + message.charAt(i);

            if(Gsm7BitCharset.getDefaultEncodeMap().containsKey(character)){
                //This is fine
            }else{
                return false;
            }
        }
        return true;
    }

}
