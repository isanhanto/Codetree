package codetree;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import codetree.common.*;
import codetree.core.*;

import codetree.vertexBased.AcgmCode;
import codetree.vertexBased.XAcgmCode;
import codetree.edgeBased.DfsCode;

class Main {
    public static void main(String[] args) {
        parseArgs(args);

        List<Graph> G = SdfFileReader.readFile(Paths.get(sdfFilename));
        // G = G.subList(0, 200);

        for (int i = 0; i < G.size() - 1; ++i) {// クエリ集合を得る
            Graph g1 = G.get(i);
            Graph g2 = G.get(i + 1);

            // Collections.sort(G, g1.compare(g1, g2));

        }

        ArrayList<Pair<Integer, Graph>> Q = new ArrayList<>();
        for (int i = 0; i < G.size(); ++i) {// クエリ集合を得る
            Graph g = G.get(i);

            final int size = g.size();
            if (34 <= size && size <= 36) {
                Q.add(new Pair<Integer, Graph>(i, g));
            }
        }

        System.out.println("G size: " + G.size());
        System.out.println("Q size: " + Q.size());

        long start = System.nanoTime();
        CodeTree tree = new CodeTree(graphCode, G, 100);// コード木構築
        System.out.println("Build tree: " + (System.nanoTime() - start) / 1000 / 1000 + "msec");

        G = null;

        Path out = Paths.get("output.txt");
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            start = System.nanoTime();

            for (Pair<Integer, Graph> q : Q) {
                List<Integer> result = tree.supergraphSearch(q.right);
                bw.write(q.left.toString() + result.toString() + "\n");
            }

            final long time = System.nanoTime() - start;
            System.out.println((time) + " nano sec");
            System.out.println((time / 1000 / 1000) + " msec");

        } catch (IOException e) {
            System.exit(1);
        }
    }

    private static void parseArgs(String[] args)// どのファイルとグラフコードで探索するか
    {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-code")) {
                if (args[++i].equals("acgm"))
                    graphCode = new AcgmCode();
                else if (args[i].equals("xacgm"))
                    graphCode = new XAcgmCode();
                else if (args[i].equals("dfs"))
                    graphCode = new DfsCode();
                else {
                    System.err.println("無効なグラフコード: " + args[i]);
                    System.exit(1);
                }
            } else if (sdfFilename == null) {
                sdfFilename = args[i];
            } else {
                System.err.println("無効な引数: " + args[i]);
                System.exit(1);
            }
        }

        if (sdfFilename == null) {
            System.err.print("入力ファイルを指定してください -> ");
            Scanner sc = new Scanner(System.in);
            sdfFilename = sc.nextLine();
        }

        if (graphCode == null) {
            graphCode = new AcgmCode();
            System.out.println("AcGMコードで動作します");
            /*
             * }else if (graphCode == XAcgmCode) {
             * System.out.println("XAcgmコードで動作します");
             * }else if (graphCode == DfsCode) {
             * System.out.println("dfsコードで動作します");
             * }
             */
        }
    }

    private static String sdfFilename = "aido99sd.sdf";
    // private static GraphCode graphCode = new XAcgmCode();//Dfs
    private static GraphCode graphCode = new AcgmCode();
    // private static GraphCode graphCode = new DfsCode();
}
