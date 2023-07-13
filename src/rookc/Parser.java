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
    private String file;
    private String sep = System.lineSeparator();
    private Lexer lex = new Lexer(this.file);
    private Node rootNode = new Node("ROOT", NodeType.ROOT);
    private TokenType type = lex.getNextTokenType();

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
     * method setNext.
     * Sets the next TokenType.
     * 
     */

    public void setNext() {
        this.type = this.lex.getNextTokenType();
    }
    
    public void parse() {
    
        // Parse statements until the end of the file
        while (type != TokenType.EOF) {
            if (type == TokenType.FUNC) {
                // Parse function declarations
                Node funcNode = parseFunctionDeclaration();
                rootNode.addChild(funcNode);
            } else {
                // Parse other statements
                Node stmtNode = parseStatement();
                rootNode.addChild(stmtNode);
            }
            setNext();
        }
    }

    private Node parseParameterList() {
        Node paramListNode = new Node("PARAM_LIST", NodeType.PARAM);
    
        setNext();
        while (type == TokenType.IDENTIFIER) {
            Node paramNode = new Node(this.lex.getTokenValue(), NodeType.PARAM);
            paramListNode.addChild(paramNode);
    
            setNext();
            if (type == TokenType.COMMA) {
                setNext();
            } else {
                break;
            }
        }
    
        if (type != TokenType.RIGHT_PAREN) {
            Report.error("Parse error. Expected ')'.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
        setNext();
    
        return paramListNode;
    }
        
    private Node parseFunctionDeclaration() {
        Node funcNode = new Node("FUNCTION", NodeType.FUNC);

        // Parse function name
        setNext();
        if (type != TokenType.IDENTIFIER) {
            Report.error("Parse error. Expected function name.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
        Node nameNode = new Node(this.lex.getTokenValue(), NodeType.IDENTIFIER);
        funcNode.addChild(nameNode);

        // Parse parameter list
        setNext();
        if (type != TokenType.LEFT_PAREN) {
            Report.error("Parse error. Expected '('.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
        Node paramListNode = parseParameterList();
        funcNode.addChild(paramListNode);

        // Parse return type
        setNext();
        if (type != TokenType.COLON) {
            Report.error("Parse error. Expected return type.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
        setNext();
        if (type != TokenType.IDENTIFIER) {
            Report.error("Parse error. Invalid return type.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
        Node returnTypeNode = new Node(this.lex.getTokenValue(), NodeType.RETURN_TYPE);
        funcNode.addChild(returnTypeNode);

        // Parse function body
        setNext();
        if (type != TokenType.LEFT_BRACE) {
            Report.error("Parse error. Expected '{'.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
        setNext();
        while (type != TokenType.RIGHT_BRACE) {
            Node statementNode = parseStatement();
            if (statementNode != null) {
                funcNode.addChild(statementNode);
            }
        }
        setNext();

        // Check if function has a return statement and return type
        boolean hasReturn = false;
        for (Node child : funcNode.getChildren()) {
            if (child.getType() == NodeType.RETURN) {
                hasReturn = true;
                break;
            }
        }
        if (hasReturn && returnTypeNode.getChildren().size() == 0) {
            Report.error("Parse error. Function with return statement must have a return type specified.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        } else if (!hasReturn && returnTypeNode.getChildren().size() > 0) {
            Report.warning("Function with return type specified does not have a return statement.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }

        return funcNode;
    }
    
    private Node parseStatement() {
        Node stmtNode = null;
    
        if (type == TokenType.INT) {
            // Parse variable declarations
            // ...
        } else if (type == TokenType.IF) {
            // Parse IF statements
            stmtNode = parseIf();
        } else {
            Report.error("Parse error. Unexpected token.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
    
        return stmtNode;
    }

    private Node parseExpression() {
        Node exprNode = null;
    
        if (type == TokenType.INTEGER_LITERAL || type == TokenType.FLOATING_LITERAL) {
            // Parse numeric literals
            Node literalNode = new Node(this.lex.getTokenValue(), NodeType.LITERAL);
            exprNode = literalNode;
            setNext();
        } else if (type == TokenType.IDENTIFIER) {
            // Parse variable references
            Node nameNode = new Node(this.lex.getTokenValue(), NodeType.IDENTIFIER);
            exprNode = nameNode;
            setNext();
        } else if (type == TokenType.LEFT_PAREN) {
            // Parse parenthesized expressions
            setNext();
            exprNode = parseExpression();
            if (type != TokenType.RIGHT_PAREN) {
                Report.error("Parse error. Expected ')'.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
            }
            setNext();
        } else {
            Report.error("Parse error. Unexpected token.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        }
    
        // Parse binary operators
        while (NodeType.isBinOp(type)) {
            Node binOpNode = new Node(this.lex.getTokenValue(), NodeType.BIN_OP);
            binOpNode.addChild(exprNode);
            setNext();
            Node rhsNode = parseExpression();
            binOpNode.addChild(rhsNode);
            exprNode = binOpNode;
        }
    
        return exprNode;
    }
    
    private Node parseIf() {
        Node ifNode = new Node("IF", NodeType.IF);
        
        setNext();
    
        if (this.type != TokenType.LEFT_PAREN) {
            Report.error("Parse error. Expected '(' after IF.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        } else {
            TokenType lastType = null;
            String cond = "";
    
            while (type != TokenType.LEFT_BRACE) {    
                cond += this.lex.getTokenValue();
                lastType = type;
                setNext();
            }
    
            if (lastType != TokenType.RIGHT_PAREN) {
                Report.error("Parse Error. Expected ')', found '{'.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
            }
    
            Node conditionNode = new Node(cond, NodeType.COND);
    
            ifNode.addChild(conditionNode);
    
            // Parse the body of the IF statement
            setNext();
            while (type != TokenType.RIGHT_BRACE) {
                if (type == TokenType.IF) {
                    // Parse nested IF statements
                    Node childNode = parseIf();
                    ifNode.addChild(childNode);
                } else if (type == TokenType.ELIF) {
                    // Parse ELIF statements
                    Node elifNode = parseElif();
                    ifNode.addChild(elifNode);
                } else if (type == TokenType.ELSE) {
                    // Parse ELSE statements
                    Node elseNode = parseElse();
                    ifNode.addChild(elseNode);
                } else {
                    // Parse other statements
                    Node stmtNode = parseStatement();
                    ifNode.addChild(stmtNode);
                }
                setNext();
            }
        }
    
        return ifNode;
    }
    
    private Node parseElif() {
        Node elifNode = new Node("ELIF", NodeType.ELIF);
    
        setNext();
    
        if (this.type != TokenType.LEFT_PAREN) {
            Report.error("Parse error. Expected '(' after ELIF.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
        } else {
            TokenType lastType = null;
            String cond = "";
    
            while (type != TokenType.LEFT_BRACE) {    
                cond += this.lex.getTokenValue();
                lastType = type;
                setNext();
            }
    
            if (lastType != TokenType.RIGHT_PAREN) {
                Report.error("Parse Error. Expected ')', found '{'.", file, this.lex.getCurrentLine(), this.lex.getCurrentCpos());
            }
    
            Node conditionNode = new Node(cond, NodeType.COND);
    
            elifNode.addChild(conditionNode);
    
            // Parse the body of the ELIF statement
            setNext();
            while (type != TokenType.RIGHT_BRACE) {
                if (type == TokenType.IF) {
                    // Parse nested IF statements
                    Node childNode = parseIf();
                    elifNode.addChild(childNode);
                } else if (type == TokenType.ELIF) {
                    // Parse nested ELIF statements
                    Node childNode = parseElif();
                    elifNode.addChild(childNode);
                } else if (type == TokenType.ELSE) {
                    // Parse ELSE statements
                    Node elseNode = parseElse();
                    elifNode.addChild(elseNode);
                } else {
                    // Parse other statements
                    Node stmtNode = parseStatement();
                    elifNode.addChild(stmtNode);
                }
                setNext();
            }
        }
    
        return elifNode;
    }
    
    private Node parseElse() {
        Node elseNode = new Node("ELSE", NodeType.ELSE);
    
        setNext();
    
        // Parse the body of the ELSE statement
        while (type != TokenType.RIGHT_BRACE) {
            if (type == TokenType.IF) {
                // Parse nested IF statements
                Node childNode = parseIf();
                elseNode.addChild(childNode);
            } else {
                // Parse other statements
                Node stmtNode = parseStatement();
                elseNode.addChild(stmtNode);
            }
            setNext();
        }
    
        return elseNode;
    }    
}