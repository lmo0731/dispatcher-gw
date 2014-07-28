/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.bson;

import lmo.utils.bson.factory.BSONNumberFactory;
import flexjson.JSONDeserializer;
import flexjson.ObjectFactory;
import java.io.InputStream;
import java.io.Reader;
import java.util.Date;
import java.util.Map;
import lmo.utils.bson.factory.BSONBeanObjectFactory;
import lmo.utils.bson.factory.BSONBinaryFactory;
import lmo.utils.bson.factory.BSONDateFactory;
import lmo.utils.bson.factory.BSONMapFactory;
import lmo.utils.bson.factory.BSONObjectIdFactory;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class BSONDeserializer<T> extends JSONDeserializer<T> {

    public BSONDeserializer() {
        this.use(Date.class, new BSONDateFactory());
        this.use(InputStream.class, new BSONBinaryFactory());
        this.use(ObjectId.class, new BSONObjectIdFactory());
        this.use(Map.class, new BSONMapFactory());
        this.use(Object.class, new BSONBeanObjectFactory());
        this.use(Integer.class, new BSONNumberFactory());
        this.use(Long.class, new BSONNumberFactory());
        this.use(Double.class, new BSONNumberFactory());
        this.use(Byte.class, new BSONNumberFactory());
        this.use(Float.class, new BSONNumberFactory());
        this.use(Short.class, new BSONNumberFactory());
    }

    @Override
    public T deserialize(Reader input) {
        return (T) BSONValidator.validate(super.deserialize(input));
    }

    @Override
    public T deserialize(String input) {
        return (T) BSONValidator.validate(super.deserialize(input));
    }

    @Override
    public T deserialize(Reader input, Class root) {
        return (T) BSONValidator.validate(super.deserialize(input, root));
    }

    @Override
    public T deserialize(Reader input, ObjectFactory factory) {
        return (T) BSONValidator.validate(super.deserialize(input, factory));
    }

    @Override
    public T deserialize(String input, Class root) {
        return (T) BSONValidator.validate(super.deserialize(input, root));
    }

    @Override
    public T deserialize(String input, ObjectFactory factory) {
        return (T) BSONValidator.validate(super.deserialize(input, factory));
    }

    @Override
    public T deserialize(Reader input, String path, Class root) {
        return (T) BSONValidator.validate(super.deserialize(input, path, root));
    }

    @Override
    public T deserialize(Reader input, String path, ObjectFactory factory) {
        return (T) BSONValidator.validate(super.deserialize(input, path, factory));
    }

    @Override
    public T deserialize(String input, String path, Class root) {
        return (T) BSONValidator.validate(super.deserialize(input, path, root));
    }

    @Override
    public T deserialize(String input, String path, ObjectFactory factory) {
        return (T) BSONValidator.validate(super.deserialize(input, path, factory));
    }

    @Override
    public T deserializeInto(Reader input, T target) {
        return (T) BSONValidator.validate(super.deserializeInto(input, target));
    }

    @Override
    public T deserializeInto(String input, T target) {
        return (T) BSONValidator.validate(super.deserializeInto(input, target));
    }

    @Override
    public T deserializeInto(Reader input, String path, T target) {
        return (T) BSONValidator.validate(super.deserializeInto(input, path, target));
    }

    @Override
    public T deserializeInto(String input, String path, T target) {
        return (T) BSONValidator.validate(super.deserializeInto(input, path, target));
    }
}
