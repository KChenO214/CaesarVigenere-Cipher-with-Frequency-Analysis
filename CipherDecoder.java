import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CipherDecoder {
    public DecimalFormat dec = new DecimalFormat("#.##");
    //Vigenere Square, Row is Key and Column is Plaintext
    private final char[][] vigenereSquare = new char[26][26];
    //Letter frequency create by Peter Norvig from google using Google's Data base of over 3 trillion letters
    //Source: https://norvig.com/mayzner.html
    /*private final double[] charFreqInEnglGtoL = {12.49, 9.28, 8.04, 7.64, 7.57, 7.23, 6.51, 6.28, 5.05, 4.07, 3.82,
            3.34, 2.73, 2.51, 2.4, 2.14, 1.87, 1.68, 1.66, 1.48, 1.05, 0.54, 0.23, 0.16, 0.12, 0.09};
    private final double[] charFreqInEnglInOrder = {8.04, 1.48, 3.34, 3.82, 12.49, 2.4, 1.87, 5.05, 7.57, 0.16, 0.54,
    4.07, 2.51, 7.23, 7.64, 2.14, 0.12, 6.28, 6.51, 9.28, 2.73, 1.05, 1.68, 0.23, 1.66, 0.09};*/
    private final String charFreqStr = "|A: 8.04%|B: 1.48%|C: 3.34%|D: 3.82%|E: 12.49%|F: 2.4%|G: 1.87%|H: 5.05%|I: 7.57%|" +
            "J: 0.16%|K: 0.54%|L: 4.07%|M: 2.51%|N: 7.23%|O: 7.64%|P: 2.14%|Q: 0.12%|R: 6.28%|S: 6.51%|T: 9.28%|" +
            "U: 2.73%|V: 1.05%|W: 1.68%|X: 0.23%|Y: 1.66%|Z: 0.09%|";

    private String possibleKey;
    private String decryptText;
    private ArrayList<Integer> coincidence;
    private int keyLength;
    private int shift;

    public CipherDecoder(){
        possibleKey = "";
        decryptText = "";
        keyLength = 0;
        shift = 0;
        coincidence = new ArrayList<>();
        fillSquare();
    }

    //Populates the Vigenere Square
    private void fillSquare(){
        //Fills the first row based on character ASCII value
        for(int i = 0; i < 26; i++){
            vigenereSquare[0][i] = ((char) (i + 65));
        }

        //Fills the other rows based on previous row
        for(int row = 1; row < 26; row++){
            char character = vigenereSquare[row - 1][0];
            for(int col = 0; col < 26; col++){
                if(col == 25)
                    vigenereSquare[row][col] = character;
                else
                    vigenereSquare[row][col] = vigenereSquare[row - 1][col + 1];
            }
        }
    }

    //Returns the coincidence to help estimate key length
    public ArrayList<Integer> getCoincidence(){
        return coincidence;
    }

    public void setKeyLength(int length){
        keyLength = length;
    }

    public void setShift(int numShift){
        shift = numShift;
    }

    //Returns string of character frequency in the English language
    public String getCharFreqStr(){
        return charFreqStr;
    }

    //Prints out the frequency of letters in the files
    public String printFrequency(int[] frequency, int totalCharacter){
        StringBuilder str = new StringBuilder("|");

        for(int i = 0; i < 26; i++){
            //Creates the string that holds all the values of the characters
            str.append((char) (i + 65)).append(": ").append(frequency[i]).append(", ").append(dec.format(
                    (frequency[i] * 100.00 / totalCharacter) * 100.00 / 100.00)).append("%|");
        }

        return str.toString();
    }

    //Calculates the Key length with coincidences
    public void findKeyLengthCoincidence(String text){
        //Holds text that will be edited to compare to original text to find coincidences for key length
        String comparedTo = text;
        /*Amount is used for counting matching letters, index for editing the text
         *Example:
         * Original: A B |B| C D E F |F| G H
         * Edited T:   A |B| B C D E |F| F G
         * .:. Amount will equal 2
         * Index will help with
         */
        int amount = 0, index;
        coincidence.clear();

        for(int i = 0; i < text.length(); i++){
            //Removes the last character to shift comparison to find coincidences
            comparedTo = comparedTo.substring(0, comparedTo.length() - 1);
            index = text.length() - comparedTo.length();

            for(int j = 0; j < comparedTo.length(); j++){
                //Compares the characters to see if they are the same
                char a = text.charAt(index), b = comparedTo.charAt(j);
                if(a == b){
                    amount++;
                }

                index++;
            }

            coincidence.add(amount);
            amount = 0;
        }
    }

    //Help determine possible key and decrypt the text with the key
    public String findKeyAndDecode(String text, Scanner scan){
        /*Array to store the separated text based on key length to find possible letters of the key stores letters in
         *the same space so if length is 3, array[0] will store characters from position 0, 3, 6..., array[1] will
         *store characters from position of 1, 4, 7..., and array[2] will store characters from position of 2, 5, 8...
        */
        String[] keyPart = new String[keyLength];

        //Frequency for the letters in each key part
        int[][] keyPartFreq = new int[keyLength][26];
        //Holds frequency as a string from each part
        String[] str = new String[keyLength];
        possibleKey = "";

        for(int i = 0; i < keyLength; i++){
            keyPart[i] = "";
        }

        //Fills array with characters based on position
        for(int i = 0; i < text.length(); i++){
            int position = i % keyLength;
            keyPart[position] = keyPart[position] + text.charAt(i);
        }

        //Finds the character frequencies of all positions
        for(int i = 0; i < keyLength; i++){
            for(int j = 0; j < keyPart[i].length(); j++){
                keyPartFreq[i][(((int) keyPart[i].charAt(j)) - 65)]++;
            }
        }

        //UI to help user determine key
        for(int i = 0; i < keyLength; i++){
            str[i] = "|";

            for(int j = 0; j < 26; j++) {
                //Creates the string that holds all the values of the characters
                str[i] = str[i] + ((char) (j + 65)) + ": " + dec.format((keyPartFreq[i][j] * 100.00 /
                        keyPart[i].length()) * 100.00 / 100.00) + "%|";
            }

            System.out.println("Character frequency in the English language:");
            System.out.println(charFreqStr);
            System.out.println("Character frequency of part " + (i + 1) +": ");
            System.out.println(str[i]);

            System.out.println("Please try and match letter " + (i + 1) + " frequency with the character frequency in" +
                    " the English language. Hint: Find the largest number assuming it is E and go back 4 spaces and " +
                    "that could be the letter.");
            System.out.println("Possible letter " + (i + 1) + ": ");
            String answer = scan.next();
            possibleKey = possibleKey + answer.toUpperCase();
            System.out.println("Possible Key: " + possibleKey);
        }

        //Decrypts text based on key found
        return decoder(text);
    }

    //Decrypts the text based on key founded by user
    public String decoder(String text){
        decryptText = "";

        //Finds decrypted letter
        for(int i = 0; i < text.length(); i++){
            int col = 0;
            //Gets the individual letter of the key to use for vigenere square
            char row = possibleKey.charAt(i % keyLength);

            //Finding the correct matching letter on vigenere square, the plain text
            for(int j = 0; j < 26; j++){
                char character;
                character = vigenereSquare[row - 65][j];

                if(character == text.charAt(i)){
                    col = j;
                    break;
                }
            }

            //Adds character to decrypted text
            decryptText = decryptText + vigenereSquare[0][col];
        }

        return decryptText;
    }

    //Caesar Cipher Solver
    public String caesar(String text){
        decryptText = "";

        //Finds and stores the correct decrypted character based on shift
        for(int i = 0; i < text.length(); i++){
            int col = 0;

            for(int j = 0; j < 26; j++){
                char character;
                character = vigenereSquare[shift][j];

                //Stop if matching character found
                if(character == text.charAt(i)){
                    col = j;
                    break;
                }
            }

            //Adds character to decrypted text
            decryptText = decryptText + vigenereSquare[0][col];
        }

        return decryptText;
    }

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        boolean quit = false;
        String address;

        CipherDecoder file;

        while(!quit){
            try{
                //Getting one file to decrypt
                System.out.println("File Location (For every \\ add another \\): ");
                address = scan.nextLine();

                //Reads file
                BufferedReader read = new BufferedReader(new FileReader(address));
                file = new CipherDecoder();
                StringBuilder text = new StringBuilder();
                String str;

                int[] frequency = new int[26];
                while((str = read.readLine()) != null){
                    //Making all characters upper case so we can use ascii dec value - 65 to set location on array
                    str = str.toUpperCase();
                    //Goes through the entire string char by char
                    for(int i = 0; i < str.length(); i++){
                        int dec = str.charAt(i);
                        //Checks if ascii of character is A-Z and stores if it is
                        if(dec > 64 && dec < 91){
                            text.append(str.charAt(i));
                            frequency[((int) str.charAt(i)) - 65]++;
                        }
                    }
                }

                //Choosing between solving a Caesar Cipher or Vigenere Cipher
                System.out.println("Would you like to try and solve a Caesar or Vigenere Cipher? C/V");
                if(scan.next().equalsIgnoreCase("C")){
                    //Caesar Cipher
                    System.out.println("Text:\n" + text);
                    System.out.println(file.getCharFreqStr());
                    System.out.println("Frequency:\n" + file.printFrequency(frequency, text.length()));

                    System.out.println("""
                        Find the highest occuring letter and match it as E. Count how far it is shift from the alphabet and that will be the shift.
                        Example:
                         A B C D|E|F G H I J K L M N O P Q R S T U V W X Y Z
                         Y Z A B C D|E|F G H I J K L M N O P Q R S T U V W X
                         --Shift of 2--            
                        Enter most likely key length:""");
                    file.setShift(scan.nextInt());

                    System.out.println(file.caesar(text.toString()));
                }else{
                    //Vigenere Cipher
                    System.out.println("Text:\n" + text);
                    System.out.println("Frequency:\n" + file.printFrequency(frequency, text.length()));

                    file.findKeyLengthCoincidence(text.toString());
                    System.out.println("Coincidence" + file.getCoincidence());

                    System.out.println("""
                        Look for patterns in the Coincidence numbers.
                        Example:
                        Large to Small
                         |50|, 45, 47, 43, |38|, 49 - key of 4
                        Large to Large
                         |52|, 38, 45, 44, |56|, 43 - key of 4
                        Small to Small
                         |37|, 39, 45, 44, |38|, 43 - key of 4
                        Small to Large
                         |36|, 38, 45, 44, |48|, 43 - key of 4""");

                    //Guessing key length, finds possible key, and decrypt
                    boolean end = false;
                    while(!end){
                        try{
                            System.out.println("Enter most likely key length: ");
                            file.setKeyLength(scan.nextInt());

                            System.out.println(file.findKeyAndDecode(text.toString(), scan));
                        }catch (InputMismatchException e){
                            System.out.println("Incorrect input. Please try again.");
                        }

                        end = true;
                    }
                }

                System.out.println("Would you like to try another? Y/N");
                if(scan.next().equalsIgnoreCase("N")){
                    quit = true;
                }
            }catch (InputMismatchException | FileNotFoundException e){
                System.out.println("Invalid address. Please try again. ");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}