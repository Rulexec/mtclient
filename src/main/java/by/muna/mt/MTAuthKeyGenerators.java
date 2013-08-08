package by.muna.mt;

import by.muna.mt.tl.*;
import by.muna.tl.ITypedData;
import by.muna.util.BytesUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MTAuthKeyGenerators {
    private List<MTAuthKeyGenerator> generators = new LinkedList<MTAuthKeyGenerator>();

    public void add(MTAuthKeyGenerator generator) {
        this.generators.add(generator);
    }

    public void remove(MTAuthKeyGenerator generator) {
        Iterator<MTAuthKeyGenerator> it = this.generators.iterator();

        while (it.hasNext()) {
            if (it.next() == generator) {
                it.remove();
                return;
            }
        }
    }

    private MTAuthKeyGenerator get(byte[] nonce) {
        for (MTAuthKeyGenerator generator : this.generators) {
            if (BytesUtil.equals(generator.getNonce(), nonce)) {
                return generator;
            }
        }

        return null;
    }

    public boolean onData(ITypedData data) {
        byte[] nonce = null;

        switch (data.getId()) {
        case MTResPQ.CONSTRUCTOR_ID: nonce = data.getTypedData(MTResPQ.nonce); break;
        case MTServerDHParamsOk.CONSTRUCTOR_ID: nonce = data.getTypedData(MTServerDHParamsOk.nonce); break;
        case MTServerDHParamsFail.CONSTRUCTOR_ID: nonce = data.getTypedData(MTServerDHParamsFail.nonce); break;
        case MTDhGenOk.CONSTRUCTOR_ID: nonce = data.getTypedData(MTDhGenOk.nonce); break;
        case MTDhGenRetry.CONSTRUCTOR_ID: nonce = data.getTypedData(MTDhGenRetry.nonce); break;
        case MTDhGenFail.CONSTRUCTOR_ID: nonce = data.getTypedData(MTDhGenFail.nonce); break;
        }

        if (nonce == null) return false;

        MTAuthKeyGenerator generator = this.get(nonce);

        if (generator != null) {
            generator.onData(data);

            return true;
        } else {
            return false;
        }
    }
}
