/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryToAST.app.Graph;



import com.queryToAST.app.Setting;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.languages.Languages;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**
 *
 * @author Niriel
 */
public class JarGraph {
    private Setting _settings = null;
    
    public JarGraph(String _internalName, boolean ast, boolean mData) {        
        _settings = new Setting(_internalName,null);
        _settings.setAst(ast);
        _settings.setMetadata(mData);
    }
    
    public JarGraph(String _internalName, String _output, boolean ast, boolean mData) {
        _settings = new Setting(_internalName,_output);
        _settings.setAst(ast);
        _settings.setMetadata(mData);
    }
    
    public Graph Factory() throws IOException {
        Graph graph = new TinkerGraph();
        Vertex j = graph.addVertex(null);
        j.setProperty("Name",getNameJar());
        j.setProperty("Typ","Jar");
                
        DecompilerSettings settings = DecompilerSettings.javaDefaults();
        settings.setLanguage(Languages.bytecode());
        
        final File jarFile = new File(this._settings.getInternalName());

        if (!jarFile.exists()) {
            System.out.println("File not found: " + this._settings.getInternalName());
        }
        final JarFile jar = new JarFile(jarFile);
        final Enumeration<JarEntry> entries = jar.entries();
        
        settings.setShowSyntheticMembers(false);

        settings.setTypeLoader(           
                new JarTypeLoader(jar)                            
        );
        
        this._settings.setSettings(settings);
        
        try {                        
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String name = entry.getName();

                if (!name.endsWith(".class")) {
                    continue;
                }                
                final String internalName = StringUtilities.removeRight(name, ".class");
                
                this._settings.setInternalName(internalName);
                
                //decompilace->ziskani metadat->graf t��dy (AST or BCEL)
                ClassVertex classVertex = new ClassVertex(_settings);
                Vertex a = null;
                if(this._settings.isMetadata())                    
                    a = classVertex.getVertexMeta(graph);
                if(this._settings.isAst())
                    a = classVertex.getVertexMeta(graph);
                //zapis vztahu tridy do Grafu
                graph.addEdge(null, setPathPackages(graph, j, internalName), a, "contain");
            }
        }
        finally {
         System.out.println("Doplnit hlaseni p�ekladu JarGraph");
        }
        
//  Vypis vsech vrcholu        
//        for(Vertex v: graph.getVertices()) {
//            System.out.println(v.getProperty("Name"));
//        }

//  Vypis trid druh� urovne        
//        GremlinPipeline<Vertex,Vertex> pipe = new GremlinPipeline();       
//        
//        for(Vertex v :pipe.start(j).out("contain").toList()) {         
//            GremlinPipeline<Vertex,Vertex> pipe2 = new GremlinPipeline();
//            for(Vertex v2:pipe2.start(v).out("contain").toList()) {
//               System.out.println(v2.getProperty("Name").toString().replaceFirst("[^\\.]*\\.", ""));
//            }
//        }
        testGraph();
        return null;
    }
    
    private String getNameJar() {
        Pattern reg = Pattern.compile("([^\\\\\\./]*).jar$");
        Matcher m = reg.matcher(this._settings.getInternalName());
        m.find();
        return m.group(1);
    }
    
    /**
     * Vr�t� Vrchol na kter� se nov� t��da p�ipoj�
     * @param g
     * @param J
     * @param internalName
     * @return
     */
    public Vertex setPathPackages(Graph g, Vertex j, String internalName){
        if(internalName.contains("/")){            
            String NamePack = internalName.replaceFirst("/.*", "");             
            internalName = internalName.replaceFirst("[^/]*/", "");
            GremlinPipeline<Vertex,Vertex> pipe = new GremlinPipeline();
            for(Vertex  v : pipe.start(j).out("contain").toList()) {
                if(v.getProperty("Name").equals(NamePack)){                    
                     return setPathPackages(g, v, internalName);                     
                 }
            }
            System.out.println(":D");
            Vertex r = g.addVertex(null);
            r.setProperty("Name", NamePack);
            r.setProperty("Typ", "Package");
            g.addEdge(null, j, r, "contain");
            return setPathPackages(g, r, internalName);
        }
        return j;
    }
    
    private Graph testGraph() {
        Graph g = new TinkerGraph();
        
        Vertex a = g.addVertex(null);
        a.setProperty("Name","Procyon");
        a.setProperty("Typ","Jar");
        
            Vertex b = g.addVertex(null);
            b.setProperty("Name","CompileTools");
            b.setProperty("Typ","Package");
            Edge e = g.addEdge(null, a, b, "contain");
            
                Vertex c = g.addVertex(null);
                c.setProperty("Name", "Decompile");
                c.setProperty("Typ", "Class");
                e = g.addEdge(null, b, c, "contain");
                
                c = g.addVertex(null);
                c.setProperty("Name", "Asemble");
                c.setProperty("Typ", "Class");
                e = g.addEdge(null, b, c, "contain");
                
            b = g.addVertex(null);
            b.setProperty("Name","Decompile");
            b.setProperty("Typ","Package");
            e = g.addEdge(null, a, b, "contain");
                
                c = g.addVertex(null);
                c.setProperty("Name", "DecompileDriver");
                c.setProperty("Typ", "Class");
                e = g.addEdge(null, b, c, "contain");
                
                c = g.addVertex(null);
                c.setProperty("Name", "ParserMetadata");
                c.setProperty("Typ", "Class");
                e = g.addEdge(null, b, c, "contain");
        GremlinPipeline<Vertex,Vertex> pipe = new GremlinPipeline();
        
//        for(Vertex v :pipe.start(a).out("contain").toList()) {
//            System.out.println(v.getProperty("Name"));
//        }
        
        
        return g;
    }
}