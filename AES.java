import java.util.Arrays;

public class AES {

    public static void main(String[] args) {
        String K = "0100101011110101";
        String W0 = "01001010";
        String W1 = "11110101";

        System.out.println("Original Key (K): " + K);
        System.out.println("Round Key : " + W0 + " " + W1);

        // Generate round keys for AES
        String[] roundKeys = generateRoundKeys(K, W0, W1);

        // Print the generated round keys
        for (int i = 0; i < roundKeys.length; i = i + 2) {
            System.out.println("Round Key " + ": " + roundKeys[i] + " " + roundKeys[i + 1]);
        }

        // Example plaintext in binary format (16 bits)
        String plaintext = "1101011101011101";
        System.out.println("Original Plain Text: " + plaintext);

        // Encrypt plaintext
        String ciphertext = encrypt(plaintext, roundKeys);
        System.out.println("Cipher Text: " + ciphertext);

        // Decrypt ciphertext
        String decryptedText = decrypt(ciphertext, roundKeys);
        System.out.println("Decrypted Text: " + decryptedText);
    }

    public static String[] generateRoundKeys(String K, String W0, String W1) {
        String[] roundKeys = new String[4];

        // Round Key 2 (W2)
        roundKeys[0] = xorBinaryStrings(W0, keySchedule(W1));

        // Round Key 3 (W3)
        roundKeys[1] = xorBinaryStrings(W1, keySchedule(roundKeys[0]));

        // Round Key 4 (W4)
        roundKeys[2] = xorBinaryStrings(roundKeys[0], keySchedule(roundKeys[1]));

        // Round Key 5 (W5)
        roundKeys[3] = xorBinaryStrings(roundKeys[1], keySchedule(roundKeys[2]));

        return roundKeys;
    }

    public static String keySchedule(String input) {
        // Rotate input to the left by 1 bit
        return input.substring(1) + input.charAt(0);
    }

    public static String xorBinaryStrings(String a, String b) {
        int maxLength = Math.max(a.length(), b.length());
        StringBuilder paddedA = new StringBuilder(a);
        StringBuilder paddedB = new StringBuilder(b);

        // Pad the shorter string with zeros at the beginning
        while (paddedA.length() < maxLength) {
            paddedA.insert(0, "0");
        }
        while (paddedB.length() < maxLength) {
            paddedB.insert(0, "0");
        }

        // Perform XOR operation
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            result.append((paddedA.charAt(i) ^ paddedB.charAt(i)));
        }
        return result.toString();
    }

    public static String encrypt(String plaintext, String[] roundKeys) {
        String state = plaintext;
        for (int round = 0; round < 3; round++) {
            state = addRoundKey(state, roundKeys[round]);
            state = mixColumns(state);
        }
        state = addRoundKey(state, roundKeys[3]); // Final round
        return state;
    }

    public static String decrypt(String ciphertext, String[] roundKeys) {
        String state = ciphertext;
        state = addRoundKey(state, roundKeys[3]); // Initial round
        for (int round = 2; round >= 0; round--) {
            state = inverseShiftRows(state);
            state = inverseSubstituteBytes(state);
            state = addRoundKey(state, roundKeys[round]);
            if (round > 0) {
                state = inverseMixColumns(state);
            }
        }
        return state;
    }

    public static String addRoundKey(String state, String roundKey) {
        return xorBinaryStrings(state, roundKey);
    }

    public static String substituteBytes(String state) {
        String[] sBox = {
                "1001", "0100", "1010", "1011",
                "1101", "0001", "1000", "0101",
                "0110", "0010", "0000", "0011",
                "1100", "1110", "1111", "0111"
        };
        String[] newState = new String[16];
        for (int i = 0; i < 16; i++) {
            String row = state.substring(i * 4, (i * 4) + 2);
            String col = state.substring((i * 4) + 2, (i * 4) + 4);
            int rowIndex = Integer.parseInt(row, 2);
            int colIndex = Integer.parseInt(col, 2);
            newState[i] = sBox[(rowIndex * 4) + colIndex];
        }
        return String.join("", newState);
    }

    public static String shiftRows(String state) {
        String[] newState = new String[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                newState[(i * 4) + j] = state.substring((j * 4) + i, (j * 4) + i + 1);
            }
        }
        return String.join("", newState);
    }

    public static String mixColumns(String state) {
        int[][] matrix = {
                {2, 3, 1, 1},
                {1, 2, 3, 1},
                {1, 1, 2, 3},
                {3, 1, 1, 2}
        };
        String[] newState = new String[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum ^= (Integer.parseInt(state.substring((i * 4) + k, (i * 4) + k + 1), 2) * matrix[j][k]);
                }
                newState[(i * 4) + j] = Integer.toBinaryString(sum);
            }
        }
        return String.join("", newState);
    }

    public static String inverseSubstituteBytes(String state) {
        String[] inverseSBox = {
                "1010", "1101", "0001", "1000",
                "0100", "0110", "1001", "0000",
                "0010", "0011", "1100", "1111",
                "0111", "0101", "1011", "1110"
        };
        String[] newState = new String[state.length() / 4]; // Adjusted for state length
        for (int i = 0; i < newState.length; i++) {
            String nibble = state.substring(i * 4, (i * 4) + 4);
            int index = Arrays.asList(inverseSBox).indexOf(nibble);
            newState[i] = Integer.toBinaryString(index);
        }
        return String.join("", newState);
    }

    public static String inverseShiftRows(String state) {
        String[] newState = new String[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                newState[(i * 4) + j] = state.substring((j * 4) + i, (j * 4) + i + 1);
            }
        }
        return String.join("", newState);
    }

   public static String inverseMixColumns(String state) {
        int[][] matrix = {
            {14, 11, 13, 9},
            {9, 14, 11, 13},
            {13, 9, 14, 11},
            {11, 13, 9, 14}
        };
        String[] newState = new String[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int sum = 0;
                for (int k = 0; k < 4; k++) {
                    int index = (i * 4) + k;
                    if (index >= state.length()) {
                        break; // Ensure index doesn't exceed state length
                    }
                    sum ^= (Integer.parseInt(state.substring(index, index + 1), 2) * matrix[j][k]);
                }
                newState[(i * 4) + j] = Integer.toBinaryString(sum);
            }
        }
        return String.join("", newState);
    }

}
