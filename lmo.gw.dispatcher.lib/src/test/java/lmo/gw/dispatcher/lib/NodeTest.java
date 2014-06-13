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

    public void testSomeMethod() throws Exception {
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
        root.insert("/example/*/url/*1/", "Example1");
        root.insert("/example/*/url/*/", "Example2");
        root.insert("/*hitone/v1/check", "hitone.check");
        root.insert("/hitone/v1/*on", "hitone.on");
        System.out.println(root.toString());
        LinkedList<String> matches = new LinkedList<String>();
        matches.clear();
        System.out.println(root.get("example/1/url/2", matches));
        System.out.println(matches);
        matches.clear();
        System.out.println(root.get("hitone/v1/on", matches));
        System.out.println(matches);
        root.insert("/*hitone/v1/*", "hitone.all"); //ambigous state
        matches.clear();
        try {
            System.out.println(root.get("hitone/v1/check", matches));
            assert (false);
        } catch (Exception ex) {
            assert (true);
        }
        System.out.println(matches);
        Map<String, String> map = new HashMap<String, String>();
        root.toMap("resource", map);
        System.out.println(map);
    }
}
