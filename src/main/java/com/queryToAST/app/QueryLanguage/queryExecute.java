/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryToAST.app.QueryLanguage;

import com.google.common.collect.Lists;
import com.queryToAST.app.Graph.GraphContext;
import com.queryToAST.app.Graph.Vertex.AnnParaEntity;
import com.queryToAST.app.Graph.Vertex.AnnotatedEntity;
import com.queryToAST.app.Graph.Vertex.ClassEntity;
import com.queryToAST.app.Graph.Vertex.MethParaEntity;
import com.queryToAST.app.Graph.Vertex.MethodEntity;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author Niriel
 */
public class queryExecute extends queryBaseListener{
    
    // <editor-fold defaultstate="collapsed" desc=" Property ">
    private GraphContext _graphContext = null;
    private List<LangContext> _langContext = new ArrayList();
    private int _depth = 0;
    private List<AnnParaEntity> _annotatedRight;
    private List<AnnParaEntity> _annotatedLeft;
    private int _index;
    private List<MethodEntity> _method = null;
    private List<ClassEntity> _result = new ArrayList();
    private List<ErrorMessage> _errMsg = new ArrayList();
    private boolean _error = false;
    private String _as = null;
    private String _alias = null;
    private boolean _isRight = false;    
// </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Get-Set ">
    public void printErr() {
        for (ErrorMessage em : _errMsg) {
            System.out.println(em);
        }
    }
    
    public GraphContext getContext() {
        return _graphContext;
    }
    
    public void setContext(GraphContext context) {
        this._graphContext = context;
    }    
    
    public List<ClassEntity> getResult() {
        return _result;
    }

// </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Every terminal ">

    @Override
    public void visitErrorNode(ErrorNode node) {
        _error =true;
        _errMsg.add(new ErrorMessage("Chyba v lexik�ln� alal�ze nebo v gramatice.", _error));
        super.visitErrorNode(node); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx); //To change body of generated methods, choose Tools | Templates.
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InnerSelect ">
    @Override
    public void exitInnerSelect(queryParser.InnerSelectContext ctx) {        
        _depth--;
    }
    
    @Override
    public void enterInnerSelect(queryParser.InnerSelectContext ctx) {
        _depth++;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Annotated ">
    @Override
    public void exitAnnotated(queryParser.AnnotatedContext ctx) {
        if (_error) {
            return;
        }
                       
        queryParser.AnnotatedStatmentContext as = ctx.annotatedStatment();
        List<ClassEntity> tmp = new ArrayList();
        List<ClassEntity> alTmp = null;
        List<AnnParaEntity> anpTmp= new ArrayList();
        
        // <editor-fold defaultstate="collapsed" desc=" IF alias ">            
        if (_alias != null) {
            boolean isTrue = false;
            for (int i = _depth; i >= 0; i--) {
                if (_langContext.get(i).mapAS.containsKey(_alias)) {
                    alTmp = _langContext.get(i).mapAS.get(_alias);
                    isTrue = true;
                    break;
                }
            }
            if (!isTrue) {
                _error = true;
                _errMsg.add(new ErrorMessage("Neexistuj�c� alias: " + _alias, isTrue));
                return;
            }
        } else {
            alTmp = _langContext.get(_index).result;
        }
        // </editor-fold>                                                
        
        String name = as.annotatedName().NAME().getText();        
        String strTMP = null;
        String paramsTMP = null;
        String[] paramTMP = null;
        String method = null;
        
        if (ctx.MM() != null) {
            strTMP = ctx.method().STRING(0).getText().replaceAll("[' ]", "");
            paramsTMP = strTMP.replaceFirst("^[\\w<>]*", "").replaceAll("[)(]", "");            
            
            if (paramsTMP.compareTo("") != 0) {
                paramTMP = paramsTMP.split(",");
            }
            method = strTMP.replaceFirst("\\(.*", "");
            
            //Dodelat!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        } else {
            for (ClassEntity ce : alTmp) {                
                boolean isAE = true;
                boolean isAnnotated = true;
                AnnParaEntity ann = null;
                AnnotatedEntity ar = null;
                
                ar = ce.getAnnotatedRelated(name);
                
                if (ar != null) //@NAME
                {
                    for (int i = 0; i < as.annotatedParams().size(); i++) {
                        queryParser.AnnotatedParamsContext apc = as.annotatedParams().get(i);
                        if (apc.NAME() != null) {
                            if (apc.index() != null) //@NAME.NAME INDEX
                            {                                
                                if (isAE) {                                    
                                    ann = ar.getAnnParaRelated(apc.NAME().getText());                                    
                                    if (ann != null) {
                                        ann = ann.getIndexRelated(Integer.parseInt(apc.index().INT().getText()));
                                        if (ann == null) {
                                            isAnnotated = false;
                                            break;
                                        }
                                    } else {
                                        isAnnotated = false;
                                        break;
                                    }
                                    isAE = false;
                                } else {
                                    ann = ann.getAnnParaRelated(apc.NAME().getText());
                                    if (ann != null) {
                                        ann = ann.getIndexRelated(Integer.parseInt(apc.index().INT().getText()));
                                        if (ann == null) {
                                            isAnnotated = false;
                                            break;
                                        }                                        
                                    } else {
                                        isAnnotated = false;
                                        break;
                                    }
                                    
                                }                                
                            } else //@NAME.NAME
                            {
                                if (isAE) {
                                    ann = ar.getAnnParaRelated(apc.NAME().getText());
                                    if (ann == null) {
                                        isAnnotated = false;
                                        break;
                                    }
                                    isAE = false;
                                } else {
                                    ann = ann.getAnnParaRelated(apc.NAME().getText());
                                    if (ann == null) {
                                        isAnnotated = false;
                                        break;
                                    }
                                }                                
                            }
                        } else if (apc.annotatedName() != null)//@NAME.NAME.@NAME
                        {
                            if (apc.annotatedName().NAME().getText().compareTo(ann.getName()) == 0) {                                
                            } else {
                                isAnnotated = false;
                                break;
                            }
                        }
                    }
                } else {                    
                    continue;
                }
                
                if (isAnnotated) {
                    anpTmp.add(ann);
                    tmp.add(ce);
                }
            }
        }
        
        
        if(!_isRight) {
            _annotatedLeft = anpTmp;
            alTmp.clear();
            alTmp.addAll(tmp);
            
        }
        else
        {
          _annotatedRight = anpTmp;  
        }
       
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" as, INDEX and alias ">

    @Override
    public void exitIndex(queryParser.IndexContext ctx) {
        _index = Integer.parseInt(ctx.INT().getText());
    }
    
    @Override
    public void exitAs(queryParser.AsContext ctx) {
        if (_error) {
            return;
        }
        
        if (ctx.NAME(0).getText().compareToIgnoreCase("as") == 0) {
            _as = ctx.NAME(1).getText();
        } else {
            _errMsg.add(new ErrorMessage("Je o�ek�v�n token AS p�i�lo: " + ctx.NAME(0).getText(), true));
            _error = true;
        }
    }

    @Override
    public void exitAlias(queryParser.AliasContext ctx) {
        _alias = ctx.NAME().getText();
    }

// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" RightStatment ">
    @Override
    public void enterRightStatment(queryParser.RightStatmentContext ctx) {
        _alias = null;
        _isRight = true;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Equal ">
    @Override
    public void exitEqual(queryParser.EqualContext ctx) {
        if (_error) return;
        
        if (ctx.OPERATORS() != null && ctx.OPERATORS().getText().compareToIgnoreCase("in") == 0)
        {
            _error=true;
            _errMsg.add(new ErrorMessage("Neplatn� oper�tor: " + ctx.OPERATORS().getText(), _error));
            return;
        }
        
        List<ClassEntity> tmp = new ArrayList();
        List<ClassEntity> alTmp = null;
                             
        // <editor-fold defaultstate="collapsed" desc=" IF alias ">
            alTmp = null;
            if (ctx.alias() != null) {
                boolean isTrue = false;
                for (int i = _depth; i >= 0; i--) {
                    if (_langContext.get(i).mapAS.containsKey(ctx.alias().NAME().getText())) {
                        alTmp = _langContext.get(i).mapAS.get(ctx.alias().NAME().getText());
                        isTrue = true;
                        break;
                    }
                }
                if (!isTrue) {
                    _error = true;
                    _errMsg.add(new ErrorMessage("Neexistuj�c� alias: " + ctx.alias().NAME().getText(), isTrue));
                    return;
                }
            } else {
                alTmp = _langContext.get(_index).result;
            }

            // </editor-fold>                        
        if(ctx.NAME() != null)  //alias? NAME OPERATORS rightStatment
        {   
            if (ctx.rightStatment().STRING() != null) // (as.)? NAME = 'String' (M:1)
            {
                // <editor-fold defaultstate="collapsed" desc=" as? name = 'String' ">
                String str = ctx.rightStatment().STRING().getText().replaceAll("'", "");                
                switch (ctx.NAME().getText().toLowerCase()) {
                    case "name":
                        for (ClassEntity ce : alTmp) {                            
                            if (ce.getName().compareTo(str) == 0) {
                                if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                    tmp.add(ce);
                                }
                            } else {
                                if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                    tmp.add(ce);
                                }
                            }
                            
                            if (ce.getName().contains(str)) {
                                if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                    tmp.add(ce);
                                }
                            }                            
                        }
                        break;
                    case "fqn":
                        for (ClassEntity ce : alTmp) {                            
                            if (ce.getFQN().compareTo(str) == 0) {
                                if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                    tmp.add(ce);
                                }
                            } else {
                                if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                    tmp.add(ce);
                                }
                            }
                            
                            if (ce.getFQN().contains(str)) {
                                if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                    tmp.add(ce);
                                }
                            }                            
                        }
                        break;
                    default:
                        _error = true;
                        _errMsg.add(new ErrorMessage("Neplatn� slovo :" + ctx.NAME().getText(), _error));
                        return;                    
                }

// </editor-fold>
                
            } else if (ctx.rightStatment().NAME() != null) // (as.)? NAME = (AS.)? NAME (M:N)
            {
                // <editor-fold defaultstate="collapsed" desc=" as? NAME = as? NAME ">
                List<ClassEntity> ceTmp = null;
                if (ctx.alias() != null) {
                    boolean isTrue = false;
                    for (int i = _depth; i >= 0; i--) {
                        if (_langContext.get(i).mapAS.containsKey(ctx.rightStatment().alias().NAME().getText())) {
                            ceTmp = _langContext.get(i).mapAS.get(ctx.rightStatment().alias().NAME().getText());
                            isTrue = true;
                            break;
                        }
                    }
                    if (!isTrue) {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Neexistuj�c� alias: " + ctx.rightStatment().alias().NAME().getText(), isTrue));
                        return;
                    }
                } else {
                    ceTmp = _langContext.get(_index).result;
                }
                
                String cmpNAME =ctx.rightStatment().NAME().getText().replaceAll("'", "");
                switch (ctx.NAME().getText().toLowerCase()) {
                    case "name":
                        for (ClassEntity ce : alTmp) {                            
                            for (ClassEntity sec : ceTmp) {
                                String cmpValue;
                                switch (cmpNAME.toLowerCase()){
                                    case "name":
                                        cmpValue =sec.getName();
                                        break;
                                    case "fqn":
                                        cmpValue =sec.getFQN();
                                        break;
                                    default:
                                        _error = true;
                                        _errMsg.add(new ErrorMessage("Neplatn� slovo :" + ctx.NAME().getText(), _error));
                                        return;
                                }
                                if (ce.getName().compareTo(cmpValue) == 0) {
                                    if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                        tmp.add(ce);
                                    }
                                } else {
                                    if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                                
                                if (ce.getName().contains(cmpValue)) {
                                    if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                            }
                        }
                        break;
                    case "fqn":
                        for (ClassEntity ce : alTmp) {                            
                            for (ClassEntity sec : ceTmp) {
                                String cmpValue;
                                switch (cmpNAME.toLowerCase()){
                                    case "name":
                                        cmpValue =sec.getName();
                                        break;
                                    case "fqn":
                                        cmpValue =sec.getFQN();
                                        break;
                                    default:
                                        _error = true;
                                        _errMsg.add(new ErrorMessage("Neplatn� slovo :" + ctx.NAME().getText(), _error));
                                        return;
                                }
                                if (ce.getFQN().compareTo(cmpValue) == 0) {
                                    if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                        tmp.add(ce);
                                    }
                                } else {
                                    if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                                
                                if (ce.getFQN().contains(cmpValue)) {
                                    if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        _error = true;
                        _errMsg.add(new ErrorMessage("Neplatn� slovo :" + ctx.NAME().getText(), _error));
                        return;                    
                }

            // </editor-fold>
                
            } else // NAME = @Annotated (M:N)
            {
                // <editor-fold defaultstate="collapsed" desc=" as? NAME = as? Annotated ">                
                switch (ctx.NAME().getText().toLowerCase()) {
                    case "name":
                        for (ClassEntity ce : alTmp) {                            
                            for ( AnnParaEntity sec : _annotatedRight) {
                                if (ce.getName().compareTo(sec.getValue()) == 0) {
                                    if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                        tmp.add(ce);
                                    }
                                } else {
                                    if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                                
                                if (ce.getName().contains(sec.getValue())) {
                                    if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                            }
                        }
                        break;
                    case "fqn":
                        for (ClassEntity ce : alTmp) {                            
                            for ( AnnParaEntity sec : _annotatedRight) {                                
                                if (ce.getFQN().compareTo(sec.getValue()) == 0) {
                                    if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                        tmp.add(ce);
                                    }
                                } else {
                                    if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                                
                                if (ce.getFQN().contains(sec.getValue())) {
                                    if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                        tmp.add(ce);
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        _error = true;
                        _errMsg.add(new ErrorMessage("Neplatn� slovo :" + ctx.NAME().getText(), _error));
                        return;                    
                }

            // </editor-fold>
                
            }                        
        }
        else if(ctx.OPERATORS() != null) //alias? annotated OPERATORS rightStatment
        {            
            if(ctx.rightStatment().STRING() != null)
            {             
                // <editor-fold defaultstate="collapsed" desc=" @Annotated = STRING ">
                String cmp = ctx.rightStatment().STRING().getText().replaceAll("'", "");
                for (int i = 0; i < _annotatedLeft.size(); i++) {
                    ClassEntity ce = alTmp.get(i);
                    AnnParaEntity left = _annotatedLeft.get(i);
                    if (left.getValue() == null) {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Anotace na lev� strane m� nevalidn� tvar porovn�vat se daj� pouze hodnoty parametru :", _error));
                        return;                        
                    }                    
                    if (left.getValue().compareTo(cmp) == 0) {
                        if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                            tmp.add(ce);
                        }
                    } else {
                        if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                            tmp.add(ce);
                        }
                    }

                    if (left.getValue().contains(cmp)) {
                        if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                            tmp.add(ce);
                        }
                    }                    
                }
                // </editor-fold>
            }
            else if(ctx.rightStatment().NAME() != null)
            {                
                // <editor-fold defaultstate="collapsed" desc=" @Annotated = NAME ">
                List<ClassEntity> ceTmp = null;
                if (ctx.alias() != null) {
                    boolean isTrue = false;
                    for (int i = _depth; i >= 0; i--) {
                        if (_langContext.get(i).mapAS.containsKey(ctx.rightStatment().alias().NAME().getText())) {
                            ceTmp = _langContext.get(i).mapAS.get(ctx.rightStatment().alias().NAME().getText());
                            isTrue = true;
                            break;
                        }
                    }
                    if (!isTrue) {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Neexistuj�c� alias: " + ctx.rightStatment().alias().NAME().getText(), isTrue));
                        return;
                    }
                } else {
                    ceTmp = _langContext.get(_index).result;
                }
                String cmpNAME =ctx.rightStatment().NAME().getText().replaceAll("'", "");
                for (int i = 0; i < _annotatedLeft.size(); i++) {
                    ClassEntity ce = alTmp.get(i);
                    AnnParaEntity left = _annotatedLeft.get(i);                    
                    if (left.getValue() == null) {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Anotace na lev� strane m� nevalidn� tvar porovn�vat se daj� pouze hodnoty parametru :", _error));
                        return;                        
                    }
                    for (ClassEntity sec : ceTmp) {
                        String cmpValue;
                        switch (cmpNAME.toLowerCase()){
                            case "name":
                                cmpValue =sec.getName();
                                break;
                            case "fqn":
                                cmpValue =sec.getFQN();
                                break;
                            default:
                                _error = true;
                                _errMsg.add(new ErrorMessage("Neplatn� slovo :" + ctx.NAME().getText(), _error));
                                return;
                        }
                        if (left.getValue().compareTo(cmpValue) == 0) {
                            if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                tmp.add(ce);
                            }
                        } else {
                            if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                tmp.add(ce);
                            }
                        }
                        
                        if (left.getValue().contains(cmpValue)) {
                            if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                tmp.add(ce);
                            }
                        }
                    }
                }

// </editor-fold>
            }
            else 
            {                          
                // <editor-fold defaultstate="collapsed" desc=" @Annotated = @Annotated ">
                for (int i = 0; i < _annotatedLeft.size(); i++) {
                    ClassEntity ce = alTmp.get(i);
                    AnnParaEntity left = _annotatedLeft.get(i);
                    if (left.getValue() == null) {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Anotace na lev� strane m� nevalidn� tvar porovn�vat se daj� pouze hodnoty parametru :", _error));
                        return;                        
                    }
                    for (AnnParaEntity sec : _annotatedRight) {
                        if (left.getValue().compareTo(sec.getValue()) == 0) {
                            if (ctx.OPERATORS().getText().compareTo("=") == 0) {
                                tmp.add(ce);
                            }
                        } else {
                            if (ctx.OPERATORS().getText().compareTo("!=") == 0) {
                                tmp.add(ce);
                            }
                        }
                        
                        if (left.getValue().contains(sec.getValue())) {
                            if (ctx.OPERATORS().getText().compareTo("~") == 0) {
                                tmp.add(ce);
                            }
                        }
                    }
                }

                // </editor-fold>
            }             
        }
        else    //alias? annotated
        {
            return; //vy��zeno v exitAnnotated;
        }
         alTmp.clear();
         alTmp.addAll(tmp);        
    }

    @Override
    public void enterEqual(queryParser.EqualContext ctx) {
        _alias = null;
        _isRight = false;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Cond ">
    @Override
    public void exitCond(queryParser.CondContext ctx) {
        if (_error) return;        
        
        List<ClassEntity> tmp = new ArrayList();
        
        if (ctx.equal() != null) // zpracovat v exitEqual(): equal 
        {
            return;
        }
        else if (ctx.OPERATORS() != null)//:innerSelect OPERATORS alias? NAME
        {                       
            if(ctx.alias() == null)
            {
                // <editor-fold defaultstate="collapsed" desc=" Not Alias ">
                if (ctx.OPERATORS().getText().compareToIgnoreCase("in") == 0) {
                    for (ClassEntity ce : _langContext.get(_depth).result) {
                        boolean isTrue = true;
                        for (ClassEntity ce2 : _langContext.get(_depth + 1).result) {
                            switch (ctx.NAME().getText().toLowerCase()) {
                                case "import":
                                    if (ce.getImportRelated(ce2.getFQN()) == null) //import extend implement name fqn
                                    {
                                        isTrue = false;
                                    }
                                    break;
                                case "extend":
                                    if (ce.getExtendsRelated(ce2.getFQN()) == null) //import extend implement name fqn
                                    {
                                        isTrue = false;
                                    }
                                    break;
                                case "implements":
                                    if (ce.getImplementsRelated(ce2.getFQN()) == null) //import extend implement name fqn
                                    {
                                        isTrue = false;
                                    }
                                    break;
                                case "name":
                                    if (ce.getName().compareTo(ce2.getName()) != 0) //import extend implement name fqn
                                    {
                                        isTrue = false;
                                    }
                                    break;
                                case "fqn":
                                    if (ce.getName().compareTo(ce2.getFQN()) != 0) //import extend implement name fqn
                                    {
                                        isTrue = false;
                                    }
                                    break;
                                default:
                                    _error = true;
                                    _errMsg.add(new ErrorMessage("O�ek�v�no kl��ov� slovo ne: " + ctx.NAME().getText(), _error));
                                    return;                                
                            }
                            
                        }
                        if (isTrue) {
                            tmp.add(ce);
                        }
                    }
                } else {
                    _error = true;
                    _errMsg.add(new ErrorMessage("Nepovolen� oper�tor: " + ctx.OPERATORS().getText(), _error));
                    return;
                }

// </editor-fold>
            }
            else    // with alias
            {                    
                // <editor-fold defaultstate="collapsed" desc=" Alias ">
                if (_langContext.get(_depth).mapAS.containsKey(ctx.alias().NAME().getText())) {
                    if (ctx.OPERATORS().getText().compareToIgnoreCase("in") == 0) {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {
                            if (_langContext.get(i).mapAS.containsKey(ctx.alias().NAME().getText())) {                                
                                for (ClassEntity ce : _langContext.get(i).mapAS.get(ctx.alias().NAME().getText())) {
                                    boolean isTrue = true;
                                    for (ClassEntity ce2 : _langContext.get(_depth + 1).result) {
                                        switch (ctx.NAME().getText().toLowerCase()) {
                                            case "import":
                                                if (ce.getImportRelated(ce2.getFQN()) == null) //import extend implement name fqn
                                                {
                                                    isTrue = false;
                                                }
                                                break;
                                            case "extend":
                                                if (ce.getExtendsRelated(ce2.getFQN()) == null) //import extend implement name fqn
                                                {
                                                    isTrue = false;
                                                }
                                                break;
                                            case "implements":
                                                if (ce.getImplementsRelated(ce2.getFQN()) == null) //import extend implement name fqn
                                                {
                                                    isTrue = false;
                                                }
                                                break;
                                            case "name":
                                                if (ce.getName().compareTo(ce2.getName()) != 0) //import extend implement name fqn
                                                {
                                                    isTrue = false;
                                                }
                                                break;
                                            case "fqn":
                                                if (ce.getName().compareTo(ce2.getFQN()) != 0) //import extend implement name fqn
                                                {
                                                    isTrue = false;
                                                }
                                                break;
                                            default:
                                                _error = true;
                                                _errMsg.add(new ErrorMessage("O�ek�v�no kl��ov� slovo ne: " + ctx.NAME().getText(), _error));
                                                return;                                            
                                        }
                                    }
                                    if (isTrue) {
                                        tmp.add(ce);
                                    }
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + ctx.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    } else {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Nepovolen� oper�tor: " + ctx.OPERATORS().getText(), _error));
                        return;
                    }
                }

// </editor-fold>
            }
        }
        else if (ctx.innerSelect() != null)//:NAME innerSelect          NAME:(EXIST |NotExist)
        {
            // <editor-fold defaultstate="collapsed" desc=" (Not)?Exist InnerSelect">

            boolean isEmpty = _langContext.get(_depth + 1).result.isEmpty();            
            switch (ctx.NAME().getText().toLowerCase()) {
                case "exist":
                    if (!isEmpty) {
                        tmp = _langContext.get(_depth).result;
                    }
                case "notexist":
                    if (isEmpty) {
                        tmp = _langContext.get(_depth).result;
                    }
                default:
                    _error = true;
                    _errMsg.add(new ErrorMessage("Nepovolen� oper�tor: " + ctx.NAME().getText(), _error));
                    return;
            }

// </editor-fold>    
        }       
        else //: (!)? alias? NAME
        {
            if(ctx.alias() == null)
            {
                // <editor-fold defaultstate="collapsed" desc=" NAME ">
                switch (ctx.NAME().getText().toLowerCase()) {
                    case "class":
                        for (ClassEntity re : _langContext.get(_depth).result) {                            
                            if (re.getType().compareToIgnoreCase("class") == 0) {
                                if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "interface":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.getType().compareToIgnoreCase("interface") == 0) {
                               if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "annotation":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.getType().compareToIgnoreCase("annotation") == 0) {
                                if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "enum":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.getType().compareToIgnoreCase("enum") == 0) {
                               if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "public":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.isPublic()) {
                               if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "protected":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.isProtected()) {
                                if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "private":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.isPrivate()) {
                                if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "final":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.isFinal()) {
                                if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    case "inner":
                        for (ClassEntity re : _langContext.get(_depth).result) {
                            if (re.isInner()) {
                                if(ctx.EXCLAMANTION() == null) {
                                    tmp.add(re);
                                }
                            }
                            else
                            {
                                if(ctx.EXCLAMANTION() != null) {
                                    tmp.add(re);
                                }
                            }
                        }
                        break;
                    default:
                        _error = true;
                        _errMsg.add(new ErrorMessage("Neo�ek�vyn� vstup: " + ctx.NAME().getText(), _error));
                        return;
                }

// </editor-fold>
            }
            else
            {
                // <editor-fold defaultstate="collapsed" desc=" alias.NAME ">
                boolean found = false;
                for (int i = _depth; i >= 0; i--) {
                    if (_langContext.get(i).mapAS.containsKey(ctx.alias().NAME().getText())) {
                        List<ClassEntity> tmc = _langContext.get(i).mapAS.get(ctx.alias().NAME().getText());
                        switch (ctx.NAME().getText().toLowerCase()) {
                            case "class":
                                for (ClassEntity re : tmc) {
                                    if (re.getType().compareToIgnoreCase("class") == 0) {
                                       if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "interface":
                                for (ClassEntity re : tmc) {
                                    if (re.getType().compareToIgnoreCase("interface") == 0) {
                                       if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "annotation":
                                for (ClassEntity re : tmc) {
                                    if (re.getType().compareToIgnoreCase("annotation") == 0) {
                                        if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "enum":
                                for (ClassEntity re : tmc) {
                                    if (re.getType().compareToIgnoreCase("enum") == 0) {
                                        if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "public":
                                for (ClassEntity re : tmc) {
                                    if (re.isPublic()) {
                                        if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "protected":
                                for (ClassEntity re : tmc) {
                                    if (re.isProtected()) {
                                        if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "private":
                                for (ClassEntity re : tmc) {
                                    if (re.isPrivate()) {
                                        if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "final":
                                for (ClassEntity re : tmc) {
                                    if (re.isFinal()) {
                                        if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            case "inner":
                                for (ClassEntity re : tmc) {
                                    if (re.isInner()) {
                                        if(ctx.EXCLAMANTION() == null) {
                                            tmp.add(re);
                                        }
                                    }
                                    else
                                    {
                                        if(ctx.EXCLAMANTION() != null) {
                                            tmp.add(re);
                                        }
                                    }
                                }
                                break;
                            default:
                                _error = true;
                                _errMsg.add(new ErrorMessage("Neo�ek�vyn� vstup: " + ctx.NAME().getText(), _error));
                                return;
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    _error = true;
                    _errMsg.add(new ErrorMessage("Alias: " + ctx.alias().NAME().getText() + " nebyl nalezen", true));
                    return;
                }

// </editor-fold>
            }
        }
        _langContext.get(_depth).result = tmp;
    }

    @Override
    public void enterCond(queryParser.CondContext ctx) {
        _alias = null;
    }
    
// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Conditions ">

    @Override
    public void enterConditions(queryParser.ConditionsContext ctx) {
        _langContext.get(_depth)._WHERE = true;
        if (!_langContext.get(_depth)._FROM) {
            _langContext.get(_depth).result = _graphContext.getClassInPackage("*");
            _langContext.get(_depth)._FROM = true;
        }
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" PackageName ">
    @Override
    public void exitPackageName(queryParser.PackageNameContext ctx) {
        if (_error) {
            return;
        }
        
        if (ctx.innerSelect() != null) {
            if (_as == null) {
                _langContext.get(_depth).result.addAll(_langContext.get(_depth + 1).result);
            } else {
                if (_langContext.get(_depth).mapAS.containsKey(_as)) //duplicitni alias
                {
                    _errMsg.add(new ErrorMessage("Tento alias u� existuje: " + _as, true));
                    _error = true;
                } else {
                    List<ClassEntity> tmp = _langContext.get(_depth + 1).result;                    
                    _langContext.get(_depth).mapAS.put(_as, tmp);
                    _as = null;
                }
            }
        } else if (ctx.EXCLAMANTION() != null) //from  !package
        {
            if (_as == null) { //from !package
                _langContext.get(_depth).result.addAll(_graphContext.getClassInPackage(ctx.STRING().getText()));
            } else //from !package as name
            {
                if (_langContext.get(_depth).mapAS.containsKey(_as)) //duplicitni alias
                {
                    _errMsg.add(new ErrorMessage("Tento alias u� existuje: " + _as, true));
                    _error = true;
                } else {
                    List<ClassEntity> tmp = _graphContext.getClassInPackage(ctx.STRING().getText());
                    if (_graphContext.isError()) {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Cest k baliku " + ctx.STRING().getText() + " neexistuje.", _error));
                        return;
                    }
                    _langContext.get(_depth).mapAS.put(_as, tmp);
                    _as = null;
                }
            }
        } else //from package
        {            
            if (_as == null) {
                _langContext.get(_depth).result.addAll(_graphContext.getClassInPackageRecursion(ctx.STRING().getText()));
            } else {
                if (_langContext.get(_depth).mapAS.containsKey(_as)) // duplicitni alias
                {
                    _errMsg.add(new ErrorMessage("Tento alias u� existuje: " + _as, true));
                    _error = true;
                } else {
                    List<ClassEntity> tmp = _graphContext.getClassInPackageRecursion(ctx.STRING().getText());
                    if (_graphContext.isError()) // spatna cesta k baliku
                    {
                        _error = true;
                        _errMsg.add(new ErrorMessage("Cest k baliku: " + ctx.STRING().getText() + " neexistuje", _error));
                        return;
                    }
                    _langContext.get(_depth).mapAS.put(_as, tmp);
                    _as = null;
                }
            }
        }
    }
    
    @Override
    public void enterPackageName(queryParser.PackageNameContext ctx) {
        _as = null;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Packages ">

    @Override
    public void exitPackages(queryParser.PackagesContext ctx) {
        if (_error) {
            return;
        }
        
        if (ctx.STAR() != null) {
            List<ClassEntity> tmp = _graphContext.getClassInPackage(ctx.STAR().getText());            
            if (tmp != null) {
                _langContext.get(_depth).result.addAll(tmp);
            }
        }        
    }    
    
    @Override
    public void enterPackages(queryParser.PackagesContext ctx) {
        _langContext.get(_depth)._FROM = true;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ParamName ">

    @Override
    public void exitParamName(queryParser.ParamNameContext ctx) {
        if (ctx.innerSelect() != null) {
            _langContext.get(_depth).selectListInner.addAll(_langContext.get(_depth + 1).result);
        }
        _langContext.get(_depth).selectList.add(ctx);
        
    }

    @Override
    public void enterParamName(queryParser.ParamNameContext ctx) {
        _alias = null;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ParamSelect ">

    @Override
    public void enterParamSelect(queryParser.ParamSelectContext ctx) {        
        _langContext.get(_depth)._SELECT = true;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SelectStatment ">
    @Override
    public void exitSelectStatment(queryParser.SelectStatmentContext ctx) {
        if (_error) {
            return;
        }        
        
        if (!_langContext.get(_depth)._FROM) {
            _langContext.get(_depth).result = _graphContext.getClassInPackage("*");
            _langContext.get(_depth)._FROM = true;
        }
        
        List<ClassEntity> tmp = new ArrayList();
        
        if (_langContext.get(_depth).selectList.isEmpty()) // select *
        {
            for (String str : _langContext.get(_depth).mapAS.keySet()) {                
                tmp.addAll(_langContext.get(_depth).mapAS.get(str));
            }            
            tmp.addAll(_langContext.get(_depth).result);
        }
        
        for (queryParser.ParamNameContext select : _langContext.get(_depth).selectList) {            
            if (select.EXCLAMANTION() != null)// ! name | ! name.name
            {                
                if (select.NAME().getText().compareToIgnoreCase("extend") == 0) //!name
                {
                    if (select.alias() == null) {
                        for (ClassEntity ce : _langContext.get(_depth).result) {
                            Iterable<ClassEntity> Related = ce.getInExtendsRelated();
                            if (Related != null) {
                                tmp.addAll(Lists.newArrayList(Related));
                            }
                        }
                    } else // !name.name
                    {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {
                            if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                for (ClassEntity ce : _langContext.get(i).mapAS.get(select.alias().NAME().getText())) {
                                    Iterable<ClassEntity> Related = ce.getInExtendsRelated();
                                    if (Related != null) {
                                        tmp.addAll(Lists.newArrayList(Related));
                                    }
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    }
                } else if (select.NAME().getText().compareToIgnoreCase("import") == 0) {
                    if (select.alias() == null) {
                        for (ClassEntity ce : _langContext.get(_depth).result) {
                            Iterable<ClassEntity> Related = ce.getInImportRelated();
                            if (Related != null) {
                                tmp.addAll(Lists.newArrayList(Related));
                            }
                        }
                    } else // !name.name
                    {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {
                            if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                for (ClassEntity ce : _langContext.get(i).mapAS.get(select.alias().NAME().getText())) {
                                    Iterable<ClassEntity> Related = ce.getInImportRelated();
                                    if (Related != null) {
                                        tmp.addAll(Lists.newArrayList(Related));
                                    }
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    }                    
                } else if (select.NAME().getText().compareToIgnoreCase("implements") == 0) {
                    if (select.alias() == null) {
                        for (ClassEntity ce : _langContext.get(_depth).result) {
                            Iterable<ClassEntity> Related = ce.getInImplementsRelated();
                            if (Related != null) {
                                tmp.addAll(Lists.newArrayList(Related));
                            }
                        }
                    } else // !name.name
                    {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {
                            if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                for (ClassEntity ce : _langContext.get(i).mapAS.get(select.alias().NAME().getText())) {
                                    Iterable<ClassEntity> Related = ce.getInImplementsRelated();
                                    if (Related != null) {
                                        tmp.addAll(Lists.newArrayList(Related));
                                    }
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    }                    
                }
            } else if (select.method() != null) {
                if (select.NAME() != null && select.NAME().getText().compareToIgnoreCase("call") == 0) {                    
                    if (select.method() != null) {                        
                        if (select.method().getChildCount() != 1) {
                            for (int i = 0; i < select.method().getChildCount(); i++) {
                                //roz���en� dotazy na casti methody !!!
                            }
                        } else {
                            String strTMP = select.method().STRING(0).getText().replaceAll("[' ]", "");
                            String paramsTMP = strTMP.replaceFirst("^[\\w<>]*", "").replaceAll("[)(]", "");
                            String[] paramTMP = {};
                            
                            if (paramsTMP.compareTo("") != 0) {
                                paramTMP = paramsTMP.split(",");
                            }
                            
                            String name = strTMP.replaceFirst("\\(.*", "");                            
                            
                            if (select.alias() == null) {
                                for (ClassEntity ce : _langContext.get(_depth).result) {
                                    for (MethodEntity me : ce.getMethodRelated(name)) {                                        
                                        if (me.getCountPara() != paramTMP.length) {
                                            continue;
                                        }
                                        boolean isTrueMethod = true;
                                        for (MethParaEntity mpr : me.getMethParaRelated()) {
                                            if (mpr.getFQN().compareTo(paramTMP[mpr.getIndex()]) != 0) {                                                
                                                isTrueMethod = false;
                                            }
                                        }
                                        if (isTrueMethod) {
                                            for (MethodEntity mic : me.getInCallRelated()) {
                                                tmp.add(mic.getInClassRelated());                                                
                                            }
                                        }
                                    }
                                }
                            } else {
                                boolean found = false;
                                for (int i = _depth; i >= 0; i--) {
                                    if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                        for (ClassEntity ce : _langContext.get(i).mapAS.get(select.alias().NAME().getText())) {
                                            for (MethodEntity me : ce.getMethodRelated(name)) {                                                
                                                if (me.getCountPara() != paramTMP.length) {
                                                    continue;
                                                }
                                                boolean isTrueMethod = true;
                                                for (MethParaEntity mpr : me.getMethParaRelated()) {
                                                    if (mpr.getFQN().compareTo(paramTMP[mpr.getIndex()]) != 0) {                                                        
                                                        isTrueMethod = false;
                                                    }
                                                }
                                                if (isTrueMethod) {
                                                    for (MethodEntity mic : me.getInCallRelated()) {
                                                        tmp.add(mic.getInClassRelated());                                                        
                                                    }
                                                }
                                            }
                                        }
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    _error = true;
                                    _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                                    return;
                                }
                            }
                        }
                    }
                }
            } else if (select.innerSelect() != null) // (inner select)
            {
                // z�stane pr�zdne vlozen� v�sledku je na konci metody _langContext.get(_depth+1).seleceListInner
            } else // name | name as name
            {                
                if (select.NAME().getText().compareToIgnoreCase("extend") == 0) {
                    if (select.alias() == null) {
                        for (ClassEntity ce : _langContext.get(_depth).result) {
                            Iterable<ClassEntity> Related = ce.getExtendsRelated();
                            if (Related != null) {
                                tmp.addAll(Lists.newArrayList(Related));
                            }
                        }
                    } else {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {                            
                            if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                for (ClassEntity ce : _langContext.get(i).mapAS.get(select.alias().NAME().getText())) {
                                    Iterable<ClassEntity> Related = ce.getExtendsRelated();
                                    if (Related != null) {
                                        tmp.addAll(Lists.newArrayList(Related));
                                    }
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    }
                } else if (select.NAME().getText().compareToIgnoreCase("import") == 0) {
                    if (select.alias() == null) {
                        for (ClassEntity ce : _langContext.get(_depth).result) {
                            Iterable<ClassEntity> Related = ce.getImportRelated();
                            if (Related != null) {
                                tmp.addAll(Lists.newArrayList(Related));
                            }
                        }
                    } else // !name.name
                    {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {
                            if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                for (ClassEntity ce : _langContext.get(i).mapAS.get(select.alias().NAME().getText())) {
                                    Iterable<ClassEntity> Related = ce.getImportRelated();
                                    if (Related != null) {
                                        tmp.addAll(Lists.newArrayList(Related));
                                    }
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    }
                } else if (select.NAME().getText().compareToIgnoreCase("implements") == 0) {
                    if (select.alias() == null) {
                        for (ClassEntity ce : _langContext.get(_depth).result) {
                            Iterable<ClassEntity> Related = ce.getImplementsRelated();
                            if (Related != null) {
                                tmp.addAll(Lists.newArrayList(Related));
                            }
                        }
                    } else {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {
                            if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                for (ClassEntity ce : _langContext.get(i).mapAS.get(select.alias().NAME().getText())) {
                                    Iterable<ClassEntity> Related = ce.getImplementsRelated();
                                    if (Related != null) {
                                        tmp.addAll(Lists.newArrayList(Related));
                                    }
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    }
                } else if (select.NAME().getText().compareToIgnoreCase("this") == 0) {
                    if (select.alias() == null) {
                        tmp.addAll(_langContext.get(_depth).result);                        
                    } else {
                        boolean found = false;
                        for (int i = _depth; i >= 0; i--) {
                            if (_langContext.get(i).mapAS.containsKey(select.alias().NAME().getText())) {
                                tmp.addAll(_langContext.get(i).mapAS.get(select.alias().NAME().getText()));                                
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            _error = true;
                            _errMsg.add(new ErrorMessage("Alias: " + select.alias().NAME().getText() + " nebyl nalezen", true));
                            return;
                        }
                    }
                }
            }
        }
        if (!_langContext.get(_depth).selectListInner.isEmpty()) {
            tmp.addAll(_langContext.get(_depth).selectListInner);
        }
        _langContext.get(_depth).result = tmp;
        if (_depth == 0) {
            _result = _langContext.get(_depth).result;
        }
    }
    
    @Override
    public void enterSelectStatment(queryParser.SelectStatmentContext ctx) {
        _langContext.add(new LangContext());
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" HelpClass ">
    public class ErrorMessage {

        public String message;        
        public boolean isError;
        
        public ErrorMessage(String m, boolean e) {
            message = m;            
            isError = e;
        }
        
        @Override
        public String toString() {
            return (isError ? "Error: " : "Warning: ") + message;
        }
        
    }    
    
    public class LangContext {

        private List<ErrorMessage> errorMessage;
        private List<ClassEntity> result;
        private Identifier id;
        private List<queryParser.ParamNameContext> selectList;
        private List<ClassEntity> selectListInner;
        private boolean _SELECT;
        private boolean _FROM;
        private boolean _WHERE;
        Map<String, List<ClassEntity>> mapAS;
        
        LangContext() {
            errorMessage = new ArrayList();
            result = new ArrayList();
            id = new Identifier();            
            selectList = new ArrayList();
            selectListInner = new ArrayList();
            mapAS = new TreeMap<String, List<ClassEntity>>();
            _SELECT = false;
            _FROM = false;
            _WHERE = false;
        }
    }
    
    public class Identifier {

        private List<Variable> variable;

        Identifier() {
            variable = new ArrayList();
        }

        public void add(String id, String value) {            
            boolean noFind = true;
            for (Variable v : variable) {
                if (v.id.compareTo(id) == 0) {
                    noFind = false;
                    v.value.add(value);
                }
            }
            if (noFind) {
                Variable v = new Variable();
                v.id = id;
                v.value.add(value);
                variable.add(v);
            }
        }

        public List<String> get(String id) {
            id = id.replaceFirst("\\\\", "$");
            for (Variable v : variable) {
                if (v.id.compareTo(id) == 0) {
                    return v.value;
                }
            }
            return null;
        }
    }
    
    public class Variable {

        private String id = null;
        private List<String> value = new ArrayList();
    }

// </editor-fold>
}