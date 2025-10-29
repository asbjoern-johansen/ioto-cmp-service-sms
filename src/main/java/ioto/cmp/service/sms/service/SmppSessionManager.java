package ioto.cmp.service.sms.service;

import ioto.cmp.service.sms.listener.IncomingMessageListener;
import jakarta.annotation.PostConstruct;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.SMPPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmppSessionManager {
    private static final Logger log = LoggerFactory.getLogger(SmppSessionManager.class);


    @Value("${ioto.cmp.service.sms.smpp.host}")
    private String host;

    @Value("${ioto.cmp.service.sms.smpp.port}")
    private int port;

    @Value("${ioto.cmp.service.sms.smpp.system-id}")
    private String systemId;

    @Value("${ioto.cmp.service.sms.smpp.password}")
    private String password;

    @Value("${ioto.cmp.service.sms.smpp.system-type}")
    private String systemType;

    @Value("${ioto.cmp.service.sms.smpp.reconnect-delay-ms:5000}")
    private long reconnectDelayMs;

    @Value("${ioto.cmp.service.sms.smpp.source-ton}")
    private String sourceTon;

    @Value("${ioto.cmp.service.sms.smpp.source-npi}")
    private String sourceNpi;


    private final IncomingMessageListener listener;
    private SMPPSession session;

    public SmppSessionManager(IncomingMessageListener listener) {
        this.listener = listener;
    }

    @PostConstruct
    public void init() {
        connect();
    }

    private void connect() {
        new Thread(() -> {
            while (true) {
                try {
                    log.info("Connecting to SMPP server {}:{}", host, port);
                    session = new SMPPSession();
                    session.setMessageReceiverListener(listener);
                    session.setTransactionTimer(20000);
                    session.setEnquireLinkTimer(30000);
                    session.connectAndBind(
                            host,
                            port,
                            BindType.BIND_TRX,
                            systemId,
                            password,
                            systemType,
                            TypeOfNumber.valueOf(sourceTon),
                            NumberingPlanIndicator.valueOf(sourceNpi),
                            null //AddressRange
                    );
                    log.info("SMPP transceiver bound successfully.");

                    // Monitor for connection loss
                    session.addSessionStateListener((newState, oldState, source) -> {
                        log.warn("SMPP session state changed from: {} to {}", newState, oldState);
                        if (!newState.isBound()) {
                            reconnect();
                        }
                    });

                    break;
                } catch (Exception e) {
                    log.error("SMPP connection failed: {}", e.getMessage());
                    try {
                        Thread.sleep(reconnectDelayMs);
                    } catch (InterruptedException ignored) {}
                }
            }
        }, "SMPP-Connection-Thread").start();
    }

    private void reconnect() {
        log.warn("Reconnecting to SMPP after disconnect...");
        close();
        try {
            Thread.sleep(reconnectDelayMs);
        } catch (InterruptedException ignored) {}
        connect();
    }

    public SMPPSession getSession() {
        return session;
    }

    private void close() {
        if (session != null && session.getSessionState().isBound()) {
            try {
                session.unbindAndClose();
            } catch (Exception e) {
                log.error("Error closing session: {}", e.getMessage());
            }
        }
    }
}