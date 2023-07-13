package codetree.vertexBased;

import codetree.core.*;
//各グラフコードにおける探索済み・未探索頂点集合を保持
final class AcgmSearchInfo implements SearchInfo
{
    boolean[] open;
    boolean[] closed;

    int[] vertexIDs;//確定された頂点を順に格納

    AcgmSearchInfo(Graph g, int v0)
    {
        final int n = g.order();

        open = new boolean[n];
        int[] adj = g.adjList[v0];
        for (int u: adj) {
            open[u] = true;
        }

        closed = new boolean[n];
        closed[v0] = true;

        vertexIDs = new int[1];
        vertexIDs[0] = v0;
    }

    AcgmSearchInfo(AcgmSearchInfo src, Graph g, Integer v)
    {
        open = src.open.clone();
        closed = src.closed.clone();

        final int n = src.vertexIDs.length;
        vertexIDs = new int[n+1];
        System.arraycopy(src.vertexIDs, 0, vertexIDs, 0, n);
        vertexIDs[n] = v;

        open[v] = false;
        closed[v] = true;

        int[] adj = g.adjList[v];
        for (int u: adj) {
            if (!closed[u]) {
                open[u] = true;
            }
        }
    }
}
