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
package rookc.parser;

import rookc.TokenType;

public enum NodeType {
    // ROOT
    ROOT,

    // IDENTIFIER
    IDENTIFIER,

    // LITERAL
    LITERAL,

    // CONDITION
    COND,

    // FUNC-related
    FUNC,PARAM,

    // RETURN-related
    RETURN, RETURN_TYPE,

    // OPERATOR
    ADD, SUB, MUL, DIV, MOD, NOT,
    ASSIGN,
    ADD_ASSIGN, SUB_ASSIGN, MUL_ASSIGN,
    DIV_ASSIGN, MOD_ASSIGN, NOT_ASSIGN,

    // BITWISE OPERATOR
    BITWISE_AND, BITWISE_OR, BITWISE_XOR,
    BITWISE_SHIFT_LEFT, BITWISE_SHIFT_RIGHT,

    BIN_OP, UN_OP,

    // IF-related
    IF, ELIF, ELSE,

    // FOR-related
    FOR,

    // WHILE-related
    WHILE,

    // IMPORT RELATED
    IMPORT, PKG_NAME,

    // CLASS
    CLASS;


    public static boolean isBinOp(TokenType tokenType) {
        return switch (tokenType) {
            case ASSIGN, PLUS, MINUS , ASTERISK, SLASH, PERCENT, BITWISE_AND, BITWISE_OR, BITWISE_XOR, BITWISE_NOT, LEFT_SHIFT, RIGHT_SHIFT,        UNSIGNED_RIGHT_SHIFT, PLUS_ASSIGN, MINUS_ASSIGN, ASTERISK_ASSIGN, SLASH_ASSIGN, PERCENT_ASSIGN, AND_ASSIGN, OR_ASSIGN, XOR_ASSIGN, LEFT_SHIFT_ASSIGN, RIGHT_SHIFT_ASSIGN, UNSIGNED_RIGHT_SHIFT_ASSIGN -> {
                yield true;
            }
            default -> {
                yield false;
            }
        };
    }
}