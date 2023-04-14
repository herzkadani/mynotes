package ch.bbzbl.mynotes.views.mynotes;

import ch.bbzbl.mynotes.components.appnav.NotesViewLayout;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@PageTitle("MyNotes")
@Route(value = "MyNotes", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class MyNotesView extends Div {

	public MyNotesView(FolderService folderService, NoteService noteService, UserService userService, AuthenticatedUser authenticatedUser) {

		setHeightFull();
		NotesViewLayout notesViewLayout = new NotesViewLayout(folderService, noteService, userService, authenticatedUser, false);
		add(notesViewLayout);

	}
}
