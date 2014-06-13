 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class NodeTest extends TestCase {

    public NodeTest(String testName) {
        super(testName);
    }

    public void testSomeMethod() {
        Node root = new Node();
        Node.SPLITTER = "/";
        root.insert("/", "k");
        root.insert("a/b/c", "d");
        root.insert("a/b", "e");
        root.insert("a/b/e", "f");
        root.insert("a/b/e", "f");
        root.insert("a/b/e", "f");
        root.insert("a/b/e", "s");
        root.insert("c/*/d/*/e/*/*", "s");
        root.insert("/example/*/url/*/", "Example");
        System.out.println(root.toString());
        LinkedList<String> matches = new LinkedList<String>();
        System.out.println(root.get("example/1/url/2", matches));
        System.out.println(matches);
        Map<String, String> map = new HashMap<String, String>();
        root.toMap("resource", map);
        System.out.println(map);
    }
}
