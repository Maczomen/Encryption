package projet.java.bsk.encryption.Message;

import lombok.*;
import lombok.experimental.SuperBuilder;
import projet.java.bsk.encryption.Encoding.AES;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Message<T> implements Serializable {
    protected String owner;
    protected T content;
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected String numberOfPartS;
    protected transient int numberOfPart;
    public abstract void encrypt(AES aes) throws Exception;
    public abstract void decrypt(AES aes) throws Exception;
    public abstract void merge(ArrayList<Message<T>> messages, AES aes);
    public abstract void show();
}
