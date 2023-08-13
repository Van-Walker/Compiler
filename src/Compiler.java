import utils.*;
import ast.*;
import grammar.*;
import semantic.*;

import java.io.InputStream;
import java.io.FileInputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Compiler {
    public static void main(String[] args) throws Exception {
        CharStream input = CharStreams.fromStream(System.in);
        MxLexer lexer = new MxLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MxErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxParser parser = new MxParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new MxErrorListener());
        ParseTree tree = parser.program();
        Builder astBuilder = new Builder();
        ProgramNode ast = (ProgramNode) astBuilder.visit(tree);
        GlobalScope globalScope = new GlobalScope();
        new SymbolCollect(globalScope).visit(ast);
        new SemanticCheck(globalScope).visit(ast);
    }

    private static InputStream FileInputStream(String string) {
        return null;
    }
}
