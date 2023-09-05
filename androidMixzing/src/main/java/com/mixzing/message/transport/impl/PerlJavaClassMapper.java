package com.mixzing.message.transport.impl;

/**
 * <p>Title: MixMoxie Java Client</p>
 *
 * <p>Description: </p> This is called by the YAML library to find the corresponding java class name
 * on the Client side for perl class names on the server side or vice versa. The assumption is that the object name
 * itself is the same. For example Messages::ClientMessageEnvelope.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: MixMoxie</p>
 *
 * @author G.Miller.
 * @version 1.0
 */
public class PerlJavaClassMapper {

    public static String inputClassName (String outputClassName) {
        // lgr.debug(outputClassName);
        String parts[] = outputClassName.split("::", 2);
        if (parts.length == 2) {
            return JAVA_ROOT + parts[0].toLowerCase() + ".impl." + parts[1];
        }
        return outputClassName;
    }

    public static String outputClassName (String javaClassName) {
        String objectName = javaClassName.substring(javaClassName.lastIndexOf('.') + 1);
        if (javaClassName.startsWith(JAVA_MESSAGES)) {
            return PERL_MESSAGES + objectName;
        } else if (javaClassName.startsWith(JAVA_MESSAGEOBJECT)) {
            return PERL_MESSAGEOBJECT + objectName;
        }
        return javaClassName;
    }
    private static final String JAVA_ROOT = "com.mixzing.message.";
    private static final String JAVA_MESSAGES = JAVA_ROOT + "messages.";
    private static final String JAVA_MESSAGEOBJECT = JAVA_ROOT + "messageobject.";
    private static final String PERL_MESSAGES = "!!perl/hash:Messages::";
    private static final String PERL_MESSAGEOBJECT = "!!perl/hash:MessageObject::";
}
