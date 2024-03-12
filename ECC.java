import java.math.BigInteger;
import java.util.Scanner;

public class ECC {

    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    static final BigInteger p = new BigInteger("17"); // Prime modulo
    static final BigInteger a = new BigInteger("2");  // Curve parameter
    static final BigInteger b = new BigInteger("2");  // Curve parameter
    static final Point G = new Point(new BigInteger("5"), new BigInteger("1")); // Base point
    static final BigInteger n = new BigInteger("19"); // Order of the base point

    static Point add(Point P, Point Q) {
        if (P == null) return Q;
        if (Q == null) return P;

        if (P.x.equals(Q.x) && P.y.equals(Q.y)) { // Point doubling
            BigInteger slope = (P.x.pow(2).multiply(new BigInteger("3")).add(a))
                                    .multiply(P.y.multiply(new BigInteger("2")).modInverse(p));
            BigInteger x3 = slope.pow(2).subtract(P.x.multiply(new BigInteger("2"))).mod(p);
            BigInteger y3 = slope.multiply(P.x.subtract(x3)).subtract(P.y).mod(p);
            return new Point(x3, y3);
        }

        BigInteger slope = (P.y.subtract(Q.y)).multiply(P.x.subtract(Q.x).modInverse(p));
        BigInteger x3 = slope.pow(2).subtract(P.x).subtract(Q.x).mod(p);
        BigInteger y3 = slope.multiply(P.x.subtract(x3)).subtract(P.y).mod(p);
        return new Point(x3, y3);
    }

    static Point multiply(Point P, BigInteger k) {
        Point R = null;
        for (int i = k.bitLength() - 1; i >= 0; i--) {
            R = add(R, R);
            if (k.testBit(i)) {
                R = add(R, P);
            }
        }
        return R;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Alice's private key: ");
        BigInteger aPrivateKey = scanner.nextBigInteger();

        System.out.print("Enter Bob's private key: ");
        BigInteger bPrivateKey = scanner.nextBigInteger();

        scanner.nextLine(); // Consume newline character
        System.out.print("Enter the message to be encrypted: ");
        String message = scanner.nextLine();

        // Generate public keys
        Point aPublicKey = multiply(G, aPrivateKey);
        Point bPublicKey = multiply(G, bPrivateKey);

        // Encryption
        Point sharedSecretAlice = multiply(bPublicKey, aPrivateKey); // Alice's side
        BigInteger encrypted = new BigInteger(message.getBytes()).xor(sharedSecretAlice.x);

        // Decryption
        Point sharedSecretBob = multiply(aPublicKey, bPrivateKey); // Bob's side
        BigInteger decrypted = encrypted.xor(sharedSecretBob.x);

        System.out.println("Encrypted text: " + encrypted);
        System.out.println("Decrypted text: " + new String(decrypted.toByteArray()));
    }
}
