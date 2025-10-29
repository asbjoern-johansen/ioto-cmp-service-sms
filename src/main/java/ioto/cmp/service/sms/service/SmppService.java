package ioto.cmp.service.sms.service;

import org.jsmpp.bean.*;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmppService {
    private static final Logger logger = LoggerFactory.getLogger(SmppService.class);
    private final SmppSessionManager sessionManager;

    @Value("${ioto.cmp.service.sms.smpp.service-type}")
    private String serviceType;

    @Value("${ioto.cmp.service.sms.smpp.source-number}")
    private String sourceNumber;

    @Value("${ioto.cmp.service.sms.smpp.source-ton}")
    private String sourceTon;

    @Value("${ioto.cmp.service.sms.smpp.source-npi}")
    private String sourceNpi;

    @Value("${ioto.cmp.service.sms.smpp.dest-ton}")
    private String destTon;

    @Value("${ioto.cmp.service.sms.smpp.dest-npi}")
    private String destNpi;

    public SmppService(SmppSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public List<String> sendSms(String destination, String text) {
        SMPPSession session = sessionManager.getSession();
        if (session == null || !session.getSessionState().isBound()) {
            logger.error("SMPP session is not connected.");
            return List.of("ERROR: SMPP session not connected.");
        }

        try {
            List<String>         messageIds = new ArrayList<>();
            MessageConfiguration message    = new MessageConfiguration(text);

            for(int i = 0; i < message.getMessageSegments().length; i++){
                SubmitSmResult submitSmResult = session.submitShortMessage(
                        serviceType,
                        TypeOfNumber.valueOf(sourceTon),
                        NumberingPlanIndicator.valueOf(sourceNpi),
                        sourceNumber,
                        TypeOfNumber.valueOf(destTon),
                        NumberingPlanIndicator.valueOf(destNpi),
                        destination,
                        new ESMClass(),
                        (byte) 0, //ProtocolId
                        (byte) 1, //PriorityFlag
                        null, //ScheduledDeliverTime
                        null, //ValidityPeriod
                        new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                        (byte) 0, //ReplaceIfPresent
                        message.getEncoding(), //new GeneralDataCoding(Alphabet.ALPHA_DEFAULT),
                        (byte) 0, //DefaultMessageId
                        message.getMessageSegments()[i]);

                messageIds.add(submitSmResult.getMessageId());

                logger.info("Message id for segment {} out of total segments {} = {}", i+1, message.getMessageSegments().length, submitSmResult.getMessageId());
            }

            logger.info("Message sent from {} to {}", sourceNumber, destination);

            return messageIds;
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage());
            return List.of("ERROR: " + e.getMessage());
        }
    }
}