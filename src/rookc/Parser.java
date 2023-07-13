/*
MIT License

Copyright (c) 2023 rooklang-dev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package rookc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import rookc.parser.*;

/**
 * Class Parser.
 * Implements the parsing methods for Rookc Java parser.
 * @author Srcydev
 * @see Lexer.java
 */
final class Parser {
    /** Instance Variables. */
    String file;
    String sep = System.lineSeparator();

    /**
     * Public constructor for Parser class.
     * This constructor takes the filename and reads it
     * line by line. Then it appends it to the instance 
     * variable file.
     * 
     * @param filename Name along with the relative / 
     * absolute path of the file. 
     */
    public Parser (String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.file += line + sep;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method parse.
     * It is invoked by method main.
     * It uses the Node class to represent the 
     * AST.
     */
    public void parse() {
        Node root = new Node("__main__");
        Lexer lex = new Lexer(this.file);
        TokenType type = lex.getNextTokenType();
        
        while (type != TokenType.EOF) {
            if (type == TokenType.IF) {
                
            }
        }
    }
}