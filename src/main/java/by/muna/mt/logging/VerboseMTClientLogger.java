package by.muna.mt.logging;

import by.muna.mt.MTClient;
import by.muna.tl.TLValue;
import by.muna.util.StringUtil;
import by.muna.yasly.logging.VerboseSocketLogger;

public class VerboseMTClientLogger implements IMTClientLogger {
    @Override
    public void onReceived(MTClient client, long authKeyId, long sessionId, long messageId, int seqNo, TLValue data) {
        String address = VerboseSocketLogger.addressToString(client.getAddress());

        System.out.println("<" + address + " received: " +
            VerboseMTClientLogger.messageSignature(authKeyId, sessionId, messageId, seqNo) + ' ' +
            data.toString());
    }

    @Override
    public void onSend(MTClient client, long authKeyId, long sessionId, long messageId, int seqNo, TLValue data) {
        String address = VerboseSocketLogger.addressToString(client.getAddress());

        System.out.println(">" + address + " send: " +
            VerboseMTClientLogger.messageSignature(authKeyId, sessionId, messageId, seqNo) + ' ' +
            data.toString());
    }

    public static String messageSignature(long authKeyId, long sessionId, long messageId, int seqNo) {
        return StringUtil.toHex(authKeyId) + ":" + StringUtil.toHex(sessionId) +
            ' ' + StringUtil.toHex(messageId) + '/' + StringUtil.toHex(seqNo);
    }

    @Override
    public void undefinedBehavior(MTClient client, String message, Object... data) {
        String address = VerboseSocketLogger.addressToString(client.getAddress());

        System.err.print("!" + address + " " + message);

        if (data.length > 0) {
            System.err.print(":");

            for (int i = 0; i < data.length; i++) {
                System.err.print(' ');
                System.err.println(data[i]);
            }
        }

        System.err.println();
    }
}
