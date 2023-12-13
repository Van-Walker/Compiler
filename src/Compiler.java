import utils.*;
import ast.*;
import grammar.*;
import semantic.*;
import ir.*;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileInputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Compiler {
    public static void main(String[] args) throws Exception {
        // CharStream input = CharStreams.fromStream(System.in);
         CharStream input = CharStreams.fromStream(new FileInputStream("input.mx"));
         PrintStream output = new PrintStream("output.ll");
         System.setOut(output);
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
        IRProgram irProgram = new IRProgram();
        new IRBuilder(globalScope, irProgram).visit(ast);
        System.out.println(irProgram);
    }

    private static InputStream FileInputStream(String string) {
        return null;
    }
}
