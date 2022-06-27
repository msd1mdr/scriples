package app.bmri.scripless.backing.investor;

import java.io.IOException;
import java.io.OutputStream;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.BindingContainer;

import oracle.jbo.AttributeDef;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.server.ViewAttributeDefImpl;
import oracle.jbo.server.ViewObjectImpl;

public class InvestorManagedBean {
    
    private static final String COL_HDR_CSV = "NO_REKENING,KODE_INVESTOR,SID,NAMA_INVESTOR,KODE_AB,FIELD1,EXCPT1,EXCPT2,ACCTNO,ACTYPE,PARTID,PARTNM,IVSTNM,IVSTID,SUBACN,BJCRT8,BJSTAT,BJVUSR,BJVDT8,BJVTME,BJVALD,BJSEQN,BJCBAL,BEJSC1,BEJSC2,BEJSC3,BEJSC4,BEJFL1,BEJFL2,\n";
    
    public InvestorManagedBean() {
    }

    /**Method to get BindingsContainer for current page
     * @return
     */
    public BindingContainer getBindingsCont() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    /**Method to download ViewObject's data in plain text file
     * @param facesContext
     * @param outputStream
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public void fileDownloadListener(FacesContext facesContext,
                                     OutputStream outputStream) throws UnsupportedEncodingException,
                                                                       IOException {
        OutputStreamWriter w = new OutputStreamWriter(outputStream, "UTF-8");
        //Get itertaor from bindings
        DCIteratorBinding deptIter =
            (DCIteratorBinding)getBindingsCont().get("InvestorView1Iterator");
        //Get ViewObject from Iterator
        ViewObjectImpl vo = (ViewObjectImpl)deptIter.getViewObject();
        ViewAttributeDefImpl[] attrDefs = vo.getViewAttributeDefImpls();
        int count = 0;
        RowSetIterator rsi = vo.createRowSetIterator(null);
        while (rsi.hasNext()) {
            Row nextRow = rsi.next();
            if (nextRow != null) {
                // Add header csv file
                w.write(COL_HDR_CSV);
                // Code to iterate over ViewObject's column to get all columns value at runtime
                for (ViewAttributeDefImpl attrDef : attrDefs) {
                    byte attrKind =
                        attrDefs[count].getAttributeKind(); //checks attribute kind for each element in an array of AttributeDefs
                    if (attrKind != AttributeDef.ATTR_ASSOCIATED_ROW &&
                        attrKind != AttributeDef.ATTR_ASSOCIATED_ROWITERATOR) {
                        String columnName = attrDef.getName();
                        if (!columnName.equals("RowID")) {
                            w.write(nextRow.getAttribute(columnName) + ",");
                        }
                    }
                }
                // Code to create new line text line for new Row
                w.write(System.getProperty("line.separator"));
            }
        }
        //Flush the writer after wrting file
        w.flush();
    }
}
