package by.muna.mt.messages;

public interface MTMessageProvider {
    void build();

    long getMessageId();
    int getSeqNo();
}
