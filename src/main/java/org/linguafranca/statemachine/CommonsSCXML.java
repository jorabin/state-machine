package org.linguafranca.statemachine;

import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.env.SimpleDispatcher;
import org.apache.commons.scxml2.env.Tracer;
import org.apache.commons.scxml2.env.jexl.JexlEvaluator;
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.SCXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Objects;

/**
 * Review commons SCXML recompiled locally: <a href="https://github.com/jorabin/commons-scxml">GitHub</a>
 */
public class CommonsSCXML {
    static Logger logger = LoggerFactory.getLogger(CommonsSCXML.class);

    public static void main(String[] args) throws ModelException, XMLStreamException, IOException {
        logger.info("Starting machine");
        SCXML scxml = SCXMLReader.read(Objects.requireNonNull(CommonsSCXML.class.getClassLoader().getResourceAsStream("hello-world.xml")));
        final Tracer tracer = new Tracer();
        final SCXMLExecutor exec = new SCXMLExecutor(new JexlEvaluator(), new SimpleDispatcher(), tracer);
        exec.setStateMachine(scxml);
        exec.addListener(scxml, tracer);
        exec.go();
        logger.info("Machine finished");
    }
}
