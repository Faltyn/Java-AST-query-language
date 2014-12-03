/*
 * Dodelat pridav�n� Argumentu p�i vol�n� funkc� TODO _arguments
 * 
 */
package com.queryToAST.app.Core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Niriel
 */
public class Data {

    private boolean _isFinal = false;
    private boolean _isStatic = false;
    private boolean _isSuper = false;
    private boolean _isabstract = false;
    private boolean _isSynchronized = false;    // variable is Synchronized
    private boolean _isVolatile = false;        // variable is Volatile
    private TypFile _typFile = null;            // typ Class | Enum | Annotation | ...
    private String _name = null;                // name class | method | ...
    private String _retVal = null;              //return
    private String _extends = null;             //Abstract
    private List<String> _implements = null;    //Interface
    private TypModifier _typMod = null;         // public | private | protected
    private List<Arg> _arguments = null;        // Arg {[Name , value], ...}
    
    /**
     * Constructor for file
     * @param name
     * @param typFile
     */
    public Data(String name, TypFile typFile){
        _name = name;
        _typFile = typFile;
    }

    /**
     * Constructor for Mehtod
     * @param isStatic
     * @param isFinal
     * @param retVal
     * @param name
     */
    public Data(boolean isStatic, boolean isFinal, String retVal, String name) {        
        _isStatic = isStatic;
        _isFinal = isFinal;
        _retVal = retVal;
        _name = name;        
    }

    public void setRetVal(String _retVal) {
        this._retVal = _retVal;
    }

    public String getRetVal() {
        return _retVal;
    }
    
    public String getName() {
        return _name;
    }

    public String getExtends() {
        return _extends;
    }

    public void setExtends(String _extends) {
        this._extends = _extends;
    }

    public List<String> getImplements() {
        return _implements;
    }

    public void setImplements(String _implement) {
        if(this._implements == null)
            this._implements = new ArrayList<String>();
        this._implements.add(_implement);
    }
    
    /**
     * For Class file
     * @param _datatype
     * @param _name
     */
    public void addArg(String _datatype, String _name){
        Arg arg = new Arg();
        arg._datatyp =_datatype;
        arg._name = _name;
        
        if(_arguments == null)
            _arguments = new ArrayList<Arg>();
        
        _arguments.add(arg);
    }

    public void setTypFile(TypFile _typFile) {
        this._typFile = _typFile;
    }
    
    
    
    public Iterator<Arg> getArg(){
        return _arguments.iterator();
    }
    
    public TypModifier getTypMod() {
        return _typMod;
    }
    
    public boolean isIsFinal() {
        return _isFinal;
    }

    public void setIsFinal(boolean _isFinal) {
        this._isFinal = _isFinal;
    }

    public boolean isIsStatic() {
        return _isStatic;
    }

    public void setIsStatic(boolean _isStatic) {
        this._isStatic = _isStatic;
    }

    public boolean isIsabstract() {
        return _isabstract;
    }

    public void setIsabstract(boolean _isabstract) {
        this._isabstract = _isabstract;
    }

    public boolean isIsSynchronized() {
        return _isSynchronized;
    }

    public void setIsSynchronized(boolean _isSynchronized) {
        this._isSynchronized = _isSynchronized;
    }

    public boolean isIsVolatile() {
        return _isVolatile;
    }

    public void setIsVolatile(boolean _isVolatile) {
        this._isVolatile = _isVolatile;
    }
    
    public static class Arg {
        private String _datatyp;
        private String _name;
    }
    
    public void setTypMod(TypModifier _typMod) {
        this._typMod = _typMod;
    }

    public boolean isIsSuper() {
        return _isSuper;
    }

    public void setIsSuper(boolean _isSuper) {
        this._isSuper = _isSuper;
    }
    
    public TypFile getTypFile() {
        return _typFile;
    }

    @Override
    public String toString() {
        String out = "";
        if(_typMod != null)
            switch(_typMod) {
                case PUBLIC:
                    out += "public ";
                    break;
                case PRIVATE:
                    out += "private ";
                    break;
                case PROTECTED:
                    out += "protected ";
                    break;
        }
        
        out += _isFinal ? "final " : "";
        out += _isStatic ? "static " : "";
        out += _isabstract ? "abstract " : "";
        
        switch(_typFile) {
            case CLASS:
                out += "class ";
            case METODA:
                out += "";
                break;
            case ANNOTATION:
                out += "@interface ";
            case ENUMERATION:
                out += "enum ";
            case INTERFACE:
                out += "interface ";
        }
        out += _retVal != null ? _retVal + " ": "";
        out += _name;
        
        if(this._extends != null)
            out += " extends " + _extends;
        
        if(this._implements != null){
            out += " implements";
            for(String s : this._implements)
                out += " " + s;
        }        
        return out;
    }
    
}