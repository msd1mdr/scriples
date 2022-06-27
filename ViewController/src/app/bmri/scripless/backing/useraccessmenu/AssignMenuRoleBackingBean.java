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

public class AssignMenuRoleBackingBean implements Serializable {

    // Shuttle operations
    @SuppressWarnings("compatibility:4429120787595570625")
    private static final long serialVersionUID = 1L;
    List selectedMenuItems;
    List allMenuItems;
    Boolean refreshSelectedList = false;
    private final String ROLE_MENU_ITEM_ITERATOR =
        "RoleMenuItemsAdminView1Iterator";
    private final String MENU_ITEMS_ITERATOR = "MenuItemsShuttleView1Iterator";

    public AssignMenuRoleBackingBean() {
        super();
    }

    public void setSelectedMenuItems(List selectedMenuItems) {
        this.selectedMenuItems = selectedMenuItems;
    }

    public List getSelectedMenuItems() {

        if (selectedMenuItems == null || refreshSelectedList) {
            selectedMenuItems =
                    attributeListForIterator(ROLE_MENU_ITEM_ITERATOR, "MitId");
        }
        return selectedMenuItems;
    }

    public void setAllMenuItems(List allMenuItems) {
        this.allMenuItems = allMenuItems;
    }

    public List getAllMenuItems() {
        if (allMenuItems == null) {
            allMenuItems =
                    selectItemsForIterator(MENU_ITEMS_ITERATOR, "Id", "Label");
        }
        return allMenuItems;
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

    public oracle.jbo.domain.DBSequence getCurrentRoleId() {
        BindingContext bctx = BindingContext.getCurrent();
        DCBindingContainer bindings =
            (DCBindingContainer)bctx.getCurrentBindingsEntry();
        AttributeBinding attr = (AttributeBinding)bindings.get("Id");
        oracle.jbo.domain.DBSequence roleId =
            (oracle.jbo.domain.DBSequence)attr.getInputValue();

        return roleId;
    }

    public String processShuttle() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        String closeAfter = "close";
        BindingContext bctx = BindingContext.getCurrent();
        DCBindingContainer binding =
            (DCBindingContainer)bctx.getCurrentBindingsEntry();
        DCIteratorBinding iter =
            (DCIteratorBinding)binding.get(ROLE_MENU_ITEM_ITERATOR);

        //Removing all rows
        for (Row r : iter.getAllRowsInRange()) {
            r.remove();
        }

        if (this.getSelectedMenuItems().size() > 0) {
            for (int i = 0; i < getSelectedMenuItems().size(); i++) {

                Row row = iter.getRowSetIterator().createRow();

                row.setNewRowState(Row.STATUS_INITIALIZED);
                row.setAttribute("RolId", getCurrentRoleId());
                row.setAttribute("MitId", getSelectedMenuItems().get(i));

                iter.getRowSetIterator().insertRow(row);
                iter.setCurrentRowWithKey(row.getKey().toStringFormat(true));
            }
        }
        String ok = doCommit();
        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu(s) has been added into " +
                             getCurrentRoleId() + ".",
                             "Menu(s) has been added into " +
                             getCurrentRoleId() + ".");
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

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

}
