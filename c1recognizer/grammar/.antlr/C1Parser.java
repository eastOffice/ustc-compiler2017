// Generated from /Users/eastOffice/.ssh/PB15111610/c1recognizer/grammar/C1Parser.g4 by ANTLR 4.7
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class C1Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Comma=1, SemiColon=2, Assign=3, LeftBracket=4, RightBracket=5, LeftBrace=6, 
		RightBrace=7, LeftParen=8, RightParen=9, If=10, Else=11, While=12, Const=13, 
		Equal=14, NonEqual=15, Less=16, Greater=17, LessEqual=18, GreaterEqual=19, 
		Plus=20, Minus=21, Multiply=22, Divide=23, Modulo=24, Int=25, Void=26, 
		Identifier=27, Number=28, BlockComment=29, LineComment=30, WhiteSpace=31;
	public static final int
		RULE_compilationUnit = 0, RULE_decl = 1, RULE_constdecl = 2, RULE_constdef = 3, 
		RULE_vardecl = 4, RULE_vardef = 5, RULE_funcdef = 6, RULE_block = 7, RULE_stmt = 8, 
		RULE_lval = 9, RULE_cond = 10, RULE_exp = 11;
	public static final String[] ruleNames = {
		"compilationUnit", "decl", "constdecl", "constdef", "vardecl", "vardef", 
		"funcdef", "block", "stmt", "lval", "cond", "exp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "';'", "'='", "'['", "']'", "'{'", "'}'", "'('", "')'", "'if'", 
		"'else'", "'while'", "'const'", "'=='", "'!='", "'<'", "'>'", "'<='", 
		"'>='", "'+'", "'-'", "'*'", "'/'", "'%'", "'int'", "'void'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "Comma", "SemiColon", "Assign", "LeftBracket", "RightBracket", "LeftBrace", 
		"RightBrace", "LeftParen", "RightParen", "If", "Else", "While", "Const", 
		"Equal", "NonEqual", "Less", "Greater", "LessEqual", "GreaterEqual", "Plus", 
		"Minus", "Multiply", "Divide", "Modulo", "Int", "Void", "Identifier", 
		"Number", "BlockComment", "LineComment", "WhiteSpace"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "C1Parser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public C1Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class CompilationUnitContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(C1Parser.EOF, 0); }
		public List<DeclContext> decl() {
			return getRuleContexts(DeclContext.class);
		}
		public DeclContext decl(int i) {
			return getRuleContext(DeclContext.class,i);
		}
		public List<FuncdefContext> funcdef() {
			return getRuleContexts(FuncdefContext.class);
		}
		public FuncdefContext funcdef(int i) {
			return getRuleContext(FuncdefContext.class,i);
		}
		public CompilationUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compilationUnit; }
	}

	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compilationUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Const) | (1L << Int) | (1L << Void))) != 0)) {
				{
				setState(26);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Const:
				case Int:
					{
					setState(24);
					decl();
					}
					break;
				case Void:
					{
					setState(25);
					funcdef();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(30);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(31);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclContext extends ParserRuleContext {
		public ConstdeclContext constdecl() {
			return getRuleContext(ConstdeclContext.class,0);
		}
		public VardeclContext vardecl() {
			return getRuleContext(VardeclContext.class,0);
		}
		public DeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decl; }
	}

	public final DeclContext decl() throws RecognitionException {
		DeclContext _localctx = new DeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_decl);
		try {
			setState(35);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Const:
				enterOuterAlt(_localctx, 1);
				{
				setState(33);
				constdecl();
				}
				break;
			case Int:
				enterOuterAlt(_localctx, 2);
				{
				setState(34);
				vardecl();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstdeclContext extends ParserRuleContext {
		public TerminalNode Const() { return getToken(C1Parser.Const, 0); }
		public List<ConstdefContext> constdef() {
			return getRuleContexts(ConstdefContext.class);
		}
		public ConstdefContext constdef(int i) {
			return getRuleContext(ConstdefContext.class,i);
		}
		public TerminalNode SemiColon() { return getToken(C1Parser.SemiColon, 0); }
		public TerminalNode Int() { return getToken(C1Parser.Int, 0); }
		public List<TerminalNode> Comma() { return getTokens(C1Parser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(C1Parser.Comma, i);
		}
		public ConstdeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constdecl; }
	}

	public final ConstdeclContext constdecl() throws RecognitionException {
		ConstdeclContext _localctx = new ConstdeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_constdecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			match(Const);
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Int) {
				{
				setState(38);
				match(Int);
				}
			}

			setState(41);
			constdef();
			setState(46);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(42);
				match(Comma);
				setState(43);
				constdef();
				}
				}
				setState(48);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(49);
			match(SemiColon);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstdefContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(C1Parser.Identifier, 0); }
		public TerminalNode Assign() { return getToken(C1Parser.Assign, 0); }
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public TerminalNode LeftBracket() { return getToken(C1Parser.LeftBracket, 0); }
		public TerminalNode RightBracket() { return getToken(C1Parser.RightBracket, 0); }
		public TerminalNode LeftBrace() { return getToken(C1Parser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(C1Parser.RightBrace, 0); }
		public List<TerminalNode> Comma() { return getTokens(C1Parser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(C1Parser.Comma, i);
		}
		public ConstdefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constdef; }
	}

	public final ConstdefContext constdef() throws RecognitionException {
		ConstdefContext _localctx = new ConstdefContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_constdef);
		int _la;
		try {
			setState(72);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(51);
				match(Identifier);
				setState(52);
				match(Assign);
				setState(53);
				exp(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(54);
				match(Identifier);
				setState(55);
				match(LeftBracket);
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LeftParen) | (1L << Plus) | (1L << Minus) | (1L << Identifier) | (1L << Number))) != 0)) {
					{
					setState(56);
					exp(0);
					}
				}

				setState(59);
				match(RightBracket);
				setState(60);
				match(Assign);
				setState(61);
				match(LeftBrace);
				setState(62);
				exp(0);
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(63);
					match(Comma);
					setState(64);
					exp(0);
					}
					}
					setState(69);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(70);
				match(RightBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VardeclContext extends ParserRuleContext {
		public TerminalNode Int() { return getToken(C1Parser.Int, 0); }
		public List<VardefContext> vardef() {
			return getRuleContexts(VardefContext.class);
		}
		public VardefContext vardef(int i) {
			return getRuleContext(VardefContext.class,i);
		}
		public TerminalNode SemiColon() { return getToken(C1Parser.SemiColon, 0); }
		public List<TerminalNode> Comma() { return getTokens(C1Parser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(C1Parser.Comma, i);
		}
		public VardeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vardecl; }
	}

	public final VardeclContext vardecl() throws RecognitionException {
		VardeclContext _localctx = new VardeclContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_vardecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(Int);
			setState(75);
			vardef();
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(76);
				match(Comma);
				setState(77);
				vardef();
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(83);
			match(SemiColon);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VardefContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(C1Parser.Identifier, 0); }
		public TerminalNode LeftBracket() { return getToken(C1Parser.LeftBracket, 0); }
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public TerminalNode RightBracket() { return getToken(C1Parser.RightBracket, 0); }
		public TerminalNode Assign() { return getToken(C1Parser.Assign, 0); }
		public TerminalNode LeftBrace() { return getToken(C1Parser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(C1Parser.RightBrace, 0); }
		public List<TerminalNode> Comma() { return getTokens(C1Parser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(C1Parser.Comma, i);
		}
		public VardefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vardef; }
	}

	public final VardefContext vardef() throws RecognitionException {
		VardefContext _localctx = new VardefContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_vardef);
		int _la;
		try {
			setState(112);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(86);
				match(Identifier);
				setState(87);
				match(LeftBracket);
				setState(88);
				exp(0);
				setState(89);
				match(RightBracket);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(91);
				match(Identifier);
				setState(92);
				match(Assign);
				setState(93);
				exp(0);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(94);
				match(Identifier);
				setState(95);
				match(LeftBracket);
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LeftParen) | (1L << Plus) | (1L << Minus) | (1L << Identifier) | (1L << Number))) != 0)) {
					{
					setState(96);
					exp(0);
					}
				}

				setState(99);
				match(RightBracket);
				setState(100);
				match(Assign);
				setState(101);
				match(LeftBrace);
				setState(102);
				exp(0);
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(103);
					match(Comma);
					setState(104);
					exp(0);
					}
					}
					setState(109);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(110);
				match(RightBrace);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncdefContext extends ParserRuleContext {
		public TerminalNode Void() { return getToken(C1Parser.Void, 0); }
		public TerminalNode Identifier() { return getToken(C1Parser.Identifier, 0); }
		public TerminalNode LeftParen() { return getToken(C1Parser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(C1Parser.RightParen, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FuncdefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcdef; }
	}

	public final FuncdefContext funcdef() throws RecognitionException {
		FuncdefContext _localctx = new FuncdefContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_funcdef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(Void);
			setState(115);
			match(Identifier);
			setState(116);
			match(LeftParen);
			setState(117);
			match(RightParen);
			setState(118);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public TerminalNode LeftBrace() { return getToken(C1Parser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(C1Parser.RightBrace, 0); }
		public List<DeclContext> decl() {
			return getRuleContexts(DeclContext.class);
		}
		public DeclContext decl(int i) {
			return getRuleContext(DeclContext.class,i);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			match(LeftBrace);
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SemiColon) | (1L << LeftBrace) | (1L << If) | (1L << While) | (1L << Const) | (1L << Int) | (1L << Identifier))) != 0)) {
				{
				setState(123);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Const:
				case Int:
					{
					setState(121);
					decl();
					}
					break;
				case SemiColon:
				case LeftBrace:
				case If:
				case While:
				case Identifier:
					{
					setState(122);
					stmt();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(128);
			match(RightBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtContext extends ParserRuleContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public TerminalNode Assign() { return getToken(C1Parser.Assign, 0); }
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TerminalNode SemiColon() { return getToken(C1Parser.SemiColon, 0); }
		public TerminalNode Identifier() { return getToken(C1Parser.Identifier, 0); }
		public TerminalNode LeftParen() { return getToken(C1Parser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(C1Parser.RightParen, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode If() { return getToken(C1Parser.If, 0); }
		public CondContext cond() {
			return getRuleContext(CondContext.class,0);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public TerminalNode Else() { return getToken(C1Parser.Else, 0); }
		public TerminalNode While() { return getToken(C1Parser.While, 0); }
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_stmt);
		try {
			setState(156);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				lval();
				setState(131);
				match(Assign);
				setState(132);
				exp(0);
				setState(133);
				match(SemiColon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(135);
				match(Identifier);
				setState(136);
				match(LeftParen);
				setState(137);
				match(RightParen);
				setState(138);
				match(SemiColon);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(139);
				block();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(140);
				match(If);
				setState(141);
				match(LeftParen);
				setState(142);
				cond();
				setState(143);
				match(RightParen);
				setState(144);
				stmt();
				setState(147);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(145);
					match(Else);
					setState(146);
					stmt();
					}
					break;
				}
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(149);
				match(While);
				setState(150);
				match(LeftParen);
				setState(151);
				cond();
				setState(152);
				match(RightParen);
				setState(153);
				stmt();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(155);
				match(SemiColon);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LvalContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(C1Parser.Identifier, 0); }
		public TerminalNode LeftBracket() { return getToken(C1Parser.LeftBracket, 0); }
		public ExpContext exp() {
			return getRuleContext(ExpContext.class,0);
		}
		public TerminalNode RightBracket() { return getToken(C1Parser.RightBracket, 0); }
		public LvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lval; }
	}

	public final LvalContext lval() throws RecognitionException {
		LvalContext _localctx = new LvalContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lval);
		try {
			setState(164);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(158);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(159);
				match(Identifier);
				setState(160);
				match(LeftBracket);
				setState(161);
				exp(0);
				setState(162);
				match(RightBracket);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CondContext extends ParserRuleContext {
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public TerminalNode Equal() { return getToken(C1Parser.Equal, 0); }
		public TerminalNode NonEqual() { return getToken(C1Parser.NonEqual, 0); }
		public TerminalNode Less() { return getToken(C1Parser.Less, 0); }
		public TerminalNode Greater() { return getToken(C1Parser.Greater, 0); }
		public TerminalNode LessEqual() { return getToken(C1Parser.LessEqual, 0); }
		public TerminalNode GreaterEqual() { return getToken(C1Parser.GreaterEqual, 0); }
		public CondContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cond; }
	}

	public final CondContext cond() throws RecognitionException {
		CondContext _localctx = new CondContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_cond);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			exp(0);
			setState(167);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Equal) | (1L << NonEqual) | (1L << Less) | (1L << Greater) | (1L << LessEqual) | (1L << GreaterEqual))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(168);
			exp(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpContext extends ParserRuleContext {
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public TerminalNode Plus() { return getToken(C1Parser.Plus, 0); }
		public TerminalNode Minus() { return getToken(C1Parser.Minus, 0); }
		public TerminalNode LeftParen() { return getToken(C1Parser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(C1Parser.RightParen, 0); }
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public TerminalNode Number() { return getToken(C1Parser.Number, 0); }
		public TerminalNode Multiply() { return getToken(C1Parser.Multiply, 0); }
		public TerminalNode Divide() { return getToken(C1Parser.Divide, 0); }
		public TerminalNode Modulo() { return getToken(C1Parser.Modulo, 0); }
		public ExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp; }
	}

	public final ExpContext exp() throws RecognitionException {
		return exp(0);
	}

	private ExpContext exp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpContext _localctx = new ExpContext(_ctx, _parentState);
		ExpContext _prevctx = _localctx;
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_exp, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Plus:
			case Minus:
				{
				setState(171);
				_la = _input.LA(1);
				if ( !(_la==Plus || _la==Minus) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(172);
				exp(6);
				}
				break;
			case LeftParen:
				{
				setState(173);
				match(LeftParen);
				setState(174);
				exp(0);
				setState(175);
				match(RightParen);
				}
				break;
			case Identifier:
				{
				setState(177);
				lval();
				}
				break;
			case Number:
				{
				setState(178);
				match(Number);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(189);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(187);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
					case 1:
						{
						_localctx = new ExpContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_exp);
						setState(181);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(182);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Multiply) | (1L << Divide) | (1L << Modulo))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(183);
						exp(6);
						}
						break;
					case 2:
						{
						_localctx = new ExpContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_exp);
						setState(184);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(185);
						_la = _input.LA(1);
						if ( !(_la==Plus || _la==Minus) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(186);
						exp(5);
						}
						break;
					}
					} 
				}
				setState(191);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 11:
			return exp_sempred((ExpContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean exp_sempred(ExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 5);
		case 1:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3!\u00c3\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\3\2\7\2\35\n\2\f\2\16\2 \13\2\3\2\3\2\3\3\3\3"+
		"\5\3&\n\3\3\4\3\4\5\4*\n\4\3\4\3\4\3\4\7\4/\n\4\f\4\16\4\62\13\4\3\4\3"+
		"\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5<\n\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5D\n\5\f"+
		"\5\16\5G\13\5\3\5\3\5\5\5K\n\5\3\6\3\6\3\6\3\6\7\6Q\n\6\f\6\16\6T\13\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7d\n\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\7\7l\n\7\f\7\16\7o\13\7\3\7\3\7\5\7s\n\7\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\t\3\t\3\t\7\t~\n\t\f\t\16\t\u0081\13\t\3\t\3\t\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0096"+
		"\n\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u009f\n\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\5\13\u00a7\n\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\5\r\u00b6\n\r\3\r\3\r\3\r\3\r\3\r\3\r\7\r\u00be\n\r\f\r\16\r\u00c1"+
		"\13\r\3\r\2\3\30\16\2\4\6\b\n\f\16\20\22\24\26\30\2\5\3\2\20\25\3\2\26"+
		"\27\3\2\30\32\2\u00d2\2\36\3\2\2\2\4%\3\2\2\2\6\'\3\2\2\2\bJ\3\2\2\2\n"+
		"L\3\2\2\2\fr\3\2\2\2\16t\3\2\2\2\20z\3\2\2\2\22\u009e\3\2\2\2\24\u00a6"+
		"\3\2\2\2\26\u00a8\3\2\2\2\30\u00b5\3\2\2\2\32\35\5\4\3\2\33\35\5\16\b"+
		"\2\34\32\3\2\2\2\34\33\3\2\2\2\35 \3\2\2\2\36\34\3\2\2\2\36\37\3\2\2\2"+
		"\37!\3\2\2\2 \36\3\2\2\2!\"\7\2\2\3\"\3\3\2\2\2#&\5\6\4\2$&\5\n\6\2%#"+
		"\3\2\2\2%$\3\2\2\2&\5\3\2\2\2\')\7\17\2\2(*\7\33\2\2)(\3\2\2\2)*\3\2\2"+
		"\2*+\3\2\2\2+\60\5\b\5\2,-\7\3\2\2-/\5\b\5\2.,\3\2\2\2/\62\3\2\2\2\60"+
		".\3\2\2\2\60\61\3\2\2\2\61\63\3\2\2\2\62\60\3\2\2\2\63\64\7\4\2\2\64\7"+
		"\3\2\2\2\65\66\7\35\2\2\66\67\7\5\2\2\67K\5\30\r\289\7\35\2\29;\7\6\2"+
		"\2:<\5\30\r\2;:\3\2\2\2;<\3\2\2\2<=\3\2\2\2=>\7\7\2\2>?\7\5\2\2?@\7\b"+
		"\2\2@E\5\30\r\2AB\7\3\2\2BD\5\30\r\2CA\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3"+
		"\2\2\2FH\3\2\2\2GE\3\2\2\2HI\7\t\2\2IK\3\2\2\2J\65\3\2\2\2J8\3\2\2\2K"+
		"\t\3\2\2\2LM\7\33\2\2MR\5\f\7\2NO\7\3\2\2OQ\5\f\7\2PN\3\2\2\2QT\3\2\2"+
		"\2RP\3\2\2\2RS\3\2\2\2SU\3\2\2\2TR\3\2\2\2UV\7\4\2\2V\13\3\2\2\2Ws\7\35"+
		"\2\2XY\7\35\2\2YZ\7\6\2\2Z[\5\30\r\2[\\\7\7\2\2\\s\3\2\2\2]^\7\35\2\2"+
		"^_\7\5\2\2_s\5\30\r\2`a\7\35\2\2ac\7\6\2\2bd\5\30\r\2cb\3\2\2\2cd\3\2"+
		"\2\2de\3\2\2\2ef\7\7\2\2fg\7\5\2\2gh\7\b\2\2hm\5\30\r\2ij\7\3\2\2jl\5"+
		"\30\r\2ki\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2np\3\2\2\2om\3\2\2\2pq"+
		"\7\t\2\2qs\3\2\2\2rW\3\2\2\2rX\3\2\2\2r]\3\2\2\2r`\3\2\2\2s\r\3\2\2\2"+
		"tu\7\34\2\2uv\7\35\2\2vw\7\n\2\2wx\7\13\2\2xy\5\20\t\2y\17\3\2\2\2z\177"+
		"\7\b\2\2{~\5\4\3\2|~\5\22\n\2}{\3\2\2\2}|\3\2\2\2~\u0081\3\2\2\2\177}"+
		"\3\2\2\2\177\u0080\3\2\2\2\u0080\u0082\3\2\2\2\u0081\177\3\2\2\2\u0082"+
		"\u0083\7\t\2\2\u0083\21\3\2\2\2\u0084\u0085\5\24\13\2\u0085\u0086\7\5"+
		"\2\2\u0086\u0087\5\30\r\2\u0087\u0088\7\4\2\2\u0088\u009f\3\2\2\2\u0089"+
		"\u008a\7\35\2\2\u008a\u008b\7\n\2\2\u008b\u008c\7\13\2\2\u008c\u009f\7"+
		"\4\2\2\u008d\u009f\5\20\t\2\u008e\u008f\7\f\2\2\u008f\u0090\7\n\2\2\u0090"+
		"\u0091\5\26\f\2\u0091\u0092\7\13\2\2\u0092\u0095\5\22\n\2\u0093\u0094"+
		"\7\r\2\2\u0094\u0096\5\22\n\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2\2\2"+
		"\u0096\u009f\3\2\2\2\u0097\u0098\7\16\2\2\u0098\u0099\7\n\2\2\u0099\u009a"+
		"\5\26\f\2\u009a\u009b\7\13\2\2\u009b\u009c\5\22\n\2\u009c\u009f\3\2\2"+
		"\2\u009d\u009f\7\4\2\2\u009e\u0084\3\2\2\2\u009e\u0089\3\2\2\2\u009e\u008d"+
		"\3\2\2\2\u009e\u008e\3\2\2\2\u009e\u0097\3\2\2\2\u009e\u009d\3\2\2\2\u009f"+
		"\23\3\2\2\2\u00a0\u00a7\7\35\2\2\u00a1\u00a2\7\35\2\2\u00a2\u00a3\7\6"+
		"\2\2\u00a3\u00a4\5\30\r\2\u00a4\u00a5\7\7\2\2\u00a5\u00a7\3\2\2\2\u00a6"+
		"\u00a0\3\2\2\2\u00a6\u00a1\3\2\2\2\u00a7\25\3\2\2\2\u00a8\u00a9\5\30\r"+
		"\2\u00a9\u00aa\t\2\2\2\u00aa\u00ab\5\30\r\2\u00ab\27\3\2\2\2\u00ac\u00ad"+
		"\b\r\1\2\u00ad\u00ae\t\3\2\2\u00ae\u00b6\5\30\r\b\u00af\u00b0\7\n\2\2"+
		"\u00b0\u00b1\5\30\r\2\u00b1\u00b2\7\13\2\2\u00b2\u00b6\3\2\2\2\u00b3\u00b6"+
		"\5\24\13\2\u00b4\u00b6\7\36\2\2\u00b5\u00ac\3\2\2\2\u00b5\u00af\3\2\2"+
		"\2\u00b5\u00b3\3\2\2\2\u00b5\u00b4\3\2\2\2\u00b6\u00bf\3\2\2\2\u00b7\u00b8"+
		"\f\7\2\2\u00b8\u00b9\t\4\2\2\u00b9\u00be\5\30\r\b\u00ba\u00bb\f\6\2\2"+
		"\u00bb\u00bc\t\3\2\2\u00bc\u00be\5\30\r\7\u00bd\u00b7\3\2\2\2\u00bd\u00ba"+
		"\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0"+
		"\31\3\2\2\2\u00c1\u00bf\3\2\2\2\26\34\36%)\60;EJRcmr}\177\u0095\u009e"+
		"\u00a6\u00b5\u00bd\u00bf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}