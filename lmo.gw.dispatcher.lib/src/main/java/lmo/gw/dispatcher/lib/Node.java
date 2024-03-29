/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lmo.utils.Pair;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class Node {

    public static String SPLITTER = "/";
    protected Map<String, Node> childs = new HashMap<String, Node>();
    protected String value = null;

    public String getValue() {
        return value;
    }

    public Node get(String path, List<String> matches) throws Exception {
        NodeMatch n = new NodeMatch();
        n.matches = new ArrayList<String>();
        n.node = this;
        n.path = "";
        List<NodeMatch> nodeMatches = n.find(path);
        if (nodeMatches.isEmpty()) {
            return null;
        } else if (nodeMatches.size() == 1) {
            NodeMatch m = nodeMatches.get(0);
            matches.addAll(m.matches);
            return m.node;
        } else {
            throw new Exception("Ambiguous for matches " + nodeMatches.toString());
        }
    }

    public void insert(String path, String value) {
        String[] paths = path
                .replaceFirst("^[" + SPLITTER + "]+", "")
                .replaceFirst("[" + SPLITTER + "]+$", "")
                .split("[" + SPLITTER + "]+", 2);
        Node n = childs.get(paths[0]);
        if (n == null) {
            try {
                n = this.getClass().newInstance();
            } catch (Exception ex) {
                n = new Node();
            }
            childs.put(paths[0].trim(), n);
        }
        if (paths.length == 2 && !paths[1].trim().isEmpty()) {
            n.insert(paths[1].trim(), value);
        } else {
            n.value = value;
        }
    }

    public String toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        prefix = prefix.trim();
        if (value != null) {
            sb.append(prefix);
            sb.append(" = ");
            sb.append(value);
            sb.append("\n");
        }
        if (prefix != null && !prefix.isEmpty()) {
            prefix = prefix + SPLITTER;
        } else {
            prefix = "";
        }
        for (Entry<String, Node> n : childs.entrySet()) {
            sb.append(n.getValue().toString(prefix + n.getKey()));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toString("");
    }

    public void toMap(String prefix, Map<String, String> map) {
        prefix = prefix.trim();
        if (value != null) {
            map.put(prefix, value);
        }
        if (prefix != null && !prefix.isEmpty()) {
            prefix = prefix + SPLITTER;
        } else {
            prefix = "";
        }
        for (Entry<String, Node> n : childs.entrySet()) {
            n.getValue().toMap(prefix + n.getKey(), map);
        }
    }
}
