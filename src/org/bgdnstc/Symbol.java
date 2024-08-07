package org.bgdnstc;

public enum Symbol {
    // variable types
    TYPE_INT, TYPE_FLOAT, TYPE_STRING, SOCKET, UDP_SERVER, UDP_CLIENT,
    // value types
    INT, FLOAT, STRING,
    // operators / conditional structures
    EQUALS, ADD, SUB, MUL, DIV, INC, LOOP, WHEN, EXIT, WHILE,
    // logic operators
    GREATER, LESS, LOGIC_EQUALS,
    // methods
    PRINT, RECEIVE, SEND,
    // punctuation marks
    DOT, L_BRACKET, R_BRACKET, L_PARENTHESIS, R_PARENTHESIS, AT,
    // identifier (variable name)
    IDENTIFIER,
    // markers (end of line)
    EOL
}
