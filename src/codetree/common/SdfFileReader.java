package codetree.common;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedReader;

import codetree.core.Graph;

public class SdfFileReader
{
    
    public static List<Graph> readFile(Path sdfFile)
    {
        ArrayList<Graph> G = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(sdfFile)) {
            Graph g;
            while ((g = next(br)) != null) {//入力ファイルからグラフデータを読み込む
                 g = g.shrink();//GからＨを削除する
                if (g.isConnected()) {//閉グラフのみをGに追加
                    G.add(g);
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        return G;
    }
//読み込んだラインが$$$$ならば次のグラフ
    private static Graph next(BufferedReader sdf) throws Exception 
    {
        ArrayList<String> mol = new ArrayList<>();

        String line;
        while ((line = sdf.readLine()) != null) {
            if (line.startsWith("$$$$") && mol.size() > 0) {//lineの接頭辞がprefixか
                return readMol(mol);
            } else {
                mol.add(line);
            }
        }

        return null;
    }

    private static Graph readMol(List<String> mol)
    {//rim() メソッドは両端の空白を取り除いた文字列を返す
        String line = mol.get(0);//グラフid
        final int id = Integer.parseInt(line.substring(0, 6).trim());

        line = mol.get(3);//頂点数とサイズ
        final int order = Integer.parseInt(line.substring(0, 3).trim());//頂点数を得る
        final int size = Integer.parseInt(line.substring(3, 6).trim());

        byte[] vertices = new byte[order];
        for (int i = 0; i < order; ++i) {
            line = mol.get(4+i);
            final String label = line.substring(31, 33).trim();//元素記号を得る
            vertices[i] = VertexLabel.string2id(label);
        }

        byte[][] edges = new byte[order][order];
        for (int i = 0; i < size; ++i) {
            line = mol.get(4+order+i);
            final int v = Integer.parseInt(line.substring(0, 3).trim()) - 1;
            final int u = Integer.parseInt(line.substring(3, 6).trim()) - 1;
            final int w = Integer.parseInt(line.substring(6, 9).trim());

            edges[v][u] = (byte)w;
            edges[u][v] = (byte)w;
        }

        return new Graph(id, vertices, edges);
    }
}
