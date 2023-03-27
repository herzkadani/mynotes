package ch.bbzbl.mynotes.views.mynotes;

import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.Note;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.bbzbl.mynotes.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Objects;

@PageTitle("MyNotes")
@Route(value = "MyNotes", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class MyNotesView extends Div {

	public static final String LEER = "Leer";
	public static final String MIN_WIDTH = "300px";
	public static final String BEARBEITEN = "Bearbeiten";
	public static final String WARNING = "Warning";
	private final VerticalLayout noteLayout = new VerticalLayout();
	VirtualList<Folder> folderList = new VirtualList<>();
	VirtualList<Note> noteList = new VirtualList<>();
	RichTextEditor textEditor = new RichTextEditor();
	SplitLayout splitLayout;
	HorizontalLayout notesSplit = new HorizontalLayout();
	private TextField popupField = new TextField();
	private RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
	private AuthenticatedUser authenticatedUser;
	private User user;
	private FolderService folderService = null;
	private NoteService noteService = null;
	private UserService userService = null;
	private List<Folder> folders;
	private List<Note> notes;
	private Folder currentFolder;
	private Note currentNote;
	private long openFolderID;
	private long openNoteID;
	private final ComponentRenderer<Component, Folder> folderCardRenderer = new ComponentRenderer<>(
			folder -> {

				HorizontalLayout cardLayout = new HorizontalLayout();
				cardLayout.setMargin(true);

				TextField titel = new TextField();
				titel.setValue(folder.getTitel());
				titel.getElement().addEventListener("click", e -> {
					openFolderID = folder.getId();
					notes = folder.getNotes();
					if (notes.isEmpty()) {
						currentNote = new Note(LEER, LEER, currentFolder);
						updateNote();
						notes = userService.getFoldersByUserId(user.getId()).stream().filter(ffolder -> ffolder.getId() == openFolderID).findFirst().get().getNotes();

					} else {
						currentNote = notes.stream().findFirst().orElse(null);
					}
					noteList.setItems(notes);
					currentFolder = folder;

					folderList.setItems(folders);
				});

				Icon finish = createIcon(VaadinIcon.CHECK);
				finish.getElement().getThemeList().add("badge success");

				Icon close = createIcon(VaadinIcon.CLOSE_SMALL);
				close.getElement().getThemeList().add("badge error");

				Icon edit = createIcon(VaadinIcon.PENCIL);

				finish.addClickListener(iconClickEvent -> {
					cardLayout.remove(edit);
					cardLayout.remove(finish);
					cardLayout.remove(close);
					titel.setReadOnly(true);
					cardLayout.add(edit);


					folder.setTitel(titel.getValue());
					updateFolder();

					folderList.setItems(folders);
				});


				close.addClickListener(iconClickEvent -> {
					titel.setReadOnly(true);
					cardLayout.remove(edit);
					cardLayout.remove(finish);
					cardLayout.remove(close);
					cardLayout.add(titel);
					cardLayout.add(edit);

					folderList.setItems(folders);
				});

				edit.addClickListener(iconClickEvent -> {
					titel.setReadOnly(false);
					cardLayout.remove(edit);
					cardLayout.add(finish);
					cardLayout.add(close);
				});

				if (!Objects.equals(folder.getId(), openFolderID) && cardLayout.getChildren().count() >= 3) {
					cardLayout.removeAll();
					titel.setReadOnly(true);
					cardLayout.add(titel);
					cardLayout.add(edit);
				} else if (Objects.equals(folder.getId(), openFolderID)) {
					titel.getStyle().set("color", "#c64343");
					titel.setReadOnly(true);
					cardLayout.add(titel);
					cardLayout.add(edit);
				} else {
					titel.setReadOnly(true);
					cardLayout.add(titel);
					cardLayout.add(edit);
				}
				return cardLayout;
			});
	private final ComponentRenderer<Component, Note> noteCardRenderer = new ComponentRenderer<>(
			note -> {

				HorizontalLayout cardLayout = new HorizontalLayout();

				cardLayout.setMargin(true);

				TextField titel = new TextField();
				titel.setValue(note.getTitel());
				titel.getElement().addEventListener("click", e -> {
					openNoteID = note.getId();
					currentNote = note;
					notesSplit.remove(noteLayout);
					notesSplit.add(getNormalLayout());


					noteList.setItems(note.getFolder().getNotes());
				});

				Icon finish = createIcon(VaadinIcon.CHECK);
				finish.getElement().getThemeList().add("badge success");

				Icon close = createIcon(VaadinIcon.CLOSE_SMALL);
				close.getElement().getThemeList().add("badge error");

				Icon edit = createIcon(VaadinIcon.PENCIL);

				finish.addClickListener(iconClickEvent -> {
					titel.setReadOnly(true);
					cardLayout.remove(edit);
					cardLayout.remove(finish);
					cardLayout.remove(close);
					cardLayout.add(edit);

					note.setTitel(titel.getValue());
					updateNote();

					noteList.setItems(note.getFolder().getNotes());
				});

				close.addClickListener(iconClickEvent -> {
					titel.setReadOnly(true);
					cardLayout.remove(edit);
					cardLayout.remove(finish);
					cardLayout.remove(close);
					cardLayout.add(edit);

					noteList.setItems(note.getFolder().getNotes());
				});

				edit.addClickListener(iconClickEvent -> {
					cardLayout.remove(titel);
					cardLayout.remove(edit);
					titel.setReadOnly(false);
					cardLayout.add(finish);
					cardLayout.add(close);
				});

				if (!Objects.equals(note.getId(), openNoteID) && cardLayout.getChildren().count() >= 3) {

					titel.setReadOnly(true);
					cardLayout.add(titel);
					cardLayout.add(edit);

				} else if (Objects.equals(note.getId(), openNoteID)) {
					titel.getStyle().set("color", "#c64343");
					titel.setReadOnly(true);
					cardLayout.add(titel);
					cardLayout.add(edit);
				} else {
					titel.setReadOnly(true);
					cardLayout.add(titel);
					cardLayout.add(edit);
				}
				return cardLayout;
			});

	public MyNotesView(FolderService folderService, NoteService noteService, UserService userService, AuthenticatedUser authenticatedUser) {
		this.folderService = folderService;
		this.noteService = noteService;
		this.userService = userService;
		this.authenticatedUser = authenticatedUser;
		setSizeFull();
		getStyle().set("text-align", "center");

		add(createContent());

	}

	private SplitLayout createContent() {

		checkUser();

		if (this.userService.getFoldersByUserId(user.getId()).isEmpty()) {
			emptyFolder(LEER, false);
		} else if (userService.getFoldersByUserId(user.getId()).stream().findFirst().get().getNotes().isEmpty()) {
			currentFolder = folders.stream().findFirst().orElse(null);
			currentNote = new Note(LEER, LEER, currentFolder);
			updateNote();
			folders = userService.getFoldersByUserId(user.getId());
			notes = folders.stream().findFirst().get().getNotes();
		} else {
			folders = userService.getFoldersByUserId(user.getId());
			notes = folders.stream().findFirst().get().getNotes();
			currentFolder = folders.stream().findFirst().orElse(null);
			currentNote = notes.stream().findFirst().orElse(null);
		}

		folderList.setItems(folders);
		folderList.setRenderer(folderCardRenderer);
		folderList.setMinWidth(MIN_WIDTH);

		noteList.setItems(notes);
		noteList.setRenderer(noteCardRenderer);
		noteList.setMinWidth(MIN_WIDTH);

		Button addFolder = new Button("Neuer Ordner", e -> {
			newFolderOrNote(true);
		});

		VerticalLayout listLayout = new VerticalLayout(addFolder, folderList);

		Button addNote = new Button("Neue Notiz", e -> {

			newFolderOrNote(false);
		});
		VerticalLayout listLayoutN = new VerticalLayout(addNote, noteList);
		listLayoutN.setMaxWidth("450px");
		notesSplit.setPadding(true);
		notesSplit.add(listLayoutN);
		notesSplit.add(getNormalLayout());

		splitLayout = new SplitLayout(listLayout, notesSplit);
		splitLayout.setSplitterPosition(20);
		splitLayout.setHeightFull();
		splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);

		return splitLayout;
	}

	private void checkUser() {
		if (Objects.isNull(authenticatedUser)) {
			UI.getCurrent().navigate("login");
		}

		if (authenticatedUser.get().isPresent()) {
			user = authenticatedUser.get().get();
		} else {
			authenticatedUser.logout();
			UI.getCurrent().navigate("login");
		}
	}

	private void newFolderOrNote(boolean sourceFromFolderButton) {
		Dialog dialog;
		dialog = new Dialog();
		dialog.setHeaderTitle("Neu");

		VerticalLayout dialogLayout = createDialogLayout(sourceFromFolderButton);

		dialog.add(dialogLayout);

		Button saveButton = new Button("speichern", b -> {
			if (sourceFromFolderButton) {
				emptyFolder(popupField.getValue(), Objects.equals(radioGroup.getValue(), "public"));

			} else {
				newNote(popupField.getValue());
			}

			folderList.setItems(folders);

			dialog.close();
		});
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Button cancelButton = new Button("abbrechen", be -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		dialog.getFooter().add(saveButton);
		dialog.getFooter().add(cancelButton);

		add(dialog);

		dialog.open();
	}

	private void newNote(String titel) {
		currentNote = new Note(titel, LEER, currentFolder);
		updateNote();
		folders = userService.getFoldersByUserId(user.getId());
		notes = userService.getFoldersByUserId(user.getId()).stream().filter(folder -> folder.getId() == openFolderID).findFirst().get().getNotes();

		folderList.setItems(folders);
		noteList.setItems(notes);
	}

	private VerticalLayout createDialogLayout(boolean sourceFromFolderButton) {

		VerticalLayout verticalLayout = new VerticalLayout();
		popupField.setLabel("Name");
		verticalLayout.add(popupField);
		if (sourceFromFolderButton) {
			radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
			radioGroup.setLabel("Sichtbarkeit");
			radioGroup.setItems("public", "private");
			verticalLayout.add(radioGroup);
		}
		verticalLayout.setPadding(false);
		verticalLayout.setSpacing(false);
		verticalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
		verticalLayout.getStyle().set("width", "18rem").set("max-width", "100%");

		return verticalLayout;

	}

	private void emptyFolder(String titel, boolean visibility) {
		currentFolder = new Folder(titel, visibility, null, user);
		updateFolder();
		currentNote = new Note(LEER, LEER, currentFolder);
		updateNote();
		folders = userService.getFoldersByUserId(user.getId());
		notes = folders.stream().findFirst().get().getNotes();
	}

	private void updateFolder() {
		try {
			folderService.update(currentFolder);
		} catch (ObjectOptimisticLockingFailureException oolfe) {
			openWarningDialog();

		}
	}

	private void updateNote() {
		try {
			noteService.update(currentNote);
		} catch (ObjectOptimisticLockingFailureException oolfe) {
			openWarningDialog();

		}
	}

	private void openWarningDialog() {
		Dialog dialog;
		dialog = new Dialog();
		dialog.setHeaderTitle(WARNING);

		dialog.add(new Html(
				"<p>There has been a version conflict. Someone has just edited the same data and saved it before you. Click <a onClick=\"window.location.reload()\" style=\"color:#0044CC;\">here</a> to reload the page.</p>"));

		Button acept = new Button("OK", be -> dialog.close());
		acept.addClickShortcut(Key.ENTER);
		acept.addThemeVariants(ButtonVariant.LUMO_ERROR);

		dialog.getFooter().add(acept);

		add(dialog);

		dialog.open();
	}

	private VerticalLayout getNormalLayout() {

		noteLayout.removeAll();

		H2 noteTitle = new H2(currentNote.getTitel());

		String valueAsHtml = currentNote.getContent();

		Label label = new Label(valueAsHtml);

		Button noteButton = new Button(BEARBEITEN, this::editMode);

		noteLayout.add(noteTitle, label, noteButton);

		return noteLayout;

	}

	private VerticalLayout getEditLayout() {

		noteLayout.removeAll();

		TextField noteTitle = new TextField("Titel");
		noteTitle.setValue(currentNote.getTitel());
		noteTitle.setClearButtonVisible(true);

		String valueAsHtml = currentNote.getContent();

		textEditor.asHtml().setValue(valueAsHtml);

		Button save = new Button("speichern", e -> {

			currentNote.setContent(textEditor.getValue());
			currentNote.setTitel(noteTitle.getValue());
			updateNote();
			notesSplit.remove(noteLayout);
			notesSplit.add(getNormalLayout());
		});
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button cancel = new Button("abbrechen", buttonClickEvent -> {
			notesSplit.remove(noteLayout);
			notesSplit.add(getNormalLayout());
		});
		cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

		HorizontalLayout buttonsLayout = new HorizontalLayout(save, cancel);

		noteLayout.add(noteTitle, textEditor, buttonsLayout);

		return noteLayout;
	}

	private void editMode(ClickEvent<Button> eB) {

		if (eB.getSource().getText().equals(BEARBEITEN)) {

			notesSplit.remove(noteLayout);
			notesSplit.add(getEditLayout());

		} else {

			notesSplit.remove(noteLayout);
			notesSplit.add(getNormalLayout());

		}
	}

	private Icon createIcon(VaadinIcon vaadinIcon) {
		Icon icon = vaadinIcon.create();
		icon.getStyle().set("padding", "var(--lumo-space-xs");
		return icon;
	}
}
