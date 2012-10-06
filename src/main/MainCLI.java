/**
 * Copyright (c) 2010-2012 Miko Kinski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
 * of the Software, and to permit persons to whom the Software is furnished to do 
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package main;

import java.io.File;
import java.io.IOException;
import java.lang.ClassLoader;
import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;

import org.eclipse.emf.mwe2.launch.runtime.Mwe2Launcher;

public class MainCLI {
    
    private static final String defaultModelDir     = "/model/objectdictionary";

    private static final String defaultModel        = "ds404.xml";
    private static final String defaultMetaModel    = "canopen.ecore";
    private static final String defaultTemplate     = "ObjectDictionaryC";
    private static final String defaultFileEncoding = "UTF-8";

    private static void printUsage() {
        System.err.println("Syntax: [-b <baseDir>] [-m <model>] [-c <metamodel>] [-e <encoding>] [-h] [-v]");
        System.err.println("Options:");
        System.err.println("  -b, --baseDir     project dir: /home/...");
        System.err.println("  -m, --model       default is: ".concat(defaultModel));
        System.err.println("  -c, --metamodel   default is: ".concat(defaultMetaModel));
//        System.err.println("  -s, --srcpath     default is: ".concat(defaultSrcPath));
        System.err.println("  -e, --encoding    default is: ".concat(defaultFileEncoding));
        System.err.println("  -h, --help        this help text");
        System.err.println("  -v, --verbose     verbose mode");
    }

    private static String checkFile(final String basedir, final String file, final boolean fail) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append(basedir).append('/').append(file);
        String sbnew = sb.toString().replace('\\','/');
        System.out.println(sb);
        final File f = new File(sb.toString()).getCanonicalFile();
        final String absolutePath = f.getAbsolutePath();
        if (f.isFile()) return sbnew;
        if (!fail) return null;
        throw new IOException(String.format("Failed to find or locate directory %s", absolutePath));
    }
    
    private static final LongOpt[] longopts = new LongOpt[] {
        new LongOpt("baseDir",   LongOpt.REQUIRED_ARGUMENT, new StringBuffer(), 'b'),
        new LongOpt("model",     LongOpt.REQUIRED_ARGUMENT, new StringBuffer(), 'm'),
        new LongOpt("metamodel", LongOpt.REQUIRED_ARGUMENT, new StringBuffer(), 'c'),
        new LongOpt("encoding",  LongOpt.REQUIRED_ARGUMENT, new StringBuffer(), 'e'),
        new LongOpt("help",      LongOpt.NO_ARGUMENT,       null,               'h'),
        new LongOpt("verbose",   LongOpt.NO_ARGUMENT,       null,               'v'),
    };

    
    public static void main(final String[] args) {
        
        System.out.println("                  ************ STANDALONE RUNNING GENERATOR MACHINE *********** ");
        System.out.println("                  * author: Miko Kinski                       ***************** ");
        System.out.println("                  * date: 10.03.2011                          ***************** ");
        System.out.println("                  ************************************************************* ");
        

        boolean windows = true;
        
        final String cwd    = System.getProperty("user.dir");
        String baseDir      = cwd;
        String modelDir     = defaultModelDir;
        String model        = defaultModel;
        String metamodel    = defaultMetaModel;
        String template     = defaultTemplate;
        String encoding     = defaultFileEncoding;
        boolean verbose     = false;
        final Getopt g = new Getopt("workflow.Workflow", args, "b:m:c:s:e:hv;", longopts);
        g.setOpterr(false); // We'll do our own error handling
        int c;
        while ((c = g.getopt()) != -1)
            switch (c) {
                case 'b':
                    baseDir     = g.getOptarg();
                    break;
                case 'm':
                    model       = g.getOptarg();
                    break;
                case 'c':
                    metamodel   = g.getOptarg();
                    break;
                case 's':
//                    srcpath     = g.getOptarg();
                    break;
                case 'e':
                    encoding    = g.getOptarg();
                    break;
                case 'h':
                    printUsage();
                    System.exit(0);
                    break;
                case 'v':
                    verbose = true;
                    break;
                case '?':
                default:
                    System.out.println("The option '" + (char) g.getOptopt() + "' is not valid");
                    printUsage();
                    System.exit(1);
                    break;
            }
        
        if (verbose)
        {
            
        }
        
        if(metamodel.equals("canopen.ecore"))
        {
            System.out.println("Choosen objectdictionary.");
            modelDir = "";
            template = "ObjectDictionaryC";
//            srcpath  = "/od";
        }
        
        System.out.println(baseDir);

        if(baseDir.substring(0).equals("/"))
        {
            windows = false;
        }
        
        boolean test        = false;
        String pfadmodel    = "";
        String pfadmeta     = "";
        
        try 
        {
            pfadmodel   = checkFile(baseDir.concat(modelDir), model, test);
        } catch(IOException e)
        {
            System.out.println("Oh oh!");
        }

        pfadmeta = ClassLoader.getSystemResource("metamodel/canopen.ecore").toString();
        
        if (pfadmodel == null)
        {
            System.out.println("Basedir is:".concat(baseDir));
            System.out.println("Path of metamodel ".concat(pfadmeta));
            System.out.println("Wrong Arguments!!!");
            printUsage();
        } 
        else 
        {
            System.out.println("Basedir is:".concat(baseDir));
            System.out.println("Path of model ".concat(pfadmodel));
            System.out.println("Path of metamodel ".concat(pfadmeta));
            
            String dest = "";
            
            if (windows)
            {
                dest = "file://";
            }
            
            Mwe2Launcher.main(
                    new String[] {
                            "workflow.Workflow",
                            "-p", "model=".concat(dest).concat(pfadmodel),
                            "-p", "metamodel=".concat(pfadmeta),
                            "-p", "fileEncoding=".concat(encoding),
                            "-p", "template=templates::".concat(template).concat("::Root FOR model"),
                            "-p", "srcpath=".concat(baseDir),
                    });
        }
    }
}
