/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson;

import java.io.Serializable;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class ObjectId implements Serializable {

    private String id;

    public ObjectId() {
        this.setId("ffffff"
                + "ffffff"
                + "ffffff"
                + "ffffff");
    }

    public ObjectId(String id) {
        this.setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id.matches("[a-f0-9]{24}")) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ObjectId format not match");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.hashCode() == obj.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
