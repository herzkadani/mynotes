package ch.bbzbl.mynotes.views.sharednotes;

import ch.bbzbl.mynotes.components.appnav.NotesViewLayout;
import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.views.MainLayout;
import ch.bbzbl.mynotes.views.manageusers.ManageUsersView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@PageTitle("Shared Notes")
@Route(value = "shared-notes", layout = MainLayout.class)
@RolesAllowed("USER")
public class SharedNotesView extends VerticalLayout {

	private Logger LOGGER = LoggerFactory.getLogger(SharedNotesView.class);
	@Autowired
	private AuthenticatedUser authenticatedUser;
	private User user;

	public SharedNotesView(FolderService folderService, NoteService noteService, UserService userService, AuthenticatedUser authenticatedUser) {

		// get authenticated user
		Optional<User> user = authenticatedUser.get();
		if (user.isPresent()) {
			this.user = user.get();
			LOGGER.info(user.get().getUsername() + " opend Shared Notes View");
		} else {
			authenticatedUser.logout();
			getUI().ifPresent(ui -> ui.navigate("login"));
		}

		setHeightFull();
		NotesViewLayout notesViewLayout = new NotesViewLayout(folderService, noteService, userService, authenticatedUser, true);
		add(notesViewLayout);

	}

}
