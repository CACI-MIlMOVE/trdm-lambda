package com.milmove.trdmlambda.milmove.util;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.springframework.stereotype.Component;

import jakarta.xml.soap.SOAPMessage;

@Component
public class MyLogInterceptor extends LoggingOutInterceptor {

    public MyLogInterceptor() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        OutputStream out = message.getContent(OutputStream.class);
        final CacheAndWriteOutputStream apacheOutput = new CacheAndWriteOutputStream(out);
        message.setContent(OutputStream.class, apacheOutput);
        apacheOutput.registerCallback(new LoggingCallback());

        // Apache cxf is supposed to close the stream itself and then write it all
        // try {
        // apacheOutput.close();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    public class LoggingCallback implements CachedOutputStreamCallback {
        public void onFlush(CachedOutputStream cos) {
        }

        public void onClose(CachedOutputStream cos) {
            try (cos) {
                StringBuilder builder = new StringBuilder();
                cos.writeCacheTo(builder); // pull the xml from cache

                String soapXml = builder.toString();
                try (FileWriter fw = new FileWriter("output.xml")) {
                    fw.write(soapXml);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // try {
            // StringBuilder builder = new StringBuilder();
            // cos.writeCacheTo(builder); // pull the xml from cache

            // String soapXml = builder.toString();
            // try (FileWriter fw = new FileWriter("output.xml")) {
            // fw.write(soapXml);
            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
        }
    }
}
