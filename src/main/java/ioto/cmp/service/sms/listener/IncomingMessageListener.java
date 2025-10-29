package ioto.cmp.service.sms.listener;

import org.jsmpp.bean.*;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IncomingMessageListener implements MessageReceiverListener {

    private static final Logger log = LoggerFactory.getLogger(IncomingMessageListener.class);

    @Override
    public void onAcceptDeliverSm(DeliverSm deliverSm){
        String msg = new String(deliverSm.getShortMessage());
        if (deliverSm.isSmscDeliveryReceipt()) {
            DeliveryReceipt receipt;
            try {
                receipt = deliverSm.getShortMessageAsDeliveryReceipt();
                log.info("Delivery receipt: ID={}, status={}, delivered={}/{}",
                        receipt.getId(), receipt.getFinalStatus(),
                        receipt.getDelivered(), receipt.getSubmitted());
            } catch (InvalidDeliveryReceiptException e) {
                log.warn("Invalid delivery receipt: {}", e.getMessage());
            }
        } else {
            log.info("Incoming message from {}: {}", deliverSm.getSourceAddr(), msg);
        }
    }

    @Override
    public void onAcceptAlertNotification(AlertNotification alertNotification) {
        log.info("Alert received: {}", alertNotification);
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session session) {
        log.info("DataSM received: {}", dataSm);
        return null;
        //TODO: What to do here=
    }

    @Override
    public void onAcceptEnquireLink(EnquireLink enquireLink, Session source) {
        MessageReceiverListener.super.onAcceptEnquireLink(enquireLink, source);
    }
}