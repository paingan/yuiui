/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.paingan.yuiui;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.yahoo.platform.yui.compressor.CssCompressor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
 
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 *
 * @author paulusyansen
 */
public class YUICompressor {
    private static Logger logger = Logger.getLogger(YUICompressor.class.getName());
    
   /**
     * 
     * @param inputFilename
     * @param outputFilename
     * @param o
     * @throws IOException 
     */
    public static void compressJavaScript(String inputFilename, String outputFilename, Options o) throws IOException {
        Reader in = null;
        Writer out = null;
        try {
            in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);

            JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
            in.close(); in = null;

            out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
            compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    
    /**
     * 
     * @param inputFilename
     * @param outputFilename
     * @param o
     * @throws IOException 
     */
    public static void compressCSS(String inputFilename, String outputFilename, Options o) throws IOException {
        Reader in = null;
        Writer out = null;
        try {
            in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);

            CssCompressor compressor = new CssCompressor(in);
            in.close(); in = null;

            out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
            compressor.compress(out, o.lineBreakPos);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    
    /**
     * 
     * @param inputFilename
     * @param outputFilename
     * @param o
     * @throws IOException 
     */
    public static void compressHTML(String inputFilename, String outputFilename, Options o) throws IOException {
        Reader in = null;
        Writer out = null;
        StringBuilder builder = new StringBuilder();
        try {
            in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);

            HtmlCompressor compressor =new HtmlCompressor();
            compressor.setCompressJavaScript(true);
            compressor.setCompressCss(true);
            
            
            char[] buffer = new char[8192];
            int read;
            while ((read = in.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            
            String html = builder.toString();
            
            if(html != null && !"".equals(html)) {
                out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
                html = compressor.compress(html);
                out.write(html);
            } 
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}

class YuiCompressorErrorReporter implements ErrorReporter {
    private static Logger logger = Logger.getLogger(YuiCompressorErrorReporter.class.getName());
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (line < 0) {
            logger.log(Level.WARNING, message);
        } else {
            logger.log(Level.WARNING, line + ':' + lineOffset + ':' + message);
        }
    }
 
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (line < 0) {
            logger.log(Level.SEVERE, message);
        } else {
            logger.log(Level.SEVERE, line + ':' + lineOffset + ':' + message);
        }
    }
 
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        return new EvaluatorException(message);
    }
}
