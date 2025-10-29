package ioto.cmp.service.sms.service;

import org.jsmpp.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MessageConfiguration.class);

    private DataCoding dataCoding       = null;
    private ESMClass   esmClass         = null;
    private byte[][]   messageSegments  = null;

    public MessageConfiguration(String text){

        //Determine Encoding
        if (MessageUtil.is7bitEncodeable(text)) {
            dataCoding      = new GeneralDataCoding(Alphabet.valueOf((byte)0));
        } else {
            dataCoding = new GeneralDataCoding(Alphabet.valueOf((byte)8));
        }

        messageSegments = MessageUtil.segmentize(text);

        if(messageSegments.length > 1){
            //We are supplying message headers to allow the receiver to assemble.
            esmClass = new ESMClass(MessageMode.DEFAULT, MessageType.DEFAULT, GSMSpecificFeature.UDHI);
        }else{
            esmClass = new ESMClass();
        }

        logger.debug(String.format(
                "Message is %d characters (%d bytes) long and will be sent as %d messages",
                text.length(), text.getBytes().length, messageSegments.length));

    }

    public DataCoding getEncoding(){
        return dataCoding;
    }

    public ESMClass getEsmClass() {
        return esmClass;
    }

    public byte[][] getMessageSegments() {
        return messageSegments;
    }
}
