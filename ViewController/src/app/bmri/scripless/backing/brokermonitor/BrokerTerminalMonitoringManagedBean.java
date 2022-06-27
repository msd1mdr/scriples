package app.bmri.scripless.backing.brokermonitor;

import app.bmri.scripless.adfextensions.ADFUtils;
import app.bmri.scripless.model.am.InvestorModuleImpl;

import oracle.adf.view.faces.bi.component.graph.UIGraph;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.event.PollEvent;

public class BrokerTerminalMonitoringManagedBean {
    private RichTable brokerMonTable;
    private UIGraph ringChartBroker;

    public BrokerTerminalMonitoringManagedBean() {
    }

    public void activatePollListener(PollEvent pollEvent) {
        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        ViewObject voInfo = investorModule.getConnectTView1();
        voInfo.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(brokerMonTable);

        ViewObject voChart = investorModule.getConnectTChartView1();
        voChart.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(ringChartBroker);

    }

    public void setBrokerMonTable(RichTable brokerMonTable) {
        this.brokerMonTable = brokerMonTable;
    }

    public RichTable getBrokerMonTable() {
        return brokerMonTable;
    }

    public void setRingChartBroker(UIGraph ringChartBroker) {
        this.ringChartBroker = ringChartBroker;
    }

    public UIGraph getRingChartBroker() {
        return ringChartBroker;
    }
}
