package by.muna.mt.logging;

import by.muna.mt.MTClient;
import by.muna.tl.TLValue;

public interface IMTClientLogger {
    void onReceived(MTClient client, long authKeyId, long sessionId, long messageId, int seqNo, TLValue data);
    void onSend(MTClient client, long authKeyId, long sessionId, long messageId, int seqNo, TLValue data);

    void undefinedBehavior(MTClient client, String message, Object... data);
}
