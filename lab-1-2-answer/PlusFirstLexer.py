# Generated from PlusFirst.g4 by ANTLR 4.7
from antlr4 import *
from io import StringIO
from typing.io import TextIO
import sys


def serializedATN():
    with StringIO() as buf:
        buf.write("\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\6")
        buf.write("\35\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\3\2\3\2\7\2\16")
        buf.write("\n\2\f\2\16\2\21\13\2\3\3\3\3\3\4\3\4\3\5\6\5\30\n\5\r")
        buf.write("\5\16\5\31\3\5\3\5\2\2\6\3\3\5\4\7\5\t\6\3\2\5\3\2\63")
        buf.write(";\3\2\62;\5\2\13\f\17\17\"\"\2\36\2\3\3\2\2\2\2\5\3\2")
        buf.write("\2\2\2\7\3\2\2\2\2\t\3\2\2\2\3\13\3\2\2\2\5\22\3\2\2\2")
        buf.write("\7\24\3\2\2\2\t\27\3\2\2\2\13\17\t\2\2\2\f\16\t\3\2\2")
        buf.write("\r\f\3\2\2\2\16\21\3\2\2\2\17\r\3\2\2\2\17\20\3\2\2\2")
        buf.write("\20\4\3\2\2\2\21\17\3\2\2\2\22\23\7-\2\2\23\6\3\2\2\2")
        buf.write("\24\25\7,\2\2\25\b\3\2\2\2\26\30\t\4\2\2\27\26\3\2\2\2")
        buf.write("\30\31\3\2\2\2\31\27\3\2\2\2\31\32\3\2\2\2\32\33\3\2\2")
        buf.write("\2\33\34\b\5\2\2\34\n\3\2\2\2\5\2\17\31\3\b\2\2")
        return buf.getvalue()


class PlusFirstLexer(Lexer):

    atn = ATNDeserializer().deserialize(serializedATN())

    decisionsToDFA = [ DFA(ds, i) for i, ds in enumerate(atn.decisionToState) ]

    Number = 1
    Plus = 2
    Multiply = 3
    WhiteSpace = 4

    channelNames = [ u"DEFAULT_TOKEN_CHANNEL", u"HIDDEN" ]

    modeNames = [ "DEFAULT_MODE" ]

    literalNames = [ "<INVALID>",
            "'+'", "'*'" ]

    symbolicNames = [ "<INVALID>",
            "Number", "Plus", "Multiply", "WhiteSpace" ]

    ruleNames = [ "Number", "Plus", "Multiply", "WhiteSpace" ]

    grammarFileName = "PlusFirst.g4"

    def __init__(self, input=None, output:TextIO = sys.stdout):
        super().__init__(input, output)
        self.checkVersion("4.7")
        self._interp = LexerATNSimulator(self, self.atn, self.decisionsToDFA, PredictionContextCache())
        self._actions = None
        self._predicates = None


