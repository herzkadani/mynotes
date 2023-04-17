package ch.bbzbl.mynotes.components.appnav;

import ch.bbzbl.mynotes.data.entity.Folder;
import ch.bbzbl.mynotes.data.entity.Note;
import ch.bbzbl.mynotes.data.entity.User;
import ch.bbzbl.mynotes.data.service.FolderService;
import ch.bbzbl.mynotes.data.service.NoteService;
import ch.bbzbl.mynotes.data.service.UserService;
import ch.bbzbl.mynotes.security.AuthenticatedUser;
import ch.qos.logback.core.joran.conditional.IfAction;
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
import com.vaadin.flow.component.html.H3;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Objects;

public class NotesViewLayout extends Div {

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
	private final TextField popupField = new TextField();
	private final RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
	private final AuthenticatedUser authenticatedUser;
	private User user;
	private FolderService folderService = null;
	private NoteService noteService = null;
	private UserService userService = null;
	private List<Folder> folders;
	private List<Note> notes;
	private Folder currentFolder;
	private Note currentNote;
	private long openFolderID;
	private final boolean sharedView;
	private final ComponentRenderer<Component, Folder> folderCardRenderer = new ComponentRenderer<>(
			folder -> {

				HorizontalLayout cardLayout = new HorizontalLayout();
				cardLayout.setMargin(true);

				H2 titel = new H2(folder.getTitel());

				titel.addClickListener(e -> {
					openFolderID = folder.getId();
					notes = folder.getNotes();
					if (notes.isEmpty()) {
						currentNote = new Note(LEER, LEER, currentFolder);
						updateNote();
						getNotes();

					} else {
						currentNote = notes.stream().findFirst().orElse(null);
					}
					noteList.setItems(notes);
					currentFolder = folder;

					folderList.setItems(folders);
				});

				Icon edit = createIcon(VaadinIcon.PENCIL);

				edit.addClickListener(iconClickEvent -> {
					newDialogFolderOrNote(true, false, folder, null);

				});

				if (Objects.equals(folder.getId(), openFolderID)) {
					titel.getStyle().set("color", "#c64343");

				}
				cardLayout.add(titel);
				cardLayout.add(edit);
				return cardLayout;
			});
	private long openNoteID;
	private final ComponentRenderer<Component, Note> noteCardRenderer = new ComponentRenderer<>(
			note -> {

				HorizontalLayout cardLayout = new HorizontalLayout();

				cardLayout.setMargin(true);

				H3 titel = new H3(note.getTitel());

				titel.addClickListener(e -> {
					openNoteID = note.getId();
					currentNote = note;
					notesSplit.remove(noteLayout);
					notesSplit.add(getNormalLayout());


					noteList.setItems(note.getFolder().getNotes());
				});

				Icon edit = createIcon(VaadinIcon.PENCIL);

				edit.addClickListener(iconClickEvent -> {
					newDialogFolderOrNote(false, false, null, note);

				});

				if (Objects.equals(note.getId(), openNoteID)) {
					titel.getStyle().set("color", "#c64343");

				}
				cardLayout.add(titel);
				cardLayout.add(edit);
				return cardLayout;
			});

	public NotesViewLayout(FolderService folderService, NoteService noteService, UserService userService, AuthenticatedUser authenticatedUser, boolean sharedView) {
		this.folderService = folderService;
		this.noteService = noteService;
		this.userService = userService;
		this.authenticatedUser = authenticatedUser;
		this.sharedView = sharedView;
		setSizeFull();
		getStyle().set("text-align", "center");

		add(createContent());
	}

	private void getNotes() {
		if (sharedView) {
			notes = folderService.getPublicFolders().stream()
					.filter(ffolder -> ffolder.getId() == openFolderID)
					.findFirst()
					.get().getNotes();
		} else {
			notes = userService.getFoldersByUserId(user.getId()).stream()
					.filter(ffolder -> ffolder.getId() == openFolderID)
					.findFirst()
					.get().getNotes();
		}

	}

	private SplitLayout createContent() {
		checkUser();

		List<Folder> tempFolders = getFolders();

		if (tempFolders.isEmpty()) {
			emptyFolder(LEER, sharedView);
		} else if (tempFolders.stream().findFirst().get().getNotes().isEmpty()) {
			currentFolder = tempFolders.stream().findFirst().orElse(null);
			currentNote = new Note(LEER, LEER, currentFolder);
			updateNote();

			folders = getFolders();
			notes = folders.stream().findFirst().get().getNotes();
		} else {
			folders = getFolders();
			notes = folders.stream().findFirst().get().getNotes();
			currentFolder = folders.stream().findFirst().orElse(null);
			currentNote = notes.stream().findFirst().orElse(null);

		}
		openFolderID = currentFolder.getId();
		openNoteID = currentNote.getId();

		folderList.setItems(folders);
		folderList.setRenderer(folderCardRenderer);
		folderList.setMinWidth(MIN_WIDTH);

		noteList.setItems(notes);
		noteList.setRenderer(noteCardRenderer);
		noteList.setMinWidth(MIN_WIDTH);

		Button addFolder = new Button("Neuer Ordner", e -> {
			newDialogFolderOrNote(true, true, null, null);
		});

		VerticalLayout listLayout = new VerticalLayout(new H2("Notizbücher"), folderList, addFolder);

		Button addNote = new Button("Neue Notiz", e -> {

			newDialogFolderOrNote(false, true, null, null);
		});
		VerticalLayout listLayoutN = new VerticalLayout(new H3("Notizen"), noteList, addNote);
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

	private List<Folder> getFolders() {
		if (sharedView) {
			return folderService.getPublicFolders();
		} else {
			return this.userService.getFoldersByUserId(user.getId());
		}
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

	private void newDialogFolderOrNote(boolean sourceFromFolderButton, boolean isNewObject, Folder folder, Note note) {
		Dialog dialog;
		dialog = new Dialog();
		if (isNewObject) {
			dialog.setHeaderTitle("Neu");
		} else {
			dialog.setHeaderTitle("Bearbeiten");
		}
		VerticalLayout dialogLayout;

		if (note != null) {
			dialogLayout = createDialogLayout(sourceFromFolderButton, note.getTitel(), sharedView);
		} else if (folder != null) {
			dialogLayout = createDialogLayout(sourceFromFolderButton, folder.getTitel(), folder.isPublic());
		} else {
			dialogLayout = createDialogLayout(sourceFromFolderButton, "", sharedView);
		}

		dialog.add(dialogLayout);

		Button saveButton = new Button("speichern", b -> {
			if (isNewObject) {
				if (sourceFromFolderButton) {
					emptyFolder(popupField.getValue(), Objects.equals(radioGroup.getValue(), "public"));

				} else {
					newNote(popupField.getValue());
				}
			} else {
				if (sourceFromFolderButton) {

					folder.setTitel(popupField.getValue());
					if ("private".equals(radioGroup.getValue())) {
						folder.setPublic(false);
					} else {
						folder.setPublic(true);
					}
					currentFolder = folder;
					updateFolder();
					folders = getFolders();
					notes = folders.stream().findFirst().get().getNotes();
				} else {
					note.setTitel(popupField.getValue());
					currentNote = note;
					updateNote();
					folders = getFolders();
					getNotes();
				}
			}

			folderList.setItems(folders);
			noteList.setItems(notes);
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
		folders = getFolders();
		getNotes();
	}

	private VerticalLayout createDialogLayout(boolean sourceFromFolderButton, String text, boolean folderVisibility) {

		VerticalLayout verticalLayout = new VerticalLayout();
		popupField.setLabel("Name");
		popupField.isRequired();
		popupField.setValue(text);
		verticalLayout.add(popupField);
		if (sourceFromFolderButton) {
			radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
			radioGroup.setLabel("Sichtbarkeit");
			radioGroup.setItems("public", "private");
			if (!folderVisibility) {
				radioGroup.setValue("private");
			} else {
				radioGroup.setValue("public");
			}
			radioGroup.isRequired();
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
		folders = getFolders();
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
				"<p>There has been a version conflict. Someone has just edited the same data and saved it before you."
						+ " Click <a onClick=\"window.location.reload()\" style=\"color:#0044CC;\">here</a> to reload the page.</p>"));

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

		Html label = new Html("<div>" + valueAsHtml + "</div>");

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

			getNotes();
			currentNote = noteService.get(openNoteID).get();
			noteList.setItems(notes);
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
