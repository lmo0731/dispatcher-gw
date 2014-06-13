/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.util.ArrayList;
import java.util.List;
import static lmo.gw.dispatcher.lib.Node.SPLITTER;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class NodeMatch {

    public Node node;
    public List<String> matches;
    public String path;

    public List<NodeMatch> find(String path) {
        List<NodeMatch> matches = new ArrayList<NodeMatch>();
        matches.add(this);
        while (true) {
            String[] paths = path
                    .replaceFirst("^[" + SPLITTER + "]+", "")
                    .replaceFirst("[" + SPLITTER + "]+$", "")
                    .split("[" + SPLITTER + "]+", 2);
            List<NodeMatch> nextMatches = new ArrayList<NodeMatch>();
            for (NodeMatch n : matches) {
                Node n1 = n.node.childs.get(paths[0].trim());
                if (n1 != null) {
                    NodeMatch nm = new NodeMatch();
                    nm.matches = new ArrayList<String>(n.matches);
                    nm.node = n1;
                    nm.path = n.path + Node.SPLITTER + paths[0];
                    nextMatches.add(nm);
                }
                Node n2 = n.node.childs.get("*" + paths[0].trim());
                if (n2 != null) {
                    NodeMatch nm = new NodeMatch();
                    nm.matches = new ArrayList<String>(n.matches);
                    nm.matches.add(paths[0].trim());
                    nm.node = n2;
                    nm.path = n.path + Node.SPLITTER + "*" + paths[0];
                    nextMatches.add(nm);
                }
                Node n3 = n.node.childs.get("*");
                if (n3 != null) {
                    NodeMatch nm = new NodeMatch();
                    nm.matches = new ArrayList<String>(n.matches);
                    nm.matches.add(paths[0].trim());
                    nm.node = n3;
                    nm.path = n.path + Node.SPLITTER + "*";
                    nextMatches.add(nm);
                }
            }
            matches.clear();
            matches.addAll(nextMatches);
            if (paths.length == 1) {
                break;
            } else {
                path = paths[1].trim();
            }
        }
        return matches;
    }

    @Override
    public String toString() {
        return path;
    }
}
