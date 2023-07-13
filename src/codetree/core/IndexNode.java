package codetree.core;

import java.util.*;

import codetree.common.Pair;

public class IndexNode
{
    public IndexNode parent;
    public ArrayList<IndexNode> children;
    protected int depth;//add2023y7m13d
    public CodeFragment frag;
    protected ArrayList<Integer> matchGraphIndices;

    //protected int count;

    IndexNode(IndexNode parent, CodeFragment frag)
    {
        this.parent = parent;
        this.frag = frag;

        children = new ArrayList<>();
        matchGraphIndices = new ArrayList<>();
        depth=0;//add2023y7m13d
        //count = 0;
    }

    List<Integer> sizeOnDepth()
    {
        ArrayList<Integer> sizeList = new ArrayList<>();
        sizeList.add(children.size());

        ArrayList<IndexNode> nodeList1;
        ArrayList<IndexNode> nodeList2 = new ArrayList<>();
        nodeList2.addAll(children);

        while (!nodeList2.isEmpty()) {
            nodeList1 = nodeList2;
            nodeList2 = new ArrayList<>();
            int size = 0;
            for (IndexNode m: nodeList1) {
                size += m.children.size();
                nodeList2.addAll(m.children);
            }
            sizeList.add(size);
        }

        return sizeList;
    }

    int size()
    {
        int s = 1;
        for (IndexNode m: children) {
            s += m.size();
        }
        return s;
    }

    void addPath(List<CodeFragment> code, int graphIndex)
    {
        //++count;

        final int height = code.size();
        int dep = -1;
        for(IndexNode a = this;a !=null; a = a.parent){
            dep++;
        }
        depth = dep;

        if (height <= 0) {
            //matchGraphIndices.add(graphIndex);
            return;
        }
        //コード分割
        CodeFragment car = code.get(0);
        List<CodeFragment> cdr = code.subList(1, height);

        for (IndexNode m: children) {
            if (m.frag.equals(car)) {
                m.addPath(cdr, graphIndex);//mから節点を追加
                return;
            }
        }

        IndexNode m = new IndexNode(this, car);
        children.add(m);

        m.addPath(cdr, graphIndex);
    }

    void addId(Graph g, int graphIndex, GraphCode impl){
        List<Pair<IndexNode, SearchInfo>> infoList = impl.beginSearch(g, this);
        for (Pair<IndexNode, SearchInfo> info: infoList) {
            info.left.addId( g, info.right, impl, graphIndex);
            return;
        }
        return;
        
    }

    private void addId(Graph g, SearchInfo info, GraphCode impl, int graphIndex){
        if(depth == g.order()){//ここの条件
            matchGraphIndices.add(graphIndex);
            System.out.println(graphIndex);
            return;
        }
        
        List<Pair<CodeFragment, SearchInfo>> nextFrags = impl.enumerateFollowableFragments(g, info);

        for (IndexNode m: children) {
            //if (m.count > 0) {
                for (Pair<CodeFragment, SearchInfo> frag: nextFrags) {
                    if (frag.left.contains(m.frag)) {
                        m.addId(g, frag.right, impl, graphIndex);
                    }
                }
            //}
        }
    }
    
    List<Integer> search(Graph q, GraphCode impl)
    {
        HashSet<IndexNode> result0 = new HashSet<>();
        //最初の探索頂点に関するinfoListを得て、infoListを用いて検索し、後に結合
        List<Pair<IndexNode, SearchInfo>> infoList = impl.beginSearch(q, this);
        for (Pair<IndexNode, SearchInfo> info: infoList) {
            info.left.search(result0, q, info.right, impl);
        }

        ArrayList<Integer> result = new ArrayList<>();
        for (IndexNode p: result0) {
            result.addAll(p.matchGraphIndices);//解を結合

            //final int c = p.matchGraphIndices.size();
            /*
             * for (; p != null; p = p.parent) {//解を持つ節点に対して解の個数を保存
                p.count += c;
            }
             */
            }

        Collections.sort(result);
        return result;
    }
    //解を持つ節点（＝matchGraphIndicesがnull）を探す
    private void search(Set<IndexNode> result, Graph q, SearchInfo info, GraphCode impl)
    {
        final int c = matchGraphIndices.size();//現在探索している節点（this)が持つ解の個数
        if (c > 0 && !result.contains(this)) {//0個でかつ、thisが答えとなる節点に含まないなら
            result.add(this);//節点thisを解となる節点に含める
            
            //for (IndexNode p = this; p != null; p = p.parent) {//最適化アルゴリズム発動
                //p.count -= c;
            //}
        }

        List<Pair<CodeFragment, SearchInfo>> nextFrags = impl.enumerateFollowableFragments(q, info);

        for (IndexNode m: children) {
            //if (m.count > 0) {
                for (Pair<CodeFragment, SearchInfo> frag: nextFrags) {
                    if (frag.left.contains(m.frag)) {
                        m.search(result, q, frag.right, impl);
                    }
                }
            //}
        }
    }
}
