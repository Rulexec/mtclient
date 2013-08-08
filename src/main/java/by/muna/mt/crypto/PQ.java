package by.muna.mt.crypto;

import by.muna.mt.MTClient;

import java.math.BigInteger;

public class PQ {
    public byte[] pq;
    public byte[] p, q;

    public PQ(byte[] numberBE) {
        this.pq = numberBE;

        BigInteger pq = new BigInteger(1, this.pq);
        BigInteger p = PQ.rho(pq);
        BigInteger q = pq.divide(p);

        this.p = p.toByteArray();
        this.q = q.toByteArray();

        if (p.compareTo(q) >= 0) {
            byte[] swap = this.p;
            this.p = this.q;
            this.q = swap;
        }
    }

    private static BigInteger getRand(BigInteger from, BigInteger to) {
        // TODO: HOLY COW!

        BigInteger x;

        do {
            x = new BigInteger(to.bitLength(), MTClient.getRandom());
        } while (x.compareTo(from) <= -1 || x.compareTo(to) >= 0);

        return x;
    }

    private static BigInteger rho(BigInteger n)
    {
        BigInteger x = PQ.getRand(BigInteger.ONE, n.subtract(new BigInteger("2")));
        BigInteger y = BigInteger.ONE;
        BigInteger i = BigInteger.ZERO;
        BigInteger stage = new BigInteger("2");

        while (n.gcd(x.subtract(y).abs()).equals(BigInteger.ONE)) {
            if (i.equals(stage)) {
                y = x;
                stage = stage.multiply(new BigInteger("2"));
            }

            x = x.pow(2).add(BigInteger.ONE).mod(n);
            i = i.add(BigInteger.ONE);
        }

        return n.gcd(x.subtract(y).abs());
    }
}
