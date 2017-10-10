from typing import Mapping

import antlr4

import PlusFirstLexer
import PlusFirstListener
import PlusFirstParser


class Listener(PlusFirstListener.PlusFirstListener):
    '''Listener doing calculation based on recognized input.
    '''

    def __init__(self, var_value_source: Mapping[str, int]):
        self.var_value_source = var_value_source

    def exitMult(self, ctx: PlusFirstParser.PlusFirstParser.EContext):
        ctx.string = "(" + ctx.getChild(0).string + " * "  + ctx.getChild(2).string + ")"
        ctx.value = ctx.getChild(0).value * ctx.getChild(2).value

    def exitNum(self, ctx: PlusFirstParser.PlusFirstParser.EContext):
        ctx.string = str(ctx.getChild(0))
        ctx.value = int(str(ctx.getChild(0)))

    #def exitId(self, ctx: MultFirstParser.MultFirstParser.IdContext):
     #   ctx.value = self.var_value_source[str(ctx.getChild(0))]

    def exitPlus(self, ctx: PlusFirstParser.PlusFirstParser.EContext):
        ctx.string = "(" + ctx.getChild(0).string + " + "  + ctx.getChild(2).string + ")"
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
    PARSER = PlusFirstParser.PlusFirstParser(antlr4.CommonTokenStream(PlusFirstLexer.PlusFirstLexer(
        antlr4.InputStream(input('Please enter an expression: ')))))
    PARSER.addParseListener(Listener(LazyInputDict()))
    exp = PARSER.e()
    print(exp.string)
    print(exp.value)