package app.bmri.scripless.backing.logfileviewer;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichSelectOneRadio;

import oracle.adf.view.rich.component.rich.nav.RichGoButton;

import oracle.jbo.format.DefaultDateFormatter;
import oracle.jbo.format.FormatErrorException;

public class LogFileViewerManagedBean {
    private RichInputDate inpDate;
    private RichSelectOneRadio inpReportType;
    private RichGoButton btnGoExp;
    private final static String URL_PREFIX = "/logfile";
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    Date now = new Date();
    String strDateNow = sdfDate.format(now);
    private String urlDestDefault =
        URL_PREFIX + "?tanggal=" + strDateNow + "&type=IVERR_";

    public LogFileViewerManagedBean() {
    }

    public void setInpDate(RichInputDate inpDate) {
        this.inpDate = inpDate;
        if (this.inpDate.getValue() == null ||
            this.inpDate.getValue().toString().equals("")) {
            this.inpDate.setValue(new Date());
        }
    }

    public RichInputDate getInpDate() {
        return inpDate;
    }

    public void setInpReportType(RichSelectOneRadio inpReportType) {
        this.inpReportType = inpReportType;
        if (this.inpReportType.getValue() == null ||
            this.inpReportType.getValue().toString().equals("")) {
            this.inpReportType.setValue("&type=IVERR_");
        }
    }

    public RichSelectOneRadio getInpReportType() {
        return inpReportType;
    }

    public void setBtnGoExp(RichGoButton btnGoExp) {
        this.btnGoExp = btnGoExp;
        if (this.btnGoExp.getDestination() == null ||
            this.btnGoExp.getDestination().toString().equals("")) {
            this.btnGoExp.setDestination(urlDestDefault);
        }
    }

    public RichGoButton getBtnGoExp() {
        return btnGoExp;
    }
    
    public void dateValueChangeListener(ValueChangeEvent valueChangeEvent) {
        DefaultDateFormatter ddf = new DefaultDateFormatter();
        String reportType="",dateValue="",urlDest="";
        
        try {
            reportType = inpReportType.getValue().toString();
        } catch (Exception e) {
            reportType = "";
        }
        
        try {
            dateValue = ddf.format("yyyy-MM-dd", inpDate.getValue());
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dateValue = "";
        }
        
        urlDest = URL_PREFIX + "?tanggal=" + dateValue + reportType;
        
        btnGoExp.setDestination(urlDest);
    }

    public void repTypeValueChangeListener(ValueChangeEvent valueChangeEvent) {
        DefaultDateFormatter ddf = new DefaultDateFormatter();
        String reportType="", dateValue="", urlDest="";
        try {
            reportType = inpReportType.getValue().toString();
        } catch (Exception e) {
            reportType = "";
        }
        
        try {
            dateValue = ddf.format("yyyy-MM-dd", inpDate.getValue());
        } catch (FormatErrorException e) {
            dateValue = "";
        }
        
        urlDest = URL_PREFIX + "?tanggal=" + dateValue + reportType;
        
        btnGoExp.setDestination(urlDest);
    }
}
