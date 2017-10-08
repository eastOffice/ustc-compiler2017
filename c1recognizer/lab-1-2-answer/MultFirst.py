from typing import Mapping

import antlr4

import MultFirstLexer
import MultFirstListener
import MultFirstParser


class Listener(MultFirstListener.MultFirstListener):
    '''Listener doing calculation based on recognized input.
    '''

    def __init__(self, var_value_source: Mapping[str, int]):
        self.var_value_source = var_value_source

    def exitMult(self, ctx: MultFirstParser.MultFirstParser.EContext):
        ctx.value = ctx.getChild(0).value * ctx.getChild(2).value

    def exitNum(self, ctx: MultFirstParser.MultFirstParser.EContext):
        ctx.value = int(str(ctx.getChild(0)))

    #def exitId(self, ctx: MultFirstParser.MultFirstParser.IdContext):
     #   ctx.value = self.var_value_source[str(ctx.getChild(0))]

    def exitPlus(self, ctx: MultFirstParser.MultFirstParser.EContext):
        ctx.value = ctx.getChild(0).value + ctx.getChild(2).value

    #def exitBrac(self, ctx: MultFirstParser.MultFirstParser.BracContext):
     #   ctx.value = ctx.getChild(1).value


class LazyInputDict(dict):
    '''A lazy dictionary asking for input when a new item is queried.'''

    def __getitem__(self, key):
        try:
            return dict.__getitem__(self, key)
        except KeyError:
            self[key] = int(
                input('Please enter value for variable \'{}\': '.format(key)))
        return dict.__getitem__(self, key)


if __name__ == '__main__':
    PARSER = MultFirstParser.MultFirstParser(antlr4.CommonTokenStream(MultFirstLexer.MultFirstLexer(
        antlr4.InputStream(input('Please enter an expression: ')))))
    PARSER.addParseListener(Listener(LazyInputDict()))
    print(PARSER.e().value)