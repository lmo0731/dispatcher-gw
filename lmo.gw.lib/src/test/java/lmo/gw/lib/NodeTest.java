 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

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
        root.insert("a.b.c", "d");
        root.insert("a.b", "e");
        root.insert("a.b.e", "f");
        root.insert("a.b.e", "f");
        root.insert("a.b.e", "f");
        root.insert("a.b.e", "s");
        root.insert("c.*.d.*.e.*.*", "s");
        System.out.println(root.toString());
        System.out.println(root.get("c.abc.d.1.e.asdadsa.12"));
        System.out.println(root.getMatches());
    }
}
