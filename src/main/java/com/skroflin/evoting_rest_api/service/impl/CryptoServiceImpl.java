package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.exceptions.CriticalSignatureErrorException;
import com.skroflin.evoting_rest_api.exceptions.VerifySignatureException;
import com.skroflin.evoting_rest_api.models.Vote;
import com.skroflin.evoting_rest_api.service.CryptoService;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Service
public class CryptoServiceImpl implements CryptoService {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519", "BC");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    @Override
    public String generateSignature(Vote vote) {
        try {
            Signature signature = Signature.getInstance("Ed25519", "BC");
            signature.initSign(privateKey);

            String dataToSign = String.format(
                    "%s:%s:%s",
                    vote.getElection().getElectionUUID(),
                    vote.getCandidate().getCandidateUUID(),
                    vote.getCastAt().format(DATE_FORMATTER)
            );

            signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(signature.sign());
        } catch (Exception e) {
            throw new CriticalSignatureErrorException("Critical error upon signing signature");
        }
    }

    @Override
    public boolean verifySignature(Vote vote, String sigHex) {
        if (sigHex == null || vote == null) return false;

        try {
            Signature signature = Signature.getInstance("Ed25519", "BC");
            signature.initVerify(publicKey);

            String dataToSign = String.format(
                    "%s:%s:%s",
                    vote.getElection().getElectionUUID(),
                    vote.getCandidate().getCandidateUUID(),
                    vote.getCastAt()
            );

            signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = HexFormat.of().parseHex(sigHex);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new VerifySignatureException("Error upon verifying signature");
        }
    }
}
