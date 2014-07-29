/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.jaxb;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class XmlValidator {

    public static <T> void validateRequired(T target, Class targetClass)
            throws JAXBException {
        StringBuilder errors = new StringBuilder();
        Field[] fields = targetClass.getFields();
        for (Field field : fields) {
            XmlElement annotation = field.getAnnotation(XmlElement.class);
            if (annotation != null && annotation.required()) {
                field.setAccessible(true);
                try {
                    if (field.get(target) == null) {
                        throw new JAXBException("Required field is missing. '" + field.getName() + "'");
                    }
                } catch (IllegalArgumentException ex) {
                } catch (IllegalAccessException ex) {
                }
            }
        }
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            XmlElement annotation = method.getAnnotation(XmlElement.class);
            if (annotation != null && annotation.required()) {
                String name = validateGetPOJO(method);
                if (name != null) {
                    try {
                        Object o = method.invoke(target);
                        if (o == null) {
                            throw new JAXBException("Required field is missing. '" + name + "'");
                        }
                    } catch (IllegalAccessException ex) {
                    } catch (IllegalArgumentException ex) {
                    } catch (InvocationTargetException ex) {
                    }
                }
            }
        }
        if (errors.length() != 0) {
            throw new JAXBException(errors.toString());
        }
    }

    private static String validateGetPOJO(Method method) {
        if (method.getParameterTypes().length == 0
                && !method.getReturnType().equals(Void.TYPE)
                && method.getName().startsWith("get")
                && method.getName().length() > 3
                && (method.getModifiers() & Modifier.PUBLIC) > 0
                && (method.getModifiers() & Modifier.STATIC) == 0) {
            return ("" + method.getName().charAt(3)).toLowerCase()
                    + method.getName().substring(4);
        }
        return null;
    }

    public static void main(String... args) throws JAXBException {
        XmlObject o = new XmlObject();
        o.setArgument(1);
        XmlValidator.validateRequired(o, o.getClass());
    }
}

@XmlRootElement(name = "xmlObject")
class XmlObject {

    private Integer argument;
    public String element;

    @XmlElement(required = true)
    public Integer getArgument() {
        return argument;
    }

    @XmlElement(required = true)
    public void setArgument(Integer argument) {
        this.argument = argument;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }
}
