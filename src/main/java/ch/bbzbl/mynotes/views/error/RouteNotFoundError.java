package ch.bbzbl.mynotes.views.error;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
public class RouteNotFoundError extends Component
       implements HasErrorParameter<NotFoundException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
          ErrorParameter<NotFoundException> parameter) {
        if (parameter.getCustomMessage().equals("Access denied")) {
            getElement().setText("Sie haben keine Berechtigung für diese Seite.");
            return HttpServletResponse.SC_FORBIDDEN;
        }
    getElement().setText("Diese Seite wurde nicht gefunden: '"
                    + event.getLocation().getPath()
                    + "'");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
