package projet.java.bsk.encryption.Message;


import lombok.*;
import lombok.experimental.SuperBuilder;
import projet.java.bsk.encryption.Encoding.RSA;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class HeaderMessage implements Serializable {
    public String encryptionType;
    public String sesionKey;
    @Builder.Default
    public String initVector = "";
    @Builder.Default
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public String numberOfPartsS = "1";
    @Builder.Default
    public transient int numberOfParts = 1;


    public void encrypt(RSA encryption, String publicKeyPartner) throws Exception{
        encryptionType = encryption.encrypt(encryptionType, publicKeyPartner);
        sesionKey = encryption.encrypt(sesionKey, publicKeyPartner);
        numberOfPartsS = encryption.encrypt(String.valueOf(numberOfParts), publicKeyPartner);
        initVector = encryption.encrypt(initVector, publicKeyPartner);
    }

    public void decrypt(RSA encryption) throws Exception{
        encryptionType = encryption.decrypt(encryptionType);
        sesionKey = encryption.decrypt(sesionKey);
        numberOfParts = Integer.valueOf(encryption.decrypt(numberOfPartsS));
        initVector = encryption.decrypt(initVector);
    }
}
