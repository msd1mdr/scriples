package app.bmri.scripless.backing.useraccessmenu;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.AttributeBinding;
import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;

public class AssignRoleUserBackingBean implements Serializable {

    // Shuttle operations
    @SuppressWarnings("compatibility:4429120787595570625")
    private static final long serialVersionUID = 1L;
    List selectedRoles;
    List allRoles;
    Boolean refreshSelectedList = false;
    private final String USER_ROLE_ITERATOR =
        "UserAccessRolesAdminView1Iterator";
    private final String ROLE_ITERATOR = "RolesShuttleView1Iterator";

    public AssignRoleUserBackingBean() {
        super();
    }

    public void setSelectedRoles(List selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public List getSelectedRoles() {

        if (selectedRoles == null || refreshSelectedList) {
            selectedRoles =
                    attributeListForIterator(USER_ROLE_ITERATOR, "Role");
        }
        return selectedRoles;
    }

    public void setAllRoles(List allRoles) {
        this.allRoles = allRoles;
    }

    public List getAllRoles() {
        if (allRoles == null) {
            allRoles = selectItemsForIterator(ROLE_ITERATOR, "Name", "Name");
        }
        return allRoles;
    }

    public void refreshSelectedList(ValueChangeEvent e) {
        refreshSelectedList = true;
    }

    // Shuttle operations

    public static List attributeListForIterator(String iteratorName,
                                                String valueAttrName) {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding =
            (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding iter = binding.findIteratorBinding(iteratorName);
        List attributeList = new ArrayList();
        for (Row r : iter.getAllRowsInRange()) {
            attributeList.add(r.getAttribute(valueAttrName));
        }
        return attributeList;
    }

    public static List<SelectItem> selectItemsForIterator(String iteratorName,
                                                          String valueAttrName,
                                                          String displayAttrName) {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding =
            (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding iter = binding.findIteratorBinding(iteratorName);
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (Row r : iter.getAllRowsInRange()) {
            selectItems.add(new SelectItem(r.getAttribute(valueAttrName),
                                           (String)r.getAttribute(displayAttrName)));
        }
        return selectItems;
    }

    public String processShuttle() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        String closeAfter = "close";
        BindingContext bctx = BindingContext.getCurrent();
        DCBindingContainer binding =
            (DCBindingContainer)bctx.getCurrentBindingsEntry();
        DCIteratorBinding iter =
            (DCIteratorBinding)binding.get(USER_ROLE_ITERATOR);

        //Removing all rows
        for (Row r : iter.getAllRowsInRange()) {
            r.remove();
        }

        if (this.getSelectedRoles().size() > 0) {
            for (int i = 0; i < getSelectedRoles().size(); i++) {

                Row row = iter.getRowSetIterator().createRow();

                row.setNewRowState(Row.STATUS_INITIALIZED);
                row.setAttribute("UserName", this.getCurrentUserName());
                row.setAttribute("Role", getSelectedRoles().get(i));

                iter.getRowSetIterator().insertRow(row);
                iter.setCurrentRowWithKey(row.getKey().toStringFormat(true));

            }
        }
        String ok = doCommit();
        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Role(s) has been added into " +
                             getCurrentUserName() + ".",
                             "Role(s) has been added into " +
                             getCurrentUserName() + ".");
        fctx.addMessage(null, msg);
        return closeAfter;
    }

    public String doCommit() {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("Commit");
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        return null;
    }

    public String getCurrentUserName() {
        BindingContext bctx = BindingContext.getCurrent();
        DCBindingContainer bindings =
            (DCBindingContainer)bctx.getCurrentBindingsEntry();
        AttributeBinding attr = (AttributeBinding)bindings.get("UserName");
        String userName = (String)attr.getInputValue();
        return userName;
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }
}
