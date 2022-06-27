package app.bmri.scripless.backing.monivestormsg;


import app.bmri.scripless.adfextensions.ADFUtils;
import app.bmri.scripless.model.am.InvestorModuleImpl;

import javax.faces.event.PhaseEvent;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.layout.RichPanelSplitter;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.event.PollEvent;

public class MonitoringInvestorManagedBean {
    private RichPanelSplitter panSplitter;
    private RichInputText txtStaticSubmit;
    private RichInputText txtStmtSubmit;
    private RichInputText txtValSubmit;
    private RichInputText txtStaticAck;
    private RichInputText txtStmtAck;
    private RichInputText txtValAck;

    public MonitoringInvestorManagedBean() {
    }

    public void splitterPositionSetter(PhaseEvent phaseEvent) {
        int width = GetScreenWorkingWidth();
        int midScreen = (width/2)-15;
        panSplitter.setSplitterPosition(midScreen);
    }
    
    public static int GetScreenWorkingWidth() {
        return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    }

    public static int GetScreenWorkingHeight() {
        return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
    }

    public void setPanSplitter(RichPanelSplitter panSplitter) {
        this.panSplitter = panSplitter;
    }

    public RichPanelSplitter getPanSplitter() {
        return panSplitter;
    }

    public void activatePollListener(PollEvent pollEvent) {
        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        ViewObject voOutgoing = investorModule.getInvestorMsgOutgoingView1();
        voOutgoing.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtStaticSubmit);
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtStmtSubmit);
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtValSubmit);
        
        ViewObject voIncoming = investorModule.getInvestorMsgIncomingView1();
        voIncoming.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtStaticAck);
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtStmtAck);
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtValAck);
    }

    public void setTxtStaticSubmit(RichInputText txtStaticSubmit) {
        this.txtStaticSubmit = txtStaticSubmit;
    }

    public RichInputText getTxtStaticSubmit() {
        return txtStaticSubmit;
    }

    public void setTxtStmtSubmit(RichInputText txtStmtSubmit) {
        this.txtStmtSubmit = txtStmtSubmit;
    }

    public RichInputText getTxtStmtSubmit() {
        return txtStmtSubmit;
    }

    public void setTxtValSubmit(RichInputText txtValSubmit) {
        this.txtValSubmit = txtValSubmit;
    }

    public RichInputText getTxtValSubmit() {
        return txtValSubmit;
    }

    public void setTxtStaticAck(RichInputText txtStaticAck) {
        this.txtStaticAck = txtStaticAck;
    }

    public RichInputText getTxtStaticAck() {
        return txtStaticAck;
    }

    public void setTxtStmtAck(RichInputText txtStmtAck) {
        this.txtStmtAck = txtStmtAck;
    }

    public RichInputText getTxtStmtAck() {
        return txtStmtAck;
    }

    public void setTxtValAck(RichInputText txtValAck) {
        this.txtValAck = txtValAck;
    }

    public RichInputText getTxtValAck() {
        return txtValAck;
    }
}
