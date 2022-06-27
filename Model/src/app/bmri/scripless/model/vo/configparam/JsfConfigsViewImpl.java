package app.bmri.scripless.model.vo.configparam;

import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Jun 08 09:30:55 ICT 2016
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class JsfConfigsViewImpl extends ViewObjectImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public JsfConfigsViewImpl() {
    }

    /**
     * Returns the variable value for confKey.
     * @return variable value for confKey
     */
    public String getconfKey() {
        return (String)ensureVariableManager().getVariableValue("confKey");
    }

    /**
     * Sets <code>value</code> for variable confKey.
     * @param value value to bind as confKey
     */
    public void setconfKey(String value) {
        ensureVariableManager().setVariableValue("confKey", value);
    }

    /**
     * Returns the variable value for confDesc.
     * @return variable value for confDesc
     */
    public String getconfDesc() {
        return (String)ensureVariableManager().getVariableValue("confDesc");
    }

    /**
     * Sets <code>value</code> for variable confDesc.
     * @param value value to bind as confDesc
     */
    public void setconfDesc(String value) {
        ensureVariableManager().setVariableValue("confDesc", value);
    }
}