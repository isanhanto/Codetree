package codetree.core;

import java.util.*;

import javax.swing.text.GapContent;

import codetree.common.*;

public class Graph
{
    public final int id;

    public final byte[] vertices;
    public final byte[][] edges;

    public final int[][] adjList;

    public Graph(int id, byte[] vertices, byte[][] edges)
    {
        this.id = id;
        this.vertices = vertices;
        this.edges = edges;

        adjList = makeAdjList();
    }

    private int[][] makeAdjList()
    {
        final int n = order();
        int[][] adjList = new int[n][];

        ArrayList<Integer> adj = new ArrayList<>();//vと隣接する頂点集合
        for (int v = 0; v < n; ++v) {
            for (int u = 0; u < n; ++u) {
                if (edges[v][u] > 0) {
                    adj.add(u);
                }
            }

            final int s = adj.size();

            adjList[v] = new int[s];
            for (int i = 0; i < adj.size(); ++i) {//ｖ行目の隣接行列作成
                adjList[v][i] = adj.get(i);
            }

            adj.clear();

            /*for (int i = 0; i + 1 < s; i++) {
				int min = i;
				for (int j = i + 1; j < s; j++) {
					if (edges[v][adjList[v][min]] > edges[v][adjList[v][j]]
							|| (edges[v][adjList[v][min]] == edges[v][adjList[v][j]] && vertices[adjList[v][min]] > vertices[adjList[v][j]]))
						min = j;
				}
				if (min != i) {
					int temp = adjList[v][i];
					adjList[v][i] = adjList[v][min];
					adjList[v][min] = temp;
				}
			}*/
        }

        return adjList;
    }

    public int order()
    {
        return vertices.length;
    }

    public int size()//グラフの辺の数を返す
    {
        int s = 0;

        final int n = order();
        for (int v = 0; v < n; ++v) {
            for (int u = 0; u < n; ++u) {
                if (edges[v][u] > 0) {
                    ++s;
                }
            }
        }

        return s / 2;
    }

    public int compare(Graph g1, Graph g2){
        return g1.order() < g2.order() ? -1 : 1;
    }

    public Graph shrink()//グラフから頻出のHを削除する
    {
        final byte H = VertexLabel.string2id("H");

        int[] map = new int[order()];
        int order = 0;
        for (int v = 0; v < map.length; ++v) {
            if (vertices[v] != H) {
                map[order++] = v;
            }
        }

        byte[] vertices = new byte[order];
        byte[][] edges = new byte[order][order];

        for (int v = 0; v < order; ++v) {
            vertices[v] = this.vertices[map[v]];

            for (int u = 0; u < order; ++u) {
                edges[v][u] = this.edges[map[v]][map[u]];
            }
        }

        return new Graph(id, vertices, edges);
    }

    public boolean isConnected()//閉グラフであればTRUE
    {
        ArrayDeque<Integer> open = new ArrayDeque<>();
        ArrayList<Integer> closed = new ArrayList<>();

        open.add(0);
        closed.add(0);

        final int n = order();

        while (!open.isEmpty()) {
            int v = open.poll();

            for (int u = 0; u < n; ++u) {
                if (edges[v][u] > 0 && !closed.contains(u)) {//v uが繋がっている＆＆uが探索済みでない 
                    open.add(u);
                    closed.add(u);
                }
            }
        }

        return closed.size() == n;//すべての頂点が探索済みであればTRUE
    }

    public byte getMaxVertexLabel()
    {
        byte max = -1;

        for (int v = 0; v < order(); ++v) {
            if (max < vertices[v]) {
                max = vertices[v];
            }
        }

        return max;
    }

    public byte getMinVertexLabel()
    {
        byte min = Byte.MAX_VALUE;

        for (int v = 0; v < order(); ++v) {
            if (min > vertices[v]) {
                min = vertices[v];
            }
        }

        return min;
    }

    public List<Integer> getVertexList(byte label)//引数のラベルと同じラベルを持つ頂点リストを返す
    {
        ArrayList<Integer> res = new ArrayList<>();

        for (int v = 0; v < order(); ++v) {
            if (vertices[v] == label) {
                res.add(v);
            }
        }

        return res;
    }
}
