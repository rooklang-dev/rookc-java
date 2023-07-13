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

import java.util.Map;
import java.util.HashMap;

/**
 * @author SrcyDev
 * @see rookc#Parser
 */
final class Lexer {
    String file,cval,val;
    int line = 1, cpos;
    int P,B;
    Map<String, TokenType> keywords; 

    /**
     * Public constructor for class Lexer.
     * It takes the contents of a file as
     * String and then stores it in an instance variable.
     */
    public Lexer(String file) {
        this.file = file;
        keywords = new HashMap<>();

        keywords.put("bool", TokenType.BOOL);
        keywords.put("break", TokenType.BREAK);
        keywords.put("char", TokenType.CHAR);
        keywords.put("class", TokenType.CLASS);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("float", TokenType.FLOAT);
        keywords.put("for", TokenType.FOR);
        keywords.put("func", TokenType.FUNC);
        keywords.put("if", TokenType.IF);
        keywords.put("import", TokenType.IMPORT);
        keywords.put("int", TokenType.INT);
        keywords.put("null", TokenType.NULL);
        keywords.put("pub", TokenType.PUB);
        keywords.put("return", TokenType.RETURN);
        keywords.put("string", TokenType.STRING);
        keywords.put("super", TokenType.SUPER);
        keywords.put("switch", TokenType.SWITCH);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("while", TokenType.WHILE);
    }

    /**
     * Checks if the current column position is at the end of
     * the file.
     * 
     * @return boolean
     */
    private boolean isEof() {
        return (cpos >= file.length());
    }

    /**
     * Sets the current colummn position after a new line.
     * 
     * @return void
     */
    private void setNewLine() {
        for (;!isEof();cpos++) {
            if (file.charAt(cpos) == '\n') break;
        }
    }

    /**
     * Check if a character is a number.
     * 
     * @param c Character to check for.
     * @return boolean
     */
    private boolean isNum(char c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Checks if a charcter is a valid punctuation.
     * 
     * @param c
     * @return
     */
    public static boolean isValidPunc(char c) {
        String punctuations = "!\"%'()*+,-./:;<=>[]{}";
        return punctuations.indexOf(c) != -1;
    }
    
    /**
     * Check if a character is a letter.
     * 
     * @param c Character to check for.
     * @return boolean
     */
    private boolean isLetter(char c) {
        return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
    }

    /**
     * Check if a character is alphanumeric.
     * 
     * @param c Character to check for.
     * @return boolean
     */
    private boolean isAlphaNum(char c) {
        return (isLetter(c) || isNum(c));
    }

    /**
     * Check if the literal starting from current column position is valid or not.
     * If valid, return the type (Integer or Floating), otherwise generate error.
     * 
     * @return TokenType
     */
    private TokenType number() {
        int start = cpos,i = 0;
        boolean isValid = true;

        if (this.file.charAt(cpos) == '-') {
            if (isEof() || !isNum(this.file.charAt(cpos + 1))) {
                Report.error("Invalid number format", file, line, cpos);
                isValid = false;
            }
            cpos++;
            i++;
        }
        boolean decimalPointSeen = false;
        boolean exponentSeen = false;
        while (cpos < this.file.length() && isNum(this.file.charAt(cpos))) {
            cpos++;
            i++;
        }
        if (cpos < this.file.length() && this.file.charAt(cpos) == '.' && cpos + 1 < this.file.length() && isNum(this.file.charAt(cpos + 1))) {
            cpos++;
            i++;
            decimalPointSeen = true;
            while (cpos < this.file.length() && isNum(this.file.charAt(cpos))) {
                cpos++;
                i++;
            }
        }
        if (cpos < this.file.length() && (this.file.charAt(cpos) == 'e' || this.file.charAt(cpos) == 'E')) {
            if (exponentSeen) {
                Report.error("Multiple 'E'/'e' detected.", file, line, cpos);
                isValid = false;
            }
            cpos++;
            i++;
            exponentSeen = true;
            if (cpos < this.file.length() && (this.file.charAt(cpos) == '+' || this.file.charAt(cpos) == '-')) {
                cpos++;
                i++;
            }
            if (cpos >= this.file.length() || !isNum(this.file.charAt(cpos))) {
                Report.error("Invalid number format", file, line, cpos);
                return TokenType.NONE;
            }
            while (cpos < this.file.length() && isNum(this.file.charAt(cpos))) {
                cpos++;
                i++;
            }
        }
        if (!isValid) return TokenType.NONE;

        this.val = this.file.substring(start, cpos + i);
        return decimalPointSeen ? TokenType.FLOATING_LITERAL : TokenType.INTEGER_LITERAL;
    }

    /**
     * Consume the String Literal and store it in val.
     * 
     * @return void
     */
    private void string() {
        boolean closedString = false;
        
        for (;!isEof();cpos++) {
            if (file.charAt(cpos) == '"' && file.charAt(cpos - 1) != '\\') {
                closedString = true;
                break;
            }
            this.val += file.charAt(cpos);
        }

        if (!closedString) {
            Report.error("String literal not closed.", file, line, cpos);
        }
    }

    /**
     * Checks if the identified literal is a valid identifier or not.
     * If valid, return TokenType.IDENTIFIER, TokenType.NONE otherwise.
     * 
     * @return TokenType
     */
    private TokenType identifier() {
        boolean isValid = true;

        if (file == null || file.isEmpty() || cpos < 0 || cpos >= file.length()) {
            isValid = false;
        }
        
        // Check the first character
        char firstChar = file.charAt(cpos);
        if (!(firstChar == '_' || Character.isLetter(firstChar))) {
            isValid = false;
        }
        
        // Check the remaining characters
        for (int i = cpos + 1; i < file.length(); i++) {
            char c = file.charAt(i);
            if (!(c == '_' || Character.isLetterOrDigit(c))) {
                isValid = false;
            }
        }
        
       return (isValid) ? TokenType.IDENTIFIER : TokenType.NONE; 
    }

    /**
     * Method getNextToken.
     * This returns the token type after identifying the type of
     * the token.
     * 
     * @return TokenType
     */
    public TokenType getNextTokenType() {
        this.cval = "";
        
        if (isEof()) {
            if (P != 0) Report.error("Unclosed parantheses detected.", file, line, cpos);
            if (B != 0) Report.error("Unclosed braces detected.", file, line, cpos);
            return TokenType.EOF;
        }

        for (;!isEof();cpos++) {
            if (!isAlphaNum(file.charAt(cpos)) && !isValidPunc(file.charAt(cpos))) break;
            this.cval += file.charAt(cpos);
        }

        TokenType type = keywords.get(this.cval);
            
        if (type != null) {
            return type;
        } else {
            return switch (this.cval) {
                case "(" -> {
                    P++;
                    yield TokenType.LEFT_PAREN;
                }
                case ")" -> {
                    P--;
                    yield TokenType.RIGHT_PAREN;
                }
                case "{" -> {
                    B++;
                    yield TokenType.LEFT_BRACE;
                }
                case "}" -> {
                    B--;
                    yield TokenType.RIGHT_BRACE;
                }
                case "[" -> {
                    yield TokenType.LEFT_BRACKET;
                }
                case "]" -> {
                    yield TokenType.RIGHT_BRACKET;
                }
                case "," -> {
                    yield TokenType.COMMA;
                }
                case "." -> {
                    yield TokenType.DOT;
                }
                case ";" -> {
                    yield TokenType.SEMICOLON;
                }
                case "-" -> {
                    if (!isNum(file.charAt(cpos + 1)) || file.charAt(cpos + 1) == ' ') 
                        yield TokenType.MINUS;
                    else {
                        yield number();
                    }
                }
                case "+" -> {
                    yield TokenType.PLUS;
                }
                case "*" -> {
                    yield TokenType.ASTERISK;
                }
                case "/" -> {
                    yield TokenType.SLASH;
                }
                case "!" -> {
                    yield TokenType.NOT;
                }
                case "=" -> {
                    yield  TokenType.EQUAL;
                }
                case "<" -> {
                    yield TokenType.LESS_THAN;
                }
                case ">" -> {
                    yield TokenType.GREATER_THAN;
                }
                case "-=" -> {
                    yield TokenType.MINUS_ASSIGN;
                }
                case "+=" -> {
                    yield TokenType.PLUS_ASSIGN;
                }
                case "*=" -> {
                    yield TokenType.ASTERISK_ASSIGN;
                }
                case "/=" -> {
                    yield TokenType.SLASH_ASSIGN;
                }
                case "!=" -> {
                    yield TokenType.NOT_EQUAL;
                }
                case "==" -> {
                    yield  TokenType.EQUAL_EQUAL;
                }
                case "<=" -> {
                    yield TokenType.LESS_THAN_OR_EQUAL;
                }
                case ">=" -> {
                    yield TokenType.GREATER_THAN_OR_EQUAL;
                }
                case "//" -> {
                    setNewLine();
                    yield TokenType.NONE;
                }
                case " ", "\r", "\t" -> {
                    yield TokenType.NONE;
                }
                case "\n" -> {
                    line++;
                    yield TokenType.NONE;
                }
                case "\"" -> {
                    string();
                    yield TokenType.STRING_LITERAL;
                }
                default -> {
                    if (isNum(cval.charAt(0))) {
                        yield number();
                    } else if (isAlphaNum(cval.charAt(0))) {
                        yield identifier();
                    } else {
                        Report.error("Unexpected character", file, line, cpos);
                        yield TokenType.NONE;
                    }
                }
            };  
        }
    }
}