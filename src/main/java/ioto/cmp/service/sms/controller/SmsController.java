package ioto.cmp.service.sms.controller;

import ioto.cmp.service.sms.service.SmppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmppService smppService;

    public SmsController(SmppService smppService) {
        this.smppService = smppService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(
            @RequestParam String to,
            @RequestParam String text) {

        List<String> messageIds = smppService.sendSms(to, text);
        return ResponseEntity.ok(String.join(",", messageIds));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}