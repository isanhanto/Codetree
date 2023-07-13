package codetree.core;

import java.util.*;
//import java.util.concurrent.*;使ってないらしい
//import java.io.*;使ってないらしい

public class CodeTree
{
    GraphCode impl;
    public IndexNode root;

    public CodeTree(GraphCode impl, List<Graph> G, int b)
    {
        this.impl = impl;
        this.root = new IndexNode(null, null);

        System.out.print("Indexing");
        for (int i = 0; i < G.size(); ++i) {
            Graph g = G.get(i);

            List<CodeFragment> code = impl.computeCanonicalCode(g, b);//準正準コードを得る
            root.addPath(code, i);

            if (i % 100000 == 0) {
                System.out.println();
            } else if (i % 10000 == 0) {
                System.out.print("*");
            } else if (i % 1000 == 0) {
                System.out.print(".");
            }
        }
        for (int i = 0; i < G.size(); ++i) {
            Graph g = G.get(i);
            root.addId(g, i, impl);
        }

        System.out.println();
        System.out.println("Tree size: " + root.size());
    }

    public List<Integer> supergraphSearch(Graph query)
    {
        return root.search(query, impl);
    }
}
