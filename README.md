# CaesarVigenere-Cipher-with-Frequency-Analysis

Program to solve a simple shift Caesar cipher and the standard Vigenère cipher both based on the use of frequency analysis. Program solves Vigenère cipher without knowing the key or the key length with the users help/interaction. Using the index of coincidence method to find key length. Then using frequency analysis to determine the key and using the Vigenère Square to decrypt the text.
***Important: The longer the text, the more characters, the longer it will take for the program to generate a list of Coincidences as well with decrypting the text.***
Instructions:
  1.	Enter directory of text file
    a.	Make sure for every \ you have another
    b.	Ex. D:\ Folder\text.txt  D:\\Folder\\text.txt
  2.	Select if you want to try a Caesar cipher or a Vigenère cipher (c or v)
    a.	If Caesar
      i.	Program will print out character frequency of the English language and character frequency of the text 
        1.	Source: 
        ii.	Find the highest occurring letter and assuming it is E since it is the most common letter and count back from the highest text file character frequency to E so if the highest occurring letter is H assuming it is a shifted E, and assuming A is 0 and Z is 25, H is 8 and E is 5, the text will have a shift of 8 – 5 = 3. Also assuming shift from plain text to encrypted text is to the right and decrypting is to the left 
    b.	If Vigenère
      i.	Program prints out text file, its character frequencies,  and prints out the index of coincidences
      ii.	User will try and find repeating pattern within the array of coincidences and that will be the key length
      iii.	Look for patterns in the Coincidence numbers.
      Example:
      Large to Small
       |50|, 45, 47, 43, |38|, 49 - key of 4
      Large to Large
       |52|, 38, 45, 44, |56|, 43 - key of 4
      Small to Small
       |37|, 39, 45, 44, |38|, 43 - key of 4
      Small to Large
       |36|, 38, 45, 44, |48|, 43 - key of 4
      iv.	Enter the key length you have found
      v.	Program will split and store the separated text based on key length to find possible letters of the key stores letters in the same space so if length is 3, array[0] will store characters from position 0, 3, 6..., array[1] will store characters from position of 1, 4, 7..., and array[2] will store characters from position of 2, 5, 8...
        1.	Each position represents the letter in the corresponding key
        2.	Example
        a.	Key length of 3
        b.	Array size of 3
                  A[0]	A[1]	A[2]
        c.	Key:	___	___	___
      vi.	For each key one by one program will print out the character frequency of that position and the character frequency of the English language which with each position you will have to find the shifts for each same as the Caesar cipher
      vii.	Program will decrypt the text based on the key found and print it out
  3.	If you want to try another file or retry current file (yes or no, If yes go back to step 1)
