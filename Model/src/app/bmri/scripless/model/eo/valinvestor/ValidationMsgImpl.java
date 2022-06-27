package app.bmri.scripless.model.eo.valinvestor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import oracle.jbo.AttributeList;
import oracle.jbo.Key;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.SequenceImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue May 03 14:09:06 ICT 2016
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class ValidationMsgImpl extends EntityImpl {
    private static EntityDefImpl mDefinitionObject;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Batchref {
            public Object get(ValidationMsgImpl obj) {
                return obj.getBatchref();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setBatchref((String)value);
            }
        }
        ,
        Detailref {
            public Object get(ValidationMsgImpl obj) {
                return obj.getDetailref();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setDetailref((String)value);
            }
        }
        ,
        Participantid {
            public Object get(ValidationMsgImpl obj) {
                return obj.getParticipantid();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setParticipantid((String)value);
            }
        }
        ,
        Sidnumber {
            public Object get(ValidationMsgImpl obj) {
                return obj.getSidnumber();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setSidnumber((String)value);
            }
        }
        ,
        Accountnumber {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAccountnumber();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAccountnumber((String)value);
            }
        }
        ,
        CreateTime {
            public Object get(ValidationMsgImpl obj) {
                return obj.getCreateTime();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ackparticipantid {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAckparticipantid();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAckparticipantid((String)value);
            }
        }
        ,
        AckTime {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAckTime();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAckTime((Date)value);
            }
        }
        ,
        Acksidnumber {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAcksidnumber();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAcksidnumber((String)value);
            }
        }
        ,
        Ackaccountnumber {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAckaccountnumber();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAckaccountnumber((String)value);
            }
        }
        ,
        Ackinvestorpassport {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAckinvestorpassport();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAckinvestorpassport((String)value);
            }
        }
        ,
        Ackinvestorid {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAckinvestorid();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAckinvestorid((String)value);
            }
        }
        ,
        Ackinvestornpwp {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAckinvestornpwp();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAckinvestornpwp((String)value);
            }
        }
        ,
        Ackinvestorname {
            public Object get(ValidationMsgImpl obj) {
                return obj.getAckinvestorname();
            }

            public void put(ValidationMsgImpl obj, Object value) {
                obj.setAckinvestorname((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(ValidationMsgImpl object);

        public abstract void put(ValidationMsgImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static final int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() +
                AttributesEnum.staticValues().length;
        }

        public static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int BATCHREF = AttributesEnum.Batchref.index();
    public static final int DETAILREF = AttributesEnum.Detailref.index();
    public static final int PARTICIPANTID = AttributesEnum.Participantid.index();
    public static final int SIDNUMBER = AttributesEnum.Sidnumber.index();
    public static final int ACCOUNTNUMBER = AttributesEnum.Accountnumber.index();
    public static final int CREATETIME = AttributesEnum.CreateTime.index();
    public static final int ACKPARTICIPANTID = AttributesEnum.Ackparticipantid.index();
    public static final int ACKTIME = AttributesEnum.AckTime.index();
    public static final int ACKSIDNUMBER = AttributesEnum.Acksidnumber.index();
    public static final int ACKACCOUNTNUMBER = AttributesEnum.Ackaccountnumber.index();
    public static final int ACKINVESTORPASSPORT = AttributesEnum.Ackinvestorpassport.index();
    public static final int ACKINVESTORID = AttributesEnum.Ackinvestorid.index();
    public static final int ACKINVESTORNPWP = AttributesEnum.Ackinvestornpwp.index();
    public static final int ACKINVESTORNAME = AttributesEnum.Ackinvestorname.index();

    /**
     * This is the default constructor (do not remove).
     */
    public ValidationMsgImpl() {
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        if (mDefinitionObject == null) {
            mDefinitionObject = EntityDefImpl.findDefObject("app.bmri.scripless.model.eo.valinvestor.ValidationMsg");
        }
        return mDefinitionObject;
    }

    /**
     * Gets the attribute value for Batchref, using the alias name Batchref.
     * @return the Batchref
     */
    public String getBatchref() {
        return (String)getAttributeInternal(BATCHREF);
    }

    /**
     * Sets <code>value</code> as the attribute value for Batchref.
     * @param value value to set the Batchref
     */
    public void setBatchref(String value) {
        setAttributeInternal(BATCHREF, value);
    }

    /**
     * Gets the attribute value for Detailref, using the alias name Detailref.
     * @return the Detailref
     */
    public String getDetailref() {
        return (String)getAttributeInternal(DETAILREF);
    }

    /**
     * Sets <code>value</code> as the attribute value for Detailref.
     * @param value value to set the Detailref
     */
    public void setDetailref(String value) {
        setAttributeInternal(DETAILREF, value);
    }

    /**
     * Gets the attribute value for Participantid, using the alias name Participantid.
     * @return the Participantid
     */
    public String getParticipantid() {
        return (String)getAttributeInternal(PARTICIPANTID);
    }

    /**
     * Sets <code>value</code> as the attribute value for Participantid.
     * @param value value to set the Participantid
     */
    public void setParticipantid(String value) {
        setAttributeInternal(PARTICIPANTID, value);
    }

    /**
     * Gets the attribute value for Sidnumber, using the alias name Sidnumber.
     * @return the Sidnumber
     */
    public String getSidnumber() {
        return (String)getAttributeInternal(SIDNUMBER);
    }

    /**
     * Sets <code>value</code> as the attribute value for Sidnumber.
     * @param value value to set the Sidnumber
     */
    public void setSidnumber(String value) {
        setAttributeInternal(SIDNUMBER, value);
    }

    /**
     * Gets the attribute value for Accountnumber, using the alias name Accountnumber.
     * @return the Accountnumber
     */
    public String getAccountnumber() {
        return (String)getAttributeInternal(ACCOUNTNUMBER);
    }

    /**
     * Sets <code>value</code> as the attribute value for Accountnumber.
     * @param value value to set the Accountnumber
     */
    public void setAccountnumber(String value) {
        setAttributeInternal(ACCOUNTNUMBER, value);
    }

    /**
     * Gets the attribute value for CreateTime, using the alias name CreateTime.
     * @return the CreateTime
     */
    public Date getCreateTime() {
        return (Date)getAttributeInternal(CREATETIME);
    }


    /**
     * Gets the attribute value for Ackparticipantid, using the alias name Ackparticipantid.
     * @return the Ackparticipantid
     */
    public String getAckparticipantid() {
        return (String)getAttributeInternal(ACKPARTICIPANTID);
    }

    /**
     * Sets <code>value</code> as the attribute value for Ackparticipantid.
     * @param value value to set the Ackparticipantid
     */
    public void setAckparticipantid(String value) {
        setAttributeInternal(ACKPARTICIPANTID, value);
    }

    /**
     * Gets the attribute value for AckTime, using the alias name AckTime.
     * @return the AckTime
     */
    public Date getAckTime() {
        return (Date)getAttributeInternal(ACKTIME);
    }

    /**
     * Sets <code>value</code> as the attribute value for AckTime.
     * @param value value to set the AckTime
     */
    public void setAckTime(Date value) {
        setAttributeInternal(ACKTIME, value);
    }

    /**
     * Gets the attribute value for Acksidnumber, using the alias name Acksidnumber.
     * @return the Acksidnumber
     */
    public String getAcksidnumber() {
        return (String)getAttributeInternal(ACKSIDNUMBER);
    }

    /**
     * Sets <code>value</code> as the attribute value for Acksidnumber.
     * @param value value to set the Acksidnumber
     */
    public void setAcksidnumber(String value) {
        setAttributeInternal(ACKSIDNUMBER, value);
    }

    /**
     * Gets the attribute value for Ackaccountnumber, using the alias name Ackaccountnumber.
     * @return the Ackaccountnumber
     */
    public String getAckaccountnumber() {
        return (String)getAttributeInternal(ACKACCOUNTNUMBER);
    }

    /**
     * Sets <code>value</code> as the attribute value for Ackaccountnumber.
     * @param value value to set the Ackaccountnumber
     */
    public void setAckaccountnumber(String value) {
        setAttributeInternal(ACKACCOUNTNUMBER, value);
    }

    /**
     * Gets the attribute value for Ackinvestorpassport, using the alias name Ackinvestorpassport.
     * @return the Ackinvestorpassport
     */
    public String getAckinvestorpassport() {
        return (String)getAttributeInternal(ACKINVESTORPASSPORT);
    }

    /**
     * Sets <code>value</code> as the attribute value for Ackinvestorpassport.
     * @param value value to set the Ackinvestorpassport
     */
    public void setAckinvestorpassport(String value) {
        setAttributeInternal(ACKINVESTORPASSPORT, value);
    }

    /**
     * Gets the attribute value for Ackinvestorid, using the alias name Ackinvestorid.
     * @return the Ackinvestorid
     */
    public String getAckinvestorid() {
        return (String)getAttributeInternal(ACKINVESTORID);
    }

    /**
     * Sets <code>value</code> as the attribute value for Ackinvestorid.
     * @param value value to set the Ackinvestorid
     */
    public void setAckinvestorid(String value) {
        setAttributeInternal(ACKINVESTORID, value);
    }

    /**
     * Gets the attribute value for Ackinvestornpwp, using the alias name Ackinvestornpwp.
     * @return the Ackinvestornpwp
     */
    public String getAckinvestornpwp() {
        return (String)getAttributeInternal(ACKINVESTORNPWP);
    }

    /**
     * Sets <code>value</code> as the attribute value for Ackinvestornpwp.
     * @param value value to set the Ackinvestornpwp
     */
    public void setAckinvestornpwp(String value) {
        setAttributeInternal(ACKINVESTORNPWP, value);
    }

    /**
     * Gets the attribute value for Ackinvestorname, using the alias name Ackinvestorname.
     * @return the Ackinvestorname
     */
    public String getAckinvestorname() {
        return (String)getAttributeInternal(ACKINVESTORNAME);
    }

    /**
     * Sets <code>value</code> as the attribute value for Ackinvestorname.
     * @param value value to set the Ackinvestorname
     */
    public void setAckinvestorname(String value) {
        setAttributeInternal(ACKINVESTORNAME, value);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index,
                                           AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value,
                                         AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }


    /**
     * @param batchref key constituent
     * @param detailref key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(String batchref, String detailref) {
        return new Key(new Object[]{batchref, detailref});
    }

    /**
     * Add attribute defaulting logic in this method.
     * @param attributeList list of attribute names/values to initialize the row
     */
    protected void create(AttributeList attributeList) {
        super.create(attributeList);
        
        if (getBatchref() == null || this.getDetailref() == null) {
            SequenceImpl seq = new SequenceImpl("VALIDATION_INVESTOR_SEQ", getDBTransaction());
            String seqNext = seq.getSequenceNumber().toString();
            java.util.Date today=new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String todayDate = sdf.format(today).toString();
            this.setDetailref("A" + todayDate + seqNext);
            this.setBatchref("A" + todayDate + seqNext + "a");
        }
    }
}
