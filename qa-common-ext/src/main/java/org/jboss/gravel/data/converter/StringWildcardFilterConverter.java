package org.jboss.gravel.data.converter;

import org.jboss.gravel.data.filter.StringWildcardFilter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 */
public final class StringWildcardFilterConverter implements Converter {
    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        return new StringWildcardFilter(string);
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        return object.toString();
    }
}