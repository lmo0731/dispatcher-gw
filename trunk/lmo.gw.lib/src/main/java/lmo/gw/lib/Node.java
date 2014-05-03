/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class Node {

    public static String SPLITTER = ".";
    protected Map<String, Node> childs = new HashMap<String, Node>();
    protected String value = null;

    public String getValue() {
        return value;
    }

    public Node get(String path, List<String> matches) {
        String[] paths = path
                .replaceFirst("^[" + SPLITTER + "]+", "")
                .replaceFirst("[" + SPLITTER + "]+$", "")
                .split("[" + SPLITTER + "]+", 2);
        Node n = childs.get(paths[0]);
        if (n == null) {
            n = childs.get("*");
            if (n != null) {
                matches.add(paths[0]);
            }
        }
        if (n != null) {
            if (paths.length == 2) {
                Node k = n.get(paths[1], matches);
                return k;
            } else {
                return n;
            }
        }
        return null;
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
            childs.put(paths[0], n);
        }
        if (paths.length == 2 && !paths[1].trim().isEmpty()) {
            n.insert(paths[1], value);
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
}
