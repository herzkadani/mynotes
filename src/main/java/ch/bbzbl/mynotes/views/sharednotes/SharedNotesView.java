package ch.bbzbl.mynotes.views.sharednotes;

import ch.bbzbl.mynotes.components.appnav.NotesViewLayout;
import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Shared Notes")
@Route(value = "shared-notes", layout = MainLayout.class)
@RolesAllowed("USER")
public class SharedNotesView extends VerticalLayout {

	public SharedNotesView(FolderService folderService, NoteService noteService, UserService userService, AuthenticatedUser authenticatedUser) {
		setHeightFull();
		NotesViewLayout notesViewLayout = new NotesViewLayout(folderService, noteService, userService, authenticatedUser, true);
		add(notesViewLayout);

	}

}
