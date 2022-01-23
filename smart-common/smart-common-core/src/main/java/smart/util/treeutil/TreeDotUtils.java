package smart.util.treeutil;


import java.util.ArrayList;
import java.util.List;

/***
 * 操作“树”的工具
 * @ClassName TreeDotUtils
 * @Author xiaowd
 * @DateTime 2020/4/22 9:14
 */
public class TreeDotUtils {

    /**
     * 将List转换为Tree
     */
    public static <T extends SumTree> List<SumTree<T>> convertListToTreeDot(List<T> tList){
        List<SumTree<T>> sumTrees = new ArrayList<>();
        if(tList != null && tList.size() > 0){
            for(T t:tList){
                if(!isTreeDotExist(tList,t.getParentId())){
                    //不存在以父ID为ID的点，说明是当前点是顶级节点
                    SumTree<T> tSumTree = getTreeDotByT(t, tList);
                    sumTrees.add(tSumTree);
                }
            }
        }
        return sumTrees;
    }

    /**
     * 根据ID判断该点是否存在
     * @MethosName isTreeDotExist
     * @Author xiaowd
     * @Date 2020/4/22 9:50
     * @param tList
     * @param id 点ID
     * @return java.lang.Boolean
     */
    private static <T extends SumTree> Boolean isTreeDotExist(List<T> tList, String id) {
        for(T t:tList){
            if(t.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定父点的子树
     * @MethosName getChildTreeList
     * @Author xiaowd
     * @Date 2020/4/22 10:02
     * @param parentTreeDot 父点
     * @param tList
     * @return java.util.List<cn.eshore.common.entity.Tree<T>>
     */
    private static <T extends SumTree> List<SumTree<T>> getChildTreeDotList(SumTree<T> parentTreeDot, List<T> tList){
        List<SumTree<T>> childTreeDotList = new ArrayList<>();
        for(T t:tList){
            if(parentTreeDot.getId().equals(t.getParentId())){
                //如果父ID是传递树点的ID，那么就是传递树点的子点
                SumTree<T> tSumTree = getTreeDotByT(t,tList);
                childTreeDotList.add(tSumTree);
            }
        }
        return childTreeDotList;
    }

    /**
     * 根据实体获取TreeDot
     * @MethosName getTreeDotByT
     * @Author xiaowd
     * @Date 2020/5/4 22:17
     * @param t
     * @param tList
     * @return pri.xiaowd.layui.pojo.TreeDot<T>
     */
    private static <T extends SumTree> SumTree<T> getTreeDotByT(T t,List<T> tList){
        SumTree<T> sumTree = t;
        List<SumTree<T>> children = getChildTreeDotList(sumTree,tList);
        sumTree.setHasChildren(children.size()==0?false:true);
        if(children.size()==0){
            children=null;
        }
        sumTree.setChildren(children);
        return sumTree;
    }

}
