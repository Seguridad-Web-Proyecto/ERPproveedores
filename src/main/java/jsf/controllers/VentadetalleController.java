package jsf.controllers;

import entidades.Ventadetalle;
import jsf.clas.util.JsfUtil;
import jsf.clas.util.PaginationHelper;
import bean.sesion.VentadetalleFacade;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("ventadetalleController")
@SessionScoped
public class VentadetalleController implements Serializable {

    private Ventadetalle current;
    private DataModel items = null;
    @EJB
    private bean.sesion.VentadetalleFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public VentadetalleController() {
    }

    public Ventadetalle getSelected() {
        if (current == null) {
            current = new Ventadetalle();
            current.setVentadetallePK(new entidades.VentadetallePK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private VentadetalleFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Ventadetalle) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Ventadetalle();
        current.setVentadetallePK(new entidades.VentadetallePK());
        selectedItemIndex = -1;
        return "List";
    }

    public String create() {
        try {
            current.getVentadetallePK().setProductoid(current.getProducto().getProductoid());
            current.getVentadetallePK().setVentaid(current.getOrdenventa().getOrdenventaid());
            getFacade().create(current);
            JsfUtil.addSuccessMessage("¡Venta detalle creada con exito!");
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addSuccessMessage("¡Lo sentimos la operación no pudo completarse intente mas tarde!");
            return "List";
        }
    }

    public String prepareEdit() {
        current = (Ventadetalle) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.getVentadetallePK().setProductoid(current.getProducto().getProductoid());
            current.getVentadetallePK().setVentaid(current.getOrdenventa().getOrdenventaid());
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("¡Venta detalle editada con exito!");
            return "List";
        } catch (Exception e) {
            JsfUtil.addSuccessMessage("¡Lo sentimos la operación no pudo completarse intente mas tarde!");
            return "List";
        }
    }

    public String destroy() {
        current = (Ventadetalle) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage("¡Venta Detalle eliminada con exito!");
        } catch (Exception e) {
            JsfUtil.addSuccessMessage("¡Lo sentimos la operación no pudo completarse intente mas tarde!");
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Ventadetalle getVentadetalle(entidades.VentadetallePK id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Ventadetalle.class)
    public static class VentadetalleControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VentadetalleController controller = (VentadetalleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ventadetalleController");
            return controller.getVentadetalle(getKey(value));
        }

        entidades.VentadetallePK getKey(String value) {
            entidades.VentadetallePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entidades.VentadetallePK();
            key.setVentaid(Long.parseLong(values[0]));
            key.setProductoid(Long.parseLong(values[1]));
            return key;
        }

        String getStringKey(entidades.VentadetallePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getVentaid());
            sb.append(SEPARATOR);
            sb.append(value.getProductoid());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Ventadetalle) {
                Ventadetalle o = (Ventadetalle) object;
                return getStringKey(o.getVentadetallePK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Ventadetalle.class.getName());
            }
        }

    }

}
