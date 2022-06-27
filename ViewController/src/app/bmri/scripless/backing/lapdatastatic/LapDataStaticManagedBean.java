package app.bmri.scripless.backing.lapdatastatic;

import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichSelectOneRadio;
import oracle.adf.view.rich.component.rich.nav.RichGoButton;

import oracle.jbo.format.DefaultDateFormatter;
import oracle.jbo.format.FormatErrorException;

public class LapDataStaticManagedBean {
    private RichInputDate inpDateStart;
    private RichInputDate inpDateEnd;
    private RichSelectOneRadio inpReportType;
    private RichGoButton btnGoExp;
    private final static String URL_PREFIX = "/laporandata?name=staticinvestor";
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    Date now = new Date();
    String strDateNow = sdfDate.format(now);
    private String urlDestDefault =
        URL_PREFIX + "&tanggalawal=" + strDateNow +
        "&tanggalakhir=" + strDateNow + "&type=pdf";

    public LapDataStaticManagedBean() {
    }

    public void setInpDateStart(RichInputDate inpDateStart) {
        this.inpDateStart = inpDateStart;
        if (this.inpDateStart.getValue() == null ||
            this.inpDateStart.getValue().toString().equals("")) {
            this.inpDateStart.setValue(new Date());
        }
    }

    public RichInputDate getInpDateStart() {
        return inpDateStart;
    }

    public void setInpDateEnd(RichInputDate inpDateEnd) {
        this.inpDateEnd = inpDateEnd;
        if (this.inpDateEnd.getValue() == null ||
            this.inpDateEnd.getValue().toString().equals("")) {
            this.inpDateEnd.setValue(new Date());
        }
    }

    public RichInputDate getInpDateEnd() {
        return inpDateEnd;
    }

    public void setInpReportType(RichSelectOneRadio inpReportType) {
        this.inpReportType = inpReportType;
        if (this.inpReportType.getValue() == null ||
            this.inpReportType.getValue().toString().equals("")) {
            this.inpReportType.setValue("pdf");
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

    public void dateStartValueChangeListener(ValueChangeEvent valueChangeEvent) {

        FacesContext fctx = FacesContext.getCurrentInstance();
        DefaultDateFormatter ddf = new DefaultDateFormatter();
        String reportType = "", dateValueStart = "", dateValueEnd =
            "", urlDest = "", dateTimeValueStart = "", dateTimeValueEnd = "";

        try {
            reportType = inpReportType.getValue().toString();
        } catch (Exception e) {
            reportType = "";
        }

        try {
            dateValueStart = ddf.format("yyyy-MM-dd", inpDateStart.getValue());
            dateTimeValueStart = dateValueStart + " 00:00:00";
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dateValueStart = "";
        }

        try {
            dateValueEnd = ddf.format("yyyy-MM-dd", inpDateEnd.getValue());
            dateTimeValueEnd = dateValueEnd + " 23:59:59";
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dateValueEnd = "";
        }

        /*
        System.out.println("DATE START: " + dateValueStart);
        System.out.println("DATE END  : " + dateValueEnd);
        */

        if (dateValueStart.length() > 0 && dateValueEnd.length() > 0) {
            boolean dateValid = false;
            try {
                dateValid = compareDates(dateTimeValueStart, dateTimeValueEnd);
            } catch (ParseException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "Failed to parse date value.",
                                     "Failed to parse date value.");
                fctx.addMessage(null, msg);
            }

            if (dateValid) {
                btnGoExp.setDisabled(false);
                urlDest =
                        URL_PREFIX + "&tanggalawal=" + dateValueStart +
                        "&tanggalakhir=" + dateValueEnd + "&type=" + reportType;
            } else {
                btnGoExp.setDisabled(true);
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "End date cannot be earlier than Start date.",
                                     "End date cannot be earlier than Start date.");
                fctx.addMessage(null, msg);
            }
        }

        btnGoExp.setDestination(urlDest);
    }

    public void dateEndValueChangeListener(ValueChangeEvent valueChangeEvent) {

        FacesContext fctx = FacesContext.getCurrentInstance();
        DefaultDateFormatter ddf = new DefaultDateFormatter();
        String reportType = "", dateValueStart = "", dateValueEnd =
            "", urlDest = "", dateTimeValueStart = "", dateTimeValueEnd = "";

        try {
            reportType = inpReportType.getValue().toString();
        } catch (Exception e) {
            reportType = "";
        }

        try {
            dateValueStart = ddf.format("yyyy-MM-dd", inpDateStart.getValue());
            dateTimeValueStart = dateValueStart + " 00:00:00";
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dateValueStart = "";
        }

        try {
            dateValueEnd = ddf.format("yyyy-MM-dd", inpDateEnd.getValue());
            dateTimeValueEnd = dateValueEnd + " 23:59:59";
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dateValueEnd = "";
        }

        /*
        System.out.println("DATE START: " + dateValueStart);
        System.out.println("DATE END  : " + dateValueEnd);
        */

        if (dateValueStart.length() > 0 && dateValueEnd.length() > 0) {
            boolean dateValid = false;
            try {
                dateValid = compareDates(dateTimeValueStart, dateTimeValueEnd);
            } catch (ParseException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "Failed to parse date value.",
                                     "Failed to parse date value.");
                fctx.addMessage(null, msg);
            }

            if (dateValid) {
                btnGoExp.setDisabled(false);
                urlDest =
                        URL_PREFIX + "&tanggalawal=" + dateValueStart +
                        "&tanggalakhir=" + dateValueEnd + "&type=" + reportType;
            } else {
                btnGoExp.setDisabled(true);
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "End date cannot be earlier than Start date.",
                                     "End date cannot be earlier than Start date.");
                fctx.addMessage(null, msg);
            }
        }

        btnGoExp.setDestination(urlDest);
    }

    public void repTypeValueChangeListener(ValueChangeEvent valueChangeEvent) {

        FacesContext fctx = FacesContext.getCurrentInstance();
        DefaultDateFormatter ddf = new DefaultDateFormatter();
        String reportType = "", dateValueStart = "", dateValueEnd =
            "", urlDest = "", dateTimeValueStart = "", dateTimeValueEnd = "";

        try {
            reportType = inpReportType.getValue().toString();
        } catch (Exception e) {
            reportType = "";
        }

        try {
            dateValueStart = ddf.format("yyyy-MM-dd", inpDateStart.getValue());
            dateTimeValueStart = dateValueStart + " 00:00:00";
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dateValueStart = "";
        }

        try {
            dateValueEnd = ddf.format("yyyy-MM-dd", inpDateEnd.getValue());
            dateTimeValueEnd = dateValueEnd + " 23:59:59";
        } catch (FormatErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            dateValueEnd = "";
        }

        /*
        System.out.println("DATE START: " + dateValueStart);
        System.out.println("DATE END  : " + dateValueEnd);
        */

        if (dateValueStart.length() > 0 && dateValueEnd.length() > 0) {
            boolean dateValid = false;
            try {
                dateValid = compareDates(dateTimeValueStart, dateTimeValueEnd);
            } catch (ParseException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "Failed to parse date value.",
                                     "Failed to parse date value.");
                fctx.addMessage(null, msg);
            }

            if (dateValid) {
                btnGoExp.setDisabled(false);
                urlDest =
                        URL_PREFIX + "&tanggalawal=" + dateValueStart +
                        "&tanggalakhir=" + dateValueEnd + "&type=" + reportType;
            } else {
                btnGoExp.setDisabled(true);
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "End date cannot be earlier than Start date.",
                                     "End date cannot be earlier than Start date.");
                fctx.addMessage(null, msg);
            }
        }

        btnGoExp.setDestination(urlDest);
    }

    public static boolean compareDates(String psDate1,
                                       String psDate2) throws ParseException {
        SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date1 = dateFormat.parse(psDate1);
        Date date2 = dateFormat.parse(psDate2);
        if (date2.after(date1)) {
            return true;
        } else {
            return false;
        }
    }
}
