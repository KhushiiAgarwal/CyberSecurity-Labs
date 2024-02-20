import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
 
public class RSAtext{
    private static HashSet<Integer> prime = new HashSet<>();
    private static Integer public_key = null;
    private static Integer private_key = null;
    private static Integer n = null;
    private static Random random = new Random();
 
    public static void main(String[] args)
    {
        primeFiller();
        setKeys();
        String message = "Hello World!";
        // System.out.println("Enter the message:");
        // message = new Scanner(System.in).nextLine();
 
        List<Integer> coded = encoder(message);
 
        System.out.print("Plain Text:");
        System.out.println(message);
        System.out.print ("Encryption: ");
        System.out.println(String.join("", coded.stream().map(Object::toString).toArray(String[] ::new)));
        System.out.print("Decrypted Text:");
        System.out.print(decoder(coded));
    }
 
    public static void primeFiller()
    {
        boolean[] sieve = new boolean[250];
        for (int i = 0; i < 250; i++) {
            sieve[i] = true;
        }
 
        sieve[0] = false;
        sieve[1] = false;
 
        for (int i = 2; i < 250; i++) {
            for (int j = i * 2; j < 250; j += i) {
                sieve[j] = false;
            }
        }
 
        for (int i = 0; i < sieve.length; i++) {
            if (sieve[i]) {
                prime.add(i);
            }
        }
    }
 
    public static int pickRandomPrime()
    {
        int k = random.nextInt(prime.size());
        List<Integer> primeList = new ArrayList<>(prime);
        int ret = primeList.get(k);
        prime.remove(ret);
        return ret;
    }
 
    public static void setKeys()
    {
        int prime1 = pickRandomPrime();
        int prime2 = pickRandomPrime();
 
        n = prime1 * prime2;
        int fi = (prime1 - 1) * (prime2 - 1);
 
        int e = 2;
        while (true) {
            if (gcd(e, fi) == 1) {
                break;
            }
            e += 1;
        }
 
        public_key = e;
 
        int d = 2;
        while (true) {
            if ((d * e) % fi == 1) {
                break;
            }
            d += 1;
        }
 
        private_key = d;
    }
 
    public static int encrypt(int message)
    {
        int e = public_key;
        int encrypted_text = 1;
        while (e > 0) {
            encrypted_text *= message;
            encrypted_text %= n;
            e -= 1;
        }
        return encrypted_text;
    }
 
    public static int decrypt(int encrypted_text)
    {
        int d = private_key;
        int decrypted = 1;
        while (d > 0) {
            decrypted *= encrypted_text;
            decrypted %= n;
            d -= 1;
        }
        return decrypted;
    }
 
    public static int gcd(int a, int b)
    {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
 
    public static List<Integer> encoder(String message)
    {
        List<Integer> encoded = new ArrayList<>();
        for (char letter : message.toCharArray()) {
            encoded.add(encrypt((int)letter));
        }
        return encoded;
    }
 
    public static String decoder(List<Integer> encoded)
    {
        StringBuilder s = new StringBuilder();
        for (int num : encoded) {
            s.append((char)decrypt(num));
        }
        return s.toString();
    }
}
