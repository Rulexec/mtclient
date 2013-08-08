package by.muna.mt;

import by.muna.mt.messages.IMTMessageStatusListener;
import by.muna.tl.ITypedData;

class MTProtoMessage {
    private ITypedData data;
    private boolean meaningful;
    private IMTMessageStatusListener statusListener;
    private long messageId = 0;

    public MTProtoMessage(ITypedData data, boolean meaningful, IMTMessageStatusListener statusListener) {
        this.data = data;
        this.meaningful = meaningful;
        this.statusListener = statusListener;
    }

    public ITypedData getData() {
        return this.data;
    }

    boolean isMeaningful() {
        return this.meaningful;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageId() {
        return this.messageId;
    }

    public IMTMessageStatusListener getStatusListener() {
        return this.statusListener;
    }
}
